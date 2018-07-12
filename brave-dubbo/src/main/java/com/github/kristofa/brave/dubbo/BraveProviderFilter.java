package com.github.kristofa.brave.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.github.kristofa.brave.*;
import com.twitter.zipkin.gen.Span;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import static com.github.kristofa.brave.IdConversion.convertToLong;

/**
 * Created by chenjg on 16/7/24.
 */

@Activate(group = Constants.PROVIDER)
public class BraveProviderFilter implements Filter {


    private static volatile Brave brave;
    private static volatile ServerRequestInterceptor serverRequestInterceptor;
    private static volatile ServerResponseInterceptor serverResponseInterceptor;
    private static volatile ServerSpanThreadBinder serverSpanThreadBinder;



    public static void setBrave(Brave brave) {
        BraveProviderFilter.brave = brave;
        BraveProviderFilter.serverRequestInterceptor = brave.serverRequestInterceptor();
        BraveProviderFilter.serverResponseInterceptor = brave.serverResponseInterceptor();
        BraveProviderFilter.serverSpanThreadBinder = brave.serverSpanThreadBinder();
    }


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        serverRequestInterceptor.handle(new DubboServerRequestAdapter(invoker,invocation,brave.serverTracer()));

        /*final String parentId = invocation.getAttachment("parentId");
        final String spanId = invocation.getAttachment("spanId");
        final String traceId = invocation.getAttachment("traceId");
        SpanId spanIds =ServerRequestAdapterImpl.getSpanId(traceId,spanId,parentId);

        serverRequestInterceptor.handle(new ServerRequestAdapterImpl("oooo", spanIds));*/
        Result rpcResult = invoker.invoke(invocation);
       // serverResponseInterceptor.handle(new ServerResponseAdapterImpl());
          serverResponseInterceptor.handle(new DubboServerResponseAdapter(rpcResult));






          return rpcResult;
    }


}
