import { EventSubscription } from './events';
import { NotificareProduct } from './models/notificare-product';
import { NotificarePurchase } from './models/notificare-purchase';

export class NotificareMonetize {
  public static async getProducts(): Promise<NotificareProduct[]> {
    return new Promise<NotificareProduct[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareMonetize', 'getProducts', []);
    });
  }

  public static async getPurchases(): Promise<NotificarePurchase[]> {
    return new Promise<NotificarePurchase[]>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareMonetize', 'getPurchases', []);
    });
  }

  public static async refresh(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareMonetize', 'refresh', []);
    });
  }

  public static async startPurchaseFlow(product: NotificareProduct): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificareMonetize', 'startPurchaseFlow', [product]);
    });
  }

  // region Events

  public static onProductsUpdated(callback: (products: NotificareProduct[]) => void): EventSubscription {
    return new EventSubscription('products_updated', callback);
  }

  public static onPurchasesUpdated(callback: (purchases: NotificarePurchase[]) => void): EventSubscription {
    return new EventSubscription('purchases_updated', callback);
  }

  public static onBillingSetupFinished(callback: () => void): EventSubscription {
    return new EventSubscription('billing_setup_finished', callback);
  }

  public static onBillingSetupFailed(callback: (data: { code: number; message: string }) => void): EventSubscription {
    return new EventSubscription('billing_setup_failed', callback);
  }

  public static onPurchaseFinished(callback: (purchase: NotificarePurchase) => void): EventSubscription {
    return new EventSubscription('purchase_finished', callback);
  }

  public static onPurchaseRestored(callback: (purchase: NotificarePurchase) => void): EventSubscription {
    return new EventSubscription('purchase_restored', callback);
  }

  public static onPurchaseCanceled(callback: () => void): EventSubscription {
    return new EventSubscription('purchase_canceled', callback);
  }

  public static onPurchaseFailed(
    callback: (data: { code?: number; message?: string; errorMessage?: string }) => void
  ): EventSubscription {
    return new EventSubscription('purchase_failed', callback);
  }

  // endregion
}
