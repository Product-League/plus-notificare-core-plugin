package re.notifica.cordova

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.NotificareCallback
import re.notifica.models.NotificareApplication
import re.notifica.models.NotificareDoNotDisturb
import re.notifica.models.NotificareNotification
import re.notifica.models.NotificareUserData

class NotificarePlugin : CordovaPlugin() {

    override fun pluginInitialize() {
        Notificare.intentReceiver = NotificarePluginReceiver::class.java
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            // Notificare
            "getConfigured" -> getConfigured(args, callback)
            "getReady" -> getReady(args, callback)
            "getUseAdvancedLogging" -> getUseAdvancedLogging(args, callback)
            "setUseAdvancedLogging" -> setUseAdvancedLogging(args, callback)
            "configure" -> configure(args, callback)
            "launch" -> launch(args, callback)
            "unlaunch" -> unlaunch(args, callback)
            "getApplication" -> getApplication(args, callback)
            "fetchApplication" -> fetchApplication(args, callback)
            "fetchNotification" -> fetchNotification(args, callback)

            // Device Manager
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

            // Events
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare

    private fun getConfigured(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.isConfigured)
    }

    private fun getReady(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.isReady)
    }

    private fun configure(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val applicationKey = args.getString(0)
        val applicationSecret = args.getString(1)

        Notificare.configure(cordova.context, applicationKey, applicationSecret)
        callback.void()
    }

    private fun getUseAdvancedLogging(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.useAdvancedLogging)
    }

    private fun setUseAdvancedLogging(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.useAdvancedLogging = args.getBoolean(0)
        callback.void()
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
        callback.nullableSuccess(Notificare.application?.toJson())
    }

    private fun fetchApplication(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.fetchApplication(object : NotificareCallback<NotificareApplication> {
            override fun onSuccess(result: NotificareApplication) {
                callback.success(result.toJson())
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
                callback.success(result.toJson())
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    // endregion

    // region Notificare Device Manager

    private fun getCurrentDevice(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.nullableSuccess(Notificare.deviceManager.currentDevice?.toJson())
    }

    private fun register(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val userId: String? = args.optionalString(0)
        val userName: String? = args.optionalString(1)

        Notificare.deviceManager.register(userId, userName, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchTags(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.deviceManager.fetchTags(object : NotificareCallback<List<String>> {
            override fun onSuccess(result: List<String>) {
                val json = JSONArray()
                result.forEach { json.put(it) }

                callback.success(json)
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun addTag(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val tag = args.getString(0)

        Notificare.deviceManager.addTag(tag, object : NotificareCallback<Unit> {
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

        Notificare.deviceManager.addTags(tags, object : NotificareCallback<Unit> {
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

        Notificare.deviceManager.removeTag(tag, object : NotificareCallback<Unit> {
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

        Notificare.deviceManager.removeTags(tags, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun clearTags(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.deviceManager.clearTags(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun getPreferredLanguage(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.deviceManager.preferredLanguage)
    }

    private fun updatePreferredLanguage(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val language: String? = args.optionalString(0)

        Notificare.deviceManager.updatePreferredLanguage(language, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchDoNotDisturb(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.deviceManager.fetchDoNotDisturb(object : NotificareCallback<NotificareDoNotDisturb?> {
            override fun onSuccess(result: NotificareDoNotDisturb?) {
                callback.nullableSuccess(result?.toJson())
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun updateDoNotDisturb(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val dnd = NotificareDoNotDisturb.fromJson(args.getJSONObject(0))

        Notificare.deviceManager.updateDoNotDisturb(dnd, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun clearDoNotDisturb(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.deviceManager.clearDoNotDisturb(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchUserData(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.deviceManager.fetchUserData(object : NotificareCallback<NotificareUserData?> {
            override fun onSuccess(result: NotificareUserData?) {
                if (result == null) return callback.void()

                val json = JSONObject()
                result.forEach { json.put(it.key, it.value) }

                callback.success(json)
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun updateUserData(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val json = args.getJSONObject(0)
        val iterator = json.keys()

        val userData = mutableMapOf<String, String>()
        while (iterator.hasNext()) {
            val key = iterator.next()
            userData[key] = json.getString(key)
        }

        Notificare.deviceManager.updateUserData(userData, object : NotificareCallback<Unit> {
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
        NotificarePluginEventManager.setup(object : NotificarePluginEventManager.Consumer {
            override fun onEvent(event: NotificarePluginEventManager.Event) {
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
