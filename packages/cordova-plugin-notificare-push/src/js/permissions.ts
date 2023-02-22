export type PushPermissionStatus = 'denied' | 'granted' | 'permanently_denied';

export interface PushPermissionRationale {
  title?: string;
  message: string;
  buttonText?: string;
}
