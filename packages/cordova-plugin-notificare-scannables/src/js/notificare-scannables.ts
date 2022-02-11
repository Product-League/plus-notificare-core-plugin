import { EventSubscription } from './events';
import { NotificareScannable } from './models/notificare-scannable';

export class NotificareScannables {
  public static async canStartNfcScannableSession(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareScannables', 'canStartNfcScannableSession', []);
    });
  }

  public static async startScannableSession(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareScannables', 'startScannableSession', []);
    });
  }

  public static async startNfcScannableSession(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareScannables', 'startNfcScannableSession', []);
    });
  }

  public static async startQrCodeScannableSession(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareScannables', 'startQrCodeScannableSession', []);
    });
  }

  public static async fetch(tag: string): Promise<NotificareScannable> {
    return new Promise<NotificareScannable>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareScannables', 'fetch', [tag]);
    });
  }

  // region Events

  public static onScannableDetected(callback: (scannable: NotificareScannable) => void): EventSubscription {
    return new EventSubscription('scannable_detected', callback);
  }

  public static onScannableSessionFailed(callback: (error: string | null) => void): EventSubscription {
    return new EventSubscription('scannable_session_failed', callback);
  }

  // endregion
}
