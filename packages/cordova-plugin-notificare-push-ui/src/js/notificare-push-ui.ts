import { NotificareNotification, NotificareNotificationAction } from 'cordova-plugin-notificare';
import { EventSubscription } from './events';

export class NotificarePushUI {
  public static async presentNotification(notification: NotificareNotification): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePushUI', 'presentNotification', [notification]);
    });
  }

  public static async presentAction(
    notification: NotificareNotification,
    action: NotificareNotificationAction
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePushUI', 'presentAction', [notification, action]);
    });
  }

  // region Events

  public static onNotificationWillPresent(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_will_present', callback);
  }

  public static onNotificationPresented(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_presented', callback);
  }

  public static onNotificationFinishedPresenting(
    callback: (notification: NotificareNotification) => void
  ): EventSubscription {
    return new EventSubscription('notification_finished_presenting', callback);
  }

  public static onNotificationFailedToPresent(
    callback: (notification: NotificareNotification) => void
  ): EventSubscription {
    return new EventSubscription('notification_failed_to_present', callback);
  }

  public static onNotificationUrlClicked(
    callback: (data: { notification: NotificareNotification; url: string }) => void
  ): EventSubscription {
    return new EventSubscription('notification_url_clicked', callback);
  }

  public static onActionWillExecute(
    callback: (data: { notification: NotificareNotification; action: NotificareNotificationAction }) => void
  ): EventSubscription {
    return new EventSubscription('action_will_execute', callback);
  }

  public static onActionExecuted(
    callback: (data: { notification: NotificareNotification; action: NotificareNotificationAction }) => void
  ): EventSubscription {
    return new EventSubscription('action_executed', callback);
  }

  public static onActionNotExecuted(
    callback: (data: { notification: NotificareNotification; action: NotificareNotificationAction }) => void
  ): EventSubscription {
    return new EventSubscription('action_not_executed', callback);
  }

  public static onActionFailedToExecute(
    callback: (data: {
      notification: NotificareNotification;
      action: NotificareNotificationAction;
      error?: string;
    }) => void
  ): EventSubscription {
    return new EventSubscription('action_failed_to_execute', callback);
  }

  public static onCustomActionReceived(
    callback: (data: {
      notification: NotificareNotification;
      action: NotificareNotificationAction;
      url: string;
    }) => void
  ): EventSubscription {
    return new EventSubscription('custom_action_received', callback);
  }

  // endregion
}
