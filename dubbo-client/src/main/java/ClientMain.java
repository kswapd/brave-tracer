import com.dcits.services.ServiceProcess;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */
public class ClientMain {


	//@Resource(name="serviceProcess")
	//public static ServiceProcess serviceProcess;
	public static void main(String[] args) {
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:META-INF/spring/*.xml","applicationConsumer.xml" });

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath*:META-INF/spring/brave-context.xml","applicationConsumer.xml"});

		context.start();

		/*DemoService service = (DemoService) context.getBean("demoService");
		System.out.println(service.sayHello("hello"));


		DemoTraceService tservice = (DemoTraceService) context.getBean("demoTraceService");
		System.out.println(tservice.sayParent("hello"));*/

		ServiceProcess process = (ServiceProcess)context.getBean("serviceProcess");
		process.setContext(context);
		process.Process();

		/*FooService foo = (FooService) context.getBean("fooService");
		System.out.println(foo.sayFoo("world "));


		BarService bar = (BarService) context.getBean("barService");
		System.out.println(bar.sayBar("world "));
*/

		//Book bb = (Book) context.getBean("mybooks");


		context.close();
	}
}
