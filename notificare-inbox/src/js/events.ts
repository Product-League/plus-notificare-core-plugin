const EVENT_SUBSCRIPTIONS: EventSubscription[] = [];

export function bootstrap(): void {
  document.addEventListener(
    'deviceready',
    function onDeviceReady() {
      cordova.exec(
        function onSuccess(event) {
          EVENT_SUBSCRIPTIONS.filter((sub) => sub.event === event.name).forEach((sub) => sub.callback(event.data));
        },
        function onFailure(error) {
          console.error('Failed to register event listener.', error);
        },
        'NotificareInbox',
        'registerListener',
        []
      );
    },
    false
  );
}

export class EventSubscription {
  constructor(readonly event: string, readonly callback: (data: any) => void) {
    EVENT_SUBSCRIPTIONS.push(this);
  }

  remove(): void {
    const index = EVENT_SUBSCRIPTIONS.indexOf(this);
    if (index >= 0) {
      EVENT_SUBSCRIPTIONS.splice(index, 1);
    }
  }
}
