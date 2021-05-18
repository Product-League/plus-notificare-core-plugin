package re.notifica.push.ui.cordova

import android.net.Uri
import re.notifica.push.ui.NotificarePushUIIntentReceiver

class NotificarePushUIPluginReceiver : NotificarePushUIIntentReceiver() {

    override fun onCustomActionReceived(uri: Uri) {
//        try {
//            NotificarePushUIPluginEventManager.dispatchEvent("notification_received", notification.toJson())
//        } catch (e: Exception) {
//            NotificareLogger.error("Failed to emit the notification_received event.", e)
//        }
    }
}
