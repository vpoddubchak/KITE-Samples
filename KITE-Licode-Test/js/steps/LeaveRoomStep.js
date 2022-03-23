const {TestUtils, TestStep, KiteTestError, Status} = require('kite-common');

class LeaveRoomStep extends TestStep {
  constructor(kiteBaseTest) {
      super();
      this.driver = kiteBaseTest.driver;
      this.page = kiteBaseTest.page;

  }

  stepDescription() {
    return 'Leaving the licode room'; 
  }

  async step() {
    try {
      await this.page.leaveRoom(this);
    } catch (error) {
        console.log(error);
        throw new KiteTestError(Status.BROKEN, "Failed to leave room");
    }
  }
}

module.exports = LeaveRoomStep;
