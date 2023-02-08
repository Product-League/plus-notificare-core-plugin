import NotificareKit
import NotificareUserInboxKit

@objc(NotificareUserInboxPlugin)
class NotificareUserInboxPlugin : CDVPlugin {

    // MARK: - Notificare User Inbox

    @objc func parseResponseFromJson(_ command: CDVInvokedUrlCommand) {
        do {
            let json = command.argument(at: 0) as! [String: Any]
            let response = try Notificare.shared.userInbox().parseResponse(json: json)

            let result = CDVPluginResult(status: .ok, messageAs: try response.toJson())
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func parseResponseFromString(_ command: CDVInvokedUrlCommand) {
        do {
            let json = command.argument(at: 0) as! String
            let response = try Notificare.shared.userInbox().parseResponse(string: json)

            let result = CDVPluginResult(status: .ok, messageAs: try response.toJson())
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func open(_ command: CDVInvokedUrlCommand) {
        let item: NotificareUserInboxItem

        do {
            let json = command.argument(at: 0) as! [String: Any]
            item = try NotificareUserInboxItem.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)

            return
        }

        Notificare.shared.userInbox().open(item) { result in
            switch result {
            case let .success(notification):
                do {
                    let json = try notification.toJson()

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

    @objc func markAsRead(_ command: CDVInvokedUrlCommand) {
        let item: NotificareUserInboxItem

        do {
            let json = command.argument(at: 0) as! [String: Any]
            item = try NotificareUserInboxItem.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)

            return
        }

        Notificare.shared.userInbox().markAsRead(item) { result in
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

    @objc func remove(_ command: CDVInvokedUrlCommand) {
        let item: NotificareUserInboxItem

        do {
            let json = command.argument(at: 0) as! [String: Any]
            item = try NotificareUserInboxItem.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)

            return
        }

        Notificare.shared.userInbox().remove(item) { result in
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
}

