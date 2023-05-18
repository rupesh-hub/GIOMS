package com.gerp.dartachalani.service.rabbitmq;

import com.gerp.shared.pojo.NotificationPojo;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RabbitMQService {

    void notificationProducer(NotificationPojo data);
}
