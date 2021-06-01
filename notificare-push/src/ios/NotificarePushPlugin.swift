import NotificareKit
import NotificarePushKit

@objc(NotificarePushPlugin)
class NotificarePushPlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        NotificarePush.shared.delegate = self
    }

    @objc
    func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificarePushPluginEventManager.startListening { event in
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

    // MARK: - Notificare Push

    @objc
    func setAuthorizationOptions(_ command: CDVInvokedUrlCommand) {
        let options = command.argument(at: 0) as! [String]
        var authorizationOptions: UNAuthorizationOptions = []

        options.forEach { option in
            if option == "alert" {
                authorizationOptions = [authorizationOptions, .alert]
            }

            if option == "badge" {
                authorizationOptions = [authorizationOptions, .badge]
            }

            if option == "sound" {
                authorizationOptions = [authorizationOptions, .sound]
            }

            if option == "carPlay" {
                authorizationOptions = [authorizationOptions, .carPlay]
            }

            if #available(iOS 12.0, *) {
                if option == "providesAppNotificationSettings" {
                    authorizationOptions = [authorizationOptions, .providesAppNotificationSettings]
                }

                if option == "provisional" {
                    authorizationOptions = [authorizationOptions, .provisional]
                }

                if option == "criticalAlert" {
                    authorizationOptions = [authorizationOptions, .criticalAlert]
                }
            }

            if #available(iOS 13.0, *) {
                if option == "announcement" {
                    authorizationOptions = [authorizationOptions, .announcement]
                }
            }
        }

        NotificarePush.shared.authorizationOptions = authorizationOptions

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func setCategoryOptions(_ command: CDVInvokedUrlCommand) {
        let options = command.argument(at: 0) as! [String]
        var categoryOptions: UNNotificationCategoryOptions = []

        options.forEach { option in
            if option == "customDismissAction" {
                categoryOptions = [categoryOptions, .customDismissAction]
            }

            if option == "allowInCarPlay" {
                categoryOptions = [categoryOptions, .allowInCarPlay]
            }

            if #available(iOS 11.0, *) {
                if option == "hiddenPreviewsShowTitle" {
                    categoryOptions = [categoryOptions, .hiddenPreviewsShowTitle]
                }

                if option == "hiddenPreviewsShowSubtitle" {
                    categoryOptions = [categoryOptions, .hiddenPreviewsShowSubtitle]
                }
            }

            if #available(iOS 13.0, *) {
                if option == "allowAnnouncement" {
                    categoryOptions = [categoryOptions, .allowAnnouncement]
                }
            }
        }

        NotificarePush.shared.categoryOptions = categoryOptions

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func setPresentationOptions(_ command: CDVInvokedUrlCommand) {
        let options = command.argument(at: 0) as! [String]
        var presentationOptions: UNNotificationPresentationOptions = []

        options.forEach { option in
            if #available(iOS 14.0, *) {
                if option == "banner" || option == "alert" {
                    presentationOptions = [presentationOptions, .banner]
                }

                if option == "list" {
                    presentationOptions = [presentationOptions, .list]
                }
            } else {
                if option == "alert" {
                    presentationOptions = [presentationOptions, .alert]
                }
            }

            if option == "badge" {
                presentationOptions = [presentationOptions, .badge]
            }

            if option == "sound" {
                presentationOptions = [presentationOptions, .sound]
            }
        }

        NotificarePush.shared.presentationOptions = presentationOptions

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func isRemoteNotificationsEnabled(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: NotificarePush.shared.isRemoteNotificationsEnabled)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func isAllowedUI(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: NotificarePush.shared.allowedUI)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func enableRemoteNotifications(_ command: CDVInvokedUrlCommand) {
        NotificarePush.shared.enableRemoteNotifications { result in
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

    @objc
    func disableRemoteNotifications(_ command: CDVInvokedUrlCommand) {
        NotificarePush.shared.disableRemoteNotifications()

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }
}

extension NotificarePushPlugin: NotificarePushDelegate {
    func notificare(_ notificarePush: NotificarePush, didReceiveNotification notification: NotificareNotification) {
        do {
            NotificarePushPluginEventManager.dispatchEvent(
                name: "notification_received",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_received event.\n\(error)")
        }
    }

    func notificare(_ notificarePush: NotificarePush, didReceiveSystemNotification notification: NotificareSystemNotification) {
        do {
            NotificarePushPluginEventManager.dispatchEvent(
                name: "system_notification_received",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the system_notification_received event.\n\(error)")
        }
    }

    func notificare(_ notificarePush: NotificarePush, didReceiveUnknownNotification userInfo: [AnyHashable : Any]) {
        NotificarePushPluginEventManager.dispatchEvent(
            name: "unknown_notification_received",
            payload: userInfo
        )
    }

    func notificare(_ notificarePush: NotificarePush, didOpenNotification notification: NotificareNotification) {
        do {
            NotificarePushPluginEventManager.dispatchEvent(
                name: "notification_opened",
                payload: try notification.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_opened event.\n\(error)")
        }
    }

    func notificare(_ notificarePush: NotificarePush, didOpenAction action: NotificareNotification.Action, for notification: NotificareNotification) {
        do {
            let payload = [
                "notification": try notification.toJson(),
                "action": try action.toJson(),
            ]

            NotificarePushPluginEventManager.dispatchEvent(
                name: "notification_action_opened",
                payload: payload
            )
        } catch {
            NotificareLogger.error("Failed to emit the notification_action_opened event.\n\(error)")
        }
    }

//    func notificare(_ notificarePush: NotificarePush, didReceiveUnknownAction action: String, for notification: [AnyHashable : Any], responseText: String?) {
//
//    }

    func notificare(_ notificarePush: NotificarePush, didChangeNotificationSettings granted: Bool) {
        NotificarePushPluginEventManager.dispatchEvent(
            name: "notification_settings_changed",
            payload: granted
        )
    }

    func notificare(_ notificarePush: NotificarePush, shouldOpenSettings notification: NotificareNotification?) {
        do {
            NotificarePushPluginEventManager.dispatchEvent(
                name: "should_open_notification_settings",
                payload: try notification?.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the should_open_notification_settings event.\n\(error)")
        }
    }

    func notificare(_ notificarePush: NotificarePush, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        NotificarePushPluginEventManager.dispatchEvent(
            name: "failed_to_register_for_remote_notifications",
            payload: error.localizedDescription
        )
    }
}
