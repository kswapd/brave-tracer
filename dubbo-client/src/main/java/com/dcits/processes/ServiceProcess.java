package com.dcits.processes;

import interfaces.DemoService;
import interfaces.DemoTraceService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ServiceProcess {

	private  ClassPathXmlApplicationContext context;

	public void Process()
	{
		DemoService service = (DemoService) context.getBean("demoService");
		System.out.println(service.sayHello("hello"));


		DemoTraceService tservice = (DemoTraceService) context.getBean("demoTraceService");
		System.out.println(tservice.sayParent("hello"));
	}

	public ClassPathXmlApplicationContext getContext() {
		return context;
	}

	public void setContext(ClassPathXmlApplicationContext context) {
		this.context = context;
	}
}
