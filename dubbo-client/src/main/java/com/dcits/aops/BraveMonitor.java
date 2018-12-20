package com.dcits.aops;

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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import static org.aspectj.weaver.AdviceKind.Around;

/**
 * Created by kongxiangwen on 9/17/18 w:38.
 */
class ClientRequestAdapterImpl implements ClientRequestAdapter {

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

class ClientResponseAdapterImpl implements ClientResponseAdapter {


   public Collection<KeyValueAnnotation> responseAnnotations() {
	   Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
	   KeyValueAnnotation kv = KeyValueAnnotation.create("client-response", "444444");
	   collection.add(kv);
	   return collection;
   }

}



 class ServerRequestAdapterImpl implements ServerRequestAdapter {

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

 class ServerResponseAdapterImpl implements ServerResponseAdapter {


	public Collection<KeyValueAnnotation> responseAnnotations() {
		Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
		KeyValueAnnotation kv = KeyValueAnnotation.create("server-response", "333333");
		collection.add(kv);
		return collection;
	}

}

@Aspect
@Component
public class BraveMonitor {

	private static HttpSpanCollector collector = null;
	private static Brave brave = null;
	private static ClientRequestAdapterImpl imp = null;
	private Map<String, Object> braveContextData = new ConcurrentHashMap<String, Object>();
	private void initBrave(String endpointName)
	{

		//brave = new Brave.Builder(endpointName).spanCollector(collector).build();


	}

	@PostConstruct
	private void init()
	{
		collector = HttpSpanCollector.create("http://192.168.246.129:9411/", new EmptySpanCollectorMetricsHandler());
		System.out.println("initing monitor...");
	}

	//@Pointcut("execution(public * com.oumyye.service..*.add(..))")
	//@Pointcut("execution(* get*(..))")
	@Pointcut("execution(* com.dcits.processes..*.*(..))")
	public void myMethod(){};


	/*@Before("execution(public void com.oumyye.dao.impl.UserDAOImpl.save(com.oumyye.model.User))")*/
	@Before("myMethod()")
	public void before() {
		System.out.println("method start"+this.getClass().getName());

	}
	@After("myMethod()")
	public void after() {
		System.out.println("method after");

	}
	//@AfterReturning("execution(public * com.oumyye.dao..*.*(..))")
	public void AfterReturning() {
		System.out.println("method AfterReturning");
	}
	//@AfterThrowing("execution(public * com.oumyye.dao..*.*(..))")
	public void AfterThrowing() {
		System.out.println("method AfterThrowing");
	}

	private void clientReq(Brave curBrave,String spanName)
	{

		ClientRequestInterceptor clientRequestInterceptor0 = curBrave.clientRequestInterceptor();
		imp = new ClientRequestAdapterImpl(spanName);

		clientRequestInterceptor0.handle(imp);

	}

	private void clientResp(Brave curBrave)
	{
		ClientResponseInterceptor clientResponseInterceptor0 = curBrave.clientResponseInterceptor();
		clientResponseInterceptor0.handle(new ClientResponseAdapterImpl());
	}




	private void serverReq(Brave curBrave, ClientRequestAdapterImpl parentImp)
	{
		System.out.println("::::"+parentImp.getSpanName());
		ServerRequestAdapterImpl serverReq1 = new ServerRequestAdapterImpl(parentImp.getSpanName(), parentImp.getSpanId());
		ServerRequestInterceptor serverRequestInterceptor2 = curBrave.serverRequestInterceptor();
		serverRequestInterceptor2.handle(serverReq1);
	}

	private void serverResp(Brave curBrave)
	{
		ServerResponseInterceptor serverResponseInterceptor2 = curBrave.serverResponseInterceptor();
		serverResponseInterceptor2.handle(new ServerResponseAdapterImpl());
	}

	@Around("myMethod()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		Object result;
		MethodSignature signature = (MethodSignature) pjp.getSignature();

		Method method = signature.getMethod();
		String className = signature.getDeclaringType().getCanonicalName();
		String spanName =  method.getName();
		String braveToken = className+"-"+spanName;
		//initBrave(className);
		System.out.println("---------------@Around前----------------"+spanName);
		imp = null;
		if(imp == null){
			braveContextData.put(braveToken+"_hasParent","0");
			brave = new Brave.Builder(className).spanCollector(collector).build();

			braveContextData.put(braveToken+"_brave",brave);


		}else{

			braveContextData.put(braveToken+"_hasParent","1");
			brave = new Brave.Builder(className).spanCollector(collector).build();
			braveContextData.put(braveToken+"_brave",brave);
		}


		if(braveContextData.get(braveToken+"_hasParent").equals("0")) {
			Brave curBrave = (Brave)braveContextData.get(braveToken+"_brave");
			clientReq(curBrave,spanName);
			serverReq(curBrave,imp);
			braveContextData.put(braveToken+"_curClientRequestAdapter",imp);
		}else if(braveContextData.get(braveToken+"_hasParent").equals("1")) {
			Brave curBrave = (Brave)braveContextData.get(braveToken+"_brave");
			serverReq(curBrave,imp);
			clientReq(curBrave, spanName);
			braveContextData.put(braveToken+"_curClientRequestAdapter",imp);
		}




		try {
			result = pjp.proceed();
		} catch (Throwable throwable) {
			System.out.println("---------------@Around异常----------------");
			// 监听参数为true则抛出异常，为false则捕获并不抛出异常
			if (pjp.getArgs().length > 0 && !(Boolean) pjp.getArgs()[0]) {
				result = null;
			} else {
				throw throwable;
			}
		}

		 signature = (MethodSignature) pjp.getSignature();
		 method = signature.getMethod();
		 className = signature.getDeclaringType().getCanonicalName();
		 spanName =  method.getName();
		 braveToken = className+"-"+spanName;

		if(braveContextData.get(braveToken+"_hasParent").equals("0")) {
			Brave curBrave = (Brave)braveContextData.get(braveToken+"_brave");
			serverResp(curBrave);
			clientResp(curBrave);
		}else if(braveContextData.get(braveToken+"_hasParent").equals("1")) {
			Brave curBrave = (Brave)braveContextData.get(braveToken+"_brave");
			clientResp(curBrave);
			serverResp(curBrave);

		}

		System.out.println("---------------@Around后----------------");




		return result;
	}

}
