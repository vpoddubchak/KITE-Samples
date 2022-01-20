package io.cosmosoftware.kite.hangouts.checks;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.hangouts.pages.MainPage;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.StepPhase;
import io.cosmosoftware.kite.steps.TestStep;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.util.List;

import static io.cosmosoftware.kite.entities.Timeouts.ONE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.videoCheck;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class AllVideoCheck extends TestStep {


  private final int numberOfParticipants;
  private final MainPage mainPage;

  public AllVideoCheck(Runner runner, int numberOfParticipants, MainPage mainPage) {
    super(runner);
    this.numberOfParticipants = numberOfParticipants;
    setStepPhase(StepPhase.ALL);
    this.setOptional(true);
    this.mainPage = mainPage;
  }

  @Override
  public String stepDescription() {
    return "Check the other videos are being received OK";
  }

  @Override
  protected void step() throws KiteTestException {
    waitAround(numberOfParticipants * ONE_SECOND_INTERVAL * 2);
    List<Integer> videoIndex = mainPage.getVideoIndex();
    if (videoIndex.size() == 0) {
      throw new KiteTestException("No valid video with proper size was found on the page", Status.FAILED);
    }

    if (videoIndex.size() < 2) {
      throw new KiteTestException("Only one video found, the number of video is not correct", Status.FAILED);
    }
    String videoCheck = "";
    boolean error = false;

    for (int i = 0; i < videoIndex.size(); i++) {
      String v = videoCheck(webDriver, videoIndex.get(i), 10000);
      videoCheck += v;
      if (i < numberOfParticipants - 1) {
        videoCheck += "|";
      }
      if (!"video".equalsIgnoreCase(v)) {
        error = true;
      }
    }
    if (error) {
      reporter.textAttachment(report, "Received Videos", videoCheck, "plain");
      throw new KiteTestException("Some videos are still or blank: " + videoCheck, Status.FAILED);
    }
  }
}
