import { NotificareNotification, NotificareNotificationAction } from 'cordova-plugin-notificare';
import { EventSubscription } from './events';
import {
  ActionExecutedEvent,
  ActionFailedToExecuteEvent,
  ActionNotExecutedEvent,
  ActionWillExecuteEvent,
  NotificationUrlClickedEvent,
} from './models';

export class NotificarePushUI {
  static async presentNotification(notification: NotificareNotification): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePushUI', 'presentNotification', [notification]);
    });
  }

  static async presentAction(
    notification: NotificareNotification,
    action: NotificareNotificationAction
  ): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      cordova.exec(resolve, reject, 'NotificarePushUI', 'presentAction', [notification, action]);
    });
  }

  // region Events

  static onNotificationWillPresent(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_will_present', callback);
  }

  static onNotificationPresented(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_presented', callback);
  }

  static onNotificationFinishedPresenting(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_finished_presenting', callback);
  }

  static onNotificationFailedToPresent(callback: (notification: NotificareNotification) => void): EventSubscription {
    return new EventSubscription('notification_failed_to_present', callback);
  }

  static onNotificationUrlClicked(callback: (data: NotificationUrlClickedEvent) => void): EventSubscription {
    return new EventSubscription('notification_url_clicked', callback);
  }

  static onActionWillExecute(callback: (data: ActionWillExecuteEvent) => void): EventSubscription {
    return new EventSubscription('action_will_execute', callback);
  }

  static onActionExecuted(callback: (data: ActionExecutedEvent) => void): EventSubscription {
    return new EventSubscription('action_executed', callback);
  }

  static onActionNotExecuted(callback: (data: ActionNotExecutedEvent) => void): EventSubscription {
    return new EventSubscription('action_not_executed', callback);
  }

  static onActionFailedToExecute(callback: (data: ActionFailedToExecuteEvent) => void): EventSubscription {
    return new EventSubscription('action_failed_to_execute', callback);
  }

  static onCustomActionReceived(callback: (url: string) => void): EventSubscription {
    return new EventSubscription('custom_action_received', callback);
  }

  // endregion
}
