package com.gerp.usermgmt.services.rabbitmq;

import com.gerp.shared.pojo.MultiReceiverNotificationPojo;
import com.gerp.shared.pojo.NotificationPojo;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RabbitMQService {

    void notificationProducer(NotificationPojo data);
    void notificationProducer(MultiReceiverNotificationPojo data);
}
