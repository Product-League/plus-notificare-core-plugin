export type PermissionGroup = 'location_when_in_use' | 'location_always' | 'bluetooth_scan';

export type PermissionStatus = 'denied' | 'granted' | 'restricted' | 'permanently_denied';

export interface PermissionRationale {
  title?: string;
  message: string;
  buttonText?: string;
}
