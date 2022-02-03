package re.notifica.push.cordova

import org.json.JSONObject
import re.notifica.NotificareLogger
import re.notifica.models.NotificareNotification
import re.notifica.push.NotificarePushIntentReceiver
import re.notifica.push.models.NotificareSystemNotification
import re.notifica.push.models.NotificareUnknownNotification

class NotificarePushPluginReceiver : NotificarePushIntentReceiver() {

    override fun onNotificationReceived(notification: NotificareNotification) {
        try {
            NotificarePushPluginEventManager.dispatchEvent("notification_received", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_received event.", e)
        }
    }

    override fun onSystemNotificationReceived(notification: NotificareSystemNotification) {
        try {
            NotificarePushPluginEventManager.dispatchEvent("system_notification_received", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the system_notification_received event.", e)
        }
    }

    override fun onUnknownNotificationReceived(notification: NotificareUnknownNotification) {
        try {
            val json = JSONObject()
            notification.data.forEach {
                json.put(it.key, it.value)
            }

            NotificarePushPluginEventManager.dispatchEvent("unknown_notification_received", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the unknown_notification_received event.", e)
        }
    }

    override fun onNotificationOpened(notification: NotificareNotification) {
        try {
            NotificarePushPluginEventManager.dispatchEvent("notification_opened", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_opened event.", e)
        }
    }

    override fun onActionOpened(notification: NotificareNotification, action: NotificareNotification.Action) {
        try {
            val json = JSONObject()
            json.put("notification", notification.toJson())
            json.put("action", action.toJson())

            NotificarePushPluginEventManager.dispatchEvent("notification_action_opened", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_action_opened event.", e)
        }
    }
}
