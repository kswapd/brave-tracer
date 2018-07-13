package com.dcits.brave.tracers;

import com.github.kristofa.brave.dubbo.BraveFactoryBean;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kongxiangwen on 7/12/18 w:28.
 */
@Configuration
public class BraveTracer {
	@PostConstruct
	public void init()
	{

		System.out.println("------initialing brave tracer:"+appName);
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
	public BraveFactoryBean getBrave()
	{
		BraveFactoryBean bfb = new BraveFactoryBean();
		bfb.setZipkinHost("http://"+zipkinAddress+":"+zipkinPort+"/");
		bfb.setRate(zipkinSampleRate);
		bfb.setServiceName(appName);
		return bfb;

	}
}
