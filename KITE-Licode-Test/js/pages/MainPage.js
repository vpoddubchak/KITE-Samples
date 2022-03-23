const {By, Key} = require('selenium-webdriver');
const {TestUtils, KiteTestError, Status} = require('kite-common'); 
const videos = By.css('video');
const audios = By.css('audio');

const getPeerConnectionsScript = function() {
  return "window.peers = []; const getPcs = () => { var pc = localStream; window.peers.push(pc.pc.peerConnection); room.remoteStreams.forEach((stream) => {if(stream.pc) {window.peers.push(stream.pc.peerConnection);}});}; getPcs();";
};

const elements = {
  startButton: By.id('startButton'),
};

class MainPage {
  constructor(driver) {
    this.driver = driver;
  }

  async open(url) {
    const optionUrl = `${url}${this.option}`
    await TestUtils.open(url);
  }
  
  async setBasicExampleOptions(stepInfo) {
    let setConfig = "";
    if (stepInfo.options) {
      Object.entries(stepInfo.options).forEach((entry) => {
        if (typeof(entry[1]) == 'string') {
          setConfig += `configFlags.${entry[0]}="${entry[1]}";`
        } else {
          setConfig += `configFlags.${entry[0]}=${entry[1]};`
        }
      });
      console.log('Setconfig is ', setConfig);
      await stepInfo.driver.executeScript(setConfig);
    }
  }

  async clickStart(url) {
    let startButton = await this.driver.findElement(elements.startButton);
    await startButton.click();
  }

  async waitForVideos(stepInfo) {
    await TestUtils.waitForVideos(stepInfo, videos);
  }
  
  async waitForAudios(stepInfo) {
    await TestUtils.waitForVideos(stepInfo, audios);
  }

  async videoCheck(stepInfo, index) {
    let checked; // Result of the verification
    let i; // iteration indicator
    let timeout = stepInfo.timeout;
    stepInfo.numberOfParticipant = parseInt(stepInfo.numberOfParticipant); // To add the first video
    console.warn(`Testing Video with ${stepInfo.numberOfParticipant} participants, index`, index);
    await TestUtils.waitForVideos(stepInfo, videos);
    // Waiting for all the videos

    // Check the status of the video
    // checked.result = 'blank' || 'still' || 'video'
    i = 0;
    checked = await TestUtils.verifyVideoDisplayByIndex(stepInfo.driver, index);
    while(checked.result === 'blank' || typeof checked.result === "undefined" && i < timeout) {
      checked = await TestUtils.verifyVideoDisplayByIndex(stepInfo.driver, index + 1);
      i++;
      await TestUtils.waitAround(1000);
    }
    
    // Check if the video is still or moving
    i = 0;
    while(i < 3 && checked.result != 'video') {
      checked = await TestUtils.verifyVideoDisplayByIndex(stepInfo.driver, index);
      i++;
      await TestUtils.waitAround(3 * 1000); // waiting 3s after each iteration
    }
    return checked.result;
  }

  async audioCheck(stepInfo, index) {
    let checked; // Result of the verification
    let i; // iteration indicator
    let timeout = stepInfo.timeout;
    stepInfo.numberOfParticipant = parseInt(stepInfo.numberOfParticipant); // To add the first video
    console.warn(`Testing Audio with ${stepInfo.numberOfParticipant} participants, index`, index);
    await TestUtils.waitForVideos(stepInfo, audios);

    // Waiting for all the audios


    return true;
  }

  async getStats(stepInfo) {
   await stepInfo.driver.executeScript(getPeerConnectionsScript());
    const stats = []
    for (let part = 0; part < stepInfo.numberOfParticipant; part++) {
      let tempStats = await TestUtils.getStats(stepInfo, 'kite', `${stepInfo.peerConnections}[${part}]`);
      stats.push(tempStats);
    }
    return stats;
  }

  async leaveRoom(stepInfo) {
    await stepInfo.driver.executeScript("room.disconnect();");
  }
}

module.exports = MainPage;
