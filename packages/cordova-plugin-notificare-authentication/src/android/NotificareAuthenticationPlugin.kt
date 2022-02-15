package re.notifica.authentication.cordova

import android.content.Intent
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.NotificareCallback
import re.notifica.authentication.ktx.authentication
import re.notifica.authentication.models.NotificareUser
import re.notifica.authentication.models.NotificareUserPreference
import re.notifica.authentication.models.NotificareUserSegment
import re.notifica.internal.NotificareLogger

class NotificareAuthenticationPlugin : CordovaPlugin() {

    override fun pluginInitialize() {
        val intent = cordova.activity.intent
        if (intent != null) onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        val passwordResetToken = Notificare.authentication().parsePasswordResetToken(intent)
        if (passwordResetToken != null) {
            NotificareAuthenticationPluginEventBroker.dispatchEvent("password_reset_token_received", passwordResetToken)
            return
        }

        val validateUserToken = Notificare.authentication().parseValidateUserToken(intent)
        if (validateUserToken != null) {
            NotificareAuthenticationPluginEventBroker.dispatchEvent("validate_user_token_received", validateUserToken)
            return
        }
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "isLoggedIn" -> isLoggedIn(args, callback)
            "login" -> login(args, callback)
            "logout" -> logout(args, callback)
            "fetchUserDetails" -> fetchUserDetails(args, callback)
            "changePassword" -> changePassword(args, callback)
            "generatePushEmailAddress" -> generatePushEmailAddress(args, callback)
            "createAccount" -> createAccount(args, callback)
            "validateUser" -> validateUser(args, callback)
            "sendPasswordReset" -> sendPasswordReset(args, callback)
            "resetPassword" -> resetPassword(args, callback)
            "fetchUserPreferences" -> fetchUserPreferences(args, callback)
            "fetchUserSegments" -> fetchUserSegments(args, callback)
            "addUserSegment" -> addUserSegment(args, callback)
            "removeUserSegment" -> removeUserSegment(args, callback)
            "addUserSegmentToPreference" -> addUserSegmentToPreference(args, callback)
            "removeUserSegmentFromPreference" -> removeUserSegmentFromPreference(args, callback)

            // Event broker
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Authentication

    private fun isLoggedIn(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.authentication().isLoggedIn)
    }

    private fun login(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val email = args.getString(0)
        val password = args.getString(1)

        Notificare.authentication().login(email, password, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun logout(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.authentication().logout(object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchUserDetails(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.authentication().fetchUserDetails(object : NotificareCallback<NotificareUser> {
            override fun onSuccess(result: NotificareUser) {
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

    private fun changePassword(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val password = args.getString(0)

        Notificare.authentication().changePassword(password, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun generatePushEmailAddress(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.authentication().generatePushEmailAddress(object : NotificareCallback<NotificareUser> {
            override fun onSuccess(result: NotificareUser) {
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

    private fun createAccount(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val email = args.getString(0)
        val password = args.getString(1)
        val name = args.getString(2)

        Notificare.authentication().createAccount(email, password, name, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun validateUser(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val token = args.getString(0)

        Notificare.authentication().validateUser(token, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun sendPasswordReset(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val email = args.getString(0)

        Notificare.authentication().sendPasswordReset(email, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun resetPassword(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val password = args.getString(0)
        val token = args.getString(1)

        Notificare.authentication().resetPassword(password, token, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun fetchUserPreferences(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.authentication().fetchUserPreferences(object : NotificareCallback<List<NotificareUserPreference>> {
            override fun onSuccess(result: List<NotificareUserPreference>) {
                try {
                    val json = JSONArray()
                    result.forEach { json.put(it.toJson()) }

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

    private fun fetchUserSegments(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.authentication().fetchUserSegments(object : NotificareCallback<List<NotificareUserSegment>> {
            override fun onSuccess(result: List<NotificareUserSegment>) {
                try {
                    val json = JSONArray()
                    result.forEach { json.put(it.toJson()) }

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

    private fun addUserSegment(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val segment = try {
            NotificareUserSegment.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.authentication().addUserSegment(segment, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun removeUserSegment(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val segment = try {
            NotificareUserSegment.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        Notificare.authentication().removeUserSegment(segment, object : NotificareCallback<Unit> {
            override fun onSuccess(result: Unit) {
                callback.void()
            }

            override fun onFailure(e: Exception) {
                callback.error(e.message)
            }
        })
    }

    private fun addUserSegmentToPreference(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val preference = try {
            NotificareUserPreference.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        try {
            val segment = NotificareUserSegment.fromJson(args.getJSONObject(1))
            Notificare.authentication()
                .addUserSegmentToPreference(segment, preference, object : NotificareCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        callback.void()
                    }

                    override fun onFailure(e: Exception) {
                        callback.error(e.message)
                    }
                })

            return
        } catch (e: Exception) {
            NotificareLogger.debug("Failed to parse segment data into a NotificareUserSegment.", e)
        }

        try {
            val option = NotificareUserPreference.Option.fromJson(args.getJSONObject(1))
            Notificare.authentication()
                .addUserSegmentToPreference(option, preference, object : NotificareCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        callback.void()
                    }

                    override fun onFailure(e: Exception) {
                        callback.error(e.message)
                    }
                })

            return
        } catch (e: Exception) {
            NotificareLogger.debug("Failed to parse segment data into a NotificareUserPreference.Option.", e)
        }

        callback.error("To execute this method, you must provide either a NotificareUserSegment or a NotificarePreferenceOption.")
    }

    private fun removeUserSegmentFromPreference(
        @Suppress("UNUSED_PARAMETER") args: CordovaArgs,
        callback: CallbackContext
    ) {
        val preference = try {
            NotificareUserPreference.fromJson(args.getJSONObject(0))
        } catch (e: Exception) {
            callback.error(e.message)
            return
        }

        try {
            val segment = NotificareUserSegment.fromJson(args.getJSONObject(1))
            Notificare.authentication()
                .removeUserSegmentFromPreference(segment, preference, object : NotificareCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        callback.void()
                    }

                    override fun onFailure(e: Exception) {
                        callback.error(e.message)
                    }
                })

            return
        } catch (e: Exception) {
            NotificareLogger.debug("Failed to parse segment data into a NotificareUserSegment.", e)
        }

        try {
            val option = NotificareUserPreference.Option.fromJson(args.getJSONObject(1))
            Notificare.authentication()
                .removeUserSegmentFromPreference(option, preference, object : NotificareCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        callback.void()
                    }

                    override fun onFailure(e: Exception) {
                        callback.error(e.message)
                    }
                })

            return
        } catch (e: Exception) {
            NotificareLogger.debug("Failed to parse segment data into a NotificareUserPreference.Option.", e)
        }

        callback.error("To execute this method, you must provide either a NotificareUserSegment or a NotificarePreferenceOption.")
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareAuthenticationPluginEventBroker.setup(object : NotificareAuthenticationPluginEventBroker.Consumer {
            override fun onEvent(event: NotificareAuthenticationPluginEventBroker.Event) {
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
