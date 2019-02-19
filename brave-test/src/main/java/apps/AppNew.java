package apps;

/*import brave.Tracer;
import brave.Tracing;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;*/

import brave.Span;
import brave.Span.Kind;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalSpan;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.alibaba.dubbo.rpc.support.RpcUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class AppNew {
	private static String zipkinAddress = "10.88.2.110";
	private static String zipkinPort = "9411";
	private static Tracer tracer = null;
	private static TraceContext.Extractor<Map<String, String>> extractor;
	private static TraceContext.Injector<Map<String, String>> injector;
	private static  Map<String, String> spanInfo = new HashMap<>();
	public static void main(String[] args) {
		//complicatedTest();
		//simpleTest();
		//newTest();
		newSimpleTest2();
		//internalTest();
		//skyTest();
	}

	private  static final Propagation.Getter<Map<String, String>, String> GETTER =
			new Propagation.Getter<Map<String, String>, String>() {

				public String get(Map<String, String> carrier, String key) {
					return carrier.get(key);
				}

				@Override
				public String toString() {
					return "Map::get";
				}
			};

	private  static final Propagation.Setter<Map<String, String>, String> SETTER =
			new Propagation.Setter<Map<String, String>, String>() {

				public void put(Map<String, String> carrier, String key, String value) {
					carrier.put(key, value);
				}

				@Override
				public String toString() {
					return "Map::set";
				}
			};


	private static void newTest()
	{
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		Sender sender = OkHttpSender.create(zipkinAddr+"api/v2/spans");


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName("app-new-test")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();


		tracer = gw_tracing.tracer();
		extractor = gw_tracing.propagation().extractor(GETTER);
		injector = gw_tracing.propagation().injector(SETTER);




		Span span;
		span = tracer.nextSpan();
		//span = ThreadLocalSpan.CURRENT_TRACER.next();
		//injector.inject(span.context(), invocation.getAttachments());

		span.tag("s1", "111");


		/*


		TraceContextOrSamplingFlags extracted = extractor.extract(invocation.getAttachments());
			span = extracted.context() != null
					? tracer.joinSpan(extracted.context())
					: tracer.nextSpan(extracted);
		 */
		span.kind(Kind.CLIENT);
		injector.inject(span.context(), spanInfo);

		span.name("span-client");

		//injector.inject(span.context(),
		span.start();



		//try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {


			//try (Tracer.SpanInScope scope = tracer.withSpanInScope(span))
			try {
				Thread.sleep(150);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}


			new Thread(() ->
			{

				TraceContextOrSamplingFlags extracted = extractor.extract(spanInfo);
			/*span = extracted.context() != null
					? tracer.joinSpan(extracted.context())
					: tracer.nextSpan(extracted);*/
				Span sspan;
				sspan = tracer.joinSpan(extracted.context());

				if (!sspan.isNoop()) {

					sspan.kind(Kind.SERVER);

					sspan.name("span-server");
					sspan.tag("s2", "222");
					sspan.start();
				}
				//try (Tracer.SpanInScope sscope = tracer.withSpanInScope(sspan)) {
					try {
						Thread.sleep(250);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}

					sspan.tag("s3", "333");
				//}

				sspan.finish();
				sspan.flush();
				System.out.println("server finish");

			}).start();


			try {
				Thread.sleep(1550);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}


			//finally
			{
				span.tag("s4", "444");
				span.finish();

				span.flush();

				System.out.println("client finish");

			}


		//}


		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("finish");


	}


	private static void newSimpleTest2()
	{
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		Sender sender = OkHttpSender.create(zipkinAddr+"api/v2/spans");


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName("app-simple-test")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();


		tracer = gw_tracing.tracer();
		extractor = gw_tracing.propagation().extractor(GETTER);
		injector = gw_tracing.propagation().injector(SETTER);

		Span twoPhase = tracer.newTrace().name("twoPhase").start();
		try {
			Span prepare = tracer.newChild(twoPhase.context()).name("prepare").start();
			try {
				try {

					Thread.sleep(1000);


					//Span prepare2 = tracer.newChild(prepare.context()).name("prepare2").start();
					System.out.println(tracer.currentSpan());
					Span prepare2 = tracer.newChild(tracer.currentSpan().context()).name("prepare2").start();

					try {

						Thread.sleep(1000);

					}catch (InterruptedException e) {
						e.printStackTrace();
					}
					prepare2.finish();


				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				prepare.finish();
			}
			Span commit = tracer.newChild(twoPhase.context()).name("commit").start();
			try {
				try {

					Thread.sleep(2000);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				commit.finish();
			}
		} finally {
			twoPhase.finish();
		}




		gw_tracing.close();
		asyncReporter.close();
		try {
			sender.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("finish2");




	}
	private static void newSimpleTest()
	{
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		Sender sender = OkHttpSender.create(zipkinAddr+"api/v2/spans");


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName("app-simple-test")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();


		tracer = gw_tracing.tracer();
		extractor = gw_tracing.propagation().extractor(GETTER);
		injector = gw_tracing.propagation().injector(SETTER);

		Span span = tracer.newTrace().name("method1").kind(Kind.CLIENT).start();
		injector.inject(span.context(), spanInfo);


		new Thread(()-> {

			try {

				Thread.sleep(1000);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}

				//sspan = tracer.joinSpan(extracted.context());
				//Span span2 = tracer.newTrace().name("method2").kind(Kind.SERVER).start();
				TraceContextOrSamplingFlags extracted = extractor.extract(spanInfo);
				Span span2 = tracer.nextSpan(extracted)
						.name("process-request")
						.kind(Kind.SERVER);
				try {
					Thread.sleep(2000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				span2.finish();
				//span2.flush();



		}).start();



		try {

			Thread.sleep(10000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			span.finish();
			//span.flush();
		}

		gw_tracing.close();
		asyncReporter.close();
		try {
			sender.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("finish");


	}
}
