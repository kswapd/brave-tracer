package services;

import com.alibaba.dubbo.config.spring.ServiceBean;
import interfaces.DemoService;
import interfaces.DemoTraceService;
import interfaces.FooService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

@Component("demoTraceService")
public class DemoTraceServiceImpl implements DemoTraceService {

	@Autowired
	private DemoTraceAnotherService another;

	@PostConstruct
	public void init()
	{
		System.out.println("initing...");
	}



	public String sayParent(String name) {
		System.out.println("init : " + name);
		//sayChild("child");
		another.sayAnother("another...");
		return "hello from parent "  +  name;
	}

	public String sayChild(String name) {
		System.out.println("init : " + name);
		return "hello from child "  +  name;
	}

}
