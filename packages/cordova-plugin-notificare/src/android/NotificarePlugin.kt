package re.notifica.cordova

import android.content.Intent
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.NotificareCallback
import re.notifica.ktx.device
import re.notifica.ktx.events
import re.notifica.models.*

class NotificarePlugin : CordovaPlugin() {

    override fun pluginInitialize() {
        Notificare.intentReceiver = NotificarePluginReceiver::class.java

        val intent = cordova.activity.intent
        if (intent != null) onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        // Try handling the test device intent.
        if (Notificare.handleTestDeviceIntent(intent)) return

        // Try handling the dynamic link intent.
        if (Notificare.handleDynamicLinkIntent(cordova.activity, intent)) return

        val url = intent.data?.toString()
        if (url != null) {
            NotificarePluginEventBroker.dispatchEvent("url_opened", url)
        }
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "isConfigured" -> isConfigured(args, callback)
            "isReady" -> isReady(args, callback)
            "launch" -> launch(args, callback)
            "unlaunch" -> unlaunch(args, callback)
            "getApplication" -> getApplication(args, callback)
            "fetchApplication" -> fetchApplication(args, callback)
            "fetchNotification" -> fetchNotification(args, callback)
            //
            // Device
            //
            "getCurrentDevice" -> getCurrentDevice(args, callback)
            "register" -> register(args, callback)
            "fetchTags" -> fetchTags(args, callback)
            "addTag" -> addTag(args, callback)
            "addTags" -> addTags(args, callback)
            "removeTag" -> removeTag(args, callback)
            "removeTags" -> removeTags(args, callback)
            "clearTags" -> clearTags(args, callback)
            "getPreferredLanguage" -> getPreferredLanguage(args, callback)
            "updatePreferredLanguage" -> updatePreferredLanguage(args, callback)
            "fetchDoNotDisturb" -> fetchDoNotDisturb(args, callback)
            "updateDoNotDisturb" -> updateDoNotDisturb(args, callback)
            "clearDoNotDisturb" -> clearDoNotDisturb(args, callback)
            "fetchUserData" -> fetchUserData(args, callback)
            "updateUserData" -> updateUserData(args, callback)
            //
            // Events
            //
            "logCustom" -> logCustom(args, callback)
            //
            // Event broker
            //
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare

    private fun isConfigured(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.isConfigured)
    }

    private fun isReady(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.isReady)
    }

    private fun launch(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.launch()
        callback.void()
    }

    private fun unlaunch(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.unlaunch()
        callback.void()
    }

    private fun getApplication(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            callback.nullableSuccess(Notificare.application?.toJson())
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun fetchApplication(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.fetchApplication(object : NotificareCallback<NotificareApplication> {
            override fun onSuccess(result: NotificareApplication) {
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

    private fun fetchNotification(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val id = args.getString(0)

        Notificare.fetchNotification(id, object : NotificareCallback<NotificareNotification> {
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

    // endregion

    // region Notificare Device Manager

    private fun getCurrentDevice(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            callback.nullableSuccess(Notificare.device().currentDevice?.toJson())
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun register(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val userId: String? = args.optionalString(0)
        val userName: String? = args.optionalString(1)

        Notificare.device().register(userId, userName, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchTags(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.device().fetchTags(object : NotificareCallback<List<String>> {
            override fun onSuccess(result: List<String>) {
                try {
                    val json = JSONArray()
                    result.forEach { json.put(it) }

                    callback.success(json)
                } catch (e: Exception) {
                    callback.error(e.message)
                }
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun addTag(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val tag = args.getString(0)

        Notificare.device().addTag(tag, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun addTags(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val json = args.getJSONArray(0)

        val tags = mutableListOf<String>()
        for (i in 0 until json.length()) {
            tags.add(json.getString(i))
        }

        Notificare.device().addTags(tags, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun removeTag(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val tag = args.getString(0)

        Notificare.device().removeTag(tag, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun removeTags(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val json = args.getJSONArray(0)

        val tags = mutableListOf<String>()
        for (i in 0 until json.length()) {
            tags.add(json.getString(i))
        }

        Notificare.device().removeTags(tags, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun clearTags(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.device().clearTags(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun getPreferredLanguage(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.nullableSuccess(Notificare.device().preferredLanguage)
    }

    private fun updatePreferredLanguage(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val language: String? = args.optionalString(0)

        Notificare.device().updatePreferredLanguage(language, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchDoNotDisturb(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.device().fetchDoNotDisturb(object : NotificareCallback<NotificareDoNotDisturb?> {
            override fun onSuccess(result: NotificareDoNotDisturb?) {
                try {
                    callback.nullableSuccess(result?.toJson())
                } catch (e: Exception) {
                    callback.error(e.message)
                }
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun updateDoNotDisturb(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val dnd: NotificareDoNotDisturb = try {
            NotificareDoNotDisturb.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.device().updateDoNotDisturb(dnd, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun clearDoNotDisturb(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.device().clearDoNotDisturb(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchUserData(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.device().fetchUserData(object : NotificareCallback<NotificareUserData> {
            override fun onSuccess(result: NotificareUserData) {
                try {
                    val json = JSONObject()
                    result.forEach { json.put(it.key, it.value) }

                    callback.success(json)
                } catch (e: Exception) {
                    callback.error(e.message)
                }
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun updateUserData(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val userData = mutableMapOf<String, String>()

        try {
            val json = args.getJSONObject(0)
            val iterator = json.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                if (!json.isNull(key)) {
                    userData[key] = json.getString(key)
                }
            }
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.device().updateUserData(userData, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    // endregion

    // region Notificare Events Manager

    private fun logCustom(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val event = args.getString(0)
        val json: JSONObject? = if (!args.isNull(1)) args.getJSONObject(1) else null

        val data: NotificareEventData? = try {
            json?.let { NotificareEvent.createData(it) }
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.events().logCustom(event, data, object : NotificareCallback<Unit> {
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
        NotificarePluginEventBroker.setup(object : NotificarePluginEventBroker.Consumer {
            override fun onEvent(event: NotificarePluginEventBroker.Event) {
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

private fun CordovaArgs.optionalString(index: Int, defaultValue: String? = null): String? {
    return if (isNull(index)) defaultValue else getString(index)
}

private fun CallbackContext.void() {
    sendPluginResult(PluginResult(PluginResult.Status.OK, null as String?))
}

private fun CallbackContext.success(b: Boolean) {
    sendPluginResult(PluginResult(PluginResult.Status.OK, b))
}

private fun CallbackContext.nullableSuccess(str: String?) {
    if (str == null) {
        void()
    } else {
        success(str)
    }
}

private fun CallbackContext.nullableSuccess(json: JSONObject?) {
    if (json == null) {
        void()
    } else {
        success(json)
    }
}
