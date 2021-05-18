import { NotificareNotification } from 'cordova-plugin-notificare';
import { EventSubscription } from './events';
import { NotificareInboxItem } from './models';

export class NotificareInbox {
  static async getItems(): Promise<NotificareInboxItem[]> {
    return new Promise<NotificareInboxItem[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'getItems', []);
    });
  }

  static async getBadge(): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'getBadge', []);
    });
  }

  static async refresh(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'refresh', []);
    });
  }

  static async open(item: NotificareInboxItem): Promise<NotificareNotification> {
    return new Promise<NotificareNotification>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'open', [item]);
    });
  }

  static async markAsRead(item: NotificareInboxItem): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'markAsRead', [item]);
    });
  }

  static async markAllAsRead(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'markAllAsRead', []);
    });
  }

  static async remove(item: NotificareInboxItem): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'remove', [item]);
    });
  }

  static async clear(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareInbox', 'clear', []);
    });
  }

  // region Events

  static onInboxUpdated(callback: (items: NotificareInboxItem[]) => void): EventSubscription {
    return new EventSubscription('inbox_updated', callback);
  }

  static onBadgeUpdated(callback: (badge: number) => void): EventSubscription {
    return new EventSubscription('badge_updated', callback);
  }

  // endregion
}
