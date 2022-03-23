const {TestStep, KiteTestError, Status} = require('kite-common');

class SubscribedAudioOnlyCheck extends TestStep {
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
    return "Check if all the audios are subscribed correctly";
  }

  async step() {
    let totalResult = '';
    let error = false;
    try {
      for (let i = 1; i < this.numberOfParticipant; i++) {
        let checkResult = await this.page.audioCheck(this, i);
        totalResult += "" + checkResult;
        if (i<this.numberOfParticipant-1) {
          totalResult+= '|';
        }
        this.testReporter.textAttachment(this.report, "Subscribed audios", totalResult, "plain");
      }
      if (error) {
          this.testReporter.textAttachment(this.report, "Subscribed audios", totalResult, "plain");
          throw new KiteTestError(Status.FAILED, "Some of the subscribed audios are still or blank: " + totalResult);
      }
    } catch (error) {
        console.log(error);
        if (error instanceof KiteTestError) {
            throw error;
        } else {
            throw new KiteTestError(Status.BROKEN, "Error looking for the audio");
        }
    }
  }
}

module.exports = SubscribedAudioOnlyCheck;
