import { NotificareNotification } from 'cordova-plugin-notificare';
import { Nullable } from 'cordova-plugin-notificare/build/js/utils';

export interface NotificareInboxItem {
  readonly id: string;
  readonly notification: NotificareNotification;
  readonly time: string; // ISO string
  readonly opened: boolean;
  readonly expires: Nullable<string>; // ISO string
}
