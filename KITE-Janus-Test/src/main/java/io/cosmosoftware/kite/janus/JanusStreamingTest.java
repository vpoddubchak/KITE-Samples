package io.cosmosoftware.kite.janus;


import io.cosmosoftware.kite.janus.checks.StreamingVideoCheck;
import io.cosmosoftware.kite.janus.pages.JanusPage;
import io.cosmosoftware.kite.janus.steps.GetStatsStep;
import io.cosmosoftware.kite.janus.steps.LeaveDemoStep;
import io.cosmosoftware.kite.janus.steps.StartDemoStep;
import io.cosmosoftware.kite.janus.steps.StartGetStatsSDKStep;
import io.cosmosoftware.kite.janus.steps.streaming.JoinStreamingStep;
import io.cosmosoftware.kite.steps.*;
import org.webrtc.kite.tests.KiteBaseTest;
import org.webrtc.kite.tests.TestRunner;

import javax.json.JsonObject;

import static org.webrtc.kite.Utils.getStackTrace;

public class JanusStreamingTest extends KiteBaseTest {

  protected boolean sfu = false;
  private JsonObject getStatsSdk = null;
  private JsonObject getChartsConfig = null;
  private boolean allureCharts = false;
  private String peerConnection;

  @Override
  protected void payloadHandling() {
    super.payloadHandling();
    if (this.payload != null) {
      sfu = payload.getBoolean("sfu", false);
      this.getStatsSdk = this.payload.getJsonObject("getStatsSdk");
      this.getChartsConfig = this.payload.getJsonObject("getCharts");
      if (getStatsConfig != null && getStatsConfig.getJsonArray("peerConnections") != null) {
        peerConnection = getStatsConfig.getJsonArray("peerConnections").getString(0);
      }
      if (getStatsSdk != null && getStatsSdk.getJsonArray("peerConnections") != null) {
        peerConnection = getStatsSdk.getJsonArray("peerConnections").getString(0);
      }          
      this.allureCharts = this.getChartsConfig != null && this.getChartsConfig.getBoolean("enabled");
    }
  }

  @Override
  protected void populateTestSteps(TestRunner runner) {
    try {
      final JanusPage janusPage = new JanusPage(runner);
      runner.addStep(new StartDemoStep(runner, this.url));
      runner.addStep(new JoinStreamingStep(runner, "videoLive"));
      runner.addStep(new StreamingVideoCheck(runner));
      if (this.getStatsSdk != null) {
        runner.addStep(new StartGetStatsSDKStep(runner, this.name, this.getStatsSdk, getTestJar(), peerConnection));
      }

      if (this.getStats()) {
        runner.addStep(new GetStatsStep(runner, getStatsConfig, sfu, janusPage));
      }
      
      if (this.allureCharts) {
        runner.addStep(new StartGetStatsStep(runner, getChartsConfig));
      }

      if (this.takeScreenshotForEachTest()) {
        runner.addStep(new ScreenshotStep(runner));
      }
      if (this.meetingDuration > 0) {
        runner.addStep(new StayInMeetingStep(runner, meetingDuration));
      }

      if (this.allureCharts) {
        runner.addStep(new StopGetStatsStep(runner));
        runner.addStep(new GenerateChartsStep(runner, getChartsConfig, getTestJar()));
      }
      runner.addStep(new LeaveDemoStep(runner));

    } catch(Exception e){
      logger.error(getStackTrace(e));
    }
  }
  
  
}
