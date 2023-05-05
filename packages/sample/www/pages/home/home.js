document.addEventListener('deviceready', onDeviceReady, false);

async function onDeviceReady() {
  try {
    if (await Notificare.isReady()) {
      return;
    }
  } catch (e) {
    console.log(e);
  }

  console.log(`---> Launching Notificare <---`);
  launchNotificare();
}

// Notificare Launch

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function statusNotificare() {
  try {
    console.log(`---> Notificare Status Clicked <---`);
    console.log(`Is configured = ${await Notificare.isConfigured()}`);
    console.log(`Is ready = ${await Notificare.isReady()}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function launchNotificare() {
  try {
    console.log(`---> Notificare Launch Clicked <---`);
    await NotificarePush.setPresentationOptions(['banner', 'badge', 'sound']);
    await Notificare.launch();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function unlaunchNotificare() {
  try {
    console.log(`---> Notificare unlaunched Clicked <---`);
    await Notificare.unlaunch();
  } catch (e) {
    console.log(e);
  }
}

// Notificare Geo

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function requestLocationPermissions() {
  try {
    console.log(`---> Request Location Permissions Clicked <---`);
    console.log(`---> Requesting When in Use Permission <---`);
    const statusForeground = await NotificareGeo.requestPermission('location_when_in_use');
    console.log(`---> When in User request status = ${statusForeground}`);

    console.log(`---> Requesting Always Permission <---`);
    const statusBackground = await NotificareGeo.requestPermission('location_always');
    console.log(`---> Always request status = ${statusBackground}`);

    console.log(`---> Requesting Bluetooth Permission <---`);
    const statusBluetooth = await NotificareGeo.requestPermission('bluetooth_scan');
    console.log(`---> Bluetooth request status = ${statusBluetooth}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function checkLocationPermissionsStatus() {
  try {
    console.log(`---> Check Location Permissions Status Clicked <---`);
    const statusForeground = await NotificareGeo.checkPermissionStatus('location_when_in_use');
    console.log(`---> When in Use permission status = ${statusForeground}`);

    const statusBackground = await NotificareGeo.checkPermissionStatus('location_always');
    console.log(`---> Always permission status = ${statusBackground}`);

    const statusBluetooth = await NotificareGeo.checkPermissionStatus('bluetooth_scan');
    console.log(`---> Bluetooth permission status = ${statusBluetooth}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function enableLocationUpdates() {
  try {
    console.log(`---> Enable Location Updates Clicked <---`);
    await NotificareGeo.enableLocationUpdates();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function disableLocationUpdates() {
  try {
    console.log(`---> Disable Location Updates Clicked <---`);
    await NotificareGeo.disableLocationUpdates();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function rangingBeacons() {
  try {
    console.log(`---> Ranging Beacons Clicked <---`);
    window.location.replace('../beacons/beacons.html');
  } catch (e) {
    console.log(e);
  }
}

// Notificare Push

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function requestNotificationsPermission() {
  try {
    console.log(`---> Request Notifications Permission Clicked <---`);
    const status = await NotificarePush.requestPermission();
    console.log(`---> Notifications Permission request status = ${status}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function checkNotificationsPermissionsStatus() {
  try {
    const status = await NotificarePush.checkPermissionStatus();
    console.log(`---> Notifications permission status = ${status}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function enableRemoteNotifications() {
  try {
    console.log(`---> Enable Remote Notifications Clicked <---`);
    await NotificarePush.enableRemoteNotifications();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function disbaleRemoteNotifications() {
  try {
    console.log(`---> Disable Remote Notifications Clicked <---`);
    await NotificarePush.disableRemoteNotifications();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function openInbox() {
  try {
    console.log(`---> Open Inbox Clicked <---`);
    window.location.replace('../inbox/inbox.html');
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchTags() {
  try {
    console.log(`---> Fetch Tags Clicked <---`);
    const tags = await Notificare.device().fetchTags();
    console.log(`---> Fetched tags = ${JSON.stringify(tags)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function addTags() {
  try {
    console.log(`---> Add Tags Clicked <---`);
    await Notificare.device().addTags(['cordova', 'sample', 'remove-me']);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function removeTags() {
  try {
    console.log(`---> Remove Tags Clicked <---`);
    await Notificare.device().removeTag('remove-me');
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function clearTags() {
  try {
    console.log(`---> Clear Tags Clicked <---`);
    await Notificare.device().clearTags();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchDoNotDisturb() {
  try {
    console.log(`---> Fetch DnD Clicked <---`);
    const dnd = await Notificare.device().fetchDoNotDisturb();
    console.log(`---> DnD = ${JSON.stringify(dnd)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function updateDoNotDisturb() {
  try {
    console.log(`---> Update DnD Clicked <---`);
    await Notificare.device().updateDoNotDisturb({ start: '23:00', end: '08:00' });
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function clearDoNotDisturb() {
  try {
    console.log(`---> Clear DnD Clicked <---`);
    await Notificare.device().clearDoNotDisturb();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchUserData() {
  try {
    console.log(`---> Fetch User Data Clicked <---`);
    const userData = await Notificare.device().fetchUserData();
    console.log(`---> User data = ${JSON.stringify(userData)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function updateUserData() {
  try {
    console.log(`---> Update User Data Clicked <---`);
    await Notificare.device().updateUserData({
      dogPerson: 'true',
    });
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchPreferredLanguage() {
  try {
    console.log(`---> Fetch Preferred Language Clicked <---`);
    const language = await Notificare.device().getPreferredLanguage();
    console.log(`---> Preferred Language = ${JSON.stringify(language)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function updatePreferredLanguage() {
  try {
    console.log(`---> Update Preferred Language Clicked <---`);
    await Notificare.device().updatePreferredLanguage('nl-NL');
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function clearPreferredLanguage() {
  try {
    console.log(`---> Clear Preferred Language Clicked <---`);
    await Notificare.device().updatePreferredLanguage(null);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchNotification() {
  try {
    console.log(`---> Fetch Notification Clicked <---`);
    let id = '';

    if (id === '') {
      console.log(`---> Update Notification ID in fetchNotification() method to continue <---`);
      return;
    }

    const notification = await Notificare.fetchNotification(id);
    console.log(`---> Fetched Notification = ${JSON.stringify(notification)}`);
  } catch (e) {
    console.log(e);
  }
}

// Notificare Assets

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchAssets() {
  try {
    console.log(`---> Fetch Assets Clicked <---`);
    let name = '';

    if (name === '') {
      console.log(`---> Update Asset name in fetchAssets() method to continue <---`);
      return;
    }

    const assets = await NotificareAssets.fetch(name);
    console.log(`---> Fetched Assets = ${JSON.stringify(assets)}`);
  } catch (e) {
    console.log(e);
  }
}

// Notificare Scannables

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function startScannableSession() {
  try {
    console.log(`---> Start Scannable Session Clicked <---`);
    await NotificareScannables.startScannableSession();
  } catch (e) {
    console.log(e);
  }
}

// Notificare Authentication

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function createUserAccount() {
  try {
    console.log(`---> Create User Account Clicked <---`);
    let email = '';
    let password = '';
    let name = '';

    if (email === '' || password === '' || name === '') {
      console.log(`---> Update data in createUserAccount() method to continue <---`);
      return;
    }

    await NotificareAuthentication.createAccount(email, password, name);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function login() {
  try {
    console.log(`---> Login Clicked <---`);
    let email = '';
    let password = '';

    if (email === '' || password === '') {
      console.log(`---> Update data in login() method to continue <---`);
      return;
    }

    await NotificareAuthentication.login(email, password);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function logout() {
  try {
    console.log(`---> Logout Clicked <---`);
    await NotificareAuthentication.logout();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchUserDetails() {
  try {
    console.log(`---> Fetch User Details Clicked <---`);
    const userDetails = await NotificareAuthentication.fetchUserDetails();
    console.log(`---> User Details = ${JSON.stringify(userDetails)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchUserPreferences() {
  try {
    console.log(`---> Fetch User Preferences Clicked <---`);
    const userPreferences = await NotificareAuthentication.fetchUserPreferences();
    console.log(`---> User Preferences = ${JSON.stringify(userPreferences)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchUserSegments() {
  try {
    console.log(`---> Fetch User Segments Clicked <---`);
    const userSegments = await NotificareAuthentication.fetchUserSegments();
    console.log(`---> User Segments = ${JSON.stringify(userSegments)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function sendPasswordReset() {
  try {
    console.log(`---> Send Password Reset Clicked <---`);
    let email = '';

    if (email === '') {
      console.log(`---> Update Email in sendPasswordReset() method to continue <---`);
      return;
    }

    await NotificareAuthentication.sendPasswordReset(email);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function resetPassword() {
  try {
    console.log(`---> Reset Password Clicked <---`);
    let password = '';
    let token = '';

    if (password === '' || token === '') {
      console.log(`---> Update Password and Token in resetPassword() method to continue <---`);
      return;
    }

    await NotificareAuthentication.resetPassword(password, token);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function changePassword() {
  try {
    console.log(`---> Change Password Clicked <---`);
    let password = '';

    if (password === '') {
      console.log(`---> Update Password in changePassword() method to continue <---`);
      return;
    }

    await NotificareAuthentication.changePassword(password);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function validateUser() {
  try {
    console.log(`---> Validate User Clicked <---`);
    let token = '';

    if (token === '') {
      console.log(`---> Update Token in validateUser() method to continue <---`);
      return;
    }

    await NotificareAuthentication.validateUser(token);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function addUserSegment() {
  try {
    console.log(`---> Add User Segment Clicked <---`);
    const segments = await NotificareAuthentication.fetchUserSegments();
    await NotificareAuthentication.addUserSegment(segments[0]);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function removeUserSegment() {
  try {
    console.log(`---> Remove User Segment Clicked <---`);
    const segments = await NotificareAuthentication.fetchUserSegments();
    await NotificareAuthentication.removeUserSegment(segments[0]);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function addUserSegmentToPreference() {
  try {
    console.log(`---> Add User Segment to Preference Clicked <---`);
    const preferences = await NotificareAuthentication.fetchUserPreferences();

    const preference = preferences[0];
    const option = preference.options[0];

    await NotificareAuthentication.addUserSegmentToPreference(preference, option);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function removeUserSegmentToPreference() {
  try {
    console.log(`---> Remove User Segment to Preference Clicked <---`);
    const preferences = await NotificareAuthentication.fetchUserPreferences();

    const preference = preferences[0];
    const option = preference.options[0];

    await NotificareAuthentication.removeUserSegment(preference, option);
  } catch (e) {
    console.log(e);
  }
}

// Notificare Loyalty

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function fetchPass() {
  try {
    console.log(`---> Fetch Pass Clicked <---`);

    let serial = '';
    if (serial === '') {
      console.log(`---> Update Pass Serial in openWallet() method to continue <---`);
      return;
    }

    const pass = await NotificareLoyalty.fetchPassBySerial(serial);
    console.log(`---> Fetched Pass = ${JSON.stringify(pass)}`);

    await NotificareLoyalty.present(pass);
  } catch (e) {
    console.log(e);
  }
}

// Notificare Monetize

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function refresh() {
  try {
    console.log(`---> Refresh Monetize Clicked <---`);
    await NotificareMonetize.refresh();
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function getProducts() {
  try {
    console.log(`---> Get Products Clicked <---`);
    const products = await NotificareMonetize.getProducts();
    console.log(`---> Products = ${JSON.stringify(products)}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function getPurchases() {
  try {
    console.log(`---> Get Purchases Clicked <---`);
    const purchases = await NotificareMonetize.getPurchases();
    console.log(`---> Purchases = ${JSON.stringify(purchases)}`);
  } catch (e) {
    console.log(e);
  }
}

// Notificare In-App-Messaging

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function checkSuppressedState() {
  try {
    console.log(`---> Check Suppressed State Clicked <---`);
    const suppressed = await NotificareInAppMessaging.hasMessagesSuppressed();
    console.log(`---> Suppressed State = ${suppressed}`);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function suppressMessages() {
  try {
    console.log(`---> Suppressed Messages Clicked <---`);
    await NotificareInAppMessaging.setMessagesSuppressed(true);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function unSuppressMessages() {
  try {
    console.log(`---> Un-Suppressed Messages Clicked <---`);
    await NotificareInAppMessaging.setMessagesSuppressed(false);
  } catch (e) {
    console.log(e);
  }
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
async function unSuppressMessagesAndEvaluateContext() {
  try {
    console.log(`---> Un-Suppressed Messages and Evaluate Context Clicked <---`);
    await NotificareInAppMessaging.setMessagesSuppressed(false, true);
  } catch (e) {
    console.log(e);
  }
}
