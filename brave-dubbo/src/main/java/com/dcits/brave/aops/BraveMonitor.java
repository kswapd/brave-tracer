package com.dcits.brave.aops;

import com.dcits.brave.annotations.ChainMonitor;
import com.dcits.brave.annotations.ChainTags;
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class BraveMonitor {


	@Value("${zipkin.address}")
	private String zipkinAddress;
	@Value("${zipkin.port}")
	private String zipkinPort;
	@Value("${zipkin.sampleRate}")
	private String zipkinSampleRate;


	@Value("${zipkin.service.name}")
	private String appName;

	private static final Logger logger = LoggerFactory.getLogger(BraveMonitor.class);

	private static HttpSpanCollector collector = null;
	private static Brave brave = null;
	private static ClientRequestAdapterImpl imp = null;
	private Map<String, Object> braveContextData = new ConcurrentHashMap<String, Object>();
	Map<String,String> tagMap = new HashMap<>();
	private void initBrave(String endpointName)
	{

		//brave = new Brave.Builder(endpointName).spanCollector(collector).build();


	}

	@PostConstruct
	private void init()
	{
		String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		collector = HttpSpanCollector.create(zipkinAddr, new EmptySpanCollectorMetricsHandler());
		brave = new Brave.Builder(appName).spanCollector(collector).build();
		logger.debug("initing monitor collector ");
	}

	//@Pointcut("execution(public * com.oumyye.service..*.add(..))")
	//@Pointcut("execution(* get*(..))")
	//com.sishuok.common.BaseService+.*()
	//@Pointcut("execution(* com.dcits.processes..*.Process(..))")
	//@Pointcut("execution(* com.dcits.orion.api.IProcess+.process(..))")
	//@Pointcut("execution(* com.dcits.orion.api.IProcess+.process(..))")
	@Pointcut("@annotation(com.dcits.brave.annotations.ChainMonitor)")
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
		imp = new ClientRequestAdapterImpl(spanName,tagMap);

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

		String methodName = signature.getMethod().getName();
		String className = signature.getDeclaringType().getCanonicalName();
		//String spanName =  method.getName();
		//String braveToken = className+"-"+spanName;

		//String className = rpcContext.getUrl().getPath();
		String simpleClassName = className.substring(className.lastIndexOf(".")+1);
		//String method = rpcContext.getMethodName();

		String spanName = simpleClassName + "."+ methodName;

		//initBrave(className);
		logger.debug("chain monitor start");


			//if(signature.getMethod().isAnnotationPresent(ChainMonitor.class))
			//{

				ChainMonitor cm = (ChainMonitor)signature.getMethod().getAnnotation(ChainMonitor.class);

				if(cm.tags() != null && cm.tags().length > 0){
					for(ChainTags tags:cm.tags()) {
						logger.debug("tags {}:{}", tags.key(),tags.value());
						tagMap.put(tags.key(),tags.value());
					}

				}


			//}
			Brave curBrave = brave;
			clientReq(curBrave,spanName);
			tagMap.clear();
			serverReq(curBrave,imp);


			try {
				result = pjp.proceed();
			} catch (Throwable throwable) {
				logger.debug("chain monitor has exceptions");
				// 监听参数为true则抛出异常，为false则捕获并不抛出异常
				if (pjp.getArgs().length > 0 && !(Boolean) pjp.getArgs()[0]) {
					result = null;
				} else {
					throw throwable;
				}
			}finally {
				serverResp(curBrave);
				clientResp(curBrave);
			}




		logger.debug("chain monitor stop");

		return result;
	}

}
