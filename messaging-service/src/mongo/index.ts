import { logger } from '@utils/logger';
import mongoose from 'mongoose';

export default class Mongo {
  private readonly uri: string;
  constructor(db: string, uri?: string) {
    this.uri = uri || 'mongodb://localhost:27017/' + db;
    mongoose.Promise = global.Promise;
    mongoose.set('strictQuery', true);
    mongoose.connect(this.uri).catch(err => logger.error(err));
    mongoose.connection.on('connected', () => logger.info('mongodb connected'));
    mongoose.connection.on('error', err => logger.error(err));
    mongoose.connection.on('disconnected', () => logger.info('mongodb disconnected'));
  }
}
