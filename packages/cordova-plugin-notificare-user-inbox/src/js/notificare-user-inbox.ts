import { NotificareNotification } from 'cordova-plugin-notificare';
import { NotificareUserInboxItem } from './models/notificare-user-inbox-item';
import { NotificareUserInboxResponse } from './models/notificare-user-inbox-response';

export class NotificareUserInbox {
  public static async parseResponseFromJson(json: Record<string, unknown>): Promise<NotificareUserInboxResponse> {
    return new Promise<NotificareUserInboxResponse>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareUserInbox', 'parseResponseFromJson', [json]);
    });
  }

  public static async parseResponseFromString(json: string): Promise<NotificareUserInboxResponse> {
    return new Promise<NotificareUserInboxResponse>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareUserInbox', 'parseResponseFromString', [json]);
    });
  }

  public static async open(item: NotificareUserInboxItem): Promise<NotificareNotification> {
    return new Promise<NotificareNotification>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareUserInbox', 'open', [item]);
    });
  }

  public static async markAsRead(item: NotificareUserInboxItem): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareUserInbox', 'markAsRead', [item]);
    });
  }

  public static async remove(item: NotificareUserInboxItem): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareUserInbox', 'remove', [item]);
    });
  }
}
