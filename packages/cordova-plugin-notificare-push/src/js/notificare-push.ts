import { EventSubscription } from './events';
import { NotificareSystemNotification } from './models';
import { NotificareNotification, NotificareNotificationAction } from 'cordova-plugin-notificare';
import { Nullable } from 'cordova-plugin-notificare/build/js/utils';

export class NotificarePush {
  static async setAuthorizationOptions(options: string[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'setAuthorizationOptions', [options]);
    });
  }

  static async setCategoryOptions(options: string[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'setCategoryOptions', [options]);
    });
  }

  static async setPresentationOptions(options: string[]): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'setPresentationOptions', [options]);
    });
  }

  static async isRemoteNotificationsEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'isRemoteNotificationsEnabled', []);
    });
  }

  static async isAllowedUI(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'isAllowedUI', []);
    });
  }

  static async enableRemoteNotifications(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'enableRemoteNotifications', []);
    });
  }

  static async disableRemoteNotifications(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePush', 'disableRemoteNotifications', []);
    });
  }

  // region Events

  static onNotificationReceived(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_received', callback);
  }

  static onSystemNotificationReceived(
    callback: (notification: NotificareSystemNotification) => void
  ): EventSubscription {
    return new EventSubscription('system_notification_received', callback);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static onUnknownNotificationReceived(callback: (notification: Record<any, any>) => void): EventSubscription {
    return new EventSubscription('unknown_notification_received', callback);
  }

  static onNotificationOpened(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_opened', callback);
  }

  static onNotificationActionOpened(
    callback: (data: { notification: NotificareNotification; action: NotificareNotificationAction }) => void
  ): EventSubscription {
    return new EventSubscription('notification_action_opened', callback);
  }

  static onNotificationSettingsChanged(callback: (granted: boolean) => void): EventSubscription {
    return new EventSubscription('notification_settings_changed', callback);
  }

  static onShouldOpenNotificationSettings(
    callback: (notification: Nullable<NotificareNotification>) => void
  ): EventSubscription {
    return new EventSubscription('should_open_notification_settings', callback);
  }

  static onFailedToRegisterForRemoteNotifications(callback: (error: string) => void): EventSubscription {
    return new EventSubscription('failed_to_register_for_remote_notifications', callback);
  }

  // endregion
}
