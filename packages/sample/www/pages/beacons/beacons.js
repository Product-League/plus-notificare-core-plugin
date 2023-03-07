document.addEventListener('deviceready', onDeviceReady, false);
document.addEventListener('backbutton', handleBackButton, false);

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function onDeviceReady() {
  const noDataView = `<div id="noDataMessage" class="centered">No Beacons Found</div>`;
  document.getElementById('beaconsList').innerHTML = noDataView;

  NotificareGeo.onBeaconsRanged(({ region, beacons }) => {
    if (beacons.length > 0) {
      const nodes = beacons.map((beacon) => createBeaconItem(region, beacon));
      document.getElementById('beaconsList').replaceChildren(...nodes);
    } else {
      document.getElementById('beaconsList').innerHTML = noDataView;
    }
  });
}

function handleBackButton() {
  window.location.replace('../home/home.html');
}

function createBeaconItem(region, beacon) {
  const beaconView = `<div class="container">
    <div class="beacons-container">
      <span>Region name: ${region.name}</span>
      <span>Name: ${beacon.name}</span>
      <span>Proximity: ${beacon.proximity}</span>
      <span>Major: ${beacon.major}</span>
      <span>Minor: ${beacon.minor ?? '-'}</span>
    </div>
  </div>`;

  const beaconElement = document.createElement('div');
  beaconElement.innerHTML = beaconView;

  const lineElement = document.createElement('div');
  lineElement.classList.add('line');

  const container = document.createElement('div');

  if (document.getElementById('beaconsList').children.length > 1) {
    container.appendChild(lineElement);
  }

  container.appendChild(beaconElement);

  return container;
}
