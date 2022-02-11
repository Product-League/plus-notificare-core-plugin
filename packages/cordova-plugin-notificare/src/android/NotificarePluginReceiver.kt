package re.notifica.cordova

import android.content.Context
import re.notifica.NotificareIntentReceiver
import re.notifica.internal.NotificareLogger
import re.notifica.models.NotificareApplication
import re.notifica.models.NotificareDevice

class NotificarePluginReceiver : NotificareIntentReceiver() {

    override fun onReady(context: Context, application: NotificareApplication) {
        try {
            NotificarePluginEventBroker.dispatchEvent("ready", application.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the ready event.", e)
        }
    }

    override fun onUnlaunched(context: Context) {
        NotificarePluginEventBroker.dispatchEvent("unlaunched", null)
    }

    override fun onDeviceRegistered(context: Context, device: NotificareDevice) {
        try {
            NotificarePluginEventBroker.dispatchEvent("device_registered", device.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the device_registered event.", e)
        }
    }
}
