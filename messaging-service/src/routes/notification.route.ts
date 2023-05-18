import { Router } from 'express';
import NotificationController from '@controllers/notification.controller';
import Route from '@interfaces/routes.interface';

class NotificationRoute implements Route {
  public path = '/notification';
  public router = Router();
  public notificationController = new NotificationController();

  constructor() {
    this.initializeRoutesForNotification();
  }

  private initializeRoutesForNotification() {
    this.router.get(`${this.path}`, this.notificationController.getNotification);
    this.router.get(`${this.path}/count`, this.notificationController.getCount);
    this.router.get(`${this.path}/filter`, this.notificationController.notificationFiltration);
    this.router.get(`${this.path}/all`, this.notificationController.markAllAsRead);
    this.router.get(`${this.path}/update`, this.notificationController.updateSeenNotification);
  }
}

export default NotificationRoute;
