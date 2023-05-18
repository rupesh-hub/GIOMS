package com.gerp.communication.service.rabbitmq;

import com.gerp.communication.pojo.mail.Mail;
import com.gerp.communication.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListner  {

	@Autowired
	private EmailService emailService;

	@RabbitListener(queues = "${rabbit.queue-1.name}")
	public void emailListener(Mail mail)  throws Exception {
		emailService.sendSimpleMessage(mail);
	}

}
