package com.dcits.processes;

import interfaces.DemoService;
import interfaces.DemoTraceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ServiceProcess {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProcess.class);
	private  ClassPathXmlApplicationContext context;

	public void Process()
	{
		DemoService service = (DemoService) context.getBean("demoService");
		logger.info(service.sayHello("hello") + "-----------");


		//DemoTraceService tservice = (DemoTraceService) context.getBean("demoTraceService");
		//logger.info(tservice.sayParent("hello"));
	}

	public ClassPathXmlApplicationContext getContext() {
		return context;
	}

	public void setContext(ClassPathXmlApplicationContext context) {
		this.context = context;
	}
}
