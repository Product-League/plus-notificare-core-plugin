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
        await Notificare.setUseAdvancedLogging(true);

        console.log(`Is configured = ${await Notificare.isConfigured()}`)
        console.log(`Is ready = ${await Notificare.isReady()}`)

        await Notificare.launch();

        Notificare.onDeviceRegistered((device) => {
            console.log(`---> Device registered: ${JSON.stringify(device)}`);
        });

        Notificare.onReady(async (application) => {
            console.log('=======================');
            console.log('= NOTIFICARE IS READY =')
            console.log('=======================');
            console.log(JSON.stringify(application));

            // Stop listening to device_registered events.
            // sub.remove();

            setTimeout(async function register() {
                await Notificare.deviceManager.register(null, null);
            }, 2000);

            setTimeout(async function registerAnonymous() {
                await Notificare.deviceManager.register('helder@notifica.re', 'Helder Pinhal');
            }, 5000);

            console.log(`---> Tags = ${JSON.stringify(await Notificare.deviceManager.fetchTags())}`);
            await Notificare.deviceManager.addTags(['cordova', 'hpinhal'])
            console.log(`---> Tags = ${JSON.stringify(await Notificare.deviceManager.fetchTags())}`);
            await Notificare.deviceManager.removeTag('hpinhal');
            console.log(`---> Tags = ${JSON.stringify(await Notificare.deviceManager.fetchTags())}`);
            await Notificare.deviceManager.clearTags();
            console.log(`---> Tags = ${JSON.stringify(await Notificare.deviceManager.fetchTags())}`);

            console.log(`---> Language = ${JSON.stringify(await Notificare.deviceManager.getPreferredLanguage())}`);
            await Notificare.deviceManager.updatePreferredLanguage('nl-NL');
            console.log(`---> Language = ${JSON.stringify(await Notificare.deviceManager.getPreferredLanguage())}`);
            await Notificare.deviceManager.updatePreferredLanguage(null);
            console.log(`---> Language = ${JSON.stringify(await Notificare.deviceManager.getPreferredLanguage())}`);

            console.log(`---> DnD = ${JSON.stringify(await Notificare.deviceManager.fetchDoNotDisturb())}`);
            await Notificare.deviceManager.updateDoNotDisturb({ start: '23:00', end: '08:00' });
            console.log(`---> DnD = ${JSON.stringify(await Notificare.deviceManager.fetchDoNotDisturb())}`);
            await Notificare.deviceManager.clearDoNotDisturb();
            console.log(`---> DnD = ${JSON.stringify(await Notificare.deviceManager.fetchDoNotDisturb())}`);

            console.log(`---> User data = ${JSON.stringify(await Notificare.deviceManager.fetchUserData())}`);
            await Notificare.deviceManager.updateUserData({ firstName: 'Helder', lastName: 'Pinhal' });
            console.log(`---> User data = ${JSON.stringify(await Notificare.deviceManager.fetchUserData())}`);
            await Notificare.deviceManager.updateUserData({});
            console.log(`---> User data = ${JSON.stringify(await Notificare.deviceManager.fetchUserData())}`);

            //
            // Push
            //

            console.log(`---> Remote notifications enabled = ${await NotificarePush.isRemoteNotificationsEnabled()}`);
            await NotificarePush.enableRemoteNotifications();
            console.log(`---> Remote notifications enabled = ${await NotificarePush.isRemoteNotificationsEnabled()}`);

            await NotificarePush.setPresentationOptions(['alert', 'sound', 'badge']);

            //
            // Inbox
            //

            console.log(`---> Badge = ${await NotificareInbox.getBadge()}`);
            console.log(`---> Items = ${(await NotificareInbox.getItems()).length}`);
        });

        NotificarePush.onNotificationReceived((notification) => {
            console.log(`---> Received notification = ${JSON.stringify(notification)}`);
        });

        NotificarePush.onNotificationOpened((notification) => {
            console.log(`---> Opened notification = ${JSON.stringify(notification)}`);
        });

        NotificareInbox.onBadgeUpdated((badge) => {
            console.log(`---> Badge updated = ${badge}`);
        });

        NotificareInbox.onInboxUpdated((items) => {
            console.log(`---> Inbox updated = ${JSON.stringify(items)}`);
        });
    })();
}
