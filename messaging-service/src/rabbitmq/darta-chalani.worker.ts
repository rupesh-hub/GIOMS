import amqp, { ConsumeMessage } from 'amqplib';
import { NotificationMQ, Notification } from '@interfaces/notification.interface';
import { SocketServer } from '../socket';

import { NotificationModel } from '@models/notification.model';

export default function (socket: SocketServer) {
  const CONN_URL = `amqp://guest:guest@rabbitmq:567`;
  const socketServer: SocketServer = socket;
  //  const firebaseMessaging: FireBaseMessaging = new FireBaseMessaging();
  const exchange = process.env.RABBITMQ_EXCHANGE || 'gerp';

  console.log(CONN_URL);

  const connect = () => {
    amqp
      .connect(CONN_URL + '?heartbeat=60')
      .then(connection => {
        process.once('SIGINT', function () {
          connection.close();
          process.exit(0);
        });

        return connection
          .createChannel()
          .then(async channel => {
            await channel.assertExchange(exchange, 'direct', { durable: true });

            await channel.assertQueue('darta-chalani', {
              durable: true,
            });

            console.log(' [*] Waiting for messages to send notification. To exit press CTRL+C');

            await channel.bindQueue('darta-chalani', exchange, 'darta_chalani');

            return channel.consume(
              'darta-chalani',
              async function (msg: ConsumeMessage) {
                const data: string = msg.content.toString();
                await onNotification('notification', data);
              },
              { noAck: true },
            );
          })
          .catch(error => {
            console.log(error);
          });
      })
      .catch(err => {
        console.log(`connection failed to rabbit mq \n${err}`);
        connect();
      });
  };

  connect();

  const onNotification = async (msg: string, data: string) => {
    console.log(data);
    const notificationData: NotificationMQ = JSON.parse(data);
    if (Array.isArray(notificationData.receiver)) {
      const receiver = notificationData.receiver;
      for (let i = 0; i < receiver.length; i++) {
        const notification: Notification = {
          primaryKey: notificationData.primaryKey,
          detail: notificationData.detail,
          receiver: receiver[i],
          sender: notificationData.sender,
          subject: notificationData.subject,
          moduleId: notificationData.moduleId,
          module: notificationData.module,
          receiverOfficeCode: notificationData.receiverOfficeCode,
          receiverSectionCode: notificationData.receiverSectionCode,
          notificationType: notificationData.notificationType,
          pushNotification: notificationData.pushNotification,
        };

        await NotificationModel.create(notification)
          .then(() => {
            console.log('notification saved');
            if (notification.pushNotification) this.firebaseMessaging.sendNotification(notification);
            else socketServer.sendNotification(notification);
          })
          .catch(err => {
            console.log('notification save failed', err);
          });
      }
    }
  };
}
