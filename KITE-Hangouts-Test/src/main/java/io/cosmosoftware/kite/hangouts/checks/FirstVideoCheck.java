package io.cosmosoftware.kite.hangouts.checks;

import io.cosmosoftware.kite.exception.KiteTestException;
import io.cosmosoftware.kite.hangouts.pages.MainPage;
import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.report.Status;
import io.cosmosoftware.kite.steps.StepPhase;
import io.cosmosoftware.kite.steps.TestStep;
import org.openqa.selenium.WebElement;

import java.util.List;

import static io.cosmosoftware.kite.entities.Timeouts.SHORT_TIMEOUT_IN_SECONDS;
import static io.cosmosoftware.kite.util.ReportUtils.saveScreenshotPNG;
import static io.cosmosoftware.kite.util.ReportUtils.timestamp;
import static io.cosmosoftware.kite.util.TestUtils.videoCheck;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;

public class FirstVideoCheck extends TestStep {

  private final MainPage mainPage;

  public FirstVideoCheck(Runner runner, MainPage mainPage) {
    super(runner);
    setStepPhase(StepPhase.ALL);
    this.mainPage = mainPage;
  }

  @Override
  public String stepDescription() {
    return "Check the first video is being sent OK";
  }

  @Override
  protected void step() throws KiteTestException {
    mainPage.videoIsPublishing(SHORT_TIMEOUT_IN_SECONDS);
    List<Integer> videoIndex = mainPage.getVideoIndex();
    if (videoIndex.size() == 0) {
      throw new KiteTestException("No valid video with proper size was found on the page", Status.FAILED);
    }
    // Checking first video, assuming there's only one at the moment
    String videoCheck = videoCheck(webDriver, videoIndex.get(0), 10000);
    if (!"video".equalsIgnoreCase(videoCheck)) {
      reporter.screenshotAttachment(report,
        "FirstVideoCheck_" + timestamp(), saveScreenshotPNG(webDriver));
      reporter.textAttachment(report, "Sent Video", videoCheck, "plain");
      throw new KiteTestException("The first video is " + videoCheck, Status.FAILED);
    }
  }
}
