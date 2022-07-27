// noinspection JSUnresolvedVariable

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Wait for the deviceready event before using any of Cordova's device APIs.
// See https://cordova.apache.org/docs/en/latest/cordova/events/events.html#deviceready
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
  // Cordova is now initialized. Have fun!

  console.log('Running cordova-' + cordova.platformId + '@' + cordova.version);
  document.getElementById('deviceready').classList.add('ready');

  (async () => {
    console.log(`Is configured = ${await Notificare.isConfigured()}`);
    console.log(`Is ready = ${await Notificare.isReady()}`);

    await Notificare.launch();

    Notificare.onDeviceRegistered((device) => {
      console.log(`---> Device registered: ${JSON.stringify(device)}`);
    });

    Notificare.onReady(async (application) => {
      console.log('=======================');
      console.log('= NOTIFICARE IS READY =');
      console.log('=======================');
      console.log(JSON.stringify(application));

      // Stop listening to device_registered events.
      // sub.remove();

      await Notificare.events().logCustom('CUSTOM_EVENT');
      await Notificare.events().logCustom('CUSTOM_EVENT', {
        color: 'blue',
        lovesNotificare: true,
        nested: {
          works: true,
          list: ['a', 'b', 'c'],
        },
      });

      setTimeout(async function register() {
        await Notificare.device().register(null, null);
      }, 2000);

      setTimeout(async function registerAnonymous() {
        await Notificare.device().register('helder@notifica.re', 'Helder Pinhal');
      }, 5000);

      console.log(`---> Tags = ${JSON.stringify(await Notificare.device().fetchTags())}`);
      await Notificare.device().addTags(['cordova', 'hpinhal']);
      console.log(`---> Tags = ${JSON.stringify(await Notificare.device().fetchTags())}`);
      await Notificare.device().removeTag('hpinhal');
      console.log(`---> Tags = ${JSON.stringify(await Notificare.device().fetchTags())}`);
      await Notificare.device().clearTags();
      console.log(`---> Tags = ${JSON.stringify(await Notificare.device().fetchTags())}`);

      console.log(`---> Language = ${JSON.stringify(await Notificare.device().getPreferredLanguage())}`);
      await Notificare.device().updatePreferredLanguage('nl-NL');
      console.log(`---> Language = ${JSON.stringify(await Notificare.device().getPreferredLanguage())}`);
      await Notificare.device().updatePreferredLanguage(null);
      console.log(`---> Language = ${JSON.stringify(await Notificare.device().getPreferredLanguage())}`);

      console.log(`---> DnD = ${JSON.stringify(await Notificare.device().fetchDoNotDisturb())}`);
      await Notificare.device().updateDoNotDisturb({ start: '23:00', end: '08:00' });
      console.log(`---> DnD = ${JSON.stringify(await Notificare.device().fetchDoNotDisturb())}`);
      await Notificare.device().clearDoNotDisturb();
      console.log(`---> DnD = ${JSON.stringify(await Notificare.device().fetchDoNotDisturb())}`);

      console.log(`---> User data = ${JSON.stringify(await Notificare.device().fetchUserData())}`);
      await Notificare.device().updateUserData({ firstName: 'Helder', lastName: 'Pinhal' });
      console.log(`---> User data = ${JSON.stringify(await Notificare.device().fetchUserData())}`);
      await Notificare.device().updateUserData({});
      console.log(`---> User data = ${JSON.stringify(await Notificare.device().fetchUserData())}`);

      //
      // Push
      //

      console.log(`---> Remote notifications enabled = ${await NotificarePush.hasRemoteNotificationsEnabled()}`);
      await NotificarePush.enableRemoteNotifications();
      console.log(`---> Remote notifications enabled = ${await NotificarePush.hasRemoteNotificationsEnabled()}`);

      await NotificarePush.setPresentationOptions(['alert', 'sound', 'badge']);

      //
      // Inbox
      //

      console.log(`---> Badge = ${await NotificareInbox.getBadge()}`);
      console.log(`---> Items = ${(await NotificareInbox.getItems()).length}`);

      //
      // Assets
      //

      const assets = await NotificareAssets.fetch('LANDSCAPES');
      console.log('---> Assets');
      console.log(JSON.stringify(assets, null, 2));

      //
      // Geo
      //

      console.log(`---> Location services enabled = ${await NotificareGeo.hasLocationServicesEnabled()}`);
      console.log(`---> Bluetooth enabled = ${await NotificareGeo.hasBluetoothEnabled()}`);
      await NotificareGeo.enableLocationUpdates();

      //
      // Loyalty
      //

      const pass = await NotificareLoyalty.fetchPassBySerial('520d974e-b3d5-4d30-93b4-259f9d4bfa1d');
      console.log('=== FETCH PASS ===');
      console.log(JSON.stringify(pass, null, 2));

      // await NotificareLoyalty.present(pass);

      //
      // Scannables
      //

      // if (await NotificareScannables.canStartNfcScannableSession()) {
      //   await NotificareScannables.startNfcScannableSession();
      // } else {
      //   await NotificareScannables.startQrCodeScannableSession();
      // }

      //
      // Authentication
      //

      console.log(`---> is logged in = ${await NotificareAuthentication.isLoggedIn()}`);
      // await NotificareAuthentication.createAccount('helder@notifica.re', '123456', 'Helder Pinhal');
      await NotificareAuthentication.login('helder@notifica.re', '123456');
      console.log(`---> is logged in = ${await NotificareAuthentication.isLoggedIn()}`);
      console.log(`---> user details = ${JSON.stringify(await NotificareAuthentication.fetchUserDetails())}`);
      console.log(`---> user preferences = ${JSON.stringify(await NotificareAuthentication.fetchUserPreferences())}`);
      console.log(`---> user segments = ${JSON.stringify(await NotificareAuthentication.fetchUserSegments())}`);
      await NotificareAuthentication.sendPasswordReset('helder@notifica.re');
      // await NotificareAuthentication.resetPassword('helder@notifica.re', '---');
      await NotificareAuthentication.changePassword('123456');
      // await NotificareAuthentication.validateUser('---');
      console.log(
        `---> generate push email = ${JSON.stringify(await NotificareAuthentication.generatePushEmailAddress())}`
      );

      const segments = await NotificareAuthentication.fetchUserSegments();
      await NotificareAuthentication.addUserSegment(segments[0]);
      await NotificareAuthentication.removeUserSegment(segments[0]);

      const preferences = await NotificareAuthentication.fetchUserPreferences();
      const preference = preferences[0];
      const option = preference.options[0];
      await NotificareAuthentication.addUserSegmentToPreference(preference, option);
      await NotificareAuthentication.removeUserSegmentFromPreference(preference, option);

      await NotificareAuthentication.logout();
      console.log(`---> is logged in = ${await NotificareAuthentication.isLoggedIn()}`);

      //
      // Monetize
      //

      setTimeout(async () => {
        const products = await NotificareMonetize.getProducts();
        console.log(`---> products = ${JSON.stringify(products)}`);

        const purchases = await NotificareMonetize.getPurchases();
        console.log(`---> purchases = ${JSON.stringify(purchases)}`);

        await NotificareMonetize.refresh();

        if (products.length > 0) {
          await NotificareMonetize.startPurchaseFlow(products[0]);
        }
      }, 2000);
    });

    NotificarePush.onNotificationReceived((notification) => {
      console.log(`---> Received notification = ${JSON.stringify(notification)}`);
    });

    NotificarePush.onUnknownNotificationReceived((notification) => {
      console.log(`---> Unknown notification received = ${JSON.stringify(notification)}`);
    });

    NotificarePush.onNotificationOpened(async (notification) => {
      console.log(`---> Opened notification = ${JSON.stringify(notification)}`);

      await NotificarePushUI.presentNotification(notification);
    });

    NotificarePush.onUnknownNotificationOpened((notification) => {
      console.log(`---> Opened unknown notification = ${JSON.stringify(notification)}`);
    });

    NotificarePush.onNotificationSettingsChanged((granted) => {
      console.log('=== NOTIFICATION SETTINGS CHANGED ===');
      console.log(JSON.stringify(granted, null, 2));
    });

    NotificareInbox.onBadgeUpdated((badge) => {
      console.log(`---> Badge updated = ${badge}`);
    });

    NotificareInbox.onInboxUpdated((items) => {
      console.log(`---> Inbox updated = ${JSON.stringify(items)}`);
    });

    NotificarePushUI.onNotificationWillPresent((notification) => {
      console.log('=== NOTIFICATION WILL PRESENT ===');
      console.log(JSON.stringify(notification));
    });

    NotificarePushUI.onNotificationPresented((notification) => {
      console.log('=== NOTIFICATION PRESENTED ===');
      console.log(JSON.stringify(notification));
    });

    NotificarePushUI.onNotificationFinishedPresenting((notification) => {
      console.log('=== NOTIFICATION FINISHED PRESENTING ===');
      console.log(JSON.stringify(notification));
    });

    NotificarePushUI.onNotificationFailedToPresent((notification) => {
      console.log('=== NOTIFICATION FAILED TO PRESENT ===');
      console.log(JSON.stringify(notification));
    });

    NotificarePushUI.onNotificationUrlClicked((data) => {
      console.log('=== NOTIFICATION URL CLICKED ===');
      console.log(JSON.stringify(data));
    });

    NotificarePushUI.onActionWillExecute((data) => {
      console.log('=== ACTION WILL EXECUTE ===');
      console.log(JSON.stringify(data));
    });

    NotificarePushUI.onActionExecuted((data) => {
      console.log('=== ACTION EXECUTED ===');
      console.log(JSON.stringify(data));
    });

    NotificarePushUI.onActionNotExecuted((data) => {
      console.log('=== ACTION NOT EXECUTED ===');
      console.log(JSON.stringify(data));
    });

    NotificarePushUI.onActionFailedToExecute((data) => {
      console.log('=== ACTION FAILED TO EXECUTE ===');
      console.log(JSON.stringify(data));
    });

    NotificarePushUI.onCustomActionReceived((data) => {
      console.log('=== CUSTOM ACTION RECEIVED ===');
      console.log(JSON.stringify(data));
    });

    NotificareGeo.onLocationUpdated((location) => {
      console.log('=== LOCATION UPDATED ===');
      console.log(JSON.stringify(location, null, 2));
    });

    NotificareGeo.onRegionEntered((region) => {
      console.log('=== REGION ENTERED ===');
      console.log(JSON.stringify(region, null, 2));
    });

    NotificareGeo.onRegionExited((region) => {
      console.log('=== REGION EXITED ===');
      console.log(JSON.stringify(region, null, 2));
    });

    NotificareGeo.onBeaconEntered((beacon) => {
      console.log('=== BEACON ENTERED ===');
      console.log(JSON.stringify(beacon, null, 2));
    });

    NotificareGeo.onBeaconExited((beacon) => {
      console.log('=== BEACON EXITED ===');
      console.log(JSON.stringify(beacon, null, 2));
    });

    NotificareGeo.onBeaconsRanged(({ region, beacons }) => {
      console.log('=== BEACONS RANGED ===');
      console.log(JSON.stringify({ region, beacons }, null, 2));
    });

    NotificareGeo.onVisit((visit) => {
      console.log('=== VISIT ===');
      console.log(JSON.stringify(visit, null, 2));
    });

    NotificareGeo.onHeadingUpdated((heading) => {
      console.log('=== HEADING UPDATED ===');
      console.log(JSON.stringify(heading, null, 2));
    });

    NotificareScannables.onScannableDetected(async (scannable) => {
      console.log('=== SCANNABLE DETECTED ===');
      console.log(JSON.stringify(scannable, null, 2));

      if (scannable.notification != null) {
        await NotificarePushUI.presentNotification(scannable.notification);
      }
    });

    NotificareScannables.onScannableSessionFailed((error) => {
      console.log('=== SCANNABLE SESSION FAILED ===');
      console.log(JSON.stringify(error, null, 2));
    });

    NotificareAuthentication.onPasswordResetTokenReceived(async (token) => {
      console.log('=== PASSWORD RESET TOKEN RECEIVED ===');
      console.log(JSON.stringify(token, null, 2));

      await NotificareAuthentication.resetPassword('123456', token);
    });

    NotificareAuthentication.onValidateUserTokenReceived(async (token) => {
      console.log('=== VALIDATE USER TOKEN RECEIVED ===');
      console.log(JSON.stringify(token, null, 2));

      await NotificareAuthentication.validateUser(token);
    });

    NotificareMonetize.onBillingSetupFinished(() => {
      console.log('=== BILLING SETUP FINISHED ===');
    });

    NotificareMonetize.onBillingSetupFailed((data) => {
      console.log('=== BILLING SETUP FAILED ===');
      console.log(JSON.stringify(data, null, 2));
    });

    NotificareMonetize.onProductsUpdated((products) => {
      console.log('=== PRODUCTS UPDATED ===');
      console.log(JSON.stringify(products, null, 2));
    });

    NotificareMonetize.onPurchasesUpdated((purchases) => {
      console.log('=== PURCHASES UPDATED ===');
      console.log(JSON.stringify(purchases, null, 2));
    });

    NotificareMonetize.onPurchaseFinished((purchase) => {
      console.log('=== PURCHASE FINISHED ===');
      console.log(JSON.stringify(purchase, null, 2));
    });

    NotificareMonetize.onPurchaseRestored((purchase) => {
      console.log('=== PURCHASE RESTORED ===');
      console.log(JSON.stringify(purchase, null, 2));
    });

    NotificareMonetize.onPurchaseCanceled(() => {
      console.log('=== PURCHASE CANCELED ===');
    });

    NotificareMonetize.onPurchaseFailed((data) => {
      console.log('=== PURCHASE FAILED ===');
      console.log(JSON.stringify(data, null, 2));
    });
  })();
}
