import { NotificareNotification } from 'cordova-plugin-notificare';
import { EventSubscription } from './events';
import { NotificareInboxItem } from './models/notificare-inbox-item';

export class NotificareInbox {
  public static async getItems(): Promise<NotificareInboxItem[]> {
    return new Promise<NotificareInboxItem[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'getItems', []);
    });
  }

  public static async getBadge(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'getBadge', []);
    });
  }

  public static async refresh(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'refresh', []);
    });
  }

  public static async open(item: NotificareInboxItem): Promise<NotificareNotification> {
    return new Promise<NotificareNotification>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'open', [item]);
    });
  }

  public static async markAsRead(item: NotificareInboxItem): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'markAsRead', [item]);
    });
  }

  public static async markAllAsRead(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'markAllAsRead', []);
    });
  }

  public static async remove(item: NotificareInboxItem): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'remove', [item]);
    });
  }

  public static async clear(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'clear', []);
    });
  }

  // region Events

  public static onInboxUpdated(callback: (items: NotificareInboxItem[]) => void): EventSubscription {
    return new EventSubscription('inbox_updated', callback);
  }

  public static onBadgeUpdated(callback: (badge: number) => void): EventSubscription {
    return new EventSubscription('badge_updated', callback);
  }

  // endregion
}
