import NotificareKit
import NotificareInboxKit

@objc(NotificareInboxPlugin)
class NotificareInboxPlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        NotificareInbox.shared.delegate = self
    }

    @objc
    func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificareInboxPluginEventManager.startListening { event in
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

    // MARK: - Notificare Inbox

    @objc
    func getItems(_ command: CDVInvokedUrlCommand) {
        do {
            let items = try NotificareInbox.shared.items.map { try $0.toJson() }
            
            let result = CDVPluginResult(status: .ok, messageAs: items)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc
    func getBadge(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: NotificareInbox.shared.badge)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func refresh(_ command: CDVInvokedUrlCommand) {
        NotificareInbox.shared.refresh()
        
        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc
    func open(_ command: CDVInvokedUrlCommand) {
        let json = command.argument(at: 0) as! [String: Any]
        let item: NotificareInboxItem
        
        do {
            item = try NotificareInboxItem.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            
            return
        }
        
        NotificareInbox.shared.open(item) { result in
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

    @objc
    func markAsRead(_ command: CDVInvokedUrlCommand) {
        let json = command.argument(at: 0) as! [String: Any]
        let item: NotificareInboxItem
        
        do {
            item = try NotificareInboxItem.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            
            return
        }
        
        NotificareInbox.shared.markAsRead(item) { result in
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
    func markAllAsRead(_ command: CDVInvokedUrlCommand) {
        NotificareInbox.shared.markAllAsRead { result in
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
    func remove(_ command: CDVInvokedUrlCommand) {
        let json = command.argument(at: 0) as! [String: Any]
        let item: NotificareInboxItem
        
        do {
            item = try NotificareInboxItem.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            
            return
        }
        
        NotificareInbox.shared.remove(item) { result in
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
    func clear(_ command: CDVInvokedUrlCommand) {
        NotificareInbox.shared.clear { result in
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

extension NotificareInboxPlugin: NotificareInboxDelegate {
    func notificare(_ notificareInbox: NotificareInbox, didUpdateInbox items: [NotificareInboxItem]) {
        do {
            NotificareInboxPluginEventManager.dispatchEvent(
                name: "inbox_updated",
                payload: try items.map { try $0.toJson() }
            )
        } catch {
            NotificareLogger.error("Failed to emit the inbox_updated event.\n\(error)")
        }
    }
    
    func notificare(_ notificareInbox: NotificareInbox, didUpdateBadge badge: Int) {
        NotificareInboxPluginEventManager.dispatchEvent(
            name: "badge_updated",
            payload: badge
        )
    }
}
