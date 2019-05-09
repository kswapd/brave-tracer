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
import com.dcits.galaxy.base.data.BaseRequest;
import com.dcits.galaxy.base.data.ISysHead;
import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	private static Pattern pattern_ret = Pattern.compile("\"retCode\"\\s*:\\s*\"(.*?)\\s*\",.*retMsg\"\\s*:\\s*\"(.*?)\\s*\"");
	private static Pattern pattern_ret_handle = Pattern.compile("\"RET_CODE\"\\s*:\\s*\"(.*?)\\s*\",.*RET_MSG\"\\s*:\\s*\"(.*?)\\s*\"");

	private static Pattern pattern_status = Pattern.compile("\"retStatus\"\\s*:\\s*\"(.*?)\\s*\"");

	private static Pattern pattern_status_handle = Pattern.compile("\"RET_STATUS\"\\s*:\\s*\"(.*?)\\s*\"");

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


	public static String getObjectJsonStr(Object obj) {
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

	public static Map<String, Object> getObjectMap(Object obj) {
		String jsonStr = null;
		BeanMap beanMap = BeanMap.create(obj);
		Map<String, Object> map = Maps.newHashMap();
		for (Object key : beanMap.keySet()) {
			map.put(key + "", beanMap.get(key));
		}
		return map;
	}

	public static String getObjectMapStr(Object obj) {
		String jsonStr = null;
		Map map = (Map) obj;

		if (map.size() > 0) {
			jsonStr = JSON.toJSONString(map);
		}
		return jsonStr;
	}


	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		//if (tracer == null) return invoker.invoke(invocation);

		if (tracer == null) {
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

			if (methodName.equals("process")) {


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

			}
			else if (methodName.equals("$invoke")) {
				Map<String, Object> map = Maps.newHashMap();
				if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[2] != null) {
					//br = (BaseRequest) RpcContext.getContext().getArguments()[0];
					String jsonStr = getObjectMapStr(((Object[]) (RpcContext.getContext().getArguments()[2]))[0]);
					if (jsonStr != null) {
						span.tag("REQUEST_INFO", jsonStr);
					}

					Map params = (Map) ((Object[]) (RpcContext.getContext().getArguments()[2]))[0];
					Map sysHead = null;
					if (params.get("sysHead") != null) {
						sysHead = (Map) params.get("sysHead");
					}


					if (!StringUtils.isEmpty(sysHead.get("threadNo"))) {
						span.tag("THREAD_NO", (String) sysHead.get("threadNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("tranTimestamp"))) {
						span.tag("TRAN_TIMESTAMP", (String) sysHead.get("tranTimestamp"));
					}
					if (!StringUtils.isEmpty(sysHead.get("userLang"))) {
						span.tag("USER_LANG", (String) sysHead.get("userLang"));
					}
					if (!StringUtils.isEmpty(sysHead.get("seqNo"))) {
						span.tag("SEQ_NO", (String) sysHead.get("seqNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("programId"))) {
						span.tag("PROGRAM_ID", (String) sysHead.get("programId"));
					}

					if (!StringUtils.isEmpty(sysHead.get("sourceBranchNo"))) {
						span.tag("SOURCE_BRANCH_NO", (String) sysHead.get("sourceBranchNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("destBranchNo"))) {
						span.tag("DEST_BRANCH_NO", (String) sysHead.get("destBranchNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("serviceCode"))) {
						span.tag("SERVICE_CODE", (String) sysHead.get("serviceCode"));
					}
					if (!StringUtils.isEmpty(sysHead.get("messageType"))) {
						span.tag("MESSAGE_TYPE", (String) sysHead.get("messageType"));
					}
					if (!StringUtils.isEmpty(sysHead.get("messageCode"))) {
						span.tag("MESSAGE_CODE", (String) sysHead.get("messageCode"));
					}
					if (!StringUtils.isEmpty(sysHead.get("tranMode"))) {
						span.tag("TRAN_MODE", (String) sysHead.get("tranMode"));
					}
					if (!StringUtils.isEmpty(sysHead.get("sourceType"))) {
						span.tag("SOURCE_TYPE", (String) sysHead.get("sourceType"));
					}
					if (!StringUtils.isEmpty(sysHead.get("branchId"))) {
						span.tag("BRANCH_ID", (String) sysHead.get("branchId"));
					}
					if (!StringUtils.isEmpty(sysHead.get("userId"))) {
						span.tag("USER_ID", (String) sysHead.get("userId"));
					}
					if (!StringUtils.isEmpty(sysHead.get("tranDate"))) {
						span.tag("TRAN_DATE", (String) sysHead.get("tranDate"));
					}
				}

			}
			else if (methodName.equals("handle")) {

				Map<String, Object> map = Maps.newHashMap();
				if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[0] != null) {
					//br = (BaseRequest) RpcContext.getContext().getArguments()[0];
					String jsonStr = getObjectMapStr(RpcContext.getContext().getArguments()[0]);
					if (jsonStr != null) {
						span.tag("REQUEST_INFO", jsonStr);
					}

					Map params = (Map) (RpcContext.getContext().getArguments()[0]);
					Map sysHead = null;
					if (params.get("SYS_HEAD") != null) {
						sysHead = (Map) params.get("SYS_HEAD");
					}


					if (!StringUtils.isEmpty(sysHead.get("THREAD_NO"))) {
						span.tag("THREAD_NO", (String) sysHead.get("threadNo"));
					}
					if (!StringUtils.isEmpty(sysHead.get("TRAN_TIMESTAMP"))) {
						span.tag("TRAN_TIMESTAMP", (String) sysHead.get("TRAN_TIMESTAMP"));
					}
					if (!StringUtils.isEmpty(sysHead.get("USER_LANG"))) {
						span.tag("USER_LANG", (String) sysHead.get("USER_LANG"));
					}
					if (!StringUtils.isEmpty(sysHead.get("SEQ_NO"))) {
						span.tag("SEQ_NO", (String) sysHead.get("SEQ_NO"));
					}
					if (!StringUtils.isEmpty(sysHead.get("PROGRAM_ID"))) {
						span.tag("PROGRAM_ID", (String) sysHead.get("PROGRAM_ID"));
					}

					if (!StringUtils.isEmpty(sysHead.get("SOURCE_BRANCH_NO"))) {
						span.tag("SOURCE_BRANCH_NO", (String) sysHead.get("SOURCE_BRANCH_NO"));
					}
					if (!StringUtils.isEmpty(sysHead.get("DEST_BRANCH_NO"))) {
						span.tag("DEST_BRANCH_NO", (String) sysHead.get("DEST_BRANCH_NO"));
					}
					if (!StringUtils.isEmpty(sysHead.get("SERVICE_CODE"))) {
						span.tag("SERVICE_CODE", (String) sysHead.get("SERVICE_CODE"));
					}
					if (!StringUtils.isEmpty(sysHead.get("MESSAGE_TYPE"))) {
						span.tag("MESSAGE_TYPE", (String) sysHead.get("MESSAGE_TYPE"));
					}
					if (!StringUtils.isEmpty(sysHead.get("MESSAGE_CODE"))) {
						span.tag("MESSAGE_CODE", (String) sysHead.get("MESSAGE_CODE"));
					}
					if (!StringUtils.isEmpty(sysHead.get("TRAN_MODE"))) {
						span.tag("TRAN_MODE", (String) sysHead.get("TRAN_MODE"));
					}
					if (!StringUtils.isEmpty(sysHead.get("SOURCE_TYPE"))) {
						span.tag("SOURCE_TYPE", (String) sysHead.get("SOURCE_TYPE"));
					}
					if (!StringUtils.isEmpty(sysHead.get("BRANCH_ID"))) {
						span.tag("BRANCH_ID", (String) sysHead.get("BRANCH_ID"));
					}
					if (!StringUtils.isEmpty(sysHead.get("USER_ID"))) {
						span.tag("USER_ID", (String) sysHead.get("USER_ID"));
					}
					if (!StringUtils.isEmpty(sysHead.get("TRAN_DATE"))) {
						span.tag("TRAN_DATE", (String) sysHead.get("TRAN_DATE"));
					}
				}

			}


		}
		else {
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
				if (invocation != null && invocation.getMethodName() != null && invocation.getMethodName().equals("process")) {
					String jsonStr = getObjectJsonStr(result.getResult());
					if (jsonStr != null) {
						span.tag("RESPONSE_INFO", jsonStr);

						Matcher matcher = pattern_ret.matcher(jsonStr);
						if (matcher.find() && matcher.groupCount() > 1) {
							span.tag("RET_CODE", matcher.group(1));
							span.tag("RET_MSG", matcher.group(2));
						}
						matcher = pattern_status.matcher(jsonStr);
						if (matcher.find()) {
							span.tag("RET_STATUS", matcher.group(1));
						}
					}
				}
				else if (invocation != null && invocation.getMethodName() != null && invocation.getMethodName().equals("$invoke")) {
					String jsonStr = getObjectMapStr(result.getResult());
					if (jsonStr != null) {
						span.tag("RESPONSE_INFO", jsonStr);

						Matcher matcher = pattern_ret.matcher(jsonStr);
						if (matcher.find() && matcher.groupCount() > 1) {
							span.tag("RET_CODE", matcher.group(1));
							span.tag("RET_MSG", matcher.group(2));
						}
						matcher = pattern_status.matcher(jsonStr);
						if (matcher.find()) {
							span.tag("RET_STATUS", matcher.group(1));
						}
					}
				}
				else if (invocation != null && invocation.getMethodName() != null && invocation.getMethodName().equals("handle")) {
					String jsonStr = getObjectMapStr(result.getResult());
					if (jsonStr != null) {
						span.tag("RESPONSE_INFO", jsonStr);
                /*Map map = (Map)rpcResult.getResult();
                if(map.get("retJsonObject") != null ){
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RETCODE",(String)((Map)map.get("retJsonObject")).get("retCode"));
                    annotations.add(kCode);

                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RETMSG",(String)((Map)map.get("retJsonObject")).get("retMsg"));
                    annotations.add(kCode);
                }*/
						Matcher matcher = pattern_ret_handle.matcher(jsonStr);
						if (matcher.find() && matcher.groupCount() > 1) {
							span.tag("RET_CODE", matcher.group(1));
							span.tag("RET_MSG", matcher.group(2));
						}
						matcher = pattern_status_handle.matcher(jsonStr);
						if (matcher.find()) {
							span.tag("RET_STATUS", matcher.group(1));
						}
					}
				}
				if (result.hasException() && result.getException().getMessage() != null) {
					span.tag("exception", result.getException().getMessage());
					span.tag("status", "failed");

				}
				else {
					span.tag("status", "success");
				}
			}


			return result;
		}
		catch (Error | RuntimeException e) {
			onError(e, span);
			throw e;
		}
		finally {
			if (isOneway) {
				span.flush();
			}
			else if (!deferFinish) {
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

		@Override
		public void done(Object response) {
			span.finish();
		}

		@Override
		public void caught(Throwable exception) {
			onError(exception, span);
			span.finish();
		}
	}
}
