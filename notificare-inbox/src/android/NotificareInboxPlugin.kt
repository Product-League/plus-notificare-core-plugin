package re.notifica.inbox.cordova

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.NotificareCallback
import re.notifica.inbox.NotificareInbox
import re.notifica.inbox.models.NotificareInboxItem
import re.notifica.models.NotificareNotification
import re.notifica.NotificareLogger
import java.util.*

class NotificareInboxPlugin : CordovaPlugin() {

    private val itemsObserver = Observer<SortedSet<NotificareInboxItem>> { items ->
        if (items == null) return@Observer

        try {
            val json = JSONArray()
            items.forEach { json.put(it.toJson()) }

            NotificareInboxPluginEventManager.dispatchEvent("inbox_updated", json)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the inbox_updated event.", e)
        }
    }

    private val badgeObserver = Observer<Int> { badge ->
        if (badge == null) return@Observer

        NotificareInboxPluginEventManager.dispatchEvent("badge_updated", badge)
    }

    override fun pluginInitialize() {
        onMainThread {
            NotificareInbox.observableItems.observeForever(itemsObserver)
            NotificareInbox.observableBadge.observeForever(badgeObserver)
        }
    }

    override fun onDestroy() {
        onMainThread {
            NotificareInbox.observableItems.removeObserver(itemsObserver)
            NotificareInbox.observableBadge.removeObserver(badgeObserver)
        }
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "getItems" -> getItems(args, callback)
            "getBadge" -> getBadge(args, callback)
            "refresh" -> refresh(args, callback)
            "open" -> open(args, callback)
            "markAsRead" -> markAsRead(args, callback)
            "markAllAsRead" -> markAllAsRead(args, callback)
            "remove" -> remove(args, callback)
            "clear" -> clear(args, callback)

            // Events
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Inbox

    private fun getItems(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            val json = JSONArray()
            NotificareInbox.items.forEach { json.put(it.toJson()) }

            callback.success(json)
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun getBadge(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(NotificareInbox.badge)
    }

    private fun refresh(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareInbox.refresh()
        callback.void()
    }

    private fun open(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val item: NotificareInboxItem

        try {
            val json = args.getJSONObject(0)
            item = NotificareInboxItem.fromJson(json)
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        NotificareInbox.open(item, object : NotificareCallback<NotificareNotification> {
            override fun onSuccess(result: NotificareNotification) {
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

    private fun markAsRead(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val item: NotificareInboxItem

        try {
            val json = args.getJSONObject(0)
            item = NotificareInboxItem.fromJson(json)
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        NotificareInbox.markAsRead(item, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun markAllAsRead(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareInbox.markAllAsRead(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun remove(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val item: NotificareInboxItem

        try {
            val json = args.getJSONObject(0)
            item = NotificareInboxItem.fromJson(json)
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        NotificareInbox.remove(item, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun clear(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareInbox.clear(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareInboxPluginEventManager.setup(object : NotificareInboxPluginEventManager.Consumer {
            override fun onEvent(event: NotificareInboxPluginEventManager.Event) {
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

private fun onMainThread(action: () -> Unit) = Handler(Looper.getMainLooper()).post(action)

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
