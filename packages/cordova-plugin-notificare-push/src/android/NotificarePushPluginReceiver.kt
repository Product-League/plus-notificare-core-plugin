package re.notifica.push.cordova

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.internal.NotificareLogger
import re.notifica.models.NotificareNotification
import re.notifica.push.NotificarePushIntentReceiver
import re.notifica.push.models.NotificareSystemNotification
import re.notifica.push.models.NotificareUnknownNotification

class NotificarePushPluginReceiver : NotificarePushIntentReceiver() {

    override fun onNotificationReceived(context: Context, notification: NotificareNotification) {
        try {
            NotificarePushPluginEventBroker.dispatchEvent("notification_received", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_received event.", e)
        }
    }

    override fun onSystemNotificationReceived(context: Context, notification: NotificareSystemNotification) {
        try {
            NotificarePushPluginEventBroker.dispatchEvent("system_notification_received", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the system_notification_received event.", e)
        }
    }

    override fun onUnknownNotificationReceived(context: Context, notification: NotificareUnknownNotification) {
        try {
            val json = JSONObject()
            json.put("messageId", notification.messageId)
            json.put("messageType", notification.messageType)
            json.put("senderId", notification.senderId)
            json.put("collapseKey", notification.collapseKey)
            json.put("from", notification.from)
            json.put("to", notification.to)
            json.put("sentTime", notification.sentTime)
            json.put("ttl", notification.ttl)
            json.put("priority", notification.priority)
            json.put("originalPriority", notification.originalPriority)
            json.put("notification", notification.notification?.let {
                JSONObject().apply {
                    put("title", it.title)
                    put("titleLocalizationKey", it.titleLocalizationKey)
                    put("titleLocalizationArgs", it.titleLocalizationArgs?.let { args -> JSONArray(args) })
                    put("body", it.body)
                    put("bodyLocalizationKey", it.bodyLocalizationKey)
                    put("bodyLocalizationArgs", it.bodyLocalizationArgs?.let { args -> JSONArray(args) })
                    put("icon", it.icon)
                    put("imageUrl", it.imageUrl?.toString())
                    put("sound", it.sound)
                    put("tag", it.tag)
                    put("color", it.color)
                    put("clickAction", it.clickAction)
                    put("channelId", it.channelId)
                    put("link", it.link?.toString())
                    put("ticker", it.ticker)
                    put("sticky", it.sticky)
                    put("localOnly", it.localOnly)
                    put("defaultSound", it.defaultSound)
                    put("defaultVibrateSettings", it.defaultVibrateSettings)
                    put("defaultLightSettings", it.defaultLightSettings)
                    it.notificationPriority?.let { priority -> put("notificationPriority", priority) }
                    it.visibility?.let { visibility -> put("visibility", visibility) }
                    it.notificationCount?.let { notificationCount -> put("notificationCount", notificationCount) }
                    it.eventTime?.let { eventTime -> put("eventTime", eventTime) }
                    put("lightSettings", it.lightSettings?.let { args -> JSONArray(args) })
                    put("vibrateSettings", it.vibrateSettings?.let { args -> JSONArray(args) })
                }
            })
            json.put("data", JSONObject().apply {
                notification.data.forEach {
                    put(it.key, it.value)
                }
            })

            NotificarePushPluginEventBroker.dispatchEvent("unknown_notification_received", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the unknown_notification_received event.", e)
        }
    }

    override fun onNotificationOpened(context: Context, notification: NotificareNotification) {
        try {
            NotificarePushPluginEventBroker.dispatchEvent("notification_opened", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_opened event.", e)
        }
    }

    override fun onActionOpened(
        context: Context,
        notification: NotificareNotification,
        action: NotificareNotification.Action
    ) {
        try {
            val json = JSONObject()
            json.put("notification", notification.toJson())
            json.put("action", action.toJson())

            NotificarePushPluginEventBroker.dispatchEvent("notification_action_opened", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_action_opened event.", e)
        }
    }
}
