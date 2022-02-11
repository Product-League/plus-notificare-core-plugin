package re.notifica.geo.cordova

import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaArgs
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.PluginResult
import org.json.JSONArray
import org.json.JSONObject
import re.notifica.Notificare
import re.notifica.geo.NotificareGeo
import re.notifica.geo.ktx.geo
import re.notifica.geo.models.NotificareBeacon
import re.notifica.geo.models.NotificareLocation
import re.notifica.geo.models.NotificareRegion
import re.notifica.geo.models.toJson
import re.notifica.internal.NotificareLogger

class NotificareGeoPlugin : CordovaPlugin(), NotificareGeo.Listener {

    override fun pluginInitialize() {
        Notificare.geo().addListener(this)
    }

    override fun onDestroy() {
        Notificare.geo().removeListener(this)
    }

    override fun execute(action: String, args: CordovaArgs, callback: CallbackContext): Boolean {
        when (action) {
            "hasLocationServicesEnabled" -> hasLocationServicesEnabled(args, callback)
            "hasBluetoothEnabled" -> hasBluetoothEnabled(args, callback)
            "enableLocationUpdates" -> enableLocationUpdates(args, callback)
            "disableLocationUpdates" -> disableLocationUpdates(args, callback)

            // Event broker
            "registerListener" -> registerListener(args, callback)

            else -> {
                callback.error("No implementation for action '$action'.")
                return false
            }
        }

        return true
    }

    // region Notificare Geo

    private fun hasLocationServicesEnabled(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.geo().hasLocationServicesEnabled)

    }

    private fun hasBluetoothEnabled(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        callback.success(Notificare.geo().hasBluetoothEnabled)
    }

    private fun enableLocationUpdates(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.geo().enableLocationUpdates()
        callback.void()
    }

    private fun disableLocationUpdates(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        Notificare.geo().disableLocationUpdates()
        callback.void()
    }

    // endregion

    // region NotificareGeo.Listener

    override fun onLocationUpdated(location: NotificareLocation) {
        try {
            NotificareGeoPluginEventBroker.dispatchEvent("location_updated", location.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the location_updated event.", e)
        }
    }

    override fun onRegionEntered(region: NotificareRegion) {
        try {
            NotificareGeoPluginEventBroker.dispatchEvent("region_entered", region.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the region_entered event.", e)
        }
    }

    override fun onRegionExited(region: NotificareRegion) {
        try {
            NotificareGeoPluginEventBroker.dispatchEvent("region_exited", region.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the region_exited event.", e)
        }
    }

    override fun onBeaconEntered(beacon: NotificareBeacon) {
        try {
            NotificareGeoPluginEventBroker.dispatchEvent("beacon_entered", beacon.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the beacon_entered event.", e)
        }
    }

    override fun onBeaconExited(beacon: NotificareBeacon) {
        try {
            NotificareGeoPluginEventBroker.dispatchEvent("beacon_exited", beacon.toJson())
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the beacon_exited event.", e)
        }
    }

    override fun onBeaconsRanged(region: NotificareRegion, beacons: List<NotificareBeacon>) {
        try {
            val payload = JSONObject()
            payload.put("region", region.toJson())
            payload.put("beacons", JSONArray().apply {
                beacons.forEach { put(it.toJson()) }
            })

            NotificareGeoPluginEventBroker.dispatchEvent("beacons_ranged", payload)
        } catch (e: Exception) {
            NotificareLogger.error("Failed to emit the beacons_ranged event.", e)
        }
    }

    // endregion

    private fun registerListener(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        NotificareGeoPluginEventBroker.setup(object : NotificareGeoPluginEventBroker.Consumer {
            override fun onEvent(event: NotificareGeoPluginEventBroker.Event) {
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
