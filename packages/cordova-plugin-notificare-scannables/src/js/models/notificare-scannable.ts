import type { NotificareNotification } from 'cordova-plugin-notificare';

export interface NotificareScannable {
  readonly id: string;
  readonly name: string;
  readonly tag: string;
  readonly type: string;
  readonly notification?: NotificareNotification;
}
