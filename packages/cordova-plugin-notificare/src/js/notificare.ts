import { NotificareDeviceModule } from './notificare-device-module';
import { NotificareEventsModule } from './notificare-events-module';
import { EventSubscription } from './events';
import { NotificareApplication } from './models/notificare-application';
import { NotificareNotification } from './models/notificare-notification';
import { NotificareDevice } from './models/notificare-device';

export class Notificare {
  private static readonly deviceModule = new NotificareDeviceModule();
  private static readonly eventsModule = new NotificareEventsModule();

  //
  // Modules
  //

  public static device(): NotificareDeviceModule {
    return this.deviceModule;
  }

  public static events(): NotificareEventsModule {
    return this.eventsModule;
  }

  //
  // Methods
  //

  public static async isConfigured(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'isConfigured', []);
    });
  }

  public static async isReady(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'isReady', []);
    });
  }

  public static async launch(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'launch', []);
    });
  }

  public static async unlaunch(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'unlaunch', []);
    });
  }

  public static async getApplication(): Promise<NotificareApplication | null> {
    return new Promise<NotificareApplication>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getApplication', []);
    });
  }

  public static async fetchApplication(): Promise<NotificareApplication> {
    return new Promise<NotificareApplication>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchApplication', []);
    });
  }

  public static async fetchNotification(id: string): Promise<NotificareNotification> {
    return new Promise<NotificareNotification>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchNotification', [id]);
    });
  }

  //
  // Events
  //

  static onReady(callback: (application: NotificareApplication) => void): EventSubscription {
    return new EventSubscription('ready', callback);
  }

  static onUnlaunched(callback: () => void): EventSubscription {
    return new EventSubscription('unlaunched', callback);
  }

  static onDeviceRegistered(callback: (device: NotificareDevice) => void): EventSubscription {
    return new EventSubscription('device_registered', callback);
  }

  static onUrlOpened(callback: (url: string) => void): EventSubscription {
    return new EventSubscription('url_opened', callback);
  }
}
