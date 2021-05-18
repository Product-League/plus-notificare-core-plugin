import NotificareKit
import NotificarePushUIKit

@objc(NotificarePushUIPlugin)
class NotificarePushUIPlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        NotificarePushUI.shared.delegate = self
    }

    @objc
    func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificarePushUIPluginEventManager.startListening { event in
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

    // MARK: - Notificare Push UI

    @objc
    func presentNotification(_ command: CDVInvokedUrlCommand) {
        let notification: NotificareNotification
        
        do {
            notification = try NotificareNotification.fromJson(json: command.argument(at: 0) as! [String: Any])
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }
        
        onMainThread {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot present a notification action with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)
                
                return
            }
            
            if notification.requiresViewController {
                let navigationController = self.createNavigationController()
                rootViewController.present(navigationController, animated: true) {
                    NotificarePushUI.shared.presentNotification(notification, in: navigationController)
                    
                    let result = CDVPluginResult(status: .ok)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            } else {
                NotificarePushUI.shared.presentNotification(notification, in: rootViewController)
                
                let result = CDVPluginResult(status: .ok)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            }
        }
    }
    
    @objc
    func presentAction(_ command: CDVInvokedUrlCommand) {
        let notification: NotificareNotification
        let action: NotificareNotification.Action
        
        do {
            notification = try NotificareNotification.fromJson(json: command.argument(at: 0) as! [String: Any])
            action = try NotificareNotification.Action.fromJson(json: command.argument(at: 1) as! [String: Any])
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }
        
        onMainThread {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot present a notification action with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)
                
                return
            }
            
            NotificarePushUI.shared.presentAction(action, for: notification, in: rootViewController)
            
            let result = CDVPluginResult(status: .ok)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }
    
    private func createNavigationController() -> UINavigationController {
        let navigationController = UINavigationController()
        let theme = Notificare.shared.options?.theme(for: navigationController)
        
        if let colorStr = theme?.backgroundColor {
            navigationController.view.backgroundColor = UIColor(hexString: colorStr)
        } else {
            navigationController.view.backgroundColor = .white
        }
        
        let closeButton: UIBarButtonItem
        if let closeButtonImage = NotificareLocalizable.image(resource: .close) {
            closeButton = UIBarButtonItem(image: closeButtonImage,
                                          style: .plain,
                                          target: self,
                                          action: #selector(onCloseClicked))
        } else {
            closeButton = UIBarButtonItem(title: NotificareLocalizable.string(resource: .closeButton),
                                          style: .plain,
                                          target: self,
                                          action: #selector(onCloseClicked))
        }
        
        if let colorStr = theme?.actionButtonTextColor {
            closeButton.tintColor = UIColor(hexString: colorStr)
        }
        
        navigationController.navigationItem.leftBarButtonItem = closeButton
        
        return navigationController
    }

    @objc private func onCloseClicked() {
        guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
            return
        }
        
        rootViewController.dismiss(animated: true, completion: nil)
    }
}

extension NotificarePushUIPlugin: NotificarePushUIDelegate {
//    func notificare(_ notificarePush: NotificarePush, didReceiveNotification notification: NotificareNotification) {
//        do {
//            NotificarePushUIPluginEventManager.dispatchEvent(
//                name: "notification_received",
//                payload: try notification.toJson()
//            )
//        } catch {
//            NotificareLogger.error("Failed to emit the notification_received event.\n\(error)")
//        }
//    }
}

extension NotificareNotification {
    var requiresViewController: Bool {
        get {
            if let type = NotificareNotification.NotificationType.init(rawValue: type) {
                switch type {
                case .alert, .none, .passbook, .rate, .urlScheme:
                    return false
                default:
                    break
                }
            }
            
            return true
        }
    }
}

fileprivate func onMainThread(_ action: @escaping () -> Void) {
    DispatchQueue.main.async {
        action()
    }
}
