const {TestStep, KiteTestError, Status} = require('kite-common');

class PublishedVideoCheck extends TestStep {
  constructor(kiteBaseTest) {
      super();
      this.driver = kiteBaseTest.driver;
      this.timeout = kiteBaseTest.timeout;
      this.numberOfParticipant = kiteBaseTest.numberOfParticipant;
      this.page = kiteBaseTest.page;

      // Test reporter if you want to add attachment(s)
      this.testReporter = kiteBaseTest.reporter;
  }

  stepDescription() {
    return "Check the video is being published correctly";
  }

  async step() {
    try {
        let result = await this.page.videoCheck(this, 0);
        if (result != 'video') {
            this.testReporter.textAttachment(this.report, "Published video", result, "plain");
            throw new KiteTestError(Status.FAILED, "The video published is " + result);
        }
    } catch (error) {
        console.log(error);
        if (error instanceof KiteTestError) {
            throw error;
        } else {
            throw new KiteTestError(Status.BROKEN, "Error looking for the published video");
        }
    }
  }
}

module.exports = PublishedVideoCheck;
