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
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;
/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
@Configuration
public class BraveTracer {
	private static final Logger logger = LoggerFactory.getLogger(BraveTracer.class);
	@PostConstruct
	public void init()
	{
		logger.info("initialing brave tracer:{}", appName);
	}

	@Value("${zipkin.address}")
	private String zipkinAddress;
	@Value("${zipkin.port}")
	private String zipkinPort;
	@Value("${zipkin.sampleRate}")
	private String zipkinSampleRate;


	@Value("${zipkin.service.name}")
	private String appName;

	@Bean(name="brave")
	public Brave getBrave()
	{
		BraveFactoryBean bfb = new BraveFactoryBean();
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		bfb.setZipkinHost(zipkinAddr);
		bfb.setRate(zipkinSampleRate);
		bfb.setServiceName(appName);
		logger.info("setting zipkin address:{}", zipkinAddr);
		Brave br = null;
		try {
			br = bfb.getObject();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return br;

	}





	@Bean(name="tracing")
	public Tracing getTracing() {
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		Sender sender = OkHttpSender.create(zipkinAddr+"api/v2/spans");


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName(appName)
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();
		return gw_tracing;
	}


	@Bean
	public SpringRabbitTracing springRabbitTracing(Tracing tracing) {

		logger.info("building springRabbitTracing");
		return SpringRabbitTracing.newBuilder(tracing)
				//.writeB3SingleFormat(true) // for more efficient propagation
				.remoteServiceName("my-mq-service")
				.build();
	}

	@Bean(name="rabbitTemplateTracing")
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
										 SpringRabbitTracing springRabbitTracing) {
		logger.info("building rabbitTemplate");
		RabbitTemplate rabbitTemplate = springRabbitTracing.newRabbitTemplate(connectionFactory);

		// other customizations as required
		return rabbitTemplate;
	}

	//@Bean(name="simpleRabbitListenerContainerFactory")
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
			ConnectionFactory connectionFactory,
			SpringRabbitTracing springRabbitTracing
	) {

		//MessageListenerAdapter listener = new MessageListenerAdapter(somePojo);
		//listener.setDefaultListenerMethod("myMethod");
		logger.info("building simpleRabbitListenerContainerFactory");
		SimpleRabbitListenerContainerFactory fact;
		//fact.setC
		return springRabbitTracing.newSimpleMessageListenerContainerFactory(connectionFactory);

		//return springRabbitTracing.newSimpleRabbitListenerContainerFactory(connectionFactory);
	}


}
