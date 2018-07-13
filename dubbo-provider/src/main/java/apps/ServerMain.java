package apps;

import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.brave.spi.interfaces.ITestHello;
import com.github.kristofa.brave.dubbo.BraveFactoryBean;
import com.github.kristofa.brave.dubbo.DubboServerNameProvider;
import java.io.IOException;
import java.util.ServiceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jline.ConsoleRunner.property;

/**
 * Created by kongxiangwen on 5/15/18 w:20.
 */
public class ServerMain{
		public  static void main(String[] args) throws IOException {



			ServiceLoader<ITestHello> loaders = ServiceLoader.load(ITestHello.class);

			int i=0;

			for (ITestHello in : loaders) {
				in.sayHi();
				//i++;
			}
			System.setProperty("java.net.preferIPv4Stack", "true");
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationProvider.xml"});


			context.start();








			System.out.println("输入任意按键退出 ~ ");
			System.in.read();



			//context.close();
		}


}
