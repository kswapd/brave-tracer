package services;

import brave.spring.rabbit.SpringRabbitTracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@EnableRabbit
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

	@Value("${zipkin.rabbit.service.routingkey:routingKeyTracingDefault}")
	private String rabbitServiceRoutingKey;

	@Value("${zipkin.rabbit.service.queue:queueTracingDefault}")
	private String rabbitServiceQueueName;

	@Value("${zipkin.rabbit.service.exchange:exchangeTracingDefault}")
	private String rabbitServiceExchangeName;


	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}


	@Bean
	//@Conditional(CheckRabbitConnectionFactory.class)
	public ConnectionFactory connectionFactory() {
		logger.info("user building connectionFactory,{}.", rabbitServiceAddress);
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitServiceAddress);
		connectionFactory.setUsername(rabbitServiceUserName);
		connectionFactory.setPassword(rabbitServiceUserPassword);

		return connectionFactory;
	}


	//@Bean(name = "rabbitTemplateTracing")
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
										 SpringRabbitTracing springRabbitTracing) {
		logger.info("user building rabbitTemplate");
		//RabbitTemplate rabbitTemplate = springRabbitTracing.newRabbitTemplate(connectionFactory);
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		//rabbitTemplate.setRoutingKey(rabbitServiceRoutingKey);
		//rabbitTemplate.setExchange(rabbitServiceExchangeName);

		// other customizations as required
		return rabbitTemplate;
	}


	//@Bean(name = "simpleRabbitListenerContainerFactoryTracing")
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory,
			SpringRabbitTracing springRabbitTracing
	) {

		//MessageListenerAdapter listener = new MessageListenerAdapter(somePojo);
		//listener.setDefaultListenerMethod("myMethod");
		logger.info("user building tracingRabbitListenerContainerFactory");
		SimpleRabbitListenerContainerFactory fact;
		//fact.setC
		fact = springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
		fact.setConcurrentConsumers(3);
		fact.setMaxConcurrentConsumers(10);
		return fact;
		//return springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
	}

	@Bean
	public TopicExchange topicExchange(
	) {
		logger.info("building topicExchange");
		TopicExchange exchange = new TopicExchange(rabbitServiceExchangeName);

		// other customizations as required
		return exchange;
	}


	@Bean(name = "q1")
	public Queue queue1() {
		return new Queue("queue-hello");
	}

	@Bean
	public Binding bindings1(TopicExchange topicExchange) {


		Queue q = (Queue) context.getBean("q1");
		return BindingBuilder.bind(q)
				.to(topicExchange)
				.with("hello");


	}


	@Bean(name = "q2")
	public Queue queue2() {
		return new Queue("queue-second");
	}

	@Bean
	public Binding bindings2(TopicExchange topicExchange) {


		Queue q = (Queue) context.getBean("q2");
		return BindingBuilder.bind(q)
				.to(topicExchange)
				.with("second");


	}

	@Bean(name = "q3")
	public Queue queue3() {
		return new Queue("queue-fanout1");
	}

	@Bean(name = "q4")
	public Queue queue4() {
		return new Queue("queue-fanout2");
	}


	@Bean
	public FanoutExchange fanout() {
		return new FanoutExchange("fanout-exchange");
	}


	@Bean
	public Binding binding3(FanoutExchange fanout) {
		Queue q = (Queue) context.getBean("q3");
		return BindingBuilder.bind(q).to(fanout);
	}

	@Bean
	public Binding binding4(FanoutExchange fanout) {
		Queue q = (Queue) context.getBean("q4");
		return BindingBuilder.bind(q).to(fanout);
	}

	/*@Bean
	public List<Binding> bindings(TopicExchange topicExchange, Queue queue) {

		Queue q1 = (Queue)context.getBean("queue");

		Queue q2 = (Queue)context.getBean("queue2");
		//Queue q3 = (Queue)context.getBean("queue3");
		return Arrays.asList(
				BindingBuilder.bind(q2).to(topicExchange).with(rabbitServiceRoutingKey)
				//BindingBuilder.bind(q1).to(topicExchange).with(rabbitServiceRoutingKey)
		);
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
				//BindingBuilder.bind(q1).to(topicExchange).with(rabbitServiceRoutingKey),
				BindingBuilder.bind(q2).to(topicExchange).with(rabbitServiceRoutingKey)
				);
	}*/

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
