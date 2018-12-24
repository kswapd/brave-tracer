package com.dcits.brave.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.dcits.brave.tracers.BraveTracer;
import com.github.kristofa.brave.*;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.github.kristofa.brave.IdConversion.convertToLong;

/**
 * Created by chenjg on 16/7/24.
 */

@Activate(group = Constants.PROVIDER)
public class BraveProviderFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(BraveProviderFilter.class);
    private static final String INVOKE_LEVEL="invoke_level";


    //@Resource(name="brave")
    //private Brave brave;
    private static volatile Brave brave;
    private static volatile ServerRequestInterceptor serverRequestInterceptor;
    private static volatile ServerResponseInterceptor serverResponseInterceptor;
    private static volatile ServerSpanThreadBinder serverSpanThreadBinder;



    public static void setBrave(Brave brave) {
        logger.debug("Setting brave for BraveProviderFilter.");
        BraveProviderFilter.brave = brave;
        BraveProviderFilter.serverRequestInterceptor = brave.serverRequestInterceptor();
        BraveProviderFilter.serverResponseInterceptor = brave.serverResponseInterceptor();
        BraveProviderFilter.serverSpanThreadBinder = brave.serverSpanThreadBinder();
    }


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {


        if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
            logger.debug("brave filter server request null invoke level");
        }else{
            int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
            logger.debug("brave filter server request invoke level:{}", ref);
        }


        logger.debug("brave filter server request spans:{},{},{}", invocation.getAttachment("parentId"),invocation.getAttachment("spanId"),invocation.getAttachment("traceId"));

        logger.debug("brave filter server request:{}", RpcContext.getContext().getMethodName());
        serverRequestInterceptor.handle(new DubboServerRequestAdapter(invoker,invocation,brave.serverTracer()));

        /*final String parentId = invocation.getAttachment("parentId");
        final String spanId = invocation.getAttachment("spanId");
        final String traceId = invocation.getAttachment("traceId");
        SpanId spanIds =ServerRequestAdapterImpl.getSpanId(traceId,spanId,parentId);

        serverRequestInterceptor.handle(new ServerRequestAdapterImpl("oooo", spanIds));*/
        Result rpcResult = invoker.invoke(invocation);
       // serverResponseInterceptor.handle(new ServerResponseAdapterImpl());
          serverResponseInterceptor.handle(new DubboServerResponseAdapter(rpcResult));


        if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
            logger.debug("brave filter server response null invoke level");
        }else{
            int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
            logger.debug("brave filter server response invoke level:{}", ref);
        }



        logger.debug("brave filter server response spans:{},{},{}", invocation.getAttachment("parentId"),invocation.getAttachment("spanId"),invocation.getAttachment("traceId"));

        logger.debug("brave filter server response:{}", RpcContext.getContext().getMethodName());



          return rpcResult;
    }


}
