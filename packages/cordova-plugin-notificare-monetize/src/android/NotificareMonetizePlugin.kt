package re.notifica.monetize.cordova

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.NotificareCallback
import re.notifica.internal.NotificareLogger
import re.notifica.monetize.NotificareMonetize
import re.notifica.monetize.ktx.monetize
import re.notifica.monetize.models.NotificareProduct
import re.notifica.monetize.models.NotificarePurchase

class NotificareMonetizePlugin : CordovaPlugin(), NotificareMonetize.Listener {

    private val productsObserver = Observer<List<NotificareProduct>> { products ->
        if (products == null) return@Observer

        try {
            val json = JSONArray()
            products.forEach { json.put(it.toJson()) }

            NotificareMonetizePluginEventBroker.dispatchEvent("products_updated", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the products_updated event.", e)
        }
    }

    private val purchasesObserver = Observer<List<NotificarePurchase>> { purchases ->
        if (purchases == null) return@Observer

        try {
            val json = JSONArray()
            purchases.forEach { json.put(it.toJson()) }

            NotificareMonetizePluginEventBroker.dispatchEvent("purchases_updated", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the purchases_updated event.", e)
        }
    }

    override fun pluginInitialize() {
        onMainThread {
            Notificare.monetize().observableProducts.observeForever(productsObserver)
            Notificare.monetize().observablePurchases.observeForever(purchasesObserver)
        }

        Notificare.monetize().addListener(this)
    }

    override fun onDestroy() {
        onMainThread {
            Notificare.monetize().observableProducts.removeObserver(productsObserver)
            Notificare.monetize().observablePurchases.removeObserver(purchasesObserver)
        }

        Notificare.monetize().removeListener(this)
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "getProducts" -> getProducts(args, callback)
            "getPurchases" -> getPurchases(args, callback)
            "refresh" -> refresh(args, callback)
            "startPurchaseFlow" -> startPurchaseFlow(args, callback)

            // Event broker
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Monetize

    private fun getProducts(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            val json = JSONArray()
            Notificare.monetize().products.forEach { json.put(it.toJson()) }

            callback.success(json)
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun getPurchases(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            val json = JSONArray()
            Notificare.monetize().purchases.forEach { json.put(it.toJson()) }

            callback.success(json)
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun refresh(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.monetize().refresh(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun startPurchaseFlow(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val product: NotificareProduct = try {
            NotificareProduct.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        val activity = cordova.activity ?: run {
            callback.error("Cannot start a purchase without an activity.")
            return
        }

        Notificare.monetize().startPurchaseFlow(activity, product)
        callback.void()
    }

    // endregion

    // region NotificareMonetize.Listener

    override fun onBillingSetupFinished() {
        NotificareMonetizePluginEventBroker.dispatchEvent("billing_setup_finished", null)
    }

    override fun onBillingSetupFailed(code: Int, message: String) {
        try {
            val json = JSONObject().apply {
                put("code", code)
                put("message", message)
            }

            NotificareMonetizePluginEventBroker.dispatchEvent("billing_setup_failed", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the billing_setup_failed event.", e)
        }
    }

    override fun onPurchaseFinished(purchase: NotificarePurchase) {
        try {
            NotificareMonetizePluginEventBroker.dispatchEvent("purchase_finished", purchase.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the purchase_finished event.", e)
        }
    }

    override fun onPurchaseRestored(purchase: NotificarePurchase) {
        try {
            NotificareMonetizePluginEventBroker.dispatchEvent("purchase_restored", purchase.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the purchase_restored event.", e)
        }
    }

    override fun onPurchaseCanceled() {
        NotificareMonetizePluginEventBroker.dispatchEvent("purchase_canceled", null)
    }

    override fun onPurchaseFailed(code: Int, message: String) {
        try {
            val json = JSONObject().apply {
                put("code", code)
                put("message", message)
            }

            NotificareMonetizePluginEventBroker.dispatchEvent("purchase_failed", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the purchase_failed event.", e)
        }
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareMonetizePluginEventBroker.setup(preferences, object : NotificareMonetizePluginEventBroker.Consumer {
            override fun onEvent(event: NotificareMonetizePluginEventBroker.Event) {
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

private fun onMainThread(action: () -> Unit) = Handler(Looper.getMainLooper()).post(action)

private fun CallbackContext.void() {
    sendPluginResult(PluginResult(PluginResult.Status.OK, null as String?))
}
