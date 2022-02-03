package re.notifica.push.ui.cordova

import android.net.Uri
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.NotificareLogger
import re.notifica.models.NotificareNotification
import re.notifica.push.ui.NotificarePushUI

class NotificarePushUIPlugin : CordovaPlugin(), NotificarePushUI.NotificationLifecycleListener {

    override fun pluginInitialize() {
        NotificarePushUI.addLifecycleListener(this)
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "presentNotification" -> presentNotification(args, callback)
            "presentAction" -> presentAction(args, callback)

            // Events
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Push UI

    private fun presentNotification(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val notification: NotificareNotification

        try {
            notification = NotificareNotification.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        val activity = cordova.activity ?: run {
            callback.error("Cannot present a notification without an activity.")
            return
        }

        NotificarePushUI.presentNotification(activity, notification)
        callback.void()
    }

    private fun presentAction(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val notification: NotificareNotification
        val action: NotificareNotification.Action

        try {
            notification = NotificareNotification.fromJson(args.getJSONObject(0))
            action = NotificareNotification.Action.fromJson(args.getJSONObject(1))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        val activity = cordova.activity ?: run {
            callback.error("Cannot present a notification without an activity.")
            return
        }

        NotificarePushUI.presentAction(activity, notification, action)
        callback.void()
    }

    // endregion

    // region NotificarePushUI.NotificationLifecycleListener

    override fun onNotificationWillPresent(notification: NotificareNotification) {
        try {

        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_will_present event.", e)
        }
    }

    override fun onNotificationPresented(notification: NotificareNotification) {
        try {
            NotificarePushUIPluginEventManager.dispatchEvent("notification_presented", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_presented event.", e)
        }
    }

    override fun onNotificationFinishedPresenting(notification: NotificareNotification) {
        try {
            NotificarePushUIPluginEventManager.dispatchEvent("notification_finished_presenting", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_finished_presenting event.", e)
        }
    }

    override fun onNotificationFailedToPresent(notification: NotificareNotification) {
        try {
            NotificarePushUIPluginEventManager.dispatchEvent("notification_failed_to_present", notification.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_failed_to_present event.", e)
        }
    }

    override fun onNotificationUrlClicked(notification: NotificareNotification, uri: Uri) {
        try {
            val json = JSONObject()
            json.put("notification", notification.toJson())
            json.put("url", uri.toString())

            NotificarePushUIPluginEventManager.dispatchEvent("notification_url_clicked", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the notification_url_clicked event.", e)
        }
    }

    override fun onActionWillExecute(notification: NotificareNotification, action: NotificareNotification.Action) {
        try {
            val json = JSONObject()
            json.put("notification", notification.toJson())
            json.put("action", action.toJson())

            NotificarePushUIPluginEventManager.dispatchEvent("action_will_execute", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the action_will_execute event.", e)
        }
    }

    override fun onActionExecuted(notification: NotificareNotification, action: NotificareNotification.Action) {
        try {
            val json = JSONObject()
            json.put("notification", notification.toJson())
            json.put("action", action.toJson())

            NotificarePushUIPluginEventManager.dispatchEvent("action_executed", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the action_executed event.", e)
        }
    }

    override fun onActionFailedToExecute(
        notification: NotificareNotification,
        action: NotificareNotification.Action,
        error: Exception?
    ) {
        try {
            val json = JSONObject()
            json.put("notification", notification.toJson())
            json.put("action", action.toJson())
            if (error != null) json.put("error", error.localizedMessage)

            NotificarePushUIPluginEventManager.dispatchEvent("action_failed_to_execute", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the action_failed_to_execute event.", e)
        }
    }

    override fun onCustomActionReceived(
        notification: NotificareNotification,
        action: NotificareNotification.Action,
        uri: Uri
    ) {
        try {
            NotificarePushUIPluginEventManager.dispatchEvent("custom_action_received", uri.toString())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the custom_action_received event.", e)
        }
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificarePushUIPluginEventManager.setup(object : NotificarePushUIPluginEventManager.Consumer {
            override fun onEvent(event: NotificarePushUIPluginEventManager.Event) {
                val payload = JSONObject()
                payload.put("name", event.name)
                when (event.payload) {
                    is Boolean -> payload.put("data", event.payload)
                    is Int -> payload.put("data", event.payload)
                    is Float -> payload.put("data", event.payload)
                    is Double -> payload.put("data", event.payload)
                    is String -> payload.put("data", event.payload)
                    is JSONObject -> payload.put("data", event.payload)
                    is JSONArray -> payload.put("data", event.payload)
                    else -> throw IllegalArgumentException("Unsupported event payload of type '${event.payload::class.java.simpleName}'.")
                }

                val result = PluginResult(PluginResult.Status.OK, payload)
                result.keepCallback = true

                callback.sendPluginResult(result)
            }
        })
    }
}

private fun CordovaArgs.optionalString(index: Int, defaultValue: String? = null): String? {
    return if (isNull(index)) defaultValue else getString(index)
}

private fun CallbackContext.success(b: Boolean) {
    sendPluginResult(PluginResult(PluginResult.Status.OK, b))
}

private fun CallbackContext.void() {
    sendPluginResult(PluginResult(PluginResult.Status.OK, null as String?))
}

private fun CallbackContext.nullableSuccess(json: JSONObject?) {
    if (json == null) {
        void()
    } else {
        success(json)
    }
}
