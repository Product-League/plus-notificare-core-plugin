import NotificareKit
import NotificareLoyaltyKit

@objc(NotificareLoyaltyPlugin)
class NotificareLoyaltyPlugin : CDVPlugin {

    private var rootViewController: UIViewController? {
        get {
            UIApplication.shared.delegate?.window??.rootViewController
        }
    }

    // MARK: - Notificare Loyalty

    @objc func fetchPassBySerial(_ command: CDVInvokedUrlCommand) {
        let serial = command.argument(at: 0) as! String

        Notificare.shared.loyalty().fetchPass(serial: serial) { result in
            switch result {
            case let .success(pass):
                do {
                    let json = try pass.toJson()

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

    @objc func fetchPassByBarcode(_ command: CDVInvokedUrlCommand) {
        let barcode = command.argument(at: 0) as! String

        Notificare.shared.loyalty().fetchPass(barcode: barcode) { result in
            switch result {
            case let .success(pass):
                do {
                    let json = try pass.toJson()

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

    @objc func present(_ command: CDVInvokedUrlCommand) {
        let json = command.argument(at: 0) as! [String: Any]
        let pass: NotificarePass

        do {
            pass = try NotificarePass.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)

            return
        }

        DispatchQueue.main.async {
            guard let rootViewController = self.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot present a pass with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)

                return
            }

            Notificare.shared.loyalty().present(pass: pass, in: rootViewController)

            let result = CDVPluginResult(status: .ok)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }
}
