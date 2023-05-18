import amqp, { ConsumeMessage } from 'amqplib';
import PubSub from 'pubsub-js';
import { EmailMQ } from '@interfaces/email.interface';
import Mailer from '../mailer/index';
export class EmailWorker {
  private CONN_URL: string;
  private mailer: Mailer;
  constructor(mailer: Mailer) {
    this.mailer = mailer;
    this.CONN_URL = `amqp://rabbitmq:5672`;

    PubSub.subscribe('email', this.onEmail);
  }

  public connect = () => {
    amqp
      .connect(this.CONN_URL + '?heartbeat=60')
      .then(connection => {
        process.once('SIGINT', function () {
          connection.close();
          process.exit(0);
        });

        return connection
          .createChannel()
          .then(channel => {
            channel.assertQueue(process.env.EMAIL_QUEUE || 'email', {
              durable: true,
            });

            console.log(' [*] Waiting for messages to send email. To exit press CTRL+C');
            return channel.consume(
              process.env.EMAIL_QUEUE || 'email',
              async function (msg: ConsumeMessage) {
                const data: string = msg.content.toString();
                const emailMQ: EmailMQ = JSON.parse(data);
                console.log(this);
                const success: boolean = await this.mailer.sendMail(emailMQ);
                if (success) channel.ack(msg);
                else channel.reject(msg, true);
              },
              { noAck: false },
            );
          })
          .catch(error => {
            console.log(error);
          });
      })
      .catch(ex => {
        console.log(ex);
        console.log(`connection failed to rabbit mq`);
        this.connect();
      });
  };

  public onEmail = (msg: string, data: string) => {
    console.log(data);
  };
}
