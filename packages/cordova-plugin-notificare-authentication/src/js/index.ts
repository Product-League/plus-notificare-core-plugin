import { NotificareAuthentication } from './notificare-authentication';
import { bootstrap } from './events';

export * from './models/notificare-user';
export * from './models/notificare-user-preference';
export * from './models/notificare-user-segment';

export default NotificareAuthentication;

bootstrap();
