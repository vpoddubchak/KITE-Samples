const {TestStep} = require('kite-common');

class OpenUrlStep extends TestStep {
  constructor(kiteBaseTest) {
    super();
    this.driver = kiteBaseTest.driver;
    this.timeout = kiteBaseTest.timeout;
    this.url = kiteBaseTest.url;
    this.uuid = kiteBaseTest.uuid;
    this.page = kiteBaseTest.page;
    this.reporter = kiteBaseTest.reporter;
    this.options = kiteBaseTest.licodeOptions;
  }

  stepDescription() {
    return 'Open ' + this.url + '';
  }

  async step() {
    await this.page.open(this);
    if (this.options) {
      this.reporter.textAttachment(this.report, "Options set", JSON.stringify(this.options), "plain");
      await this.page.setBasicExampleOptions(this);
    }
    await this.page.clickStart(this);
    await this.page.waitForVideos(this);
  }
}

module.exports = OpenUrlStep;
