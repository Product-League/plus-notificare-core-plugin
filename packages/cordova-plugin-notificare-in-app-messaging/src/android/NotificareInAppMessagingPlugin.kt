package re.notifica.iam.cordova

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.iam.NotificareInAppMessaging
import re.notifica.iam.ktx.inAppMessaging
import re.notifica.iam.models.NotificareInAppMessage
import re.notifica.internal.NotificareLogger

class NotificareInAppMessagingPlugin : CordovaPlugin(), NotificareInAppMessaging.MessageLifecycleListener {

    override fun pluginInitialize() {
        Notificare.inAppMessaging().addLifecycleListener(this)
    }

    override fun onDestroy() {
        Notificare.inAppMessaging().removeLifecycleListener(this)
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "hasMessagesSuppressed" -> hasMessagesSuppressed(args, callback)
            "setMessagesSuppressed" -> setMessagesSuppressed(args, callback)

            // Event broker
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare In-App Messaging

    private fun hasMessagesSuppressed(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.inAppMessaging().hasMessagesSuppressed)
    }

    private fun setMessagesSuppressed(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            val suppressed = args.getBoolean(0)
            val evaluateContext =
                if (!args.isNull(1)) {
                    args.getBoolean(1)
                } else {
                    false
                }

            Notificare.inAppMessaging().setMessagesSuppressed(suppressed, evaluateContext)

            callback.void()
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    // endregion

    // region NotificareInAppMessaging.MessageLifecycleListener

    override fun onMessagePresented(message: NotificareInAppMessage) {
        try {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent("message_presented", message.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the message_presented event.", e)
        }
    }

    override fun onMessageFinishedPresenting(message: NotificareInAppMessage) {
        try {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent("message_finished_presenting", message.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the message_finished_presenting event.", e)
        }
    }

    override fun onMessageFailedToPresent(message: NotificareInAppMessage) {
        try {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent("message_failed_to_present", message.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the message_failed_to_present event.", e)
        }
    }

    override fun onActionExecuted(message: NotificareInAppMessage, action: NotificareInAppMessage.Action) {
        try {
            val json = JSONObject()
            json.put("message", message.toJson())
            json.put("action", action.toJson())

            NotificareInAppMessagingPluginEventBroker.dispatchEvent("action_executed", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the action_executed event.", e)
        }
    }

    override fun onActionFailedToExecute(
        message: NotificareInAppMessage,
        action: NotificareInAppMessage.Action,
        error: Exception?
    ) {
        try {
            val json = JSONObject()
            json.put("message", message.toJson())
            json.put("action", action.toJson())

            if (error != null) {
                json.put("error", error.message)
            }

            NotificareInAppMessagingPluginEventBroker.dispatchEvent("action_failed_to_execute", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the action_failed_to_execute event.", e)
        }
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareInAppMessagingPluginEventBroker.setup(object : NotificareInAppMessagingPluginEventBroker.Consumer {
            override fun onEvent(event: NotificareInAppMessagingPluginEventBroker.Event) {
                val payload = JSONObject()
                payload.put("name", event.name)
                when (event.payload) {
                    null -> {} // Skip encoding null payloads.
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

private fun CallbackContext.void() {
    sendPluginResult(PluginResult(PluginResult.Status.OK, null as String?))
}

private fun CallbackContext.success(b: Boolean) {
    sendPluginResult(PluginResult(PluginResult.Status.OK, b))
}
