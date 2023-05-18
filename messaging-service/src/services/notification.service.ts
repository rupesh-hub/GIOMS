import { type } from '@interfaces/notification.interface';
import { NotificationModel } from '@models/notification.model';

class NotificationService {
  public async getNotification(receiverId: string, page: number, limit: number, notificationType: type): Promise<any> {
    const whereQuery: any = {
      receiver: receiverId,
      notificationType: notificationType,
    };

    if (notificationType === type.G) {
      await NotificationModel.updateMany({ receiver: receiverId, notificationType: notificationType, seen: false }, { seen: true });
    }

    return await NotificationModel.find(whereQuery)
      .sort({ date: -1 })
      .limit(limit)
      .skip(page * limit);
  }

  public async markAllNotificationAsRead(id: string, notificationType: type) {
    await NotificationModel.updateMany({ receiver: id, notificationType: notificationType, read: false }, { seen: true, read: true });
  }

  public async updateNotificationToRead(notificationId: string | undefined, id: string, primaryKey?: number) {
    const whereQuery: any = {
      _id: notificationId,
      receiver: id,
    };

    if (primaryKey) {
      delete whereQuery['_id'];
      whereQuery.primaryKey = primaryKey;
    }

    await NotificationModel.findOneAndUpdate({ $or: [whereQuery] }, { seen: true, read: true });
  }

  public async getCount(receiverId: string, notificationType: type): Promise<number> {
    const whereQuery: any = {
      receiver: receiverId,
      notificationType: notificationType,
      seen: false,
    };

    return await NotificationModel.count(whereQuery);
  }

  public async filter(id: string, seen: boolean, startDate, endDate, limit: number, page: number, notificationType: type): Promise<any> {
    const whereQuery: any = {
      receiver: id,
    };

    if (notificationType) {
      whereQuery.notificationType = notificationType;
    }

    if (startDate && endDate) {
      whereQuery.date = { $gte: startDate, $lte: endDate };
      // sql += ` and created_at::date >=:startDate `;
    }
    // if (endDate) {
    //   sql += ` and created_at::date <= :endDate`;
    // }
    if (seen) {
      whereQuery.seen = seen;
    }
    if (!seen) {
      whereQuery.seen = seen;
    }

    return await NotificationModel.find(whereQuery)
      .limit(limit)
      .skip(page * limit);
  }
}

export default NotificationService;
