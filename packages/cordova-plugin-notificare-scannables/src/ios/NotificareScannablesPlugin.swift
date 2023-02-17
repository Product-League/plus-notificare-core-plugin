import NotificareKit
import NotificareScannablesKit

@objc(NotificareScannablesPlugin)
class NotificareScannablesPlugin : CDVPlugin {

    private var rootViewController: UIViewController? {
        get {
            UIApplication.shared.delegate?.window??.rootViewController
        }
    }

    override func pluginInitialize() {
        super.pluginInitialize()

        Notificare.shared.scannables().delegate = self
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificareScannablesPluginEventBroker.startListening(settings: commandDelegate.settings) { event in
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

    // MARK: - Notificare Scannables

    @objc func canStartNfcScannableSession(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.scannables().canStartNfcScannableSession)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func startScannableSession(_ command: CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            guard let rootViewController = self.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot start a scannable session with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)

                return
            }

            Notificare.shared.scannables().startScannableSession(controller: rootViewController)

            let result = CDVPluginResult(status: .ok)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func startNfcScannableSession(_ command: CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            Notificare.shared.scannables().startNfcScannableSession()

            let result = CDVPluginResult(status: .ok)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func startQrCodeScannableSession(_ command: CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            guard let rootViewController = self.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot start a scannable session with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)

                return
            }

            Notificare.shared.scannables().startQrCodeScannableSession(controller: rootViewController, modal: true)

            let result = CDVPluginResult(status: .ok)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func fetch(_ command: CDVInvokedUrlCommand) {
        let tag = command.argument(at: 0) as! String

        Notificare.shared.scannables().fetch(tag: tag) { result in
            switch result {
            case let .success(scannable):
                do {
                    let json = try scannable.toJson()

                    let result = CDVPluginResult(status: .ok, messageAs: json)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                } catch {
                    let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            case let .failure(error):
                let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            }
        }
    }
}

extension NotificareScannablesPlugin: NotificareScannablesDelegate {
    func notificare(_ notificareScannables: NotificareScannables, didDetectScannable scannable: NotificareScannable) {
        do {
            NotificareScannablesPluginEventBroker.dispatchEvent(
                name: "scannable_detected",
                payload: try scannable.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the scannable_detected event.", error: error)
        }
    }

    func notificare(_ notificareScannables: NotificareScannables, didInvalidateScannerSession error: Error) {
        NotificareScannablesPluginEventBroker.dispatchEvent(
            name: "scannable_session_failed",
            payload: error.localizedDescription
        )
    }
}
