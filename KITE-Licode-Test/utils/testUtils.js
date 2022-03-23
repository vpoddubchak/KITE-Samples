const fs = require('fs');

class KiteConfig {
  constructor(name, gridUrl, inputFile){
    if (inputFile) {
      const rawData = fs.readFileSync(inputFile);
      this.config = JSON.parse(inputFile);
    } else {
      this.config = {};
      this.config.name = `${name} %ts`
      this.config.grids = [];
      this.config.tests = [];
      this.config.clients = [];
    }
  }
  addGridHub(hub) {
    this.config.grids.push({
      type: 'local',
      url: hub
    });
  }
  addTest(test) {
    this.config.tests.push(test.test);
  }
  addFirefox() {
    this.config.clients.push({
      browserName: 'firefox',
      platform: 'LINUX',
    });
  }
  addChrome() {
    this.config.clients.push({
      browserName: 'chrome',
      platform: 'LINUX',
    });
  }
  toJSON() {
    return JSON.stringify(this.config);
  }
  writeToFile(filePath) {
    fs.writeFileSync(filePath, this.toJSON());
  }
}

class KiteTest {
  constructor(name, licodeUrl) {
    console.log(`Building test name: ${name}, url: ${licodeUrl}`);
    this.test = {};
    this.test.name = `${name} %ts`
    this.test.tupleSize = 2;
    this.test.permute = false
    this.test.description = 'Licode test for BasicExample'
    this.test.testImpl = 'LicodeTest.js'
    this.test.payload = {
      configOptions: {
        simulcast: false,
        singlePC: false,
        onlyAudio: false,
        room: 'kiteroom',
        mediaConfiguration: 'default',
        type: 'erizo',
      },
      url: ''+licodeUrl,
      testTimeout: 60,
      getStats: {
        enabled: true,
        statsCollectionTime: 5,
        statsCollectionInterval: 1,
        peerConnections: ['window.peers'],
        selectedStats: ['inbound-rtp', 'outbound-rtp', 'candidate-pair', 'stream']
      }
    }
  }
  setTupleSize(size) {
    this.test.tupleSize = size;
  }
  setDescription(description) {
    this.description = description;
  }
  setSimulcast(enabled) {
    this.test.payload.configOptions.simulcast = enabled;
  }
  setSinglePC(enabled) {
    this.test.payload.configOptions.singlePC = enabled;
  }
  setOnlyAudio(enabled) {
    this.test.payload.configOptions.onlyAudio = enabled;
  }
  setLicodeUrl(url) {
    this.test.payload.url = url;
  }
  setType(type) {
    this.test.payload.type = type;
  }
  getJSON() {
    return JSON.stringify(this.test);
  }
}

exports.KiteTest = KiteTest;
exports.KiteConfig = KiteConfig;
