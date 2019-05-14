package com.dcits.brave.aops;

import brave.Span;
import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.dcits.brave.annotations.ChainMonitor;
import com.dcits.brave.annotations.ChainTags;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;


@Aspect
@Component
public class LocalMethodTracing implements ApplicationContextAware {



	@Value("${zipkin.address}")
	private String zipkinAddress;
	@Value("${zipkin.port}")
	private String zipkinPort;
	@Value("${zipkin.sampleRate}")
	private String zipkinSampleRate;


	@Value("${zipkin.service.name}")
	private String appName;

	private ApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(LocalMethodTracing.class);


	private Map<String, Object> braveContextData = new ConcurrentHashMap<String, Object>();

	Sender sender;
	AsyncReporter asyncReporter;
	Tracing tracing;

	ThreadLocal<Stack<Span>> spanInfo = new ThreadLocal<>();

	private void initBrave(String endpointName) {

		//brave = new Brave.Builder(endpointName).spanCollector(collector).build();


	}

	@PostConstruct
	private void init() {
		//String zipkinAddr = "http://"+zipkinAddress+":"+zipkinPort+"/";
		//collector = HttpSpanCollector.create(zipkinAddr, new EmptySpanCollectorMetricsHandler());
		//brave = new Brave.Builder(appName).spanCollector(collector).build();


		String zipkinAddr = "http://" + zipkinAddress + ":" + zipkinPort + "/";
		sender = OkHttpSender.create(zipkinAddr + "api/v2/spans");


		asyncReporter = AsyncReporter.builder(sender)
				.closeTimeout(5000, TimeUnit.MILLISECONDS)
				.build(SpanBytesEncoder.JSON_V2);

		//Tracing.current();

		/*tracing = Tracing.newBuilder()
				.localServiceName(appName)
				.spanReporter(asyncReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
				.build();*/

		tracing = context.getBean(Tracing.class);


		logger.debug("chain monitor start local method tracing.{}", zipkinAddr);


	}

	//@Pointcut("execution(public * com.oumyye.service..*.add(..))")
	//@Pointcut("execution(* get*(..))")
	//com.sishuok.common.BaseService+.*()
	//@Pointcut("execution(* com.dcits.services..*.Process(..))")
	//@Pointcut("execution(* com.dcits.orion.api.IProcess+.process(..))")
	//@Pointcut("execution(* com.dcits.orion.api.IProcess+.process(..))")
	@Pointcut("@annotation(com.dcits.brave.annotations.ChainMonitor)")
	public void myMethod() {
	}

	;


	/*@Before("execution(public void com.oumyye.dao.impl.UserDAOImpl.save(com.oumyye.model.User))")*/
	@Before("myMethod()")
	public void before() {
		logger.debug("method start" + this.getClass().getName());

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


	@Around("myMethod()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		Object result;
		MethodSignature signature = (MethodSignature) pjp.getSignature();

		String methodName = signature.getMethod().getName();
		String className = signature.getDeclaringType().getCanonicalName();
		//String spanName =  method.getName();
		//String braveToken = className+"-"+spanName;

		//String className = rpcContext.getUrl().getPath();
		String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
		//String method = rpcContext.getMethodName();

		String spanName = simpleClassName + "." + methodName;

		//initBrave(className);


		Span span;
		if (spanInfo.get() == null) {
			spanInfo.set(new Stack<Span>());
		}
		if (spanInfo.get().size() == 0) {
			TraceContext context = tracing.currentTraceContext().get();
			if (context == null) {
				span = tracing.tracer().newTrace().name(spanName).start();
			}
			else {
				span = tracing.tracer().newChild(tracing.currentTraceContext().get()).name(spanName).start();
			}


			logger.debug("chain monitor new stack.{}", Thread.currentThread().getId());
		}
		else {
			Span parentSpan = spanInfo.get().peek();
			span = tracing.tracer().newChild(parentSpan.context()).name(spanName).start();
			//spanInfo.get().push(span);
			logger.debug("chain monitor add stack.{},{}", spanInfo.get().size(), Thread.currentThread().getId());
		}


		ChainMonitor cm = (ChainMonitor) signature.getMethod().getAnnotation(ChainMonitor.class);

		if (cm.tags() != null && cm.tags().length > 0) {
			for (ChainTags tags : cm.tags()) {
				logger.debug("tags {}:{}", tags.key(), tags.value());
				span.tag(tags.key(), tags.value());
			}

		}

		spanInfo.get().push(span);


		try {
			result = pjp.proceed();
		}
		catch (Throwable throwable) {
			logger.debug("chain monitor  exception");
			// 监听参数为true则抛出异常，为false则捕获并不抛出异常
			if (pjp.getArgs().length > 0 && !(Boolean) pjp.getArgs()[0]) {
				result = null;
			}
			else {
				throw throwable;
			}
		}
		finally {
			span.finish();
			if (spanInfo.get() != null && spanInfo.get().size() > 0) {
				logger.debug("chain monitor pop stack.{},{}", spanInfo.get().size(), Thread.currentThread().getId());
				spanInfo.get().pop();
				if (spanInfo.get().size() == 0) {
					spanInfo.remove();
				}
			}
		}

		return result;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
