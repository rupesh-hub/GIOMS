import glob from 'glob';
import { SocketServer } from 'socket';

export default function (socket: SocketServer) {
  const workers = glob.sync(__dirname + '/**/*.worker.{ts,js}', { absolute: true });
  workers.forEach(worker => {
    import(worker).then(functions => {
      functions.default(socket);
    });
  });
}
