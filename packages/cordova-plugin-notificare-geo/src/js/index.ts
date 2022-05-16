import { NotificareGeo } from './notificare-geo';
import { bootstrap } from './events';

export * from './models/notificare-beacon';
export * from './models/notificare-heading';
export * from './models/notificare-location';
export * from './models/notificare-region';
export * from './models/notificare-visit';

export * from './permissions';

export default NotificareGeo;

bootstrap();
