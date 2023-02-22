package re.notifica.scannables.cordova

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.NotificareCallback
import re.notifica.internal.NotificareLogger
import re.notifica.scannables.NotificareScannables
import re.notifica.scannables.ktx.scannables
import re.notifica.scannables.models.NotificareScannable
import re.notifica.scannables.models.toJson

class NotificareScannablesPlugin : CordovaPlugin(), NotificareScannables.ScannableSessionListener {

    override fun pluginInitialize() {
        Notificare.scannables().addListener(this)
    }

    override fun onDestroy() {
        Notificare.scannables().removeListener(this)
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "canStartNfcScannableSession" -> canStartNfcScannableSession(args, callback)
            "startScannableSession" -> startScannableSession(args, callback)
            "startNfcScannableSession" -> startNfcScannableSession(args, callback)
            "startQrCodeScannableSession" -> startQrCodeScannableSession(args, callback)
            "fetch" -> fetch(args, callback)

            // Event broker
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Scannables

    private fun canStartNfcScannableSession(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.scannables().canStartNfcScannableSession)
    }

    private fun startScannableSession(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val activity = cordova.activity ?: run {
            callback.error("Cannot start a scannable session without an activity.")
            return
        }

        Notificare.scannables().startScannableSession(activity)
        callback.void()
    }

    private fun startNfcScannableSession(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val activity = cordova.activity ?: run {
            callback.error("Cannot start a scannable session without an activity.")
            return
        }

        Notificare.scannables().startNfcScannableSession(activity)
        callback.void()
    }

    private fun startQrCodeScannableSession(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val activity = cordova.activity ?: run {
            callback.error("Cannot start a scannable session without an activity.")
            return
        }

        Notificare.scannables().startQrCodeScannableSession(activity)
        callback.void()
    }

    private fun fetch(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val tag = args.getString(0)

        Notificare.scannables().fetch(tag, object : NotificareCallback<NotificareScannable> {
            override fun onSuccess(result: NotificareScannable) {
                try {
                    callback.success(result.toJson())
                } catch (e: Exception) {
                    callback.error(e.message)
                }
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    // endregion

    // region NotificareScannables.ScannableSessionListener

    override fun onScannableDetected(scannable: NotificareScannable) {
        try {
            NotificareScannablesPluginEventBroker.dispatchEvent("scannable_detected", scannable.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the scannable_detected event.", e)
        }
    }

    override fun onScannableSessionError(error: Exception) {
        try {
            NotificareScannablesPluginEventBroker.dispatchEvent("scannable_session_failed", error.localizedMessage)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the scannable_session_failed event.", e)
        }
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareScannablesPluginEventBroker.setup(preferences, object : NotificareScannablesPluginEventBroker.Consumer {
            override fun onEvent(event: NotificareScannablesPluginEventBroker.Event) {
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
