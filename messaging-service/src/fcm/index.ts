// import * as admin from 'firebase-admin';
// import { NotificationMQ } from '@interfaces/notification.interface';
// import DB from '@databases';
// import { FCMTokenModel } from '@models/fcmtoken.model';
// export class FireBaseMessaging {
//   private FCMToken = DB.FCMToken;
//   constructor() {
//     // const path = "src\fcm\\certificate\\certificate.json";
//     admin.initializeApp({
//       credential: admin.credential.cert('src/fcm/certificate/newcertificate-json.json'),
//     });
//   }

//   async sendNotification(notification: NotificationMQ) {
//     try {
//       const options = {
//         priority: 'normal',
//         timeToLive: 60 * 60,
//       };
//       let payload = {
//         notification: {
//           title: notification.subject,
//           body: notification.detail,
//         },
//       };
//       const tokens: Array<FCMTokenModel> = await this.FCMToken.findAll({
//         where: {
//           user: notification.receiver,
//           isActive: true,
//         },
//       });
//       if (tokens.length > 0) {
//         admin
//           .messaging()
//           .sendToDevice(tokens[0].token, payload, options)
//           .then(a => {
//             console.log('push notification sent successfully');
//           })
//           .catch(err => {
//             console.log('notification Sent failed');
//             console.log(err.message);
//           });
//       }
//     } catch (ex: any) {
//       console.log(ex.message);
//     }
//   }
// }
