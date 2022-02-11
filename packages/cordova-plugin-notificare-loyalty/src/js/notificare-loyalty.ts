import { NotificarePass } from './models/notificare-pass';

export class NotificareLoyalty {
  public static async fetchPassBySerial(serial: string): Promise<NotificarePass> {
    return new Promise<NotificarePass>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareLoyalty', 'fetchPassBySerial', [serial]);
    });
  }

  public static async fetchPassByBarcode(barcode: string): Promise<NotificarePass> {
    return new Promise<NotificarePass>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareLoyalty', 'fetchPassByBarcode', [barcode]);
    });
  }

  public static async present(pass: NotificarePass): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareLoyalty', 'present', [pass]);
    });
  }
}
