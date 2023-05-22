export class NotificareEventsModule {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public async logCustom(event: string, data?: Record<string, any>) {
    return new Promise((resolve, reject) => {
      cordova.exec(resolve, reject, 'Notificare', 'logCustom', [event, data]);
    });
  }
}
