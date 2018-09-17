package services;

import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

@Component
public class DemoTraceAnotherFooService {


	public String sayAnotherFoo(String name) {
		System.out.println("init : " + name);
		return "hello from another foo "  +  name;
	}

}
