import { Notificare } from './notificare';
import { bootstrap } from './events';

export * from './models/notificare-application';
export * from './models/notificare-device';
export * from './models/notificare-do-not-disturb';
export * from './models/notificare-notification';

export default Notificare;

bootstrap();
