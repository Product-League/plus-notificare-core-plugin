import NotificareKit
import NotificareMonetizeKit

@objc(NotificareMonetizePlugin)
class NotificareMonetizePlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        Notificare.shared.monetize().delegate = self
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificareMonetizePluginEventBroker.startListening { event in
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

    // MARK: - Notificare Monetize

    @objc func getProducts(_ command: CDVInvokedUrlCommand) {
        do {
            let products = try Notificare.shared.monetize().products.map { try $0.toJson() }

            let result = CDVPluginResult(status: .ok, messageAs: products)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func getPurchases(_ command: CDVInvokedUrlCommand) {
        do {
            let purchases = try Notificare.shared.monetize().purchases.map { try $0.toJson() }

            let result = CDVPluginResult(status: .ok, messageAs: purchases)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func refresh(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.monetize().refresh { result in
            switch result {
            case .success:
                let result = CDVPluginResult(status: .ok)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            case let .failure(error):
                let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            }
        }
    }

    @objc func startPurchaseFlow(_ command: CDVInvokedUrlCommand) {
        let json = command.argument(at: 0) as! [String: Any]
        let product: NotificareProduct

        do {
            product = try NotificareProduct.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)

            return
        }
        
        Notificare.shared.monetize().startPurchaseFlow(for: product)
    }
}

extension NotificareMonetizePlugin: NotificareMonetizeDelegate {
    func notificare(_ notificareMonetize: NotificareMonetize, didUpdateProducts products: [NotificareProduct]) {
        do {
            NotificareMonetizePluginEventBroker.dispatchEvent(
                name: "products_updated",
                payload: try products.map { try $0.toJson() }
            )
        } catch {
            NotificareLogger.error("Failed to emit the products_updated event.", error: error)
        }
    }
    
    func notificare(_ notificareMonetize: NotificareMonetize, didUpdatePurchases purchases: [NotificarePurchase]) {
        do {
            NotificareMonetizePluginEventBroker.dispatchEvent(
                name: "purchases_updated",
                payload: try purchases.map { try $0.toJson() }
            )
        } catch {
            NotificareLogger.error("Failed to emit the purchases_updated event.", error: error)
        }
    }
    
    func notificare(_ notificareMonetize: NotificareMonetize, didFinishPurchase purchase: NotificarePurchase) {
        do {
            NotificareMonetizePluginEventBroker.dispatchEvent(
                name: "purchase_finished",
                payload: try purchase.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the purchase_finished event.", error: error)
        }
    }
    
    func notificare(_ notificareMonetize: NotificareMonetize, didRestorePurchase purchase: NotificarePurchase) {
        do {
            NotificareMonetizePluginEventBroker.dispatchEvent(
                name: "purchase_restored",
                payload: try purchase.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the purchase_restored event.", error: error)
        }
    }
    
    func notificareDidCancelPurchase(_ notificareMonetize: NotificareMonetize) {
        NotificareMonetizePluginEventBroker.dispatchEvent(name: "purchase_canceled", payload: nil)
    }
    
    func notificare(_ notificareMonetize: NotificareMonetize, didFailToPurchase error: Error) {
        NotificareMonetizePluginEventBroker.dispatchEvent(
            name: "purchase_failed",
            payload: ["errorMessage": error.localizedDescription]
        )
    }
}
