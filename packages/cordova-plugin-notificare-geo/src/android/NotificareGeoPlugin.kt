package re.notifica.geo.cordova

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private companion object {
        private val TAG = NotificareGeoPlugin::class.java.simpleName
    }

    private var shouldShowRationale = false
    private var hasOnGoingPermissionRequest = false
    private var permissionRequestCallback: CallbackContext? = null

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun pluginInitialize() {
        Notificare.geo().addListener(this)

        permissionsLauncher = cordova.activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val status = permissions
                .all { it.value }
                .let { granted ->
                    if (granted) {
                        PermissionStatus.GRANTED
                    } else {
                        if (!shouldShowRationale &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(
                                cordova.activity,
                                permissions.keys.first()
                            )
                        ) {
                            PermissionStatus.PERMANENTLY_DENIED
                        } else {
                            PermissionStatus.DENIED
                        }
                    }
                }

            permissionRequestCallback?.success(status.rawValue)
            permissionRequestCallback = null
            hasOnGoingPermissionRequest = false
        }
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
            "checkPermissionStatus" -> checkPermissionStatus(args, callback)
            "shouldShowPermissionRationale" -> shouldShowPermissionRationale(args, callback)
            "presentPermissionRationale" -> presentPermissionRationale(args, callback)
            "requestPermission" -> requestPermission(args, callback)
            "openAppSettings" -> openAppSettings(args, callback)

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

    private fun checkPermissionStatus(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val context = cordova.context ?: run {
            callback.error("Cannot continue without a context.")
            return
        }

        val permission =
            if (!args.isNull(0)) {
                val parameter = args.getString(0)
                PermissionGroup.parse(parameter) ?: run {
                    callback.error("Unsupported permission parameter: $parameter")
                    return
                }
            } else {
                callback.error("Missing permission parameter.")
                return
            }

        val status = determinePermissionStatus(context, permission)
        callback.success(status.rawValue)
    }

    private fun shouldShowPermissionRationale(
        @Suppress("UNUSED_PARAMETER") args: CordovaArgs,
        callback: CallbackContext
    ) {
        val activity = cordova.activity ?: run {
            NotificareLogger.warning("Unable to acquire a reference to the current activity.")
            callback.error("Unable to acquire a reference to the current activity.")
            return
        }

        val permission =
            if (!args.isNull(0)) {
                val parameter = args.getString(0)
                PermissionGroup.parse(parameter) ?: run {
                    callback.error("Unsupported permission parameter: $parameter")
                    return
                }
            } else {
                callback.error("Missing permission parameter.")
                return
            }

        val manifestPermissions = getManifestPermissions(activity, permission)

        if (manifestPermissions.isEmpty()) {
            NotificareLogger.warning("No permissions found in the manifest for $permission")
            callback.success(false)
            return
        }

        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, manifestPermissions.first())
        callback.success(showRationale)
    }

    private fun presentPermissionRationale(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val activity = cordova.activity ?: run {
            NotificareLogger.warning("Unable to acquire a reference to the current activity.")
            callback.error("Unable to acquire a reference to the current activity.")
            return
        }

        val permission =
            if (!args.isNull(0)) {
                val parameter = args.getString(0)
                PermissionGroup.parse(parameter) ?: run {
                    callback.error("Unsupported permission parameter: $parameter")
                    return
                }
            } else {
                callback.error("Missing permission parameter.")
                return
            }

        val rationale =
            if (!args.isNull(1)) {
                args.getJSONObject(1)
            } else {
                callback.error("Missing rationale parameter.")
                return
            }

        val title = if (!rationale.isNull("title")) rationale.getString("title") else null
        val message =
            if (!rationale.isNull("message")) {
                rationale.getString("message")
            } else {
                callback.error("Missing message parameter.")
                return
            }
        val buttonText =
            if (!rationale.isNull("buttonText")) rationale.getString("buttonText")
            else activity.getString(android.R.string.ok)

        try {
            NotificareLogger.debug("Presenting permission rationale for '$permission'.")

            activity.runOnUiThread {
                AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(buttonText, null)
                    .setOnDismissListener { callback.success() }
                    .show()
            }
        } catch (e: Exception) {
            callback.error("Unable to present the rationale alert.")
        }
    }

    private fun requestPermission(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        val activity = cordova.activity ?: run {
            callback.error("Cannot continue without a activity.")
            return
        }

        val permission =
            if (!args.isNull(0)) {
                val parameter = args.getString(0)
                PermissionGroup.parse(parameter) ?: run {
                    callback.error("Unsupported permission parameter: $parameter")
                    return
                }
            } else {
                callback.error("Missing permission parameter.")
                return
            }

        if (hasOnGoingPermissionRequest) {
            NotificareLogger.warning("A request for permissions is already running, please wait for it to finish before doing another request.")
            callback.error("A request for permissions is already running, please wait for it to finish before doing another request.")
            return
        }

        val status = determinePermissionStatus(activity, permission)
        if (status == PermissionStatus.GRANTED) {
            callback.success(status.rawValue)
            return
        }

        val manifestPermissions = getManifestPermissions(activity, permission)

        if (manifestPermissions.isEmpty()) {
            NotificareLogger.warning("No permissions found in the manifest for $permission")
            callback.success(PermissionStatus.DENIED.rawValue)
            return
        }

        shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, manifestPermissions.first())
        hasOnGoingPermissionRequest = true
        permissionRequestCallback = callback

        permissionsLauncher.launch(manifestPermissions.toTypedArray())
    }

    private fun openAppSettings(@Suppress("UNUSED_PARAMETER") args: CordovaArgs, callback: CallbackContext) {
        try {
            val context = cordova.context ?: run {
                callback.error("Cannot continue without a context.")
                return
            }

            val packageName = Uri.fromParts("package", context.packageName, null)
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageName)
                    .addCategory(Intent.CATEGORY_DEFAULT)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            )

            callback.void()
        } catch (e: Exception) {
            callback.error("Unable to open the app settings.")
        }
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

    private fun determinePermissionStatus(context: Context, permission: PermissionGroup): PermissionStatus {
        if (permission == PermissionGroup.BLUETOOTH) {
            return checkBluetoothPermissionStatus(context)
        }

        if (permission == PermissionGroup.BLUETOOTH_SCAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return checkBluetoothPermissionStatus(context)
        }

        val manifestPermissions = getManifestPermissions(context, permission)

        // If no permissions were found there's an issue and the permission is not set in the Android Manifest.
        if (manifestPermissions.isEmpty()) {
            NotificareLogger.warning("No permissions found in the manifest for $permission")
            return PermissionStatus.DENIED
        }

        val granted = manifestPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        return if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED
    }

    private fun getManifestPermissions(context: Context, permission: PermissionGroup): List<String> {
        val permissions = mutableListOf<String>()

        when (permission) {
            PermissionGroup.LOCATION_WHEN_IN_USE -> {
                if (hasPermissionInManifest(context, Manifest.permission.ACCESS_COARSE_LOCATION))
                    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

                if (hasPermissionInManifest(context, Manifest.permission.ACCESS_FINE_LOCATION))
                    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            PermissionGroup.LOCATION_ALWAYS -> {
                // Note that the LOCATION_ALWAYS will deliberately request LOCATION_WHEN_IN_USE
                // case on pre Android Q devices. The ACCESS_BACKGROUND_LOCATION permission was only
                // introduced in Android Q, before it should be treated as the ACCESS_COARSE_LOCATION or
                // ACCESS_FINE_LOCATION.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (hasPermissionInManifest(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    permissions.addAll(getManifestPermissions(context, PermissionGroup.LOCATION_WHEN_IN_USE))
                }
            }
            PermissionGroup.BLUETOOTH_SCAN -> {
                // The BLUETOOTH_SCAN permission is introduced in Android S, meaning we should
                // not handle permissions on pre Android S devices.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (hasPermissionInManifest(context, Manifest.permission.BLUETOOTH_SCAN))
                        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
                } else {
                    permissions.addAll(getManifestPermissions(context, PermissionGroup.BLUETOOTH))
                }
            }
            PermissionGroup.BLUETOOTH -> {
                if (hasPermissionInManifest(context, Manifest.permission.BLUETOOTH))
                    permissions.add(Manifest.permission.BLUETOOTH)
            }
        }

        return permissions
    }

    private fun hasPermissionInManifest(context: Context, permission: String): Boolean {
        try {
            val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            }
            if (info == null) {
                Log.d(TAG, "Unable to get Package info, will not be able to determine permissions to request.")
                return false
            }

            for (r in info.requestedPermissions) {
                if (r == permission) {
                    return true
                }
            }
        } catch (ex: Exception) {
            Log.d(TAG, "Unable to check manifest for permission: ", ex)
        }

        return false
    }

    private fun checkBluetoothPermissionStatus(context: Context): PermissionStatus {
        val manifestPermissions = getManifestPermissions(context, PermissionGroup.BLUETOOTH)

        if (manifestPermissions.isEmpty()) {
            NotificareLogger.warning("Bluetooth permission missing in the manifest.")
            return PermissionStatus.DENIED
        }

        return PermissionStatus.GRANTED
    }

    internal enum class PermissionGroup {
        LOCATION_WHEN_IN_USE,
        LOCATION_ALWAYS,
        BLUETOOTH_SCAN,
        BLUETOOTH;

        internal val rawValue: String
            get() = when (this) {
                LOCATION_WHEN_IN_USE -> "location_when_in_use"
                LOCATION_ALWAYS -> "location_always"
                BLUETOOTH_SCAN -> "bluetooth_scan"
                BLUETOOTH -> "bluetooth"
            }

        internal companion object {
            internal fun parse(permission: String): PermissionGroup? {
                values().forEach {
                    if (it.rawValue == permission) return it
                }

                return null
            }
        }
    }

    internal enum class PermissionStatus {
        DENIED,
        GRANTED,
        PERMANENTLY_DENIED;

        internal val rawValue: String
            get() = when (this) {
                DENIED -> "denied"
                GRANTED -> "granted"
                PERMANENTLY_DENIED -> "permanently_denied"
            }

        internal companion object {
            internal fun parse(status: String): PermissionStatus? {
                values().forEach {
                    if (it.rawValue == status) return it
                }

                return null
            }
        }
    }
}

private fun CallbackContext.void() {
    sendPluginResult(PluginResult(PluginResult.Status.OK, null as String?))
}

private fun CallbackContext.success(b: Boolean) {
    sendPluginResult(PluginResult(PluginResult.Status.OK, b))
}
