import NotificareKit

class NotificarePushPluginEventBroker {

    typealias Consumer = (_ event: Event) -> Void

    private static var eventQueue = [Event]()
    private static var consumer: Consumer?
    private static var canEmitEvents = false

    static func startListening(settings: [AnyHashable : Any]?, _ consumer: @escaping Consumer) {
        let holdEventsUntilReady = settings?["re.notifica.cordova.hold_events_until_ready"] as? String == "true"

        self.consumer = consumer
        canEmitEvents = !holdEventsUntilReady || Notificare.shared.isReady

        if (!canEmitEvents) {
            NotificationCenter.default.addObserver(self, selector: #selector(self.onNotificareReady), name: Notification.Name("re.notifica.cordova.notificare_is_ready"), object: nil)
            return
        }

        processQueue()
    }

    static func dispatchEvent(name: String, payload: Any?) {
        let event = Event(name: name, payload: payload)

        if consumer != nil && canEmitEvents {
            consumer!(event)
            return
        }

        eventQueue.append(event)
    }

    static private func processQueue() {
        guard let consumer = consumer else {
            NotificareLogger.debug("Cannot process event queue without a consumer.")
            return
        }

        if eventQueue.isEmpty {
            return
        }

        NotificareLogger.debug("Processing event queue with ${eventQueue.size} items.")
        eventQueue.forEach { consumer($0) }
        eventQueue.removeAll()
    }

    @objc static func onNotificareReady() {
        if canEmitEvents {
            return
        }

        canEmitEvents = true
        processQueue()
    }

    private init() {}

    struct Event {
        let name: String
        let payload: Any?
    }
}
