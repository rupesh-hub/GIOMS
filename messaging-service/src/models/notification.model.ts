import mongoose, { Schema } from 'mongoose';

const notificationSchema = new Schema({
  primaryKey: Number, //primaryKey of relations from GERP database
  detail: String,
  receiver: String,
  sender: String,
  subject: String,
  moduleId: Number,
  module: String,
  seen: { type: Boolean, default: false }, //seen by user
  read: { type: Boolean, default: false }, //read by user or has taken action on notification
  receiverOfficeCode: String,
  receiverSectionCode: String,
  notificationType: {
    type: String,
    enum: ['P', 'G'],
  },
});

export const NotificationModel = mongoose.model('Notification', notificationSchema, 'notification');