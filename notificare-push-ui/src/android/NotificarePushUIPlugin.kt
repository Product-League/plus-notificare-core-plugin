package re.notifica.push.ui.cordova

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.models.NotificareNotification
import re.notifica.push.ui.NotificarePushUI

class NotificarePushUIPlugin : CordovaPlugin() {

    override fun pluginInitialize() {
        NotificarePushUI.intentReceiver = NotificarePushUIPluginReceiver::class.java
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
