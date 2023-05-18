package com.gerp.communication.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>Summary:</h3><p>RabbitMQConfig</p>
 * <h3>Features:</h3><p>spring bean initialization</p>
 * @author bibek
 * @since 0.1
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {

	@Value("${rabbit.queue-1.name}")
	private String emailQueue;
	@Value("${rabbit.queue-1.exchange}")
	private String emailExchange;
	@Value("${rabbit.queue-1.route}")
	private String emailRoute;

	@Bean
	public Queue emailQueue() {
		return new Queue(emailQueue);
	}


	@Bean
	public TopicExchange emailExchange() {
		return new TopicExchange(emailExchange);
	}

	@Bean
	public Binding emailBinding(@Qualifier("emailQueue") Queue queue, @Qualifier("emailExchange") TopicExchange topicExchange) {
		return BindingBuilder.bind(queue).to(topicExchange).with(emailRoute);
	}

	@Bean
	public Jackson2JsonMessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}
}
