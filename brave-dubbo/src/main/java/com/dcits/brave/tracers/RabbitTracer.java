package com.dcits.brave.tracers;

import brave.Tracing;
import brave.spring.rabbit.SpringRabbitTracing;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
@Configuration
@EnableRabbit
public class RabbitTracer {
	private static final Logger logger = LoggerFactory.getLogger(RabbitTracer.class);

	@PostConstruct
	public void init() {
		logger.info("initialing rabbit tracer:{}", rabbitServiceName);
	}

	@Value("${zipkin.address:127.0.0.1}")
	private String zipkinAddress;
	@Value("${zipkin.port:9411}")
	private String zipkinPort;
	@Value("${zipkin.sampleRate:1.0}")
	private String zipkinSampleRate;


	@Value("${zipkin.service.name:myApp}")
	private String appName;

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

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitServiceAddress);
		connectionFactory.setUsername(rabbitServiceUserName);
		connectionFactory.setPassword(rabbitServiceUserPassword);

		return connectionFactory;
	}


	@Bean
	public SpringRabbitTracing springRabbitTracing(Tracing tracing) {

		logger.info("building springRabbitTracing,{}.", rabbitServiceName);
		return SpringRabbitTracing.newBuilder(tracing)
				//.writeB3SingleFormat(true) // for more efficient propagation
				.remoteServiceName(rabbitServiceName)
				.build();
	}


	@Bean(name = "simpleRabbitListenerContainerFactoryTracing")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory,
			SpringRabbitTracing springRabbitTracing
	) {

		//MessageListenerAdapter listener = new MessageListenerAdapter(somePojo);
		//listener.setDefaultListenerMethod("myMethod");
		logger.info("building tracingRabbitListenerContainerFactory");
		SimpleRabbitListenerContainerFactory fact;
		//fact.setC
		fact = springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
		fact.setConcurrentConsumers(3);
		fact.setMaxConcurrentConsumers(10);
		return fact;
		//return springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
	}

	@Bean(name = "rabbitTemplateTracing")
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
										 SpringRabbitTracing springRabbitTracing) {
		logger.info("building rabbitTemplate");
		RabbitTemplate rabbitTemplate = springRabbitTracing.newRabbitTemplate(connectionFactory);
		//RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setRoutingKey(rabbitServiceRoutingKey);
		rabbitTemplate.setExchange(rabbitServiceExchangeName);

		// other customizations as required
		return rabbitTemplate;
	}


	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}


	@Bean
	public Queue queue() {
		return new Queue(rabbitServiceQueueName);
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


}
