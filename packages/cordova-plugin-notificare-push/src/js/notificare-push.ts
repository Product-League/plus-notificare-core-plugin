import { EventSubscription } from './events';
import { NotificareNotification, NotificareNotificationAction } from 'cordova-plugin-notificare';
import { NotificareSystemNotification } from './models/notificare-system-notification';

export class NotificarePush {
  public static async setAuthorizationOptions(options: string[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'setAuthorizationOptions', [options]);
    });
  }

  public static async setCategoryOptions(options: string[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'setCategoryOptions', [options]);
    });
  }

  public static async setPresentationOptions(options: string[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'setPresentationOptions', [options]);
    });
  }

  public static async hasRemoteNotificationsEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'hasRemoteNotificationsEnabled', []);
    });
  }

  public static async allowedUI(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'allowedUI', []);
    });
  }

  public static async enableRemoteNotifications(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'enableRemoteNotifications', []);
    });
  }

  public static async disableRemoteNotifications(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'disableRemoteNotifications', []);
    });
  }

  // region Events

  public static onNotificationReceived(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_received', callback);
  }

  public static onSystemNotificationReceived(
    callback: (notification: NotificareSystemNotification) => void
  ): EventSubscription {
    return new EventSubscription('system_notification_received', callback);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public static onUnknownNotificationReceived(callback: (notification: Record<any, any>) => void): EventSubscription {
    return new EventSubscription('unknown_notification_received', callback);
  }

  public static onNotificationOpened(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_opened', callback);
  }

  public static onNotificationActionOpened(
    callback: (data: { notification: NotificareNotification; action: NotificareNotificationAction }) => void
  ): EventSubscription {
    return new EventSubscription('notification_action_opened', callback);
  }

  public static onNotificationSettingsChanged(callback: (granted: boolean) => void): EventSubscription {
    return new EventSubscription('notification_settings_changed', callback);
  }

  public static onShouldOpenNotificationSettings(
    callback: (notification: NotificareNotification | null) => void
  ): EventSubscription {
    return new EventSubscription('should_open_notification_settings', callback);
  }

  public static onFailedToRegisterForRemoteNotifications(callback: (error: string) => void): EventSubscription {
    return new EventSubscription('failed_to_register_for_remote_notifications', callback);
  }

  // endregion
}
