import { NotificareDeviceManager } from './notificare-device-manager';
import { NotificareEventsManager } from './notificare-events-manager';
import { EventSubscription } from './events';
import { NotificareApplication, NotificareNotification } from './models';
import { Nullable } from './utils';

export class Notificare {
  // Modules
  static readonly deviceManager = NotificareDeviceManager;
  static readonly eventsManager = NotificareEventsManager;

  // Functions
  static isConfigured(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getConfigured', []);
    });
  }

  static isReady(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getReady', []);
    });
  }

  static async getUseAdvancedLogging(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getUseAdvancedLogging', []);
    });
  }

  static async setUseAdvancedLogging(useAdvancedLogging: boolean): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'setUseAdvancedLogging', [useAdvancedLogging]);
    });
  }

  static configure(applicationKey: string, applicationSecret: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'configure', [applicationKey, applicationSecret]);
    });
  }

  static launch(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'launch', []);
    });
  }

  static unlaunch(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'unlaunch', []);
    });
  }

  static async getApplication(): Promise<Nullable<NotificareApplication>> {
    return new Promise<NotificareApplication>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getApplication', []);
    });
  }

  static async fetchApplication(): Promise<NotificareApplication> {
    return new Promise<NotificareApplication>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchApplication', []);
    });
  }

  static async fetchNotification(id: string): Promise<NotificareNotification> {
    return new Promise<NotificareNotification>((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchNotification', [id]);
    });
  }

  static onReady(callback: (application: NotificareApplication) => void): EventSubscription {
    return new EventSubscription('ready', callback);
  }

  static onDeviceRegistered(callback: (application: NotificareApplication) => void): EventSubscription {
    return new EventSubscription('device_registered', callback);
  }

  static onUrlOpened(callback: (url: string) => void): EventSubscription {
    return new EventSubscription('url_opened', callback);
  }
}
