package com.dcits.brave.tracers;

import com.dcits.brave.dubbo.BraveFactoryBean;
import com.github.kristofa.brave.Brave;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
