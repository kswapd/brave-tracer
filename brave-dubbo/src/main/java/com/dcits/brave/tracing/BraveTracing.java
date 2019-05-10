package com.dcits.brave.tracing;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.StrictCurrentTraceContext;
//import brave.sampler.Sampler;
import brave.sampler.Sampler;
import brave.spring.beans.CurrentTraceContextFactoryBean;
import brave.spring.beans.TracingFactoryBean;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.http.HttpSpanCollector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
@Component
//@EnableRabbit
//@ComponentScan(basePackages="services")
public class BraveTracing implements ApplicationContextAware {
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

		if(tracing == null) {
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

			tracing = Tracing.current();

		}
		return tracing;
	}







	public static Tracing tracingInst(){
		//return context.getBean(Tracing.class);
		return braveTracing();
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			context = applicationContext;
	}
}
