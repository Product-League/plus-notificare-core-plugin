import NotificareKit
import NotificarePushUIKit

@objc(NotificarePushUIPlugin)
class NotificarePushUIPlugin : CDVPlugin {

    private var rootViewController: UIViewController? {
        get {
            UIApplication.shared.delegate?.window??.rootViewController
        }
    }

    override func pluginInitialize() {
        super.pluginInitialize()

        Notificare.shared.pushUI().delegate = self
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificarePushUIPluginEventBroker.startListening { event in
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

    @objc func presentNotification(_ command: CDVInvokedUrlCommand) {
        let notification: NotificareNotification

        do {
            notification = try NotificareNotification.fromJson(json: command.argument(at: 0) as! [String: Any])
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }

        onMainThread {
            guard let rootViewController = self.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot present a notification action with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)

                return
            }

            if notification.requiresViewController {
                let navigationController = self.createNavigationController()
                rootViewController.present(navigationController, animated: true) {
                    Notificare.shared.pushUI().presentNotification(notification, in: navigationController)

                    let result = CDVPluginResult(status: .ok)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            } else {
                Notificare.shared.pushUI().presentNotification(notification, in: rootViewController)

                let result = CDVPluginResult(status: .ok)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            }
        }
    }

    @objc func presentAction(_ command: CDVInvokedUrlCommand) {
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
            guard let rootViewController = self.rootViewController else {
                let result = CDVPluginResult(status: .error, messageAs: "Cannot present a notification action with a nil root view controller.")
                self.commandDelegate!.send(result, callbackId: command.callbackId)

                return
            }

            Notificare.shared.pushUI().presentAction(action, for: notification, in: rootViewController)

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
            if #available(iOS 13.0, *) {
                navigationController.view.backgroundColor = .systemBackground
            } else {
                navigationController.view.backgroundColor = .white
            }
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
    func notificare(_ notificarePushUI: NotificarePushUI, willPresentNotification notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "notification_will_present",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_will_present event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didPresentNotification notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "notification_presented",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_presented event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didFinishPresentingNotification notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "notification_finished_presenting",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_finished_presenting event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didFailToPresentNotification notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "notification_failed_to_present",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_failed_to_present event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didClickURL url: URL, in notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "notification_url_clicked",
                payload: [
                    "notification": try notification.toJson(),
                    "url": url.absoluteString,
                ]
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_url_clicked event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, willExecuteAction action: NotificareNotification.Action, for notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "action_will_execute",
                payload: [
                    "notification": try notification.toJson(),
                    "action": try action.toJson(),
                ]
            )
        } catch {
            NotificareLogger.error("Failed to emit the action_will_execute event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didExecuteAction action: NotificareNotification.Action, for notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "action_executed",
                payload: [
                    "notification": try notification.toJson(),
                    "action": try action.toJson(),
                ]
            )
        } catch {
            NotificareLogger.error("Failed to emit the action_executed event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didNotExecuteAction action: NotificareNotification.Action, for notification: NotificareNotification) {
        do {
            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "action_not_executed",
                payload: [
                    "notification": try notification.toJson(),
                    "action": try action.toJson(),
                ]
            )
        } catch {
            NotificareLogger.error("Failed to emit the action_not_executed event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, didFailToExecuteAction action: NotificareNotification.Action, for notification: NotificareNotification, error: Error?) {
        do {
            var payload: [String: Any] = [
                "notification": try notification.toJson(),
                "action": try action.toJson(),
            ]

            if let error = error {
                payload["error"] = error.localizedDescription
            }

            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "action_failed_to_execute",
                payload: payload
            )
        } catch {
            NotificareLogger.error("Failed to emit the action_failed_to_execute event.", error: error)
        }
    }

    func notificare(_ notificarePushUI: NotificarePushUI, shouldPerformSelectorWithURL url: URL, in action: NotificareNotification.Action, for notification: NotificareNotification) {
        do {
            let payload: [String : Any] = [
                "notification": try notification.toJson(),
                "action": try action.toJson(),
                "url": url.absoluteString,
            ]

            NotificarePushUIPluginEventBroker.dispatchEvent(
                name: "custom_action_received",
                payload: payload
            )
        } catch {
            NotificareLogger.error("Failed to emit the custom_action_received event.", error: error)
        }
    }
}

private func onMainThread(_ action: @escaping () -> Void) {
    DispatchQueue.main.async {
        action()
    }
}
