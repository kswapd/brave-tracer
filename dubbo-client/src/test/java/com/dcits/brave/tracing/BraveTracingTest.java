package com.dcits.brave.tracing;

import com.dcits.services.ServiceProcess;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
class BraveTracingTest {
	private BraveTracing bt;
	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		 bt = new BraveTracing();

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath*:META-INF/spring/brave-context.xml","applicationConsumer.xml"});

		context.start();

		/*DemoService service = (DemoService) context.getBean("demoService");
		System.out.println(service.sayHello("hello"));


		DemoTraceService tservice = (DemoTraceService) context.getBean("demoTraceService");
		System.out.println(tservice.sayParent("hello"));*/

		ServiceProcess process = (ServiceProcess)context.getBean("serviceProcess");
		process.setContext(context);
		process.Process();

	}

	@org.junit.jupiter.api.AfterEach
	void tearDown() {

		assertEquals(5, 5);
	}

	@org.junit.jupiter.api.Test
	void init() {
		assertEquals(5, 5);
		bt.init();

	}

	@org.junit.jupiter.api.Test
	void tracingInst() {
		BraveTracing.tracingInst();
	}

	@org.junit.jupiter.api.Test
	void setApplicationContext() {
	}

	@org.junit.jupiter.api.Test
	void onApplicationEvent() {
	}
}