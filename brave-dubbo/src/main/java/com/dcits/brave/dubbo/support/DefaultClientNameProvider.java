package com.dcits.brave.dubbo.support;


import com.alibaba.dubbo.rpc.RpcContext;
import com.github.kristofa.brave.SpanId;
import com.dcits.brave.dubbo.DubboClientNameProvider;

/**
 *   解析dubbo consumer applicationName
 *   @see com.dcits.brave.dubbo.DubboClientRequestAdapter#addSpanIdToRequest(SpanId spanId)
 *   RpcContext.getContext().setAttachment("clientName", application);

 */
public class DefaultClientNameProvider implements DubboClientNameProvider {
    @Override
    public String resolveClientName(RpcContext rpcContext) {
        //kxw type
        //String application = RpcContext.getContext().getUrl().getParameter("application");
       String application = RpcContext.getContext().getAttachment("clientName");
        //String application = RpcContext.getContext().getUrl().getParameter("clientName");
        //String application = RpcContext.getContext().getUrl().getParameter("clientName");

        //String application = RpcContext.getContext().getUrl().getParameter("clientName");
        return application;
    }
}
