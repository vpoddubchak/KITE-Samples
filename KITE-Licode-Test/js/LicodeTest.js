const {TestUtils, WebDriverFactory, KiteBaseTest, ScreenshotStep} = require('./node_modules/kite-common'); 
const {OpenUrlStep, GetStatsStep, LeaveRoomStep} = require('./steps');
const {PublishedVideoCheck, SubscribedVideoCheck, PublishedAudioOnlyCheck, SubscribedAudioOnlyCheck} = require('./checks');
const {MainPage} = require('./pages');

class LicodeTest extends KiteBaseTest {
  constructor(name, kiteConfig) {
    super(name, kiteConfig);
    this.licodeOptions = this.payload.configOptions;
  }
  
  async testScript() {
    try {
      // this.driver = await WebDriverFactory.getDriver(this.capabilities, this.remoteUrl);
      this.driver = await WebDriverFactory.getDriver(this.capabilities);

      this.page = new MainPage(this.driver);
      let openUrlStep = new OpenUrlStep(this);
      await openUrlStep.execute(this);

      if (this.licodeOptions && !this.licodeOptions.onlyAudio) {
        let publishedVideoCheck = new PublishedVideoCheck(this);
        await publishedVideoCheck.execute(this);
        let subscribedVideoCheck = new SubscribedVideoCheck(this);
        await subscribedVideoCheck.execute(this);
      } else {
        let publishedAudioOnlyCheck = new PublishedAudioOnlyCheck(this);
        await publishedAudioOnlyCheck.execute(this);
        let subscribedAudioOnlyCheck = new SubscribedAudioOnlyCheck(this);
        await subscribedAudioOnlyCheck.execute(this);
      }

      let screenshotStep = new ScreenshotStep(this);
      await screenshotStep.execute(this);

      if (this.getStats) {
        let getStatsStep = new GetStatsStep(this);
        await getStatsStep.execute(this);
      }

      let leaveRoomStep = new LeaveRoomStep(this);
      await leaveRoomStep.execute(this);

      await this.waitAllSteps();

    } catch (e) {
      console.log(e);
    } finally {
      if (this.driver) {
        await this.driver.quit();
      }
    }
  }
}

module.exports= LicodeTest;

(async () => {
  const kiteConfig = await TestUtils.getKiteConfig(__dirname);
  let test = new LicodeTest('LicodeTest test', kiteConfig);
  await test.run();
})();
