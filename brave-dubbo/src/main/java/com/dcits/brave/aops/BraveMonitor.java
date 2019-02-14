package com.dcits.brave.aops;

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
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
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
		collector = HttpSpanCollector.create("http://127.0.0.1:9411/", new EmptySpanCollectorMetricsHandler());
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
		if(imp == null)
		{
			braveContextData.put(braveToken+"_hasParent","0");
			brave = new Brave.Builder(className).spanCollector(collector).build();
			braveContextData.put(braveToken+"_brave",brave);
		}
		else{

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
