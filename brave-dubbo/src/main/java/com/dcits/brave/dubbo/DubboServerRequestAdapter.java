package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.brave.dubbo.support.ClientRequestCommonData;
import com.dcits.brave.filters.BraveTracerFilter;
import com.dcits.galaxy.base.data.BaseRequest;
import com.dcits.galaxy.base.data.ISysHead;
import com.github.kristofa.brave.*;
import com.dcits.brave.dubbo.support.DefaultClientNameProvider;
import com.dcits.brave.dubbo.support.DefaultServerNameProvider;
import com.dcits.brave.dubbo.support.DefaultSpanNameProvider;

import static com.github.kristofa.brave.IdConversion.convertToLong;


import com.google.common.collect.Maps;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by chenjg on 16/7/24.
 */
public class DubboServerRequestAdapter  implements ServerRequestAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DubboServerRequestAdapter.class);

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
          logger.debug("brave filter server request adapter null sample,{}", RpcContext.getContext().getMethodName());
          return TraceData.builder().sample(false).build();

      }else {
          final String parentId = invocation.getAttachment("parentId");
          final String spanId = invocation.getAttachment("spanId");
          final String traceId = invocation.getAttachment("traceId");
          logger.debug("brave filter server request adapter spans:{},{}-{}-{}", RpcContext.getContext().getMethodName(),parentId,spanId,traceId);

          if (traceId != null && spanId != null) {
              SpanId span = getSpanId(traceId, spanId, parentId);
              return TraceData.builder().sample(true).spanId(span).build();
          }
      }
        TraceData td = TraceData.builder().build();
        logger.debug("brave filter server request adapter new trace {},{}", RpcContext.getContext().getMethodName(),td.getSpanId());

        return td;

    }


    public String getSpanName() {
        String spanName = spanNameProvider.resolveSpanName(RpcContext.getContext());
        //String spanName = clientNameProvider.resolveClientName(RpcContext.getContext());
        //String spanName = serverNameProvider.resolveServerName(RpcContext.getContext());
        return spanName;
    }


    public Collection<KeyValueAnnotation> requestAnnotations() {


        List<KeyValueAnnotation> annotations = new ArrayList();
        String methodName = RpcContext.getContext().getMethodName();
        KeyValueAnnotation keyValueAnnotation;

        String ipAddr = RpcContext.getContext().getUrl().getIp();
        InetSocketAddress inetSocketAddress = RpcContext.getContext().getRemoteAddress();


        //kxw todo
        // final String clientName = clientNameProvider.resolveClientName(RpcContext.getContext());
        //serverTracer.setServerReceived(IPConversion.convertToInt(ipAddr),inetSocketAddress.getPort(),clientName);
        InetSocketAddress socketAddress = RpcContext.getContext().getLocalAddress();
        if (socketAddress != null) {
            KeyValueAnnotation remoteAddrAnnotation = KeyValueAnnotation.create("address", socketAddress.toString());
            //return Collections.singleton(remoteAddrAnnotation);
            annotations.add(remoteAddrAnnotation);
        }

        if(methodName.equals("process")){
            BaseRequest br = null;//(BaseRequest)RpcContext.getContext().getArguments()[0];

            Map<String, Object> map = Maps.newHashMap();
            if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[0] != null) {
                br = (BaseRequest) RpcContext.getContext().getArguments()[0];
                String jsonStr = BraveTracerFilter.getObjectJsonStr(RpcContext.getContext().getArguments()[0]);
                if(jsonStr != null){
                    keyValueAnnotation = KeyValueAnnotation.create("REQUEST_INFO", jsonStr);
                    annotations.add(keyValueAnnotation);

                }

                ISysHead sh = br.getSysHead();


                if (!StringUtils.isEmpty(sh.getThreadNo())) {
                    keyValueAnnotation = KeyValueAnnotation.create("THREAD_NO", sh.getThreadNo());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getTranTimestamp())) {
                    keyValueAnnotation = KeyValueAnnotation.create("TRAN_TIMESTAMP", sh.getTranTimestamp());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getUserLang())) {

                    keyValueAnnotation = KeyValueAnnotation.create("USER_LANG", sh.getUserLang());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getSeqNo())) {

                    keyValueAnnotation = KeyValueAnnotation.create("SEQ_NO", sh.getSeqNo());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getProgramId())) {

                    keyValueAnnotation = KeyValueAnnotation.create("PROGRAM_ID", sh.getProgramId());
                    annotations.add(keyValueAnnotation);
                }

                if (!StringUtils.isEmpty(sh.getSourceBranchNo())) {

                    keyValueAnnotation = KeyValueAnnotation.create("SOURCE_BRANCH_NO", sh.getSourceBranchNo());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getDestBranchNo())) {

                    keyValueAnnotation = KeyValueAnnotation.create("DEST_BRANCH_NO", sh.getDestBranchNo());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getServiceCode())) {

                    keyValueAnnotation = KeyValueAnnotation.create("SERVICE_CODE", sh.getServiceCode());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getMessageType())) {

                    keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_TYPE", sh.getMessageType());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getMessageCode())) {

                    keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_CODE", sh.getMessageCode());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getTranMode())) {

                    keyValueAnnotation = KeyValueAnnotation.create("TRAN_MODE", sh.getTranMode());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getSourceType())) {

                    keyValueAnnotation = KeyValueAnnotation.create("SOURCE_TYPE", sh.getSourceType());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getBranchId())) {

                    keyValueAnnotation = KeyValueAnnotation.create("BRANCH_ID", sh.getBranchId());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getUserId())) {

                    keyValueAnnotation = KeyValueAnnotation.create("USER_ID", sh.getUserId());
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sh.getTranDate())) {

                    keyValueAnnotation = KeyValueAnnotation.create("TRAN_DATE", sh.getTranDate());
                    annotations.add(keyValueAnnotation);
                }

            }

        }else if(methodName.equals("$invoke")){
            BaseRequest br = null;//(BaseRequest)RpcContext.getContext().getArguments()[0];

            Map<String, Object> map = Maps.newHashMap();
            if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[2] != null) {
                //br = (BaseRequest) RpcContext.getContext().getArguments()[0];
                String jsonStr = BraveTracerFilter.getObjectMapStr(((Object[])(RpcContext.getContext().getArguments()[2]))[0]);
                if(jsonStr != null){

                    keyValueAnnotation = KeyValueAnnotation.create("REQUEST_INFO", jsonStr);
                    annotations.add(keyValueAnnotation);
                }

                Map params = (Map)((Object[])(RpcContext.getContext().getArguments()[2]))[0];
                Map sysHead = null;
                if(params.get("sysHead") != null){
                    sysHead = (Map)params.get("sysHead");
                }


                if (!StringUtils.isEmpty(sysHead.get("threadNo"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("THREAD_NO", (String)sysHead.get("threadNo"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("tranTimestamp"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("TRAN_TIMESTAMP", (String)sysHead.get("tranTimestamp"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("userLang"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("USER_LANG", (String)sysHead.get("userLang"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("seqNo"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("SEQ_NO", (String)sysHead.get("seqNo"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("programId"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("PROGRAM_ID", (String)sysHead.get("programId"));
                    annotations.add(keyValueAnnotation);
                }

                if (!StringUtils.isEmpty(sysHead.get("sourceBranchNo"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("SOURCE_BRANCH_NO", (String)sysHead.get("sourceBranchNo"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("destBranchNo"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("DEST_BRANCH_NO", (String)sysHead.get("destBranchNo"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("serviceCode"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("SERVICE_CODE", (String)sysHead.get("serviceCode"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("messageType"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_TYPE", (String)sysHead.get("messageType"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("messageCode"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_CODE", (String)sysHead.get("messageCode"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("tranMode"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("TRAN_MODE", (String)sysHead.get("tranMode"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("sourceType"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("SOURCE_TYPE", (String)sysHead.get("sourceType"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("branchId"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("BRANCH_ID", (String)sysHead.get("branchId"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("userId"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("USER_ID", (String)sysHead.get("userId"));
                    annotations.add(keyValueAnnotation);
                }
                if (!StringUtils.isEmpty(sysHead.get("tranDate"))) {

                    keyValueAnnotation = KeyValueAnnotation.create("TRAN_DATE", (String)sysHead.get("tranDate"));
                    annotations.add(keyValueAnnotation);
                }
            }

        }

        if(!annotations.isEmpty()){
            return annotations;
        }else {
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
