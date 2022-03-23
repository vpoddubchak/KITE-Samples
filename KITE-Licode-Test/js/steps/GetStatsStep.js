const {TestUtils, TestStep, KiteTestError, Status} = require('kite-common');

class GetStatsStep extends TestStep {
  constructor(kiteBaseTest) {
      super();
      this.driver = kiteBaseTest.driver;
      this.statsCollectionTime = kiteBaseTest.statsCollectionTime;
      this.statsCollectionInterval = kiteBaseTest.statsCollectionInterval;
      this.selectedStats = kiteBaseTest.selectedStats;
      this.peerConnections = kiteBaseTest.peerConnections;
      this.numberOfParticipant = kiteBaseTest.numberOfParticipant;
      this.page = kiteBaseTest.page;

      // Test reporter if you want to add attachment(s)
      this.testReporter = kiteBaseTest.reporter;
  }

  stepDescription() {
    return 'Getting WebRTC stats for all PeerConnections via getStats'; 
  }

  async step() {
    try {
      const allStats = await this.page.getStats(this);
      let row = 0;
      allStats.forEach((rawStats) => {
        let summaryStats = TestUtils.extractJson(rawStats, 'both');
        // // Data
        this.testReporter.textAttachment(this.report, 'GetStatsRaw' + row, JSON.stringify(rawStats, null, 4), "json");
        this.testReporter.textAttachment(this.report, 'GetStatsSummary' + row, JSON.stringify(summaryStats, null, 4), "json");
        row++;
      });
    } catch (error) {
        console.log(error);
        throw new KiteTestError(Status.BROKEN, "Failed to getStats");
    }
  }
}

module.exports = GetStatsStep;
