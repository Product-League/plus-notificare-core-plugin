import { Nullable } from 'cordova-plugin-notificare/build/js/utils';

export interface NotificareSystemNotification {
  readonly id: string;
  readonly type: string;
  readonly extra: Record<string, Nullable<string>>;
}
