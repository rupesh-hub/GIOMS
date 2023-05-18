import { Server, Socket } from 'socket.io';
const redisAdapter = require('socket.io-redis');
const RedisSentinel = require('@gitterhq/redis-sentinel-client');
import { createClient } from 'redis';
import { createServer } from 'http';
import { ConnectionEvent } from './constants';
import { Notification, ResultResponse } from '@interfaces/notification.interface';
import Keycloak from 'keycloak-connect';

process.env['NODE_CONFIG_DIR'] = './src/configs';

export class SocketServer {
  // { host: process.env.REDIS_SERVER, port: 6379, no_ready_check: true, auth_pass: process.env.REDIS_PASSWORD }
  private io: Server;
  constructor(app: Express.Application, keycloak: Keycloak.Keycloak) {
    const socketServer = createServer(app);
    // const redisConnection = new rediss();
    // const option = {
    //   // sentinels: [{ host: process.env.REDIS_1_SERVER, port: process.env.REDIS_1_SERVER_PORT }],
    //   host: process.env.REDIS_HOST,
    //   port: process.env.REDIS_PORT,
    //   name: `${process.env.REDIS_NAME}`,
    //   // sentinelPassword: process.env.REDIS_PASSWORD,
    //   // password: process.env.REDIS_PASSWORD,
    // };

    this.io = new Server(socketServer, {});
    const pubClient = createClient();
    const subClient = createClient();
    this.io.adapter(redisAdapter({ pubClient, subClient }));
    this.listen();
    this.io.use((socket, next) => {
      console.log(socket.handshake);
      if (socket.handshake.query && socket.handshake.query.token) {
        const token: string = socket.handshake.query.token.toString();
        console.log(token);
        keycloak.grantManager
          .userInfo(token)
          .then((result: ResultResponse) => {
            console.log(result);
            if (result) {
              // const sub: string = result.sub;
              // const data: string[] = sub.split(':');
              // socket['username'] = data[2];
              // socket['userId'] = data[1];
              // next();
              const payload = JSON.parse(Buffer.from(token.split('.')[1], 'base64').toString());
              const userInfo = (({ sub, email_verified, preferred_username, email, username }) => ({
                sub,
                email_verified,
                preferred_username,
                email,
                username,
              }))(payload);
              console.log(userInfo);
              // const sub: string = userInfo.sub;
              // const data: string[] = sub.split(':');
              userInfo['username'] = userInfo.username.toUpperCase();
              socket['userInfo'] = userInfo.sub;
              next();
            } else {
              socket.disconnect(true);
            }
          })
          .catch(err => {
            console.log(err);
            socket.disconnect(true);
          });
      }
    });
  }

  private listen(): void {
    this.io.on(ConnectionEvent.CONNECT, (socket: Socket) => {
      console.log('Connected client on port &s', '1');
      console.log(socket.handshake.auth);
      socket.on('message', (m: any) => {
        console.log(m);
        socket.broadcast.emit('for-all', m);
      });

      socket.on(ConnectionEvent.DISCONNECT, () => {
        console.log('Client disconneced');
      });
    });
  }

  public sendNotification(data: Notification): void {
    console.log(`data sent to ${data?.receiver}`);
    this.io.emit(data?.receiver, { subject: data.subject, detail: data.detail });
  }
}
