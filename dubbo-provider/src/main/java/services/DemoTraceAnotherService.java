package services;

import interfaces.DemoTraceService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

@Component
public class DemoTraceAnotherService {

	@Autowired
	private DemoTraceAnotherFooService anotherFoo;
	public String sayAnother(String name) {
		System.out.println("init : " + name);
		anotherFoo.sayAnotherFoo("foo");
		return "hello from another "  +  name;
	}

}
