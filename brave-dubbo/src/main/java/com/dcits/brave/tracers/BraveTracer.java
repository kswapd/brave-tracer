package com.dcits.brave.tracers;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import com.dcits.brave.dubbo.BraveFactoryBean;
import com.github.kristofa.brave.Brave;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


	private static Tracing sTracing = null;

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
		logger.info("setting brave tracer:{}", zipkinAddr);

		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		sTracing = Tracing.newBuilder()
				.localServiceName(appName)
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();
		return sTracing;
	}

	public static Tracing getTracingInst()
	{
		return sTracing;

	}




}
