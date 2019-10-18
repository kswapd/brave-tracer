package com.dcits.brave.tracers;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import brave.Tracing;
import brave.spring.rabbit.SpringRabbitTracing;

/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
//@Component
@Configuration
@EnableRabbit
public class RabbitTracer implements ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(RabbitTracer.class);

	private ApplicationContext context;
	@PostConstruct
	public void init() {
		logger.info("rabbit tracing:{}", rabbitServiceName);
	}

	@Value("${zipkin.address}")
	private String zipkinAddress;
	@Value("${zipkin.port}")
	private String zipkinPort;
	@Value("${zipkin.sampleRate}")
	private String zipkinSampleRate;


	@Value("${zipkin.service.name}")
	private String appName;

	@Value("${zipkin.rabbit.service.name}")
	private String rabbitServiceName;

	@Value("${zipkin.rabbit.service.address:#{null}}")
	private String rabbitServiceAddress;

	@Value("${zipkin.rabbit.service.user.name:#{null}}")
	private String rabbitServiceUserName;

	@Value("${zipkin.rabbit.service.user.password:#{null}}")
	private String rabbitServiceUserPassword;



	@Value("${spring.rabbitmq.addresses:#{null}}")
	private String rabbitOriginServiceAddresses;

	@Value("${spring.rabbitmq.username:#{null}}")
	private String rabbitOriginServiceUserName;

	@Value("${spring.rabbitmq.password:#{null}}")
	private String rabbitOriginServiceUserPassword;

	/*@Value("${zipkin.rabbit.service.routingkey}")
	private String rabbitServiceRoutingKey;

	@Value("${zipkin.rabbit.service.queue}")
	private String rabbitServiceQueueName;

	@Value("${zipkin.rabbit.service.exchange}")
	private String rabbitServiceExchangeName;*/

	@Value("${spring.rabbitmq.listener.simple.concurrency:#{5}}")
	private Integer rabbitConsumerConcurrency;

	@Value("${spring.rabbitmq.listener.simple.max-concurrency:#{50}}")
	private Integer rabbitConsumerMaxConcurrency;
	/*@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}*/

	//@Conditional(CheckRabbitConnectionFactory.class)
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = null;
		if(StringUtils.isEmpty(rabbitOriginServiceAddresses)) {
			logger.info("building connectionFactory,{},{},{},{}.", rabbitServiceAddress, zipkinPort,rabbitConsumerConcurrency,rabbitConsumerMaxConcurrency);
			connectionFactory = new CachingConnectionFactory(rabbitServiceAddress);
			connectionFactory.setUsername(rabbitServiceUserName);
			connectionFactory.setPassword(rabbitServiceUserPassword);
		}else{
			logger.info("building connectionFactory by original info,{},{},{},{}.", rabbitOriginServiceAddresses, rabbitOriginServiceUserName,rabbitConsumerConcurrency,rabbitConsumerMaxConcurrency);
			connectionFactory = new CachingConnectionFactory();
			connectionFactory.setAddresses(rabbitOriginServiceAddresses);
			connectionFactory.setUsername(rabbitOriginServiceUserName);
			connectionFactory.setPassword(rabbitOriginServiceUserPassword);
		}

		return connectionFactory;
	}


	@Bean
	public SpringRabbitTracing springRabbitTracing(Tracing tracing) {

		Tracing currentTracing = tracing;
		logger.info("building springRabbitTracing,{},{},{}.", rabbitServiceName,rabbitConsumerConcurrency,rabbitConsumerMaxConcurrency);
		return SpringRabbitTracing.newBuilder(currentTracing)
				//.writeB3SingleFormat(true) // for more efficient propagation
				.remoteServiceName(rabbitServiceName)
				.build();
	}

	//@Bean(name = "simpleRabbitListenerContainerFactoryTracing")
	//@Conditional(CheckRabbitListener.class)
	@Bean
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
		fact.setConcurrentConsumers(rabbitConsumerConcurrency);
		fact.setMaxConcurrentConsumers(rabbitConsumerMaxConcurrency);

		return fact;
		//return springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
	}

	//@Bean(name = "rabbitTemplateTracing")
	//@Conditional(CheckRabbitTemplate.class)
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
										 SpringRabbitTracing springRabbitTracing) {
		logger.info("building rabbitTemplate");
		RabbitTemplate rabbitTemplate = springRabbitTracing.newRabbitTemplate(connectionFactory);
		//RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		//rabbitTemplate.setRoutingKey(rabbitServiceRoutingKey);
		//rabbitTemplate.setExchange(rabbitServiceExchangeName);

		// other customizations as required
		return rabbitTemplate;
	}


	/*@Bean
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
	}*/



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
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}*/

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}


}
