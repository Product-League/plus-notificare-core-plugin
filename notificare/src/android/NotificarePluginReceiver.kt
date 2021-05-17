package re.notifica.cordova

import re.notifica.NotificareIntentReceiver
import re.notifica.NotificareLogger
import re.notifica.models.NotificareApplication
import re.notifica.models.NotificareDevice

class NotificarePluginReceiver : NotificareIntentReceiver() {

    override fun onReady(application: NotificareApplication) {
        try {
            NotificarePluginEventManager.dispatchEvent("ready", application.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the ready event.", e)
        }
    }

    override fun onDeviceRegistered(device: NotificareDevice) {
        try {
            NotificarePluginEventManager.dispatchEvent("device_registered", device.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the device_registered event.", e)
        }
    }
}
