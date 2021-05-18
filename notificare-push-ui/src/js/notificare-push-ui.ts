import { NotificareNotification, NotificareNotificationAction } from 'cordova-plugin-notificare';

export class NotificarePushUI {
  static async presentNotification(notification: NotificareNotification): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePushUI', 'presentNotification', [notification]);
    });
  }

  static async presentAction(
    notification: NotificareNotification,
    action: NotificareNotificationAction
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePushUI', 'presentAction', [notification, action]);
    });
  }

  // region Events

  // static onNotificationReceived(callback: (notification: NotificareNotification) => void): EventSubscription {
  //   return new EventSubscription('notification_received', callback);
  // }

  // endregion
}
