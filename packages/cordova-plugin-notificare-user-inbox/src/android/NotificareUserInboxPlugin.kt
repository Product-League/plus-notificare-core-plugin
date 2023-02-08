package re.notifica.inbox.user.cordova

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import re.notifica.Notificare
import re.notifica.NotificareCallback
import re.notifica.inbox.user.ktx.userInbox
import re.notifica.inbox.user.models.NotificareUserInboxItem
import re.notifica.models.NotificareNotification

class NotificareUserInboxPlugin : CordovaPlugin() {

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "parseResponseFromJson" -> parseResponseFromJson(args, callback)
            "parseResponseFromString" -> parseResponseFromString(args, callback)
            "open" -> open(args, callback)
            "markAsRead" -> markAsRead(args, callback)
            "remove" -> remove(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare User Inbox

    private fun parseResponseFromJson(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val json = try {
            args.getJSONObject(0)
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        try {
            val response = Notificare.userInbox().parseResponse(json)
            callback.success(response.toJson())
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun parseResponseFromString(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val jsonStr = try {
            args.getString(0)
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        try {
            val response = Notificare.userInbox().parseResponse(jsonStr)
            callback.success(response.toJson())
        } catch (e: Exception) {
            callback.error(e.message)
        }
    }

    private fun open(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val item: NotificareUserInboxItem = try {
            NotificareUserInboxItem.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.userInbox().open(item, object : NotificareCallback<NotificareNotification> {
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
        val item: NotificareUserInboxItem = try {
            NotificareUserInboxItem.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.userInbox().markAsRead(item, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun remove(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val item: NotificareUserInboxItem = try {
            NotificareUserInboxItem.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.userInbox().remove(item, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    // endregion
}

private fun CallbackContext.void() {
    sendPluginResult(PluginResult(PluginResult.Status.OK, null as String?))
}
