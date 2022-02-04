import * as cordova from 'cordova';
import { Nullable } from './utils';

export class NotificareEventsManager {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  static logCustom(event: string, data?: Nullable<Record<string, any>>): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'logCustom', [event, data]);
    });
  }
}
