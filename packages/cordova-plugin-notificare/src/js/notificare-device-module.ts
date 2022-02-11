import { NotificareDevice } from './models/notificare-device';
import { NotificareDoNotDisturb } from './models/notificare-do-not-disturb';

export class NotificareDeviceModule {
  public async getCurrentDevice(): Promise<NotificareDevice | null> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getCurrentDevice', []);
    });
  }

  public async getPreferredLanguage(): Promise<string | null> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'getPreferredLanguage', []);
    });
  }

  public async updatePreferredLanguage(language: string | null): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'updatePreferredLanguage', [language]);
    });
  }

  public async register(userId: string | null, userName: string | null): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'register', [userId, userName]);
    });
  }

  public async fetchTags(): Promise<string[]> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchTags', []);
    });
  }

  public async addTag(tag: string): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'addTag', [tag]);
    });
  }

  public async addTags(tags: string[]): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'addTags', [tags]);
    });
  }

  public async removeTag(tag: string): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'removeTag', [tag]);
    });
  }

  public async removeTags(tags: string[]): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'removeTags', [tags]);
    });
  }

  public async clearTags(): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'clearTags', []);
    });
  }

  public async fetchDoNotDisturb(): Promise<NotificareDoNotDisturb | null> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchDoNotDisturb', []);
    });
  }

  public async updateDoNotDisturb(dnd: NotificareDoNotDisturb): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'updateDoNotDisturb', [dnd]);
    });
  }

  public async clearDoNotDisturb(): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'clearDoNotDisturb', []);
    });
  }

  public async fetchUserData(): Promise<Record<string, string>> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'fetchUserData', []);
    });
  }

  public async updateUserData(userData: Record<string, string>): Promise<void> {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'updateUserData', [userData]);
    });
  }
}
