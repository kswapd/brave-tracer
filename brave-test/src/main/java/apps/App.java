package apps;

/**
 * Created by kongxiangwen on 7/10/18 w:28.
 */

import com.github.kristofa.brave.ServerSpan;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by kongxiangwen on 7/10/18 w:28.
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.ClientRequestInterceptor;
import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.ClientResponseInterceptor;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerRequestAdapter;
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseAdapter;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.TraceData;
import com.github.kristofa.brave.http.HttpSpanCollector;
import com.twitter.zipkin.gen.Endpoint;


public class App {

	private static String zipkinUrl = "http://127.0.0.1:9411";
	private static HttpSpanCollector collector = null;
	private static Brave brave = null;
	private static Brave brave2 = null;
	private static Brave brave0 = null, brave3=null;
	private static Brave brave_gw = null, brave_server=null,brave_server1=null, brave_server2 = null, brave_server3 = null;

	public static ClientRequestAdapterImpl imp0,imp1,imp2,req_from_gw,req_from_server1, req_from_server2;
	private static void braveInit(){
		collector = HttpSpanCollector.create(zipkinUrl, new EmptySpanCollectorMetricsHandler());
		brave0 = new Brave.Builder("appgateway").spanCollector(collector).build();
		brave = new Brave.Builder("appserver").spanCollector(collector).build();
		brave2 = new Brave.Builder("datacenter").spanCollector(collector).build();
		brave3 = new Brave.Builder("omscenter").spanCollector(collector).build();

	}

	static class Task {
		String name;
		SpanId spanId;
		public Task(String name, SpanId spanId) {
			super();
			this.name = name;
			this.spanId = spanId;
		}
	}



	public static void main(String[] args) throws Exception {
		//complicatedTest();
		//simpleTest();
		singleThrdTest();
		//newTest();
		//internalTest();
		//skyTest();
	}


	private static void complicatedTest() throws Exception
	{

		braveInit();

		final BlockingQueue<Task> queue = new ArrayBlockingQueue<Task>(10);
		Thread thread = new Thread(){
			public void run() {
				while (true) {
					try {
						Task task = queue.take();
						dcHandle(task.name, task.spanId);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();




		{
			ServerRequestInterceptor serverRequestInterceptor = brave.serverRequestInterceptor();
			ServerResponseInterceptor serverResponseInterceptor = brave.serverResponseInterceptor();
			ClientRequestInterceptor clientRequestInterceptor = brave.clientRequestInterceptor();
			ClientResponseInterceptor clientResponseInterceptor = brave.clientResponseInterceptor();


			ClientRequestInterceptor clientRequestInterceptor0 = brave0.clientRequestInterceptor();
			ClientResponseInterceptor clientResponseInterceptor0 = brave0.clientResponseInterceptor();


			imp0 = new ClientRequestAdapterImpl("aaaa");
			clientRequestInterceptor0.handle(imp0);



			new Thread(new Runnable()
			{

				public void run()

				{




					ServerRequestInterceptor serverRequestInterceptor = brave.serverRequestInterceptor();
					ServerResponseInterceptor serverResponseInterceptor = brave.serverResponseInterceptor();
					ClientRequestInterceptor clientRequestInterceptor = brave.clientRequestInterceptor();
					ClientResponseInterceptor clientResponseInterceptor = brave.clientResponseInterceptor();


					ClientRequestInterceptor clientRequestInterceptor0 = brave0.clientRequestInterceptor();
					ClientResponseInterceptor clientResponseInterceptor0 = brave0.clientResponseInterceptor();

					try {
						Thread.sleep(20);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					//serverRequestInterceptor.handle(new ServerRequestAdapterImpl("sssss"));
					serverRequestInterceptor.handle(new ServerRequestAdapterImpl("aa", imp0.getSpanId()));


					try {
						Thread.sleep(20);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}

					ClientRequestAdapterImpl clientRequestAdapterImpl = new ClientRequestAdapterImpl("get_user_list");
					clientRequestInterceptor.handle(clientRequestAdapterImpl);
					queue.offer(new Task("get_user_list2", clientRequestAdapterImpl.getSpanId()));

					try {
						Thread.sleep(50);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					clientResponseInterceptor.handle(new ClientResponseAdapterImpl());


			/*clientRequestAdapterImpl = new ClientRequestAdapterImpl("get_program_list");
			clientRequestInterceptor.handle(clientRequestAdapterImpl);
			queue.offer(new Task("get_program_list2", clientRequestAdapterImpl.getSpanId()));
			Thread.sleep(50);
			clientResponseInterceptor.handle(new ClientResponseAdapterImpl());*/


					try {
						Thread.sleep(20);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					serverResponseInterceptor.handle(new ServerResponseAdapterImpl());
					try {
						Thread.sleep(20);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}


				}
			}



			).start();













			Thread.sleep(200);
			brave0.clientResponseInterceptor().handle(new ClientResponseAdapterImpl());
			System.out.println("over2");





		}
		Thread.sleep(3000);


	}

	private static void func2() throws Exception
	{
		Thread.sleep(1000);
		ServerRequestInterceptor serverRequestInterceptor1 = brave2.serverRequestInterceptor();
		ServerResponseInterceptor serverResponseInterceptor1 = brave2.serverResponseInterceptor();

		ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("func2", imp1.getSpanId());
		serverRequestInterceptor1.handle(serverReq0);
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());

	}

	private static void func1() throws Exception
	{
		Thread.sleep(1000);
		ServerRequestInterceptor serverRequestInterceptor1 = brave.serverRequestInterceptor();
		ServerResponseInterceptor serverResponseInterceptor1 = brave.serverResponseInterceptor();

		ClientRequestInterceptor clientRequestInterceptor1 = brave.clientRequestInterceptor();
		ClientResponseInterceptor clientResponseInterceptor1 = brave.clientResponseInterceptor();

		ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("func1", imp0.getSpanId());
		serverRequestInterceptor1.handle(serverReq0);
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}





		imp1 = new ClientRequestAdapterImpl("func2");

		clientRequestInterceptor1.handle(imp1);


		new Thread(new Runnable(){
			public void run() {
				try {
					func2();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();


		//	func2();
		clientResponseInterceptor1.handle(new ClientResponseAdapterImpl());


		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());

	}

	private static void func3()
	{
		System.out.println("func3");
		func4();
	}
	private static void func4()
	{
		System.out.println("func4");
	}
	private static void skyTest() throws Exception
	{

		System.out.println("hihihi");
		func3();
	}
	private static void internalTest() throws Exception
	{




		braveInit();
		ClientRequestInterceptor clientRequestInterceptor0 = brave0.clientRequestInterceptor();
		ClientResponseInterceptor clientResponseInterceptor0 = brave0.clientResponseInterceptor();

		imp0 = new ClientRequestAdapterImpl("func1");
		clientRequestInterceptor0.handle(imp0);
		func1();

		/*Thread.sleep(120);
		ServerRequestInterceptor serverRequestInterceptor1 = brave.serverRequestInterceptor();
		ServerResponseInterceptor serverResponseInterceptor1 = brave.serverResponseInterceptor();

		ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("aaaa", imp0.getSpanId());
		serverRequestInterceptor1.handle(serverReq0);
		try {
			Thread.sleep(200);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());*/



		/*new Thread(new Runnable(){
			public void run() {


				ServerRequestInterceptor serverRequestInterceptor1 = brave.serverRequestInterceptor();
				ServerResponseInterceptor serverResponseInterceptor1 = brave.serverResponseInterceptor();

				ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("aaaa", imp0.getSpanId());
				serverRequestInterceptor1.handle(serverReq0);
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());
			}

		}).start();*/

		Thread.sleep(1000);
		clientResponseInterceptor0.handle(new ClientResponseAdapterImpl());
		System.out.println("over2");

	}





	private static void simpleTest() throws Exception
	{
		collector = HttpSpanCollector.create(zipkinUrl+"/", new EmptySpanCollectorMetricsHandler());
		brave_gw = new Brave.Builder("appgateway").spanCollector(collector).build();
		brave_server = new Brave.Builder("appserver").spanCollector(collector).build();



		ClientRequestInterceptor clientRequestInterceptor0 = brave_gw.clientRequestInterceptor();
		ClientResponseInterceptor clientResponseInterceptor0 = brave_gw.clientResponseInterceptor();


		imp0 = new ClientRequestAdapterImpl("span-name-1");
		clientRequestInterceptor0.handle(imp0);



		new Thread(new Runnable(){
			//@Override
			public void run() {



				ServerRequestInterceptor serverRequestInterceptor1 = brave_server.serverRequestInterceptor();
				ServerResponseInterceptor serverResponseInterceptor1 = brave_server.serverResponseInterceptor();
				ClientRequestInterceptor clientRequestInterceptor1 = brave_server.clientRequestInterceptor();
				ClientResponseInterceptor clientResponseInterceptor1 = brave_server.clientResponseInterceptor();


				ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("span-name-1", imp0.getSpanId());
				serverRequestInterceptor1.handle(serverReq0);
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

				/*imp1 = new ClientRequestAdapterImpl("bbbb");
				clientRequestInterceptor1.handle(imp1);
				try {
					Thread.sleep(20);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}


				ServerRequestAdapterImpl serverReq1 = new ServerRequestAdapterImpl("bbbb", imp1.getSpanId());

				serverRequestInterceptor2.handle(serverReq1);
				try {
					Thread.sleep(2000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				serverResponseInterceptor2.handle(new ServerResponseAdapterImpl());

				try {
					Thread.sleep(20);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				clientResponseInterceptor1.handle(new ClientResponseAdapterImpl());*/



				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

				serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());
				System.out.println("over in thread");





			}

		}).start();


		Thread.sleep(1520);
		clientResponseInterceptor0.handle(new ClientResponseAdapterImpl());
		System.out.println("over all");


	}


	private static void singleThrdTest() throws Exception
	{
		collector = HttpSpanCollector.create(zipkinUrl+"/", new EmptySpanCollectorMetricsHandler());
		brave_gw = new Brave.Builder("appgateway").spanCollector(collector).build();
		brave_server1 = new Brave.Builder("appserver1").spanCollector(collector).build();
		brave_server2 = new Brave.Builder("appserver2").spanCollector(collector).build();
		brave_server3 = new Brave.Builder("appserver3").spanCollector(collector).build();
		ClientRequestInterceptor clientRequestInterceptor0 = brave_gw.clientRequestInterceptor();
		ClientResponseInterceptor clientResponseInterceptor0 = brave_gw.clientResponseInterceptor();


		//span gateway->server1

		req_from_gw = new ClientRequestAdapterImpl("span-gw-s1");
		clientRequestInterceptor0.handle(req_from_gw);

		//brave_server = brave_gw;

		//new Thread(new Runnable(){public void run() {



			ServerRequestInterceptor serverRequestInterceptor1 = brave_server1.serverRequestInterceptor();
			ServerResponseInterceptor serverResponseInterceptor1 = brave_server1.serverResponseInterceptor();
			ClientRequestInterceptor clientRequestInterceptor1 = brave_server1.clientRequestInterceptor();
			ClientResponseInterceptor clientResponseInterceptor1 = brave_server1.clientResponseInterceptor();


			ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("span-gw-s1", req_from_gw.getSpanId());
			serverRequestInterceptor1.handle(serverReq0);
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}


			//--------------------span server1->server2-----------------

				req_from_server1 = new ClientRequestAdapterImpl("span-s1-s2");
				clientRequestInterceptor1.handle(req_from_server1);

				//new Thread(new Runnable(){public void run() {



						ServerRequestInterceptor serverRequestInterceptor2 = brave_server2.serverRequestInterceptor();
						ServerResponseInterceptor serverResponseInterceptor2 = brave_server2.serverResponseInterceptor();

						try {
							Thread.sleep(20);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}


						ServerRequestAdapterImpl serverReq1 = new ServerRequestAdapterImpl("span-s1-s2", req_from_server1.getSpanId());

						serverRequestInterceptor2.handle(serverReq1);
						try {
							Thread.sleep(400);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						serverResponseInterceptor2.handle(new ServerResponseAdapterImpl());

						try {
							Thread.sleep(20);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}





				//}}).start();


						try {
							Thread.sleep(420);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}


						clientResponseInterceptor1.handle(new ClientResponseAdapterImpl());


						System.out.println("over s1->s2");

						//-------------------------s1->s2 finish----------------




						//--------------------span server1->server3-----------------

						req_from_server1 = new ClientRequestAdapterImpl("span-s1-s3");
						clientRequestInterceptor1.handle(req_from_server1);


				//new Thread(new Runnable(){public void run() {


						ServerRequestInterceptor serverRequestInterceptor3 = brave_server3.serverRequestInterceptor();
						ServerResponseInterceptor serverResponseInterceptor3 = brave_server3.serverResponseInterceptor();

						try {
							Thread.sleep(20);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}


						ServerRequestAdapterImpl serverReq2 = new ServerRequestAdapterImpl("span-s1-s3", req_from_server1.getSpanId());

						serverRequestInterceptor3.handle(serverReq2);
						try {
							Thread.sleep(400);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						serverResponseInterceptor3.handle(new ServerResponseAdapterImpl());

						try {
							Thread.sleep(20);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}





				//}}).start();


						try {
							Thread.sleep(520);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}


						clientResponseInterceptor1.handle(new ClientResponseAdapterImpl());


						System.out.println("over s1->s3");

						//-------------------------s1->s3 finish----------------






						try {
							Thread.sleep(200);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}

						serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());






		//}}).start();


		Thread.sleep(4520);
		clientResponseInterceptor0.handle(new ClientResponseAdapterImpl());
		System.out.println("over gw->s1");








	}


	private static void singleThrdTestbk() throws Exception
	{
		collector = HttpSpanCollector.create(zipkinUrl+"/", new EmptySpanCollectorMetricsHandler());
		brave_gw = new Brave.Builder("appgateway").spanCollector(collector).build();
		brave_server = new Brave.Builder("appserver").spanCollector(collector).build();
		brave_server2 = new Brave.Builder("appserver2").spanCollector(collector).build();

		ClientRequestInterceptor clientRequestInterceptor0 = brave_gw.clientRequestInterceptor();
		ClientResponseInterceptor clientResponseInterceptor0 = brave_gw.clientResponseInterceptor();


		imp0 = new ClientRequestAdapterImpl("span-name-1");
		clientRequestInterceptor0.handle(imp0);

		brave_server = brave_gw;

		//new Thread(new Runnable(){public void run() {



				ServerRequestInterceptor serverRequestInterceptor1 = brave_server.serverRequestInterceptor();
				ServerResponseInterceptor serverResponseInterceptor1 = brave_server.serverResponseInterceptor();
				ClientRequestInterceptor clientRequestInterceptor1 = brave_server.clientRequestInterceptor();
				ClientResponseInterceptor clientResponseInterceptor1 = brave_server.clientResponseInterceptor();


				ServerRequestAdapterImpl serverReq0 = new ServerRequestAdapterImpl("span-name-1", imp0.getSpanId());
				serverRequestInterceptor1.handle(serverReq0);
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}




			//--------------------span 2-----------------



				ServerRequestInterceptor serverRequestInterceptor2 = brave_server2.serverRequestInterceptor();
				ServerResponseInterceptor serverResponseInterceptor2 = brave_server2.serverResponseInterceptor();

				imp1 = new ClientRequestAdapterImpl("span-name-2");
				clientRequestInterceptor1.handle(imp1);
				try {
					Thread.sleep(20);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
		//ServerSpan a;
				//a.getSpan() == null;

				ServerRequestAdapterImpl serverReq1 = new ServerRequestAdapterImpl("span-name-2", imp1.getSpanId());

				serverRequestInterceptor2.handle(serverReq1);
				try {
					Thread.sleep(2000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				serverResponseInterceptor2.handle(new ServerResponseAdapterImpl());

				try {
					Thread.sleep(20);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}


				clientResponseInterceptor1.handle(new ClientResponseAdapterImpl());








		//-------------------------

				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}

				serverResponseInterceptor1.handle(new ServerResponseAdapterImpl());
				System.out.println("over in thread");





			//}}).start();


		Thread.sleep(1520);
		clientResponseInterceptor0.handle(new ClientResponseAdapterImpl());
		System.out.println("over all");


	}







	public static void dcHandle(String spanName, SpanId spanId){
		ServerRequestInterceptor serverRequestInterceptor = brave2.serverRequestInterceptor();
		ServerResponseInterceptor serverResponseInterceptor = brave2.serverResponseInterceptor();


		serverRequestInterceptor.handle(new ServerRequestAdapterImpl(spanName, spanId));

		try {
			Thread.sleep(40);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		serverResponseInterceptor.handle(new ServerResponseAdapterImpl());
		System.out.println("over");
	}


	static class ServerRequestAdapterImpl implements ServerRequestAdapter {

		Random randomGenerator = new Random();
		SpanId spanId;
		String spanName;

		ServerRequestAdapterImpl(String spanName){
			this.spanName = spanName;
			long startId = randomGenerator.nextLong();
			SpanId spanId = SpanId.builder().spanId(startId).traceId(startId).parentId(startId).build();
			this.spanId = spanId;
			System.out.println(String.format("ServerRequestAdapterImpl:trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

		}

		ServerRequestAdapterImpl(String spanName, SpanId spanId){
			this.spanName = spanName;
			this.spanId = spanId;
		}


		public TraceData getTraceData() {
			if (this.spanId != null) {
				System.out.println(String.format("ServerRequestAdapterImpl:getTraceData trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

				return TraceData.builder().spanId(this.spanId).build();
			}
			System.out.println(String.format("ServerRequestAdapterImpl:getTraceData generate trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

			long startId = randomGenerator.nextLong();
			SpanId spanId = SpanId.builder().spanId(startId).traceId(startId).parentId(startId).build();
			return TraceData.builder().spanId(spanId).build();
		}


		public String getSpanName() {
			return spanName;
		}


		public Collection<KeyValueAnnotation> requestAnnotations() {
			Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
			KeyValueAnnotation kv = KeyValueAnnotation.create("server-request", "222222");
			collection.add(kv);
			return collection;
		}

	}



	static class ServerResponseAdapterImpl implements ServerResponseAdapter {


		public Collection<KeyValueAnnotation> responseAnnotations() {
			Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
			KeyValueAnnotation kv = KeyValueAnnotation.create("server-response", "333333");
			collection.add(kv);
			return collection;
		}

	}

	static class ClientRequestAdapterImpl implements ClientRequestAdapter {

		String spanName;
		SpanId spanId;

		ClientRequestAdapterImpl(String spanName){
			this.spanName = spanName;
		}

		public SpanId getSpanId() {
			return spanId;
		}


		public String getSpanName() {
			return this.spanName;
		}


		public void addSpanIdToRequest(SpanId spanId) {
			//记录传输到远程服务
			//System.out.println(spanId);
			if (spanId != null) {
				this.spanId = spanId;
				System.out.println(String.format("ClientRequestAdapterImpl:addSpanIdToRequest:trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));
			}else {
				System.out.println(String.format("ClientRequestAdapterImpl:addSpanIdToRequest: null"));
			}

		}


		public Collection<KeyValueAnnotation> requestAnnotations() {
			Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
			KeyValueAnnotation kv = KeyValueAnnotation.create("client-request", "111111");
			collection.add(kv);
			return collection;
		}


		public Endpoint serverAddress() {
			return null;
		}

	}

	static class ClientResponseAdapterImpl implements ClientResponseAdapter {


		public Collection<KeyValueAnnotation> responseAnnotations() {
			Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
			KeyValueAnnotation kv = KeyValueAnnotation.create("client-response", "444444");
			collection.add(kv);
			return collection;
		}

	}
}

