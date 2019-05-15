package com.dcits.brave.tracing;

import brave.Tracing;
import brave.spring.rabbit.SpringRabbitTracing;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

//import brave.sampler.Sampler;

/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
@Component
//@EnableRabbit
//@ComponentScan(basePackages="services")
public class BraveTracing implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(BraveTracing.class);
	private static ApplicationContext context;

	@PostConstruct
	public void init() {
		logger.info("brave tracing:{}", appName);
	}

	@Value("${zipkin.address}")
	private String zipkinAddress;
	@Value("${zipkin.port}")
	private String zipkinPort;
	@Value("${zipkin.sampleRate}")
	private String zipkinSampleRate;


	@Value("${zipkin.service.name}")
	private String appName;

	private static Tracing tracing = null;

	//private static Tracing serverTracing = null;


	//@Bean(name="brave")
	/*public Brave getBrave()
	{
		Brave.Builder builder = new Brave.Builder(appName);
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		//if (this.zipkinHost != null && !"".equals(zipkinAddr))
		{
			builder.spanCollector(HttpSpanCollector.create(zipkinAddr, new EmptySpanCollectorMetricsHandler())).traceSampler(Sampler.create(Float.parseFloat(zipkinSampleRate))).build();

		}
		Brave br = builder.build();


		logger.info("setting zipkin address:{}", zipkinAddr);

		return br;

	}*/


	//@Bean(name = "tracing")
	private static Tracing braveTracing() {

		if (tracing == null) {
			//String zipkinAddr = "http://" + zipkinAddress + ":" + zipkinPort + "/";
			//Sender sender = OkHttpSender.create(zipkinAddr + "api/v2/spans");

			//logger.info("tracing setting zipkin address:{}", zipkinAddr);


			/*AsyncReporter asyncReporter = AsyncReporter.builder(sender)
					.closeTimeout(5000, TimeUnit.MILLISECONDS)

					.build(SpanBytesEncoder.JSON_V2);

			//CurrentTraceContextFactoryBean b  = new CurrentTraceContextFactoryBean();
			ScopeDecorator scopeDecorator = MDCScopeDecorator.create();
			List<ScopeDecorator> li = new ArrayList<ScopeDecorator>();
			li.add(scopeDecorator);
			//b.setScopeDecorators(li);

			TracingFactoryBean beanTracingFact;
			beanTracingFact = new TracingFactoryBean();
			beanTracingFact.setSingleton(false);
			beanTracingFact.setLocalServiceName(appName);
			beanTracingFact.setSpanReporter(asyncReporter);
			try {
				tracing = (Tracing)beanTracingFact.getObject();

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			//beanTracingFact.setCurrentTraceContext(b.getObject());
			//beanTracingFact.setPropagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"));
			try {
				tracing = (Tracing)beanTracingFact.getObject();

			}
			catch (Exception e) {
				e.printStackTrace();
			}*/
			/*



			tracing = Tracing.newBuilder()
                                       .localServiceName(appName)
                                       .spanReporter(asyncReporter)
                                       .currentTraceContext(new StrictCurrentTraceContext())
                                       //.sampler(Sampler.create(1.0f))
                                       .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
                                       .build();
			 */

			tracing = context.getBean(Tracing.class);//Tracing.current();

		}
		return tracing;
	}


	public static Tracing tracingInst() {
		//return context.getBean(Tracing.class);
		return braveTracing();
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;


	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		logger.info("Decorate user rabbit utils.");

		SpringRabbitTracing springRabbitTracing = context.getBean(SpringRabbitTracing.class);

		RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
		if(rabbitTemplate != null){
			logger.info("Decorate RabbitTemplate.");
			springRabbitTracing.decorateRabbitTemplate(rabbitTemplate);
		}
			SimpleRabbitListenerContainerFactory fact = context.getBean(SimpleRabbitListenerContainerFactory.class);
		if(fact != null) {
			logger.info("Decorate SimpleRabbitListenerContainerFactory.");
			springRabbitTracing.decorateSimpleRabbitListenerContainerFactory(fact);
		}
	}
}
