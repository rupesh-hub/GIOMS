package com.gerp.communication.service.rabbitmq;

import com.gerp.communication.pojo.mail.Mail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducer {

	@Resource( name = "rabbitTemplate" )
	private RabbitTemplate rabbitTemplate;
	@Value("${rabbit.queue-1.exchange}")
	private String emailExchange;
	@Value("${rabbit.queue-1.route}")
	private String emailRoute;

	public void emailProducer(Mail mail) {
		rabbitTemplate.convertAndSend(emailExchange, emailRoute, mail);
	}




}
