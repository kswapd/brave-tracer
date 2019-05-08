package apps;

import java.io.IOException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */
public class ServerMainWithRabbit {
		public  static void main(String[] args) throws IOException {

			System.setProperty("java.net.preferIPv4Stack", "true");
			//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath*:META-INF/spring/*.xml","applicationProvider.xml"});
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath*:META-INF/spring/brave-context.xml","applicationProvider.xml"});

			context.start();
			System.out.println("输入任意按键退出 ~ ");
			System.in.read();



			//context.close();
		}


}
