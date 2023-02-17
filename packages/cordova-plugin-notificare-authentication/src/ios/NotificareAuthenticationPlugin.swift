import NotificareKit
import NotificareAuthenticationKit

@objc(NotificareAuthenticationPlugin)
class NotificareAuthenticationPlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        _ = NotificareSwizzler.addInterceptor(self)
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificareAuthenticationPluginEventBroker.startListening(settings: commandDelegate.settings) { event in
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

    // MARK: - Notificare Authentication

    @objc func isLoggedIn(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.authentication().isLoggedIn)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func login(_ command: CDVInvokedUrlCommand) {
        let email = command.argument(at: 0) as! String
        let password = command.argument(at: 1) as! String

        Notificare.shared.authentication().login(email: email, password: password) { result in
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

    @objc func logout(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.authentication().logout { result in
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

    @objc func fetchUserDetails(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.authentication().fetchUserDetails { result in
            switch result {
            case let .success(user):
                do {
                    let json = try user.toJson()

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

    @objc func changePassword(_ command: CDVInvokedUrlCommand) {
        let password = command.argument(at: 0) as! String

        Notificare.shared.authentication().changePassword(password) { result in
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

    @objc func generatePushEmailAddress(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.authentication().generatePushEmailAddress { result in
            switch result {
            case let .success(user):
                do {
                    let json = try user.toJson()

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

    @objc func createAccount(_ command: CDVInvokedUrlCommand) {
        let email = command.argument(at: 0) as! String
        let password = command.argument(at: 1) as! String
        let name = command.argument(at: 2) as! String?

        Notificare.shared.authentication().createAccount(email: email, password: password, name: name) { result in
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

    @objc func validateUser(_ command: CDVInvokedUrlCommand) {
        let token = command.argument(at: 0) as! String

        Notificare.shared.authentication().validateUser(token: token) { result in
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

    @objc func sendPasswordReset(_ command: CDVInvokedUrlCommand) {
        let email = command.argument(at: 0) as! String

        Notificare.shared.authentication().sendPasswordReset(email: email) { result in
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

    @objc func resetPassword(_ command: CDVInvokedUrlCommand) {
        let password = command.argument(at: 0) as! String
        let token = command.argument(at: 1) as! String

        Notificare.shared.authentication().resetPassword(password, token: token) { result in
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

    @objc func fetchUserPreferences(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.authentication().fetchUserPreferences { result in
            switch result {
            case let .success(preferences):
                do {
                    let json = try preferences.map { try $0.toJson() }

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

    @objc func fetchUserSegments(_ command: CDVInvokedUrlCommand) {
        Notificare.shared.authentication().fetchUserSegments { result in
            switch result {
            case let .success(segments):
                do {
                    let json = try segments.map { try $0.toJson() }

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

    @objc func addUserSegment(_ command: CDVInvokedUrlCommand) {
        let segment: NotificareUserSegment

        do {
            let json = command.argument(at: 0) as! [String: Any]
            segment = try NotificareUserSegment.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }

        Notificare.shared.authentication().addUserSegment(segment) { result in
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

    @objc func removeUserSegment(_ command: CDVInvokedUrlCommand) {
        let segment: NotificareUserSegment

        do {
            let json = command.argument(at: 0) as! [String: Any]
            segment = try NotificareUserSegment.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }

        Notificare.shared.authentication().removeUserSegment(segment) { result in
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


    @objc func addUserSegmentToPreference(_ command: CDVInvokedUrlCommand) {
        let preference: NotificareUserPreference

        do {
            let json = command.argument(at: 0) as! [String: Any]
            preference = try NotificareUserPreference.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }

        do {
            let json = command.argument(at: 1) as! [String: Any]
            let segment = try NotificareUserSegment.fromJson(json: json)
            Notificare.shared.authentication().addUserSegmentToPreference(segment, to: preference) { result in
                switch result {
                case .success:
                    let result = CDVPluginResult(status: .ok)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                case let .failure(error):
                    let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            }

            return
        } catch {
            NotificareLogger.debug("Failed to parse segment data into NotificareUserSegment.", error: error)
        }

        do {
            let json = command.argument(at: 1) as! [String: Any]
            let option = try NotificareUserPreference.Option.fromJson(json: json)
            Notificare.shared.authentication().addUserSegmentToPreference(option: option, to: preference) { result in
                switch result {
                case .success:
                    let result = CDVPluginResult(status: .ok)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                case let .failure(error):
                    let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            }

            return
        } catch {
            NotificareLogger.debug("Failed to parse segment data into NotificareUserPreference.Option.", error: error)
        }

        let result = CDVPluginResult(status: .error, messageAs: "To execute this method, you must provide either a NotificareUserSegment or a NotificarePreferenceOption.")
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func removeUserSegmentFromPreference(_ command: CDVInvokedUrlCommand) {
        let preference: NotificareUserPreference

        do {
            let json = command.argument(at: 0) as! [String: Any]
            preference = try NotificareUserPreference.fromJson(json: json)
        } catch {
            let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
            self.commandDelegate!.send(result, callbackId: command.callbackId)
            return
        }

        do {
            let json = command.argument(at: 1) as! [String: Any]
            let segment = try NotificareUserSegment.fromJson(json: json)
            Notificare.shared.authentication().removeUserSegmentFromPreference(segment, from: preference) { result in
                switch result {
                case .success:
                    let result = CDVPluginResult(status: .ok)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                case let .failure(error):
                    let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            }

            return
        } catch {
            NotificareLogger.debug("Failed to parse segment data into NotificareUserSegment.", error: error)
        }

        do {
            let json = command.argument(at: 1) as! [String: Any]
            let option = try NotificareUserPreference.Option.fromJson(json: json)
            Notificare.shared.authentication().removeUserSegmentFromPreference(option: option, from: preference) { result in
                switch result {
                case .success:
                    let result = CDVPluginResult(status: .ok)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                case let .failure(error):
                    let result = CDVPluginResult(status: .error, messageAs: error.localizedDescription)
                    self.commandDelegate!.send(result, callbackId: command.callbackId)
                }
            }

            return
        } catch {
            NotificareLogger.debug("Failed to parse segment data into NotificareUserPreference.Option.", error: error)
        }

        let result = CDVPluginResult(status: .error, messageAs: "To execute this method, you must provide either a NotificareUserSegment or a NotificarePreferenceOption.")
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }
}

extension NotificareAuthenticationPlugin: NotificareAppDelegateInterceptor {
    func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        guard let url = userActivity.webpageURL else {
            return false
        }

        if let token = Notificare.shared.authentication().parsePasswordResetToken(url) {
            NotificareAuthenticationPluginEventBroker.dispatchEvent(name: "password_reset_token_received", payload: token)
            return true
        }

        if let token = Notificare.shared.authentication().parseValidateUserToken(url) {
            NotificareAuthenticationPluginEventBroker.dispatchEvent(name: "validate_user_token_received", payload: token)
            return false
        }

        return false
    }
}
