process.env['NODE_CONFIG_DIR'] = __dirname + '/configs';
console.log();
const dotenv = require('dotenv');
import validateEnv from '@utils/validateEnv';
if (process.env.NODE_ENV === 'development') {
  dotenv.config({ path: '.env' });
  validateEnv();
}
import App from './app';
import IndexRoute from '@routes/index.route';
import NotificationRoute from '@routes/notification.route';
import FileConversionRoute from '@routes/file-conversion.route';

try {
  new App([new IndexRoute(), new NotificationRoute(), new FileConversionRoute()]).listen();
} catch (error) {
  console.log(error);
}
