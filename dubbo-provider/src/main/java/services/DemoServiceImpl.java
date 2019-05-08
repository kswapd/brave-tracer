package services;

import com.alibaba.dubbo.config.spring.ServiceBean;
import interfaces.DemoService;
import interfaces.FooService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

public class DemoServiceImpl implements DemoService, ApplicationContextAware {

	private ApplicationContext ctx;
	private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);
	public String sayHello(String name) {
		logger.info("sayHello : " + name);

		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationProvider.xml"});
		//context.start();
		//ApplicationContext context = RpcContext.getContext();

		/*ApplicationContext context= ServiceBean.getSpringContext();
		BarService bar = (BarService) context.getBean("barService");
		String barStr = bar.sayBar("bar");*/

/*
		ApplicationContext context= ServiceBean.getSpringContext();
		FooService foo = (FooService) context.getBean("fooService");
		foo.sayFoo("foo");*/
		//String barStr = bar.sayBar("bar");


		AmqpTemplate template = (AmqpTemplate)ctx.getBean("rabbitTemplateTracing");


		template.convertAndSend("kxwExchange","foo.bar","Hello, world!");

		// template.convertAndSend("Hello, world!");

		System.out.println("producer finished");

		return "hello from dubbo provider "  +  name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;

	}
}
