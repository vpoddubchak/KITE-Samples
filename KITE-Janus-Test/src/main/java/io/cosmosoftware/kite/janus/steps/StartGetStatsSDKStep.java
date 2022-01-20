package io.cosmosoftware.kite.janus.steps;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.TestStep;
import org.openqa.selenium.JavascriptExecutor;

import javax.json.JsonObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static io.cosmosoftware.kite.util.TestUtils.executeJsScript;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class StartGetStatsSDKStep extends TestStep {

  private final String pathToGetStats;
  private final String testName;
  private final String testId;
  private final String logstashUrl;
  private final String sfu;
  private final int statsPublishingInterval;
  private final String pathToJar;
  private final String peerConnection;

  //todo: refactor StartGetStatsSDKStep and getStats-sdk.js and properly pass user/roomId/sfu/testname... from Java to the SDK and move to kite-extras
  
  public StartGetStatsSDKStep(
      Runner runner,
      String testName,
      JsonObject getStatsSdk,
      String pathToJar,
      String peerConnection) {
    super(runner);
    this.pathToGetStats = getStatsSdk.getString("pathToGetStatsSdk");
    this.testName = testName;
    this.testId =
        getStatsSdk.getString(
            "testId",
            "\""
                + testName
                + "_"
                + new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date())
                + "\"");
    this.logstashUrl = getStatsSdk.getString("logstashUrl");
    this.sfu = "\"" + getStatsSdk.getString("sfu") + "\"";
    this.statsPublishingInterval = getStatsSdk.getInt("statsPublishingInterval", 30000);
    this.pathToJar = pathToJar;
    this.peerConnection = peerConnection;
  }

  @Override
  public String stepDescription() {
    return "Loading GetStats Script for " + testId;
  }

  @Override
  protected void step() throws KiteTestException {
    logger.info("Loading GetStats script from " + pathToGetStats);
    try {
      if (peerConnection != null) {
        executeJsScript(webDriver,"window.pc =  streaming.webrtcStuff.pc;");
        executeJsScript(webDriver,"username = \"user\";");
        executeJsScript(webDriver,"myroom = \"roomId\";");
        executeJsScript(webDriver,"window.pc =  streaming.webrtcStuff.pc;");
      }

      loadGetStats(
          readFile(pathToGetStats), testName, testId, logstashUrl, sfu, statsPublishingInterval);
      logger.info("GetStats-SDK Script loaded");
    } catch (IOException e) {
      e.printStackTrace();
      throw new KiteTestException("Failed to load GetStats", Status.BROKEN, e);
    }
    waitAround(2000);
  }

  /**
   * Load GetStats script into browser
   *
   * @param getStatsFile
   * @param testName
   * @param testId
   * @param logstashUrl
   * @param sfu
   * @param statsPublishingInterval
   * @return the javascript return string
   */
  private String loadGetStats(
      String getStatsFile,
      String testName,
      String testId,
      String logstashUrl,
      String sfu,
      int statsPublishingInterval)
      throws KiteTestException {

    String[] sendSplit = getStatsFile.split("KITETestName, KITETestId");
    String getStatsScript = sendSplit[0];
    for (int i = 1; i <= 2; i++) {
      sendSplit[i] = "\"" + testName + "\", \"" + testId + "\"" + sendSplit[i];
      getStatsScript = getStatsScript + sendSplit[i];
    }

    String[] initSplit = getStatsScript.split("testStats.init.* pc, ");
    getStatsScript =
        initSplit[0]
            + "testStats.init(\""
            + logstashUrl
            + "\", "
            + "username, myroom, "
            + sfu
            + ", pc, "
            + initSplit[1];

    String[] publishingSplit = getStatsScript.split("testStats.startPublishing\\(15000\\)");
    getStatsScript =
        publishingSplit[0]
            + "testStats.startPublishing("
            + statsPublishingInterval
            + ")"
            + publishingSplit[1];

    logger.info("Executing Javascript getStatsScript: \n" + getStatsScript);
    return (String) executeJsScript(webDriver, getStatsScript);
  }

  private String readFile(String pathToScript) throws IOException, KiteTestException {
    /*
    Files.lines(Paths.get(pathToGetStats), StandardCharsets.UTF_8)
         .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    */
    logger.info("Loading " + pathToScript);
    InputStream in = null;
    if (pathToJar != null) {
      JarFile jarFile = new JarFile(new File(pathToJar));
      if (pathToScript.startsWith("/")) {
        pathToScript = pathToScript.substring(1);
      }
      JarEntry jarEntry = jarFile.getJarEntry(pathToScript);
      in = jarFile.getInputStream(jarEntry);
    } else {
      in = getClass().getResourceAsStream(pathToScript);
    }
    if (in == null) {
      throw new IOException("File " + pathToScript + " not found in classpath");
    }
    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
    StringWriter out = new StringWriter();
    int b;
    while ((b = buf.read()) != -1) {
      out.write(b);
    }
    out.flush();
    out.close();
    in.close();
    //        executeJsScript(webDriver, out.toString());
    return out.toString();
  }
}
