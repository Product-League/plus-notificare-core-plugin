import NotificareKit
import NotificareGeoKit

@objc(NotificareGeoPlugin)
class NotificareGeoPlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        Notificare.shared.geo().delegate = self
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificareGeoPluginEventBroker.startListening { event in
            var payload: [String: Any] = [
                "name": event.name,
            ]

            if let data = event.payload {
                payload["data"] = data
            }

            let result = CDVPluginResult(status: .ok, messageAs: payload)
            result!.keepCallback = true

            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    // MARK: - Notificare Geo

    @objc func hasLocationServicesEnabled(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.geo().hasLocationServicesEnabled)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func hasBluetoothEnabled(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.geo().hasBluetoothEnabled)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func enableLocationUpdates(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.geo().enableLocationUpdates()

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func disableLocationUpdates(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.geo().disableLocationUpdates()

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }
}

extension NotificareGeoPlugin: NotificareGeoDelegate {
    func notificare(_ notificareGeo: NotificareGeo, didUpdateLocations locations: [NotificareLocation]) {
        guard let location = locations.first else { return }

        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "location_updated",
                payload: try location.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the location_updated event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didEnter region: NotificareRegion) {
        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "region_entered",
                payload: try region.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the region_entered event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didExit region: NotificareRegion) {
        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "region_exited",
                payload: try region.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the region_exited event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didEnter beacon: NotificareBeacon) {
        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "beacon_entered",
                payload: try beacon.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the beacon_entered event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didExit beacon: NotificareBeacon) {
        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "beacon_exited",
                payload: try beacon.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the beacon_exited event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didRange beacons: [NotificareBeacon], in region: NotificareRegion) {
        do {
            let payload: [String: Any] = [
                "region": try region.toJson(),
                "beacons": try beacons.map { try $0.toJson() },
            ]

            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "beacons_ranged",
                payload: payload
            )
        } catch {
            NotificareLogger.error("Failed to emit the beacons_ranged event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didVisit visit: NotificareVisit) {
        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "visit",
                payload: try visit.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the visit event.", error: error)
        }
    }

    func notificare(_ notificareGeo: NotificareGeo, didUpdateHeading heading: NotificareHeading) {
        do {
            NotificareGeoPluginEventBroker.dispatchEvent(
                name: "heading_updated",
                payload: try heading.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the heading_updated event.", error: error)
        }
    }
}
