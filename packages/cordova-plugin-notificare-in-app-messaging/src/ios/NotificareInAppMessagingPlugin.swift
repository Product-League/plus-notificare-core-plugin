import NotificareKit
import NotificareInAppMessagingKit

@objc(NotificareInAppMessagingPlugin)
class NotificareInAppMessagingPlugin : CDVPlugin {

    override func pluginInitialize() {
        super.pluginInitialize()

        Notificare.shared.inAppMessaging().delegate = self
    }

    @objc func registerListener(_ command: CDVInvokedUrlCommand) {
        NotificareInAppMessagingPluginEventBroker.startListening(settings: commandDelegate.settings) { event in
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

    // MARK: - Notificare In-App Messaging

    @objc func hasMessagesSuppressed(_ command: CDVInvokedUrlCommand) {
        let result = CDVPluginResult(status: .ok, messageAs: Notificare.shared.inAppMessaging().hasMessagesSuppressed)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }

    @objc func setMessagesSuppressed(_ command: CDVInvokedUrlCommand) {
        let suppressed = command.argument(at: 0) as! Bool
        let evaluateContext = command.argument(at: 1) as? Bool ?? false

        Notificare.shared.inAppMessaging().setMessagesSuppressed(suppressed, evaluateContext: evaluateContext)

        let result = CDVPluginResult(status: .ok)
        self.commandDelegate!.send(result, callbackId: command.callbackId)
    }
}

extension NotificareInAppMessagingPlugin: NotificareInAppMessagingDelegate {
    func notificare(_ notificare: NotificareInAppMessaging, didPresentMessage message: NotificareInAppMessage) {
        do {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent(
                name: "message_presented",
                payload: try message.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the message_presented event.", error: error)
        }
    }

    func notificare(_ notificare: NotificareInAppMessaging, didFinishPresentingMessage message: NotificareInAppMessage) {
        do {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent(
                name: "message_finished_presenting",
                payload: try message.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the message_finished_presenting event.", error: error)
        }
    }

    func notificare(_ notificare: NotificareInAppMessaging, didFailToPresentMessage message: NotificareInAppMessage) {
        do {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent(
                name: "message_failed_to_present",
                payload: try message.toJson()
            )
        } catch {
            NotificareLogger.error("Failed to emit the message_failed_to_present event.", error: error)
        }
    }

    func notificare(_ notificare: NotificareInAppMessaging, didExecuteAction action: NotificareInAppMessage.Action, for message: NotificareInAppMessage) {
        do {
            NotificareInAppMessagingPluginEventBroker.dispatchEvent(
                name: "action_executed",
                payload: [
                    "message": try message.toJson(),
                    "action": try action.toJson(),
                ]
            )
        } catch {
            NotificareLogger.error("Failed to emit the action_executed event.", error: error)
        }
    }

    func notificare(_ notificare: NotificareInAppMessaging, didFailToExecuteAction action: NotificareInAppMessage.Action, for message: NotificareInAppMessage, error: Error?) {
        do {
            var payload: [String: Any] = [
                "message": try message.toJson(),
                "action": try action.toJson(),
            ]

            if let error = error {
                payload["error"] = error.localizedDescription
            }

            NotificareInAppMessagingPluginEventBroker.dispatchEvent(
                name: "action_failed_to_execute",
                payload: payload
            )
        } catch {
            NotificareLogger.error("Failed to emit the action_failed_to_execute event.", error: error)
        }
    }
}
