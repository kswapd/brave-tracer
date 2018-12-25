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
		newTest();
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
				.closeTimeout(500, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		Tracing tracing = Tracing.newBuilder()
				.localServiceName("tracer-demo")
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				//.currentTraceContext(ThreadContextCurrentTraceContext.create())
				.build();
		Tracer tracer = tracing.tracer();
		Span span = tracer.newTrace().name("encode").start();
		try {
			doSomethingExpensive();
		} finally {
			span.finish();
		}


		Span twoPhase = tracer.newTrace().name("twoPhase").start();
		try {
			Span prepare = tracer.newChild(twoPhase.context()).name("prepare").start();
			try {
				prepare();
			} finally {
				prepare.finish();
			}
			Span commit = tracer.newChild(twoPhase.context()).name("commit").start();
			try {
				commit();
			} finally {
				commit.finish();
			}
		} finally {
			twoPhase.finish();
		}


		sleep(1000);

	}



	private static void doSomethingExpensive() {
		sleep(500);
	}

	private static void commit() {
		sleep(500);
	}

	private static void prepare() {
		sleep(500);
	}

}
