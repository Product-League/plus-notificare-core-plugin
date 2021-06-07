package re.notifica.push.cordova

import android.content.Intent
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.push.NotificarePush

class NotificarePushPlugin : CordovaPlugin() {

    override fun pluginInitialize() {
        NotificarePush.intentReceiver = NotificarePushPluginReceiver::class.java

        val intent = cordova.activity.intent
        if (intent != null) onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        NotificarePush.handleTrampolineIntent(intent)
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "setAuthorizationOptions" -> setAuthorizationOptions(args, callback)
            "setCategoryOptions" -> setCategoryOptions(args, callback)
            "setPresentationOptions" -> setPresentationOptions(args, callback)
            "isRemoteNotificationsEnabled" -> isRemoteNotificationsEnabled(args, callback)
            "isAllowedUI" -> isAllowedUI(args, callback)
            "enableRemoteNotifications" -> enableRemoteNotifications(args, callback)
            "disableRemoteNotifications" -> disableRemoteNotifications(args, callback)

            // Events
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Push

    private fun setAuthorizationOptions(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        // no-op: iOS-only method
        callback.void()
    }

    private fun setCategoryOptions(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        // no-op: iOS-only method
        callback.void()
    }

    private fun setPresentationOptions(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        // no-op: iOS-only method
        callback.void()
    }

    private fun isRemoteNotificationsEnabled(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(NotificarePush.isRemoteNotificationsEnabled)
    }

    private fun isAllowedUI(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(NotificarePush.allowedUI)
    }

    private fun enableRemoteNotifications(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificarePush.enableRemoteNotifications()
        callback.void()
    }

    private fun disableRemoteNotifications(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificarePush.disableRemoteNotifications()
        callback.void()
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificarePushPluginEventManager.setup(object : NotificarePushPluginEventManager.Consumer {
            override fun onEvent(event: NotificarePushPluginEventManager.Event) {
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
