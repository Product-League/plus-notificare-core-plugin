import * as cordova from 'cordova';
import { NotificareDevice, NotificareDoNotDisturb } from './models';
import { Nullable } from './utils';

export class NotificareDeviceManager {
  static getCurrentDevice(): Promise<NotificareDevice> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getCurrentDevice', []);
    });
  }

  static register(userId: Nullable<string>, userName: Nullable<string>): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'register', [userId, userName]);
    });
  }

  static getPreferredLanguage(): Promise<Nullable<string>> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getPreferredLanguage', []);
    });
  }

  static updatePreferredLanguage(language: Nullable<string>): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'updatePreferredLanguage', [language]);
    });
  }

  static fetchTags(): Promise<string[]> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchTags', []);
    });
  }

  static addTag(tag: string): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'addTag', [tag]);
    });
  }

  static addTags(tags: string[]): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'addTags', [tags]);
    });
  }

  static removeTag(tag: string): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'removeTag', [tag]);
    });
  }

  static removeTags(tags: string[]): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'removeTags', [tags]);
    });
  }

  static clearTags(): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'clearTags', []);
    });
  }

  static fetchDoNotDisturb(): Promise<Nullable<NotificareDoNotDisturb>> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchDoNotDisturb', []);
    });
  }

  static updateDoNotDisturb(dnd: NotificareDoNotDisturb): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'updateDoNotDisturb', [dnd]);
    });
  }

  static clearDoNotDisturb(): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'clearDoNotDisturb', []);
    });
  }

  static fetchUserData(): Promise<Nullable<Record<string, string>>> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchUserData', []);
    });
  }

  static updateUserData(userData: Record<string, string>): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'updateUserData', [userData]);
    });
  }
}
