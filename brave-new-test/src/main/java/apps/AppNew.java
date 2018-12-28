package apps;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import java.util.concurrent.TimeUnit;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;
//import brave.context.log4j2.ThreadContextCurrentTraceContext;

public class AppNew {
	private static String zipkinUrl = "http://127.0.0.1:9411";
	public static void main(String[] args) throws Exception {
		//complicatedTest();
		//simpleTest();
		//newTest();
		multipleNodeTest();
		//internalTest();
		//skyTest();
	}
	private static void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void newTest() throws Exception
	{
		/*OkHttpSender sender = OkHttpSender.create(zipkinUrl+"/api/v2/spans");
		AsyncReporter spanReporter = AsyncReporter.create(sender);

		Tracing tracing = Tracing.newBuilder()
				.localServiceName("my-service")
				.spanReporter(spanReporter)
				.build();

		Tracer tracer  = tracing.tracer();*/


		Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName("tracer-gw")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();
		Tracer gw_tracer = gw_tracing.tracer();




		/*Tracing server1_tracing = Tracing.newBuilder()
				.localServiceName("tracer-server1")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name2"))
				.build();
		Tracer server1_tracer = server1_tracing.tracer();*/

		/*Span span = gw_tracer.newTrace().name("encode").start();
		try {
			doSomethingExpensive();
		} finally {
			span.finish();
		}
*/


		Span twoPhase = gw_tracer.newTrace().name("twoPhase").start();
		sleep(10);

		try {

			/*Span prepare = gw_tracer.newChild(twoPhase.context()).name("prepare").start();
			try {
				prepare();
			}
			finally {
				prepare.finish();
			}*/


			Span commit = gw_tracer.newChild(twoPhase.context()).name("commit").start();
			try {
				timeElapsed(300);
			}
			finally {
				commit.finish();
			}

			/*new Thread(()-> {
				Span prepare = server1_tracer.newChild(twoPhase.context()).name("prepare").start();
				try {
					prepare();
				}
				finally {
					prepare.finish();
				}

				Span commit = server1_tracer.newChild(twoPhase.context()).name("commit").start();
				try {
					commit();
				}
				finally {
					commit.finish();
				}
			}).start();*/


		} finally {
			twoPhase.finish();
		}


		sleep(3000);

	}


	private static void multipleNodeTest() throws Exception
	{
		/*OkHttpSender sender = OkHttpSender.create(zipkinUrl+"/api/v2/spans");
		AsyncReporter spanReporter = AsyncReporter.create(sender);

		Tracing tracing = Tracing.newBuilder()
				.localServiceName("my-service")
				.spanReporter(spanReporter)
				.build();

		Tracer tracer  = tracing.tracer();*/


		Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");


		AsyncReporter asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing gw_tracing = Tracing.newBuilder()
				.localServiceName("tracer-gw")
				.spanReporter(asyncReporter)

				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();

		Tracer gw_tracer = gw_tracing.tracer();




		Tracing server1_tracing = Tracing.newBuilder()
				.localServiceName("tracer-server1")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name2"))
				.build();
		Tracer server1_tracer = server1_tracing.tracer();

		/*Span span = gw_tracer.newTrace().name("encode").start();
		try {
			doSomethingExpensive();
		} finally {
			span.finish();
		}
*/


		Span processSpan = gw_tracer.newTrace().name("process").start();
		sleep(10);

		try {

			/*Span prepare = gw_tracer.newChild(twoPhase.context()).name("prepare").start();
			try {
				prepare();
			}
			finally {
				prepare.finish();
			}*/


			Span innerProcess = gw_tracer.newChild(processSpan.context()).name("innerProcess").start();
			try {
				timeElapsed(200);
			}
			finally {
				innerProcess.finish();
			}

			//new Thread(()-> {
				Span server_func1 = server1_tracer.newChild(processSpan.context()).name("server_func1").start();
				try {
					timeElapsed(300);
				}
				finally {
					server_func1.finish();
				}

				Span server_func2 = server1_tracer.newChild(processSpan.context()).name("server_func2").start();
				try {
					timeElapsed(400);
				}
				finally {
					server_func2.finish();
				}
			//}).start();


		} finally {
			processSpan.finish();
		}

		System.out.println("finished.");
		sleep(3000);

	}


	private static void timeElapsed(int elapsed){
		sleep(elapsed);
	}
	private static void doSomethingExpensive() {
		sleep(200);
	}

	private static void innerProcess() {
		sleep(300);
	}

	private static void prepare() {
		sleep(400);
	}

}
