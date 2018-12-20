package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.github.kristofa.brave.*;
import com.dcits.brave.dubbo.support.DefaultClientNameProvider;
import com.dcits.brave.dubbo.support.DefaultServerNameProvider;
import com.dcits.brave.dubbo.support.DefaultSpanNameProvider;

import static com.github.kristofa.brave.IdConversion.convertToLong;


import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by chenjg on 16/7/24.
 */
public class DubboServerRequestAdapter  implements ServerRequestAdapter {

    private Invoker<?> invoker;
    private Invocation invocation;
    private ServerTracer serverTracer;
    private final static  DubboSpanNameProvider spanNameProvider = new DefaultSpanNameProvider();
    private final static  DubboClientNameProvider clientNameProvider = new DefaultClientNameProvider();
    private final static DubboServerNameProvider serverNameProvider = new DefaultServerNameProvider();



    public DubboServerRequestAdapter(Invoker<?> invoker, Invocation invocation,ServerTracer serverTracer) {
        this.invoker = invoker;
        this.invocation = invocation;
        this.serverTracer = serverTracer;
    }


    public TraceData getTraceData() {
      String sampled =   invocation.getAttachment("sampled");
      if(sampled != null && sampled.equals("0")){
          return TraceData.builder().sample(false).build();
      }else {
          final String parentId = invocation.getAttachment("parentId");
          final String spanId = invocation.getAttachment("spanId");
          final String traceId = invocation.getAttachment("traceId");
          if (traceId != null && spanId != null) {
              SpanId span = getSpanId(traceId, spanId, parentId);
              return TraceData.builder().sample(true).spanId(span).build();
          }
      }
       return TraceData.builder().build();

    }


    public String getSpanName() {
        String spanName = spanNameProvider.resolveSpanName(RpcContext.getContext());
        //String spanName = clientNameProvider.resolveClientName(RpcContext.getContext());
        //String spanName = serverNameProvider.resolveServerName(RpcContext.getContext());
        return spanName;
    }


    public Collection<KeyValueAnnotation> requestAnnotations() {

        String ipAddr = RpcContext.getContext().getUrl().getIp();
        InetSocketAddress inetSocketAddress = RpcContext.getContext().getRemoteAddress();

        //kxw todo
        // final String clientName = clientNameProvider.resolveClientName(RpcContext.getContext());
        //serverTracer.setServerReceived(IPConversion.convertToInt(ipAddr),inetSocketAddress.getPort(),clientName);
        InetSocketAddress socketAddress = RpcContext.getContext().getLocalAddress();
        if (socketAddress != null) {
            KeyValueAnnotation remoteAddrAnnotation = KeyValueAnnotation.create("address", socketAddress.toString());
            return Collections.singleton(remoteAddrAnnotation);
        } else {
            return Collections.emptyList();
        }

    }

    static SpanId getSpanId(String traceId, String spanId, String parentSpanId) {
        return SpanId.builder()
                .traceId(convertToLong(traceId))
                .spanId(convertToLong(spanId))
                .parentId(parentSpanId == null ? null : convertToLong(parentSpanId)).build();
    }


}
