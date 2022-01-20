package io.cosmosoftware.kite.jitsi.checks;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.jitsi.pages.MeetingPage;
import io.cosmosoftware.kite.report.Reporter;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.StepPhase;
import io.cosmosoftware.kite.steps.TestStep;
import io.cosmosoftware.kite.steps.VideoDisplayCheck;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.videoCheck;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class AllVideoCheck extends TestStep {
  
  private final int numberOfParticipants;
  private final MeetingPage meetingPage;
  private final Runner runner;

  public AllVideoCheck(Runner runner, int numberOfParticipants) {
    super(runner);
    this.numberOfParticipants = numberOfParticipants;
    this.meetingPage = new MeetingPage(runner);
    this.runner = runner;
  }

  @Override
  public String stepDescription() {
    return "Check the other videos are being received OK";
  }

  @Override
  protected void step() {
    meetingPage.clickVideoToggle();
    List<VideoDisplayCheck> checks = prepareChecks();
    for (VideoDisplayCheck check : checks) {
      check.processTestStep(StepPhase.DEFAULT, this.report, false);
    }
  }

  private List<VideoDisplayCheck> prepareChecks() {
    List<VideoDisplayCheck> checks = new ArrayList<>();
    for (int i = 1; i < numberOfParticipants; i++) {
      checks.add(new VideoDisplayCheck(this.runner, meetingPage, i, "remote-"+i, false));
    }
    return checks;
  }
}
