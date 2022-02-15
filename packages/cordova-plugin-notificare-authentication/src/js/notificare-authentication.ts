import { EventSubscription } from './events';
import { NotificareUser } from './models/notificare-user';
import { NotificareUserPreference, NotificareUserPreferenceOption } from './models/notificare-user-preference';
import { NotificareUserSegment } from './models/notificare-user-segment';

export class NotificareAuthentication {
  public static async isLoggedIn(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'isLoggedIn', []);
    });
  }

  public static async login(email: string, password: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'login', [email, password]);
    });
  }

  public static async logout(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'logout', []);
    });
  }

  public static async fetchUserDetails(): Promise<NotificareUser> {
    return new Promise<NotificareUser>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'fetchUserDetails', []);
    });
  }

  public static async changePassword(password: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'changePassword', [password]);
    });
  }

  public static async generatePushEmailAddress(): Promise<NotificareUser> {
    return new Promise<NotificareUser>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'generatePushEmailAddress', []);
    });
  }

  public static async createAccount(email: string, password: string, name?: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'createAccount', [email, password, name]);
    });
  }

  public static async validateUser(token: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'validateUser', [token]);
    });
  }

  public static async sendPasswordReset(email: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'sendPasswordReset', [email]);
    });
  }

  public static async resetPassword(password: string, token: string): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'resetPassword', [password, token]);
    });
  }

  public static async fetchUserPreferences(): Promise<NotificareUserPreference[]> {
    return new Promise<NotificareUserPreference[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'fetchUserPreferences', []);
    });
  }

  public static async fetchUserSegments(): Promise<NotificareUserSegment[]> {
    return new Promise<NotificareUserSegment[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'fetchUserSegments', []);
    });
  }

  public static async addUserSegment(segment: NotificareUserSegment): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'addUserSegment', [segment]);
    });
  }

  public static async removeUserSegment(segment: NotificareUserSegment): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'removeUserSegment', [segment]);
    });
  }

  public static async addUserSegmentToPreference(
    preference: NotificareUserPreference,
    segment: NotificareUserSegment | NotificareUserPreferenceOption
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'addUserSegmentToPreference', [preference, segment]);
    });
  }

  public static async removeUserSegmentFromPreference(
    preference: NotificareUserPreference,
    segment: NotificareUserSegment | NotificareUserPreferenceOption
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareAuthentication', 'removeUserSegmentFromPreference', [
        preference,
        segment,
      ]);
    });
  }

  // region Events

  public static onPasswordResetTokenReceived(callback: (token: string) => void): EventSubscription {
    return new EventSubscription('password_reset_token_received', callback);
  }

  public static onValidateUserTokenReceived(callback: (token: string) => void): EventSubscription {
    return new EventSubscription('validate_user_token_received', callback);
  }

  // endregion
}
