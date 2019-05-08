package com.dcits.brave.tracers;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.spring.rabbit.SpringRabbitTracing;
import com.dcits.brave.dubbo.BraveFactoryBean;
import com.github.kristofa.brave.Brave;
import java.util.concurrent.TimeUnit;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
@Configuration
@EnableRabbit
public class RabbitTracer {
	private static final Logger logger = LoggerFactory.getLogger(RabbitTracer.class);
	@PostConstruct
	public void init()
	{
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

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitServiceAddress);
		connectionFactory.setUsername(rabbitServiceUserName);
		connectionFactory.setPassword(rabbitServiceUserPassword);

		return connectionFactory;
	}



	@Bean
	public SpringRabbitTracing springRabbitTracing(Tracing tracing) {

		logger.info("building springRabbitTracing,{}.",rabbitServiceName);
		return SpringRabbitTracing.newBuilder(tracing)
				//.writeB3SingleFormat(true) // for more efficient propagation
				.remoteServiceName(rabbitServiceName)
				.build();
	}


	@Bean(name="rabbitListenerContainerFactory")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory,
			SpringRabbitTracing springRabbitTracing
	) {

		//MessageListenerAdapter listener = new MessageListenerAdapter(somePojo);
		//listener.setDefaultListenerMethod("myMethod");
		logger.info("building simpleRabbitListenerContainerFactory");
		SimpleRabbitListenerContainerFactory fact;
		//fact.setC
		fact = springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
		fact.setConcurrentConsumers(3);
		fact.setMaxConcurrentConsumers(10);
		return fact;
		//return springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
	}

	@Bean(name="rabbitTemplateTracing")
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
										 SpringRabbitTracing springRabbitTracing) {
		logger.info("building rabbitTemplate");
		RabbitTemplate rabbitTemplate = springRabbitTracing.newRabbitTemplate(connectionFactory);
		//RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setRoutingKey("foo.bar");
		// other customizations as required
		return rabbitTemplate;
	}


	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}


	@Bean
	public Queue queue() {
		return new Queue("kxwQueue");
	}

	@Bean(name="topicExchange")
	public TopicExchange topicExchange(
										) {
		logger.info("building topicExchange");
		TopicExchange exchange = new TopicExchange("kxwExchange");

		// other customizations as required
		return exchange;
	}


	@Bean
	public Binding bindings(TopicExchange topicExchange,
							 Queue queue) {
		return BindingBuilder.bind(queue)
				.to(topicExchange)
				.with("foo.*");
	}











}