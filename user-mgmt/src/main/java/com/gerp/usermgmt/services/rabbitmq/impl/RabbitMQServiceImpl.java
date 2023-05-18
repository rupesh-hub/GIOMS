package com.gerp.usermgmt.services.rabbitmq.impl;


import com.gerp.shared.pojo.MultiReceiverNotificationPojo;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.usermgmt.services.rabbitmq.RabbitMQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class RabbitMQServiceImpl implements RabbitMQService {

    @Resource( name = "rabbitTemplate" )
    private RabbitTemplate rabbitTemplate;
    @Value("${rabbit.queue-1.name}")
    private String emailQueue;
    @Value("${rabbit.queue-2.name}")
    private String notificationQueue;

    @Override
    public void notificationProducer(NotificationPojo data) {
        rabbitTemplate.convertAndSend(notificationQueue, data);
    }

    @Override
    public void notificationProducer(MultiReceiverNotificationPojo data) {
        rabbitTemplate.convertAndSend(notificationQueue, data);
    }
}
