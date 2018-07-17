package apps;

/**
 * Created by kongxiangwen on 7/10/18 w:28.
 */
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
	private static HttpSpanCollector collector = null;
	private static Brave brave = null;
	private static Brave brave2 = null;
	private static Brave brave0 = null;
	public static ClientRequestAdapterImpl imp0;
	private static void braveInit(){
		collector = HttpSpanCollector.create("http://10.88.2.115:9411/", new EmptySpanCollectorMetricsHandler());


		brave0 = new Brave.Builder("appgateway").spanCollector(collector).build();

		brave = new Brave.Builder("appserver").spanCollector(collector).build();

		brave2 = new Brave.Builder("datacenter").spanCollector(collector).build();


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
				@Override
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

