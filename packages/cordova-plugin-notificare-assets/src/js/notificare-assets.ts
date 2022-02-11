import { NotificareAsset } from './models/notificare-asset';

export class NotificareAssets {
  public static async fetch(group: string): Promise<NotificareAsset[]> {
    return new Promise<NotificareAsset[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAssets', 'fetch', [group]);
    });
  }
}
