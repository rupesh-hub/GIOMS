process.env['NODE_CONFIG_DIR'] = __dirname + '/configs';
import compression from 'compression';
import cookieParser from 'cookie-parser';
import cors from 'cors';
import express from 'express';
import helmet from 'helmet';
import hpp from 'hpp';
import morgan from 'morgan';
import swaggerJSDoc from 'swagger-jsdoc';
import swaggerUi from 'swagger-ui-express';
import Routes from '@interfaces/routes.interface';
import errorMiddleware from '@middlewares/error.middleware';
import { logger, stream } from '@utils/logger';
import { SocketServer } from './socket';
import notificationWorkers from './rabbitmq/index';
import { KeycloakService } from './keycloak';
import authMiddleWare from './middlewares/auth.middleware';
import { EmailWorker } from './rabbitmq/email-worker';
import Mongo from 'mongo';
import Mailer from 'mailer';

class App {
  public app: express.Application;
  public port: string | number;
  public env: string;
  public mongoCollection: string;
  public mongoUri?: string;
  private socketServer: SocketServer;
  private mailer: Mailer;
  private keycloakService: KeycloakService;
  constructor(routes: Routes[]) {
    this.app = express();
    this.port = process.env.PORT || 3000;
    this.env = process.env.NODE_ENV || 'development';
    this.mongoCollection = process.env.COLLECTION_NAME;
    this.mongoUri = process.env.MONGO_URI;
    this.keycloakService = new KeycloakService();
    this.keycloakService.initKeycloak();
    this.connectToDatabase();
    this.initializeMiddlewares();
    // authMiddleWare(this.app);
    this.initializeRoutes(routes);
    this.initializeErrorHandling();
    this.initializeSwagger();
    this.initializeSocket();
    this.initializeMailer();
    this.initializeRabbitMq();
    process.on('uncaughtException', function (err) {
      // Handle the error safely
      console.log(err);
    });
    // client.start();
  }

  public listen() {
    this.app.listen(this.port, () => {
      logger.info(`=================================`);
      logger.info(`======= ENV: ${this.env} =======`);
      logger.info(`🚀 App listening on the port ${this.port}`);
      logger.info(`=================================`);
    });
  }

  public getServer() {
    return this.app;
  }

  private async connectToDatabase() {
    new Mongo(this.mongoCollection, this.mongoUri);
  }

  private initializeMiddlewares() {
    if (this.env === 'production') {
      this.app.use(morgan('combined', { stream }));
      this.app.use(cors({ origin: '*', credentials: true }));
    } else {
      this.app.use(morgan('dev', { stream }));
      // this.app.use(cors({ origin: true, credentials: true }));
    }

    // const memoryStore: any = this.keycloakService.getMemoryStore();

    // this.app.use(
    //   session({
    //     secret: 'mySecret',
    //     resave: false,
    //     saveUninitialized: true,
    //     store: memoryStore,
    //   }),
    // );

    // this.app.use(this.keycloakService.getKeycloakInstance().middleware());
    // this.app.use(this.keycloakService.getKeycloakInstance().protect());
    this.app.use(hpp());
    this.app.use(helmet());
    this.app.use(compression());
    this.app.use(express.json({ limit: '50mb', strict: false }));
    this.app.use(express.urlencoded({ extended: true, limit: '50mb' }));
    this.app.use(cookieParser());
  }

  private initializeRoutes(routes: Routes[]) {
    routes.forEach(route => {
      this.app.use('/', route.router);
    });
  }

  private initializeSwagger() {
    const options = {
      swaggerDefinition: {
        openapi: '3.0.0',
        info: {
          title: 'REST API',
          version: '1.0.0',
          description: 'Example docs',
        },
        servers: [
          {
            url: 'http://localhost:3000/v1',
            description: 'Local server',
          },
          {
            url: 'http://103.69.124.84:9090/socket',
            description: 'Development server',
          },
        ],
      },
      apis: ['swagger.yaml'],
    };

    const specs = swaggerJSDoc(options);
    this.app.use('/docs', swaggerUi.serve, swaggerUi.setup(specs));
  }

  private initializeErrorHandling() {
    this.app.use(errorMiddleware);
  }

  private initializeSocket() {
    this.socketServer = new SocketServer(this.app, this.keycloakService.getKeycloakInstance());
  }

  private initializeMailer() {
    this.mailer = new Mailer();
  }

  private initializeRabbitMq() {
    notificationWorkers(this.socketServer);
    // new EmailWorker(this.mailer).connect();
  }
}

export default App;
