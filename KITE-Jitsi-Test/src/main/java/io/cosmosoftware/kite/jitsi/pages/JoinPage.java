package io.cosmosoftware.kite.jitsi.pages;

import io.cosmosoftware.kite.interfaces.Runner;
import io.cosmosoftware.kite.pages.BasePage;

import static io.cosmosoftware.kite.entities.Timeouts.FIVE_SECOND_INTERVAL;
import static io.cosmosoftware.kite.util.TestUtils.waitAround;
import static io.cosmosoftware.kite.util.WebDriverUtils.loadPage;

public class JoinPage extends BasePage {
  
  public JoinPage(Runner runner) {
    super(runner);
  }

  public void joinRoom(String url) {
    webDriver.manage().window().maximize();
    loadPage(url, 10);
    logger.info("Open " + url);
    waitAround(FIVE_SECOND_INTERVAL);
  }
}
