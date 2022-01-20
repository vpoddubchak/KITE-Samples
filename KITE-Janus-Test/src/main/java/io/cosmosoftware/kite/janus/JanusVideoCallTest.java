package io.cosmosoftware.kite.janus;

import io.cosmosoftware.kite.instrumentation.Scenario;
import io.cosmosoftware.kite.janus.checks.FirstVideoCheck;
import io.cosmosoftware.kite.janus.checks.ReceiverVideoCheck;
import io.cosmosoftware.kite.janus.pages.JanusPage;
import io.cosmosoftware.kite.janus.steps.GetStatsStep;
import io.cosmosoftware.kite.janus.steps.LeaveDemoStep;
import io.cosmosoftware.kite.janus.steps.NWInstrumentationStep;
import io.cosmosoftware.kite.janus.steps.StartDemoStep;
import io.cosmosoftware.kite.janus.steps.videocall.CallPeerStep;
import io.cosmosoftware.kite.janus.steps.videocall.JoinVideoCallStep;
import io.cosmosoftware.kite.janus.steps.videocall.RegisterUserToVideoCallStep;
import io.cosmosoftware.kite.steps.*;
import org.webrtc.kite.config.client.Client;
import org.webrtc.kite.tests.KiteBaseTest;
import org.webrtc.kite.tests.TestRunner;

import javax.json.JsonObject;

import static org.webrtc.kite.Utils.getStackTrace;

public class JanusVideoCallTest extends KiteBaseTest {
  

  protected boolean sfu = false;
  private JsonObject getChartsConfig = null;
  private boolean allureCharts = false;

  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    if (this.payload != null) {
      sfu = payload.getBoolean("sfu", false);
      this.getChartsConfig = this.payload.getJsonObject("getCharts");
      this.allureCharts = this.getChartsConfig != null && this.getChartsConfig.getBoolean("enabled");
    }
  }
  
  @Override
  protected void populateTestSteps(TestRunner runner) {
    try {
      int runnerId = runner.getId();
      String testCaseName = shortTCName();
      System.setProperty("webdriver.http.factory", "apache");

      final JanusPage janusPage = new JanusPage(runner);
      runner.addStep(new StartDemoStep(runner, this.url));
      runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));

      runner.addStep(new RegisterUserToVideoCallStep(runner, runnerId, testCaseName));
      runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));

      runner.addStep(new CallPeerStep(runner, runnerId, testCaseName));
      runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));

      runner.addStep(new JoinVideoCallStep(runner, runnerId, testCaseName));
      runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));

      runner.addStep(new FirstVideoCheck(runner));
      runner.addStep(new ReceiverVideoCheck(runner));
      runner.addStep(new ScreenshotStep(runner));
      
      if (this.allureCharts) {
        runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));
        runner.addStep(new StartGetStatsStep(runner, getChartsConfig));
      }
      
      if (this.getStats()) {
        runner.addStep(new GetStatsStep(runner, getStatsConfig, sfu, janusPage)); //need to find the name of the remote Peer connections
        runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));
      }

      for (Scenario scenario : scenarioArrayList ) {
        runner.addStep(new WaitForOthersStep(runner, this, runner.getLastStep()));
        runner.addStep(new NWInstrumentationStep(runner, scenario, runner.getId()));
        runner.addStep(new ScreenshotStep(runner));
      }

      if (this.meetingDuration > 0) {
        runner.addStep(new StayInMeetingStep(runner, meetingDuration));
      }

      if (this.allureCharts) {
        runner.addStep(new StopGetStatsStep(runner));
        runner.addStep(new GenerateChartsStep(runner, getChartsConfig, getTestJar()));
      }
      
      if (this.takeScreenshotForEachTest()) {
        runner.addStep(new ScreenshotStep(runner));
      }
      
      runner.addStep(new LeaveDemoStep(runner));
    } catch (Exception e) {
      logger.error(getStackTrace(e));
    }
  }

  private String shortTCName() {
    StringBuilder name = new StringBuilder();
    for(int index = 0; index < this.tuple.size(); ++index) {
      Client client = this.tuple.get(index);
      name.append(client.getBrowserSpecs().getPlatform().name(), 0, 3);
      name.append(client.getBrowserSpecs().getBrowserName(), 0, 2);
      if (client.getBrowserSpecs().getVersion() != null) {
        name.append(client.getBrowserSpecs().getVersion());
      }
    }
    return name.toString();
  }
  
  
}
