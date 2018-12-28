package services;

import com.alibaba.dubbo.config.spring.ServiceBean;
import interfaces.DemoService;
import interfaces.DemoTraceService;
import interfaces.FooService;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

@Component("demoTraceService")
public class DemoTraceServiceImpl implements DemoTraceService, ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(DemoTraceServiceImpl.class);
	@Autowired
	private DemoTraceAnotherService another;

	private ApplicationContext context;
	//@Resource(name="fooServiceRef")
	private FooService fooService;

	@PostConstruct
	public void init()
	{
		logger.info("initing...");
	}



	public String sayParent(String name) {
		logger.info("sayParent : " + name);
		//sayChild("child");
		//fooService.sayFoo("foo...");
		fooService = (FooService)context.getBean("fooServiceRef");
		fooService.sayFoo("foo..");
		return "hello from parent "  +  name;
	}

	public String sayChild(String name) {
		logger.info("init : " + name);
		return "hello from child "  +  name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
}
