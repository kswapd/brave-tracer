package com.dcits.brave.tracing;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.sampler.Sampler;
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
//@EnableRabbit
//@ComponentScan(basePackages="services")
public class BraveTracing {
	private static final Logger logger = LoggerFactory.getLogger(BraveTracing.class);

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


	@Bean(name = "tracing")
	public Tracing getTracing() {

		String zipkinAddr = "http://" + zipkinAddress + ":" + zipkinPort + "/";
		Sender sender = OkHttpSender.create(zipkinAddr + "api/v2/spans");

		logger.info("setting zipkin address:{}", zipkinAddr);


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName(appName)
				.spanReporter(asyncReporter)
				.sampler(Sampler.create(1.0f))
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();
		return gw_tracing;
	}



}
