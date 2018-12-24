package com.dcits.brave.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.github.kristofa.brave.*;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenjg on 16/7/24.
 */
@Activate(group = Constants.CONSUMER)
public class BraveConsumerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(BraveConsumerFilter.class);
    private static final String INVOKE_LEVEL="invoke_level";
    //@Resource(name="brave")
    //private Brave brave;
    private static volatile Brave brave;
    private static volatile String clientName;
    private static volatile ClientRequestInterceptor clientRequestInterceptor;
    private static volatile ClientResponseInterceptor clientResponseInterceptor;
    private static volatile ClientSpanThreadBinder clientSpanThreadBinder;

    public static void setBrave(Brave brave) {
        logger.debug("Setting brave for BraveConsumerFilter.");
        BraveConsumerFilter.brave = brave;
        BraveConsumerFilter.clientRequestInterceptor = brave.clientRequestInterceptor();
        BraveConsumerFilter.clientResponseInterceptor = brave.clientResponseInterceptor();
        BraveConsumerFilter.clientSpanThreadBinder = brave.clientSpanThreadBinder();
        //brave.
    }


    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
            logger.debug("brave filter client request init invoke level");
            RpcContext.getContext().set(INVOKE_LEVEL, 0);
        }else{
            logger.debug("brave filter client request increment invoke level");
            int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
            RpcContext.getContext().set(INVOKE_LEVEL, ref+1);
            logger.debug("brave filter client request current invoke level:{}", (int)RpcContext.getContext().get(INVOKE_LEVEL));

        }
        logger.debug("brave filter client request:{}", RpcContext.getContext().getMethodName());
        DubboClientRequestAdapter adapter = new DubboClientRequestAdapter(invoker,invocation);

        clientRequestInterceptor.handle(adapter);
        try{
            Result rpcResult = invoker.invoke(invocation);

            clientResponseInterceptor.handle(new DubboClientResponseAdapter(rpcResult));

            if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
                logger.debug("brave filter client response invoke level null");
               // RpcContext.getContext().set(INVOKE_LEVEL, 0);
            }else{
                logger.debug("brave filter client response decrement invoke level");
                int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
                RpcContext.getContext().set(INVOKE_LEVEL, ref-1);
                logger.debug("brave filter client response current invoke level:{}", (int)RpcContext.getContext().get(INVOKE_LEVEL));
            }

            logger.debug("brave filter client response:{}", RpcContext.getContext().getMethodName());
            return rpcResult;
        }catch (Exception ex){
            clientResponseInterceptor.handle(new DubboClientResponseAdapter(ex));


            if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
                logger.debug("brave filter client response exception invoke level null");
                // RpcContext.getContext().set(INVOKE_LEVEL, 0);
            }else{
                logger.debug("brave filter client response exception decrement invoke level");
                int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
                RpcContext.getContext().set(INVOKE_LEVEL, ref-1);
                logger.debug("brave filter client response current exception invoke level:{}", (int)RpcContext.getContext().get(INVOKE_LEVEL));
            }

            logger.debug("brave filter client response exception:{}", RpcContext.getContext().getMethodName());
           // throw  ex;
        }finally {

            clientSpanThreadBinder.setCurrentSpan(null);
        }
        return null;
    }
}
