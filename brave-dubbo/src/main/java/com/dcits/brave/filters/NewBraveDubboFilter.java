package com.dcits.brave.filters;


import brave.Span;
import brave.Span.Kind;
import brave.Tracer;
import brave.Tracing;
import brave.internal.Platform;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;
import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.protocol.dubbo.FutureAdapter;
import com.alibaba.dubbo.rpc.support.RpcUtils;
import com.alibaba.fastjson.JSON;
import com.dcits.brave.dubbo.support.ClientRequestCommonData;
import com.dcits.galaxy.base.data.BaseRequest;
import com.dcits.galaxy.base.data.ISysHead;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

//@Activate(group = {Constants.PROVIDER, Constants.CONSUMER}, value = "tracing")
// http://dubbo.apache.org/en-us/docs/dev/impls/filter.html
// public constructor permitted to allow dubbo to instantiate this
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public final class NewBraveDubboFilter implements Filter {



	Tracer tracer = null;
	TraceContext.Extractor<Map<String, String>> extractor;
	TraceContext.Injector<Map<String, String>> injector;

	/**
	 * {@link ExtensionLoader} supplies the tracing implementation which must be named "tracing". For
	 * example, if using the {@link SpringExtensionFactory}, only a bean named "tracing" will be
	 * injected.
	 */
	public void setTracing(Tracing tracing) {
		tracer = tracing.tracer();
		extractor = tracing.propagation().extractor(GETTER);
		injector = tracing.propagation().injector(SETTER);
	}



	public static String getObjectJsonStr(Object obj)
	{
		String jsonStr = null;
		BeanMap beanMap = BeanMap.create(obj);
		Map<String, Object> map = Maps.newHashMap();
		for (Object key : beanMap.keySet()) {
			map.put(key + "", beanMap.get(key));
		}


		if (map.size() > 0) {
			jsonStr = JSON.toJSONString(map);
		}
		return jsonStr;
	}

	public static String getObjectMapStr(Object obj)
	{
		String jsonStr = null;
		Map map = (Map)obj;

		if (map.size() > 0) {
			jsonStr = JSON.toJSONString(map);
		}
		return jsonStr;
	}


	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		//if (tracer == null) return invoker.invoke(invocation);

		if(tracer == null) {
			ApplicationContext context = ServiceBean.getSpringContext();
			Tracing tracing = (Tracing) context.getBean("tracing");
			setTracing(tracing);
		}
		RpcContext rpcContext = RpcContext.getContext();
		Kind kind = rpcContext.isProviderSide() ? Kind.SERVER : Kind.CLIENT;
		final Span span;
		if (kind.equals(Kind.CLIENT)) {
			span = tracer.nextSpan();
			injector.inject(span.context(), invocation.getAttachments());


			String methodName = RpcContext.getContext().getMethodName();

			if(methodName.equals("process")){


				if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[0] != null) {
					String jsonStr = getObjectJsonStr(RpcContext.getContext().getArguments()[0]);
					if (jsonStr != null) {
						span.tag("REQUEST_INFO", jsonStr);
					}

					BaseRequest br = (BaseRequest) RpcContext.getContext().getArguments()[0];
					ISysHead sh = br.getSysHead();

					if (!StringUtils.isEmpty(sh.getThreadNo())) {
						span.tag("THREAD_NO", sh.getThreadNo());
					}
					if (!StringUtils.isEmpty(sh.getTranTimestamp())) {
						span.tag("TRAN_TIMESTAMP", sh.getTranTimestamp());
					}
					if (!StringUtils.isEmpty(sh.getUserLang())) {
						span.tag("USER_LANG", sh.getUserLang());
					}
					if (!StringUtils.isEmpty(sh.getSeqNo())) {
						span.tag("SEQ_NO", sh.getSeqNo());
					}
					if (!StringUtils.isEmpty(sh.getProgramId())) {
						span.tag("PROGRAM_ID", sh.getProgramId());
					}

					if (!StringUtils.isEmpty(sh.getSourceBranchNo())) {
						span.tag("SOURCE_BRANCH_NO", sh.getSourceBranchNo());
					}
					if (!StringUtils.isEmpty(sh.getDestBranchNo())) {
						span.tag("DEST_BRANCH_NO", sh.getDestBranchNo());
					}
					if (!StringUtils.isEmpty(sh.getServiceCode())) {
						span.tag("SERVICE_CODE", sh.getServiceCode());
					}
					if (!StringUtils.isEmpty(sh.getMessageType())) {
						span.tag("MESSAGE_TYPE", sh.getMessageType());
					}
					if (!StringUtils.isEmpty(sh.getMessageCode())) {
						span.tag("MESSAGE_CODE", sh.getMessageCode());
					}
					if (!StringUtils.isEmpty(sh.getTranMode())) {
						span.tag("TRAN_MODE", sh.getTranMode());
					}
					if (!StringUtils.isEmpty(sh.getSourceType())) {
						span.tag("SOURCE_TYPE", sh.getSourceType());
					}
					if (!StringUtils.isEmpty(sh.getBranchId())) {
						span.tag("BRANCH_ID", sh.getBranchId());
					}
					if (!StringUtils.isEmpty(sh.getUserId())) {
						span.tag("USER_ID", sh.getUserId());
					}
					if (!StringUtils.isEmpty(sh.getTranDate())) {
						span.tag("TRAN_DATE", sh.getTranDate());
					}
				}

			}else if(methodName.equals("$invoke")){
				Map<String, Object> map = Maps.newHashMap();
				if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[2] != null) {
					//br = (BaseRequest) RpcContext.getContext().getArguments()[0];
					String jsonStr = getObjectMapStr(((Object[])(RpcContext.getContext().getArguments()[2]))[0]);
					if(jsonStr != null){
						span.tag("REQUEST_INFO", jsonStr);
					}

					Map params = (Map)((Object[])(RpcContext.getContext().getArguments()[2]))[0];
					Map sysHead = null;
					if(params.get("sysHead") != null){
						sysHead = (Map)params.get("sysHead");
					}


					if (!StringUtils.isEmpty(sysHead.get("threadNo"))) {
						span.tag("THREAD_NO", (String)sysHead.get("threadNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("tranTimestamp"))) {
						span.tag("TRAN_TIMESTAMP", (String)sysHead.get("threadNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("userLang"))) {
						span.tag("USER_LANG", (String)sysHead.get("userLang"));
					}
					if (!StringUtils.isEmpty(sysHead.get("seqNo"))) {
						span.tag("SEQ_NO", (String)sysHead.get("seqNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("programId"))) {
						span.tag("PROGRAM_ID", (String)sysHead.get("programId"));
					}

					if (!StringUtils.isEmpty(sysHead.get("sourceBranchNo"))) {
						span.tag("SOURCE_BRANCH_NO", (String)sysHead.get("sourceBranchNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("destBranchNo"))) {
						span.tag("DEST_BRANCH_NO", (String)sysHead.get("destBranchNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("serviceCode"))) {
						span.tag("SERVICE_CODE", (String)sysHead.get("serviceCode"));
					}
					if (!StringUtils.isEmpty(sysHead.get("messageType"))) {
						span.tag("MESSAGE_TYPE", (String)sysHead.get("messageType"));
					}
					if (!StringUtils.isEmpty(sysHead.get("messageCode"))) {
						span.tag("MESSAGE_CODE", (String)sysHead.get("messageCode"));
					}
					if (!StringUtils.isEmpty(sysHead.get("tranMode"))) {
						span.tag("TRAN_MODE", (String)sysHead.get("tranMode"));
					}
					if (!StringUtils.isEmpty(sysHead.get("sourceType"))) {
						span.tag("SOURCE_TYPE", (String)sysHead.get("sourceType"));
					}
					if (!StringUtils.isEmpty(sysHead.get("branchId"))) {
						span.tag("BRANCH_ID", (String)sysHead.get("branchId"));
					}
					if (!StringUtils.isEmpty(sysHead.get("userId"))) {
						span.tag("USER_ID", (String)sysHead.get("userId"));
					}
					if (!StringUtils.isEmpty(sysHead.get("tranDate"))) {
						span.tag("TRAN_DATE", (String)sysHead.get("tranDate"));
					}
				}

			}





		} else {
			TraceContextOrSamplingFlags extracted = extractor.extract(invocation.getAttachments());
			span = extracted.context() != null
					? tracer.joinSpan(extracted.context())
					: tracer.nextSpan(extracted);
		}

		if (!span.isNoop()) {
			span.kind(kind);
			String service = invoker.getInterface().getSimpleName();
			String method = RpcUtils.getMethodName(invocation);
			span.name(service + "/" + method);
			parseRemoteAddress(rpcContext, span);
			span.start();
		}

		boolean isOneway = false, deferFinish = false;
		try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
			Result result = invoker.invoke(invocation);
			if (result.hasException()) {
				onError(result.getException(), span);
			}
			isOneway = RpcUtils.isOneway(invoker.getUrl(), invocation);
			Future<Object> future = rpcContext.getFuture(); // the case on async client invocation
			if (future instanceof FutureAdapter) {
				deferFinish = true;
				((FutureAdapter) future).getFuture().setCallback(new FinishSpanCallback(span));
			}

			if (kind.equals(Kind.CLIENT)) {

				if (invocation.getMethodName() != null && invocation.getMethodName().equals("process")) {
					String jsonStr = getObjectJsonStr(result.getResult());
					if (jsonStr != null) {
						span.tag("RESPONSE_INFO", jsonStr);
					}
				}
				else if (invocation.getMethodName() != null && invocation.getMethodName().equals("$invoke")) {
					String jsonStr = getObjectMapStr(result.getResult());
					if (jsonStr != null) {
						span.tag("RESPONSE_INFO", jsonStr);
					}
				}
			}


			return result;
		} catch (Error | RuntimeException e) {
			onError(e, span);
			throw e;
		} finally {
			if (isOneway) {
				span.flush();
			} else if (!deferFinish) {
				span.finish();
			}
		}
	}

	static void parseRemoteAddress(RpcContext rpcContext, Span span) {
		InetSocketAddress remoteAddress = rpcContext.getRemoteAddress();
		if (remoteAddress == null) return;
		span.remoteIpAndPort(Platform.get().getHostString(remoteAddress), remoteAddress.getPort());
	}

	static void onError(Throwable error, Span span) {
		span.error(error);
		if (error instanceof RpcException) {
			span.tag("dubbo.error_code", Integer.toString(((RpcException) error).getCode()));
		}
	}

	static final Propagation.Getter<Map<String, String>, String> GETTER =
			new Propagation.Getter<Map<String, String>, String>() {
				@Override
				public String get(Map<String, String> carrier, String key) {
					return carrier.get(key);
				}

				@Override
				public String toString() {
					return "Map::get";
				}
			};

	static final Propagation.Setter<Map<String, String>, String> SETTER =
			new Propagation.Setter<Map<String, String>, String>() {
				@Override
				public void put(Map<String, String> carrier, String key, String value) {
					carrier.put(key, value);
				}

				@Override
				public String toString() {
					return "Map::set";
				}
			};

	static final class FinishSpanCallback implements ResponseCallback {
		final Span span;

		FinishSpanCallback(Span span) {
			this.span = span;
		}

		@Override public void done(Object response) {
			span.finish();
		}

		@Override public void caught(Throwable exception) {
			onError(exception, span);
			span.finish();
		}
	}
}
