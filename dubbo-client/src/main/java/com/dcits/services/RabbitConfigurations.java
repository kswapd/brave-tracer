package com.dcits.services;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Configuration
//@Component
public class RabbitConfigurations implements ApplicationContextAware {

	private ApplicationContext context;
	private static final Logger logger = LoggerFactory.getLogger(RabbitConfigurations.class);

	@Value("${zipkin.rabbit.service.name:rabbitService}")
	private String rabbitServiceName;

	@Value("${zipkin.rabbit.service.address:127.0.0.1}")
	private String rabbitServiceAddress;

	@Value("${zipkin.rabbit.service.user.name:guest}")
	private String rabbitServiceUserName;

	@Value("${zipkin.rabbit.service.user.password:guest}")
	private String rabbitServiceUserPassword;

	@Value("${zipkin.rabbit.service.routingkey:routingKeyTracing}")
	private String rabbitServiceRoutingKey;

	@Value("${zipkin.rabbit.service.queue:queueTracing}")
	private String rabbitServiceQueueName;

	@Value("${zipkin.rabbit.service.exchange:exchangeTracing}")
	private String rabbitServiceExchangeName;


	/*@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}


	@Bean
	public Queue queue1() {
		return new Queue("queue1");
	}


	@Bean
	public Queue queue2() {
		return new Queue("queue2");
	}

	@Bean
	public Queue queue3() {
		return new Queue("queue3");
	}

	@Bean
	public TopicExchange topicExchange(
	) {
		logger.info("building topicExchange");
		TopicExchange exchange = new TopicExchange(rabbitServiceExchangeName);

		// other customizations as required
		return exchange;
	}


	@Bean
	public List<Binding> bindingsTopicExchange (TopicExchange topicExchange
							*//*Queue queue*//*) {
		*//*return BindingBuilder.bind(queue)
				.to(topicExchange)
				.with(rabbitServiceRoutingKey);*//*

		Queue q1 = (Queue)context.getBean("queue1");

		Queue q2 = (Queue)context.getBean("queue2");
		Queue q3 = (Queue)context.getBean("queue3");
		return Arrays.asList(
				//BindingBuilder.bind(q1).to(topicExchange).with("abc"),
				BindingBuilder.bind(q2).to(topicExchange).with(rabbitServiceRoutingKey)
				);
	}*/

	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}


	@Bean
	public Queue queue() {
		return new Queue(rabbitServiceQueueName);
	}

	@Bean
	public Queue queue2() {
		return new Queue("queue2");
	}

	@Bean
	public TopicExchange topicExchange(
	) {
		logger.info("building topicExchange");
		TopicExchange exchange = new TopicExchange(rabbitServiceExchangeName);

		// other customizations as required
		return exchange;
	}


	@Bean
	public Binding bindings(TopicExchange topicExchange,
							Queue queue) {
		return BindingBuilder.bind(queue)
				.to(topicExchange)
				.with(rabbitServiceRoutingKey);
	}

	/*@Bean
	public List<Binding> bindings(TopicExchange topicExchange, Queue queue) {

		Queue q1 = (Queue)context.getBean("queue");

		Queue q2 = (Queue)context.getBean("queue2");
		//Queue q3 = (Queue)context.getBean("queue3");
		return Arrays.asList(
				BindingBuilder.bind(queue).to(topicExchange).with(rabbitServiceRoutingKey)
				//BindingBuilder.bind(q1).to(topicExchange).with(rabbitServiceRoutingKey)
		);
	}*/

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.context = applicationContext;
	}
}
