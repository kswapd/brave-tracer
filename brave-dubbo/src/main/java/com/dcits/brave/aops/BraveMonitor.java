package com.dcits.brave.aops;

import com.dcits.brave.dubbo.BraveProviderFilter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.aspectj.weaver.AdviceKind.Around;

/**
 * Created by kongxiangwen on 9/17/18 w:38.
 */
class ClientRequestAdapterImpl implements ClientRequestAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ClientRequestAdapterImpl.class);
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
			logger.debug(String.format("ClientRequestAdapterImpl:addSpanIdToRequest:trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));
		}else {
			logger.debug(String.format("ClientRequestAdapterImpl:addSpanIdToRequest: null"));
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
	private static final Logger logger = LoggerFactory.getLogger(ServerRequestAdapterImpl.class);
   Random randomGenerator = new Random();
   SpanId spanId;
   String spanName;

   ServerRequestAdapterImpl(String spanName){
	   this.spanName = spanName;
	   long startId = randomGenerator.nextLong();
	   SpanId spanId = SpanId.builder().spanId(startId).traceId(startId).parentId(startId).build();
	   this.spanId = spanId;
	   logger.debug(String.format("ServerRequestAdapterImpl:trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

   }

   ServerRequestAdapterImpl(String spanName, SpanId spanId){
	   this.spanName = spanName;
	   this.spanId = spanId;
   }


   public TraceData getTraceData() {
	   if (this.spanId != null) {
		   logger.debug(String.format("ServerRequestAdapterImpl:getTraceData trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

		   return TraceData.builder().spanId(this.spanId).build();
	   }
	   logger.debug(String.format("ServerRequestAdapterImpl:getTraceData generate trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

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

//@Aspect
//@Component
public class BraveMonitor {
	private static final Logger logger = LoggerFactory.getLogger(BraveMonitor.class);

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
		logger.debug("initing monitor collector ");
	}

	//@Pointcut("execution(public * com.oumyye.service..*.add(..))")
	//@Pointcut("execution(* get*(..))")
	//com.sishuok.common.BaseService+.*()
	//@Pointcut("execution(* com.dcits.processes..*.Process(..))")
	@Pointcut("execution(* com.dcits.orion.api.IProcess+.process(..))")
	public void myMethod(){};


	/*@Before("execution(public void com.oumyye.dao.impl.UserDAOImpl.save(com.oumyye.model.User))")*/
	@Before("myMethod()")
	public void before() {
		logger.debug("method start"+this.getClass().getName());

	}
	@After("myMethod()")
	public void after() {
		logger.debug("method after");

	}
	//@AfterReturning("execution(public * com.oumyye.dao..*.*(..))")
	public void AfterReturning() {
		logger.debug("method AfterReturning");
	}
	//@AfterThrowing("execution(public * com.oumyye.dao..*.*(..))")
	public void AfterThrowing() {
		logger.debug("method AfterThrowing");
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
		logger.debug("::::"+parentImp.getSpanName());
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
		logger.debug("---------------@Around前----------------"+braveToken);
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
			logger.debug("---------------@Around异常----------------");
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

		logger.debug("---------------@Around后----------------");




		return result;
	}

}
