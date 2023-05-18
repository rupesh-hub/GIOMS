import { NextFunction, Response } from 'express';
import NotificationService from '@services/notification.service';
import { KeycloakService } from '../keycloak';
import { type } from '@interfaces/notification.interface';

class NotificationController {
  public notificationService = new NotificationService();
  public keycloakService = new KeycloakService();

  public getNotification = async (req, res: Response, next: NextFunction) => {
    try {
      const limit = req.query.limit || 10;
      const page = req.query.page || 0;
      const { id } = req.userInfo;
      const notificationType: type = req.query.type;
      const responseData = await this.notificationService.getNotification(id, page, limit, notificationType);
      res.status(200).json({ data: responseData, message: 'All notification data is fetched successfully' });
    } catch (ex) {
      next(ex);
    }
  };

  public getCount = async (req, res: Response, next: NextFunction) => {
    try {
      const { id } = req.userInfo;
      const notificationType: type = req.query.type;
      const count = await this.notificationService.getCount(id, notificationType);
      res.status(200).json({ data: { count }, message: 'All notification data is fetched successfully' });
    } catch (ex) {
      next(ex);
    }
  };

  public updateSeenNotification = async (req, res: Response, next: NextFunction) => {
    try {
      const notificationId = req.query?.notificationId;
      const primaryKey = req.query?.primaryKey;
      const { id } = req.userInfo;
      await this.notificationService.updateNotificationToRead(notificationId, id, primaryKey);
      res.status(200).json({ data: { id: notificationId }, message: 'Notification data is seen successfully' });
    } catch (ex) {
      next(ex);
    }
  };

  public markAllAsRead = async (req, res: Response, next: NextFunction) => {
    try {
      const notificationType: type = req.query.type;
      const { id } = req.userInfo;
      await this.notificationService.markAllNotificationAsRead(id, notificationType);
      res.status(200).json({ data: { id: id }, message: 'Notification data is seen successfully' });
    } catch (ex) {
      next(ex);
    }
  };

  public notificationFiltration = async (req, res: Response) => {
    const { seen, startDate, endDate, type } = req.query;
    const limit = req.query.limit ? parseInt(req.query.limit) : 10;
    const page = req.query.page ? parseInt(req.query.page) : 0;
    const { id } = req.userInfo;
    const data = await this.notificationService.filter(id, seen, startDate, endDate, limit, page, type);
    res.status(200).json({ data: data, message: 'Notification data fetched successfully' });
  };
}

export default NotificationController;
