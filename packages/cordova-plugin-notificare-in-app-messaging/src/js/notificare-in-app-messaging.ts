import { EventSubscription } from './events';
import { NotificareInAppMessage, NotificareInAppMessageAction } from './models/notificare-in-app-message';

export class NotificareInAppMessaging {
  public static async hasMessagesSuppressed(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInAppMessaging', 'hasMessagesSuppressed', []);
    });
  }

  public static async setMessagesSuppressed(suppressed: boolean): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInAppMessaging', 'setMessagesSuppressed', [suppressed]);
    });
  }

  // region Events

  public static onMessagePresented(callback: (message: NotificareInAppMessage) => void): EventSubscription {
    return new EventSubscription('message_presented', callback);
  }

  public static onMessageFinishedPresenting(callback: (message: NotificareInAppMessage) => void): EventSubscription {
    return new EventSubscription('message_finished_presenting', callback);
  }

  public static onMessageFailedToPresent(callback: (message: NotificareInAppMessage) => void): EventSubscription {
    return new EventSubscription('message_failed_to_present', callback);
  }

  public static onActionExecuted(
    callback: (data: { message: NotificareInAppMessage; action: NotificareInAppMessageAction }) => void
  ): EventSubscription {
    return new EventSubscription('action_executed', callback);
  }

  public static onActionFailedToExecute(
    callback: (data: { message: NotificareInAppMessage; action: NotificareInAppMessageAction; error?: string }) => void
  ): EventSubscription {
    return new EventSubscription('action_failed_to_execute', callback);
  }

  // endregion
}
