import NotificareKit

@objc(NotificarePlugin)
class NotificarePlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        Notificare.shared.delegate = self
    }

    override func handleOpenURL(_ notification: Notification!) {
        guard let url = notification.object as? URL else {
            return
        }

        if Notificare.shared.handleTestDeviceUrl(url) {
            return
        }

        if Notificare.shared.handleDynamicLinkUrl(url) {
            return
        }

        NotificarePluginEventBroker.dispatchEvent(
            name: "url_opened",
            payload: url.absoluteString
        )
    }

    @objc func handleUserActivity(_ userActivity: NSUserActivity) -> Bool {
        guard let url = userActivity.webpageURL else {
            return false
        }

        if Notificare.shared.handleTestDeviceUrl(url) {
            return true
        }

        return Notificare.shared.handleDynamicLinkUrl(url)
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificarePluginEventBroker.startListening { event in
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

    // MARK: - Notificare

    @objc func isConfigured(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.isConfigured)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func isReady(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.isReady)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }


    @objc func launch(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.launch()

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func unlaunch(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.unlaunch()

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func getApplication(_ command: CDVInvokedUrlCommand) {
        do {
            let json = try Notificare.shared.application?.toJson()

            let result = CDVPluginResult(status: .ok, messageAs: json)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func fetchApplication(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.fetchApplication { result in
            switch result {
            case let .success(application):
                do {
                    let json = try application.toJson()

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

    @objc func fetchNotification(_ command: CDVInvokedUrlCommand) {
        let id = command.argument(at: 0) as! String

        Notificare.shared.fetchNotification(id) { result in
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

    // MARK: - Notificare Device Module

    @objc func getCurrentDevice(_ command: CDVInvokedUrlCommand) {
        do {
            let json = try Notificare.shared.device().currentDevice?.toJson()

            let result = CDVPluginResult(status: .ok, messageAs: json)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
        }
    }

    @objc func register(_ command: CDVInvokedUrlCommand) {
        let userId = command.argument(at: 0) as! String?
        let userName = command.argument(at: 1) as! String?

        Notificare.shared.device().register(userId: userId, userName: userName) { result in
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

    @objc func fetchTags(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.device().fetchTags { result in
            switch result {
            case let .success(tags):
                let result = CDVPluginResult(status: .ok, messageAs: tags)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            case let .failure(error):
                let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            }
        }
    }

    @objc func addTag(_ command: CDVInvokedUrlCommand) {
        let tag = command.argument(at: 0) as! String

        Notificare.shared.device().addTag(tag) { result in
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

    @objc func addTags(_ command: CDVInvokedUrlCommand) {
        let tags = command.argument(at: 0) as! [String]

        Notificare.shared.device().addTags(tags) { result in
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

    @objc func removeTag(_ command: CDVInvokedUrlCommand) {
        let tag = command.argument(at: 0) as! String

        Notificare.shared.device().removeTag(tag) { result in
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

    @objc func removeTags(_ command: CDVInvokedUrlCommand) {
        let tags = command.argument(at: 0) as! [String]

        Notificare.shared.device().removeTags(tags) { result in
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

    @objc func clearTags(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.device().clearTags { result in
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

    @objc func getPreferredLanguage(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.device().preferredLanguage)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func updatePreferredLanguage(_ command: CDVInvokedUrlCommand) {
        let language = command.argument(at: 0) as! String?

        Notificare.shared.device().updatePreferredLanguage(language) { result in
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

    @objc func fetchDoNotDisturb(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.device().fetchDoNotDisturb { result in
            switch result {
            case let .success(dnd):
                do {
                    let json = try dnd?.toJson()

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

    @objc func updateDoNotDisturb(_ command: CDVInvokedUrlCommand) {
        let json = command.argument(at: 0) as! [String: Any]
        let dnd: NotificareDoNotDisturb

        do {
            dnd = try NotificareDoNotDisturb.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }

        Notificare.shared.device().updateDoNotDisturb(dnd) { result in
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

    @objc func clearDoNotDisturb(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.device().clearDoNotDisturb { result in
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

    @objc func fetchUserData(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.device().fetchUserData { result in
            switch result {
            case let .success(userData):
                let result = CDVPluginResult(status: .ok, messageAs: userData)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            case let .failure(error):
                let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                self.commandDelegate!.send(result, callbackId: command.callbackId)
            }
        }
    }

    @objc func updateUserData(_ command: CDVInvokedUrlCommand) {
        let userData = command.argument(at: 0) as! [String: String]

        Notificare.shared.device().updateUserData(userData) { result in
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

    @objc func logCustom(_ command: CDVInvokedUrlCommand) {
        let event = command.argument(at: 0) as! String
        let data = command.argument(at: 1) as? [String: Any]

        Notificare.shared.events().logCustom(event, data: data) { result in
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

extension NotificarePlugin: NotificareDelegate {
    func notificare(_ notificare: Notificare, didRegisterDevice device: NotificareDevice) {
        do {
            NotificarePluginEventBroker.dispatchEvent(
                name: "device_registered",
                payload: try device.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the ready event.", error: error)
        }
    }

    func notificare(_ notificare: Notificare, onReady application: NotificareApplication) {
        do {
            NotificarePluginEventBroker.dispatchEvent(
                name: "ready",
                payload: try application.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the ready event.", error: error)
        }
    }

    func notificareDidUnlaunch(_ notificare: Notificare) {
        NotificarePluginEventBroker.dispatchEvent(name: "unlaunched", payload: nil)
    }
}
