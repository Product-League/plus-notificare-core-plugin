import { NotificarePush } from './notificare-push';
import { bootstrap } from './events';

export * from './models/notificare-notification-delivery-mechanism';
export * from './models/notificare-system-notification';

export default NotificarePush;

bootstrap();
