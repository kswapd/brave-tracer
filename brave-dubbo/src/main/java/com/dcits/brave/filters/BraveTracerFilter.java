package com.dcits.brave.filters;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.dcits.brave.dubbo.DubboClientRequestAdapter;
import com.dcits.brave.dubbo.DubboClientResponseAdapter;
import com.dcits.brave.dubbo.DubboServerRequestAdapter;
import com.dcits.brave.dubbo.DubboServerResponseAdapter;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientRequestInterceptor;
import com.github.kristofa.brave.ClientResponseInterceptor;
import com.github.kristofa.brave.ClientSpanThreadBinder;
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.ServerSpanThreadBinder;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by chenjg on 16/7/24.
 */

@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class BraveTracerFilter implements Filter {


    public static ThreadLocal<Map<String, Object>>  invokeInfo = new ThreadLocal<Map<String, Object>>() {
        protected Map<String, Object> initialValue() {
            return new HashMap();
        }
    };


    public static Map<String,Object>globalContext = new HashMap();

    private static final Logger logger = LoggerFactory.getLogger(BraveTracerFilter.class);
    private static final String INVOKE_LEVEL="invoke_level";

    public static final String PROCESS_METHOD="process";

    private static final String KEY_CR = "stat_info_cr";
    private static final String KEY_SR = "stat_info_sr";
    private static final String KEY_SS = "stat_info_ss";
    private static final String KEY_CS = "stat_info_cs";

    public static final String PROCESS_TRACE_ID = "process_trace_id";
    public static final String PROCESS_PARENT_ID = "process_parent_id";
    public static final String PROCESS_SPAN_ID = "process_span_id";

    private static String tagInfo ="CR";
    @Resource(name="brave")
    private static volatile Brave brave = null;
    private static volatile ServerRequestInterceptor serverRequestInterceptor;
    private static volatile ServerResponseInterceptor serverResponseInterceptor;
    private static volatile ServerSpanThreadBinder serverSpanThreadBinder;



    private static volatile String clientName;
    private static volatile ClientRequestInterceptor clientRequestInterceptor;
    private static volatile ClientResponseInterceptor clientResponseInterceptor;
    private static volatile ClientSpanThreadBinder clientSpanThreadBinder;

    public static Map<String, Object> getGlobalContext() {
        return globalContext;
    }

    public static void setGlobalContext(Map<String, Object> globalContext) {
        BraveTracerFilter.globalContext = globalContext;
    }

    public static void setBrave(Brave brave) {

        logger.debug("Setting brave for BraveTracerFilter.");
        BraveTracerFilter.brave = brave;
        BraveTracerFilter.serverRequestInterceptor = brave.serverRequestInterceptor();
        BraveTracerFilter.serverResponseInterceptor = brave.serverResponseInterceptor();
        BraveTracerFilter.serverSpanThreadBinder = brave.serverSpanThreadBinder();


        BraveTracerFilter.clientRequestInterceptor = brave.clientRequestInterceptor();
        BraveTracerFilter.clientResponseInterceptor = brave.clientResponseInterceptor();
        BraveTracerFilter.clientSpanThreadBinder = brave.clientSpanThreadBinder();
        //brave.
    }



    public static void prtInvokeInfo()
    {
       // logger.debug("aa:{},{},{}", "a","b","b");
        logger.debug("invoke info {},{},{},{},{},{}",String.valueOf(Thread.currentThread().getId()),
                tagInfo,
                (String) invokeInfo.get().get(KEY_CR),
                (String)invokeInfo.get().get(KEY_SR),
                (String)invokeInfo.get().get(KEY_SS),
                (String)invokeInfo.get().get(KEY_CS));

       // logger.debug("invoke info {},{},{}", "a","aa","a");

    }
    public void incCR()
    {
        tagInfo = "CR";
        incKey(KEY_CR);
    }

    public void incSR()
    {
        tagInfo = "SR";
        incKey(KEY_SR);
    }
    public void incSS()
    {
        tagInfo = "SS";
        incKey(KEY_SS);
    }
    public void incCS()
    {
        tagInfo = "CS";
        incKey(KEY_CS);
    }


    public void incKey(String key){
        int ref = (int)invokeInfo.get().get(key);
        ref = ref + 1;
        invokeInfo.get().put(key,ref);
    }

    public Result consumerInvoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if(RpcContext.getContext().getMethodName().equals(PROCESS_METHOD)){
            logger.debug("clear invoke info");
            invokeInfo.get().clear();
            invokeInfo.get().put("stat_info","start");
            invokeInfo.get().put(KEY_CR,0);
            invokeInfo.get().put(KEY_SR,0);
            invokeInfo.get().put(KEY_SS,0);
            invokeInfo.get().put(KEY_CS,0);
        }


        incCR();
        prtInvokeInfo();

        /*if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
            logger.debug("brave filter client request init invoke level");
            RpcContext.getContext().set(INVOKE_LEVEL, 0);
        }else{
            logger.debug("brave filter client request increment invoke level");
            int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
            RpcContext.getContext().set(INVOKE_LEVEL, ref+1);
            logger.debug("brave filter client request current invoke level:{}", (int)RpcContext.getContext().get(INVOKE_LEVEL));

        }*/




        logger.debug("brave filter client request:{}", RpcContext.getContext().getMethodName());
        DubboClientRequestAdapter adapter = new DubboClientRequestAdapter(invoker,invocation);

        clientRequestInterceptor.handle(adapter);
        try{
            Result rpcResult = invoker.invoke(invocation);



            clientResponseInterceptor.handle(new DubboClientResponseAdapter(rpcResult));


            incCS();
            prtInvokeInfo();

            /*if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
                logger.debug("brave filter client response invoke level null");
                // RpcContext.getContext().set(INVOKE_LEVEL, 0);
            }else{
                logger.debug("brave filter client response decrement invoke level");
                int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
                RpcContext.getContext().set(INVOKE_LEVEL, ref-1);
                logger.debug("brave filter client response current invoke level:{}", (int)RpcContext.getContext().get(INVOKE_LEVEL));
            }*/



            logger.debug("brave filter client response:{}", RpcContext.getContext().getMethodName());
            return rpcResult;
        }catch (Exception ex){
            clientResponseInterceptor.handle(new DubboClientResponseAdapter(ex));


            incCS();
            prtInvokeInfo();
            /*if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
                logger.debug("brave filter client response exception invoke level null");
                // RpcContext.getContext().set(INVOKE_LEVEL, 0);
            }else{
                logger.debug("brave filter client response exception decrement invoke level");
                int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
                RpcContext.getContext().set(INVOKE_LEVEL, ref-1);
                logger.debug("brave filter client response current exception invoke level:{}", (int)RpcContext.getContext().get(INVOKE_LEVEL));
            }*/

            logger.debug("brave filter client response exception:{}", RpcContext.getContext().getMethodName());
            // throw  ex;
        }finally {

           // clientSpanThreadBinder.setCurrentSpan(null);
           // serverSpanThreadBinder.s
        }
        return null;
    }








    private Result providerInvoke(Invoker<?> invoker, Invocation invocation) throws RpcException{


        incSR();
        prtInvokeInfo();
        /* if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
            logger.debug("brave filter server request null invoke level");
        }else{
            int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
            logger.debug("brave filter server request invoke level:{}", ref);
        }*/


        logger.debug("brave filter server request spans:{},{},{}", invocation.getAttachment("parentId"),invocation.getAttachment("spanId"),invocation.getAttachment("traceId"));

        logger.debug("brave filter server request:{}", RpcContext.getContext().getMethodName());
        serverRequestInterceptor.handle(new DubboServerRequestAdapter(invoker,invocation,brave.serverTracer()));


        Result rpcResult = invoker.invoke(invocation);
        // serverResponseInterceptor.handle(new ServerResponseAdapterImpl());
        serverResponseInterceptor.handle(new DubboServerResponseAdapter(rpcResult));



        incSS();
        prtInvokeInfo();
        /*if(RpcContext.getContext().get(INVOKE_LEVEL) == null){
            logger.debug("brave filter server response null invoke level");
        }else{
            int ref = (int)RpcContext.getContext().get(INVOKE_LEVEL);
            logger.debug("brave filter server response invoke level:{}", ref);
        }*/



        logger.debug("brave filter server response spans:{},{},{}", invocation.getAttachment("parentId"),invocation.getAttachment("spanId"),invocation.getAttachment("traceId"));

        logger.debug("brave filter server response:{}", RpcContext.getContext().getMethodName());


        return rpcResult;

    }




    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if(invokeInfo.get().get("stat_info") == null){
            logger.debug("init invoke info");
            invokeInfo.get().put("stat_info","start");
            invokeInfo.get().put(KEY_CR,0);
            invokeInfo.get().put(KEY_SR,0);
            invokeInfo.get().put(KEY_SS,0);
            invokeInfo.get().put(KEY_CS,0);
        }

        if(BraveTracerFilter.brave == null) {
            ApplicationContext context = ServiceBean.getSpringContext();
            Brave brave = (Brave) context.getBean("brave");
            setBrave(brave);
        }

        String side = invoker.getUrl().getParameter(Constants.SIDE_KEY);
        if (Constants.CONSUMER_SIDE.equals(side)) {
            return consumerInvoke(invoker, invocation);
        } else if (Constants.PROVIDER_SIDE.equals(side)) {
            return providerInvoke(invoker, invocation);
        }
        return invoker.invoke(invocation);

    }


}
