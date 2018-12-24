package apps;

/*import brave.Tracer;
import brave.Tracing;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;*/

public class AppNew {
	/*private static String zipkinUrl = "http://127.0.0.1:9411";
	public static void main(String[] args) throws Exception {
		//complicatedTest();
		//simpleTest();
		newTest();
		//internalTest();
		//skyTest();
	}
	private static void newTest() throws Exception
	{
		OkHttpSender sender = OkHttpSender.create(zipkinUrl+"/api/v2/spans");
		AsyncReporter spanReporter = AsyncReporter.create(sender);

		Tracing tracing = Tracing.newBuilder()
				.localServiceName("my-service")
				.spanReporter(spanReporter)
				.build();

		Tracer tracer  = tracing.tracer();

// Failing to close resources can result in dropped spans! When tracing is no
// longer needed, close the components you made in reverse order. This might be
// a shutdown hook for some users.
		tracing.close();
		spanReporter.close();
		sender.close();
	}*/
}
