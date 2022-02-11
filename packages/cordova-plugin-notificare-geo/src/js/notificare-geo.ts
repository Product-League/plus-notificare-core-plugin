import { EventSubscription } from './events';
import { NotificareLocation } from './models/notificare-location';
import { NotificareRegion } from './models/notificare-region';
import { NotificareBeacon } from './models/notificare-beacon';
import { NotificareVisit } from './models/notificare-visit';
import { NotificareHeading } from './models/notificare-heading';

export class NotificareGeo {
  public static async hasLocationServicesEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareGeo', 'hasLocationServicesEnabled', []);
    });
  }

  public static async hasBluetoothEnabled(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareGeo', 'hasBluetoothEnabled', []);
    });
  }

  public static async enableLocationUpdates(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareGeo', 'enableLocationUpdates', []);
    });
  }

  public static async disableLocationUpdates(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareGeo', 'disableLocationUpdates', []);
    });
  }

  // region Events

  public static onLocationUpdated(callback: (location: NotificareLocation) => void): EventSubscription {
    return new EventSubscription('location_updated', callback);
  }

  public static onRegionEntered(callback: (region: NotificareRegion) => void): EventSubscription {
    return new EventSubscription('region_entered', callback);
  }

  public static onRegionExited(callback: (region: NotificareRegion) => void): EventSubscription {
    return new EventSubscription('region_exited', callback);
  }

  public static onBeaconEntered(callback: (beacon: NotificareBeacon) => void): EventSubscription {
    return new EventSubscription('beacon_entered', callback);
  }

  public static onBeaconExited(callback: (beacon: NotificareBeacon) => void): EventSubscription {
    return new EventSubscription('beacon_exited', callback);
  }

  public static onBeaconsRanged(
    callback: (data: { region: NotificareRegion; beacons: NotificareBeacon[] }) => void
  ): EventSubscription {
    return new EventSubscription('beacons_ranged', callback);
  }

  public static onVisit(callback: (visit: NotificareVisit) => void): EventSubscription {
    return new EventSubscription('visit', callback);
  }

  public static onHeadingUpdated(callback: (heading: NotificareHeading) => void): EventSubscription {
    return new EventSubscription('heading_updated', callback);
  }

  // endregion
}
