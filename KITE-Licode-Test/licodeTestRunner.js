const child_process = require('child_process');
const { KiteConfig, KiteTest } = require('./utils/testUtils')

const configs = [];
const configPath = './configs'

let gridUrl = process.env.GRID_URL;
let licodeUrl = process.env.LICODE_URL;
if (!gridUrl || !licodeUrl) {
  console.error('Both GRID_URL and LICODE_URL environment variables are needed to run tests');
  process.exit(1);
}
gridUrl = `http://${gridUrl}:4444/wd/hub`
licodeUrl = `https://${licodeUrl}?forceStart=1`

console.log(`Configuring tests with gridUrl: ${gridUrl} and licodeUrl ${licodeUrl}`);

const runTestConfig = (configFile) => {
  console.log('Will run test', configFile);
  var script =`r ${configFile}`; 
  result = child_process.execSync(script,  {
    maxBuffer: 1000 * 1024,
    shell: '/bin/bash',
  });
  console.log('Ended test', configFile);
  console.log('Result ', result.toString());
};

const runAllTests = () => {
  let configNum = 1;
  configs.forEach((config) => {
    const configName = `${configPath}/config${configNum}.json`
    config.writeToFile(configName);
    runTestConfig(configName);
    configNum++;
  });
};

const firefoxAndChromeConfig = new KiteConfig('Firefox and Chrome Test Suite');
firefoxAndChromeConfig.addGridHub(gridUrl)
firefoxAndChromeConfig.addFirefox();
firefoxAndChromeConfig.addChrome();

const testNoSimulcast = new KiteTest('No Simulcast', licodeUrl);
const testSimulcast = new KiteTest('Simulcast', licodeUrl);
testSimulcast.setSimulcast(true);
const testAudioOnly = new KiteTest('Test audio only', licodeUrl);
testAudioOnly.setOnlyAudio(true);
const testP2P = new KiteTest('Test p2p', licodeUrl);
testP2P.setType('p2p');
firefoxAndChromeConfig.addTest(testNoSimulcast);
firefoxAndChromeConfig.addTest(testSimulcast);
firefoxAndChromeConfig.addTest(testAudioOnly);
firefoxAndChromeConfig.addTest(testP2P);

const onlyChromeConfig = new KiteConfig('Only Chrome Test Suite');
onlyChromeConfig.addGridHub(gridUrl)
onlyChromeConfig.addChrome();
const testSinglePCSimulcast = new KiteTest('Simulcast and SinglePC', licodeUrl);
testSinglePCSimulcast.setSimulcast(true);
testSinglePCSimulcast.setSinglePC(true);
onlyChromeConfig.addTest(testSinglePCSimulcast);

configs.push(firefoxAndChromeConfig);
configs.push(onlyChromeConfig);
runAllTests();
