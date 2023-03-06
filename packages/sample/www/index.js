document.addEventListener('deviceready', onDeviceReady, false);

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function onDeviceReady() {
  console.log(`---> Cordova "deviceready" event fires, opening home page <---`);
  console.log('Running cordova-' + cordova.platformId + '@' + cordova.version);

  window.location.replace('pages/home/home.html');
}
