import { NotificareNotification, NotificareNotificationAction } from 'cordova-plugin-notificare';
import { Nullable } from 'cordova-plugin-notificare/build/js/utils';

export interface NotificationUrlClickedEvent {
  readonly notification: NotificareNotification;
  readonly url: string;
}

export interface ActionWillExecuteEvent {
  readonly notification: NotificareNotification;
  readonly action: NotificareNotificationAction;
}

export interface ActionExecutedEvent {
  readonly notification: NotificareNotification;
  readonly action: NotificareNotificationAction;
}

export interface ActionNotExecutedEvent {
  readonly notification: NotificareNotification;
  readonly action: NotificareNotificationAction;
}

export interface ActionFailedToExecuteEvent {
  readonly notification: NotificareNotification;
  readonly action: NotificareNotificationAction;
  readonly error: Nullable<string>;
}
