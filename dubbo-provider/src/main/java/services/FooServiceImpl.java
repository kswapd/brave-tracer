package services;

import com.alibaba.dubbo.config.spring.ServiceBean;
import interfaces.BarService;
import interfaces.FooService;
import org.springframework.context.ApplicationContext;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */

public class FooServiceImpl implements FooService {

	public String sayFoo(String name) {
		System.out.println("init : " + name);

		ApplicationContext context= ServiceBean.getSpringContext();
		BarService bar = (BarService) context.getBean("barService");
		String barStr = bar.sayBar("bar");

		return "foo dubbo foo provider "  + name;
	}

}
