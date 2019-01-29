package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.brave.filters.BraveTracerFilter;
import com.dcits.galaxy.base.data.BaseRequest;
import com.dcits.galaxy.base.data.ISysHead;
import com.github.kristofa.brave.*;
import com.dcits.brave.dubbo.support.ClientRequestCommonData;
import com.dcits.brave.dubbo.support.DefaultClientNameProvider;
import com.dcits.brave.dubbo.support.DefaultServerNameProvider;
import com.dcits.brave.dubbo.support.DefaultSpanNameProvider;
import com.github.kristofa.brave.internal.Nullable;
import com.twitter.zipkin.gen.Endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by chenjg on 16/7/24.
 */
public class DubboClientRequestAdapter implements ClientRequestAdapter {
    private static final Logger logger = LoggerFactory.getLogger(DubboClientRequestAdapter.class);
    private Invoker<?> invoker;
    private Invocation invocation;
    private final static DubboSpanNameProvider spanNameProvider = new DefaultSpanNameProvider();
    private final static DubboServerNameProvider serverNameProvider = new DefaultServerNameProvider();
    private final static  DubboClientNameProvider clientNameProvider = new DefaultClientNameProvider();


    public DubboClientRequestAdapter(Invoker<?> invoker, Invocation invocation) {
        this.invoker = invoker;
        this.invocation = invocation;


    }


    public String getSpanName() {

        String name = spanNameProvider.resolveSpanName(RpcContext.getContext());
       // String name = serverNameProvider.resolveServerName(RpcContext.getContext());
        return name;

    }


    public void addSpanIdToRequest(@Nullable SpanId spanId) {
        String application = RpcContext.getContext().getUrl().getParameter("application");
        //System.out.println(application + ".......");
        RpcContext.getContext().setAttachment("clientName", application);

        logger.debug("brave filter client request adapter spans:{},{}", RpcContext.getContext().getMethodName(),spanId);
        if (spanId == null) {
            //RpcContext.getContext().setAttachment("sampled", "0");

        }else{

            /*if(RpcContext.getContext().getMethodName().equals(BraveTracerFilter.PROCESS_METHOD)){
                logger.debug("save process info:{},{},{}",spanId.traceId,spanId.spanId,spanId.parentId);
                BraveTracerFilter.getGlobalContext().put(BraveTracerFilter.PROCESS_SPAN_ID,spanId.spanId);
                BraveTracerFilter.getGlobalContext().put(BraveTracerFilter.PROCESS_TRACE_ID,spanId.traceId);
                if (spanId.nullableParentId() != null) {
                    BraveTracerFilter.getGlobalContext().put(BraveTracerFilter.PROCESS_PARENT_ID, spanId.parentId);
                }

            }*/


            RpcContext.getContext().setAttachment("traceId", IdConversion.convertToString(spanId.traceId));
            RpcContext.getContext().setAttachment("spanId", IdConversion.convertToString(spanId.spanId));
            if (spanId.nullableParentId() != null) {
                RpcContext.getContext().setAttachment("parentId", IdConversion.convertToString(spanId.parentId));
            }



            long processSpanId = -1, processParentId = -1,processTraceId = -1;


            if( BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_PARENT_ID) != null) {
                processParentId = (long) BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_PARENT_ID);
                RpcContext.getContext().setAttachment("parentId", IdConversion.convertToString(processParentId));

            }

            if( BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_TRACE_ID) != null) {
                processTraceId = (long) BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_TRACE_ID);
                RpcContext.getContext().setAttachment("traceId", IdConversion.convertToString(processTraceId));
            }

            if( BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_SPAN_ID) != null){
                processSpanId = (long)BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_SPAN_ID);

            }

            if(processParentId != -1 || processSpanId != -1 || processTraceId != -1){
                logger.debug("brave filter get process info {},{},{}",
                        BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_PARENT_ID),
                        BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_SPAN_ID),
                        BraveTracerFilter.getGlobalContext().get(BraveTracerFilter.PROCESS_TRACE_ID));
            }







        }
    }


    public Collection<KeyValueAnnotation> requestAnnotations() {
        List<KeyValueAnnotation> annotations = new ArrayList();
        KeyValueAnnotation keyValueAnnotation;
        String methodName = RpcContext.getContext().getMethodName();

        if(methodName.equals("process")){
            BaseRequest br = (BaseRequest)RpcContext.getContext().getArguments()[0];
            ISysHead sh = br.getSysHead();

            ClientRequestCommonData.attachmentData.clear();

            if(!StringUtils.isEmpty(sh.getThreadNo())) {
                ClientRequestCommonData.attachmentData.put("THREAD_NO", sh.getThreadNo());
            }
            if(!StringUtils.isEmpty(sh.getTranTimestamp())) {
                ClientRequestCommonData.attachmentData.put("TRAN_TIMESTAMP", sh.getTranTimestamp());
            }
            if(!StringUtils.isEmpty(sh.getUserLang())) {
                ClientRequestCommonData.attachmentData.put("USER_LANG", sh.getUserLang());
            }
            if(!StringUtils.isEmpty(sh.getSeqNo())) {
                ClientRequestCommonData.attachmentData.put("SEQ_NO", sh.getSeqNo());
            }
            if(!StringUtils.isEmpty(sh.getProgramId())) {
                ClientRequestCommonData.attachmentData.put("PROGRAM_ID", sh.getProgramId());
            }

            if(!StringUtils.isEmpty(sh.getSourceBranchNo())) {
                ClientRequestCommonData.attachmentData.put("SOURCE_BRANCH_NO",sh.getSourceBranchNo());
            }
            if(!StringUtils.isEmpty(sh.getDestBranchNo())) {
            ClientRequestCommonData.attachmentData.put("DEST_BRANCH_NO",sh.getDestBranchNo());
            }
            if(!StringUtils.isEmpty(sh.getServiceCode())) {
            ClientRequestCommonData.attachmentData.put("SERVICE_CODE",sh.getServiceCode());
            }
            if(!StringUtils.isEmpty(sh.getMessageType())) {
            ClientRequestCommonData.attachmentData.put("MESSAGE_TYPE",sh.getMessageType());
            }
            if(!StringUtils.isEmpty(sh.getMessageCode())) {
            ClientRequestCommonData.attachmentData.put("MESSAGE_CODE",sh.getMessageCode());
            }
            if(!StringUtils.isEmpty(sh.getTranMode())) {
            ClientRequestCommonData.attachmentData.put("TRAN_MODE",sh.getTranMode());
            }
            if(!StringUtils.isEmpty(sh.getSourceType())) {
            ClientRequestCommonData.attachmentData.put("SOURCE_TYPE",sh.getSourceType());
            }
            if(!StringUtils.isEmpty(sh.getBranchId())) {
            ClientRequestCommonData.attachmentData.put("BRANCH_ID",sh.getBranchId());
            }
            if(!StringUtils.isEmpty(sh.getUserId())) {
            ClientRequestCommonData.attachmentData.put("USER_ID",sh.getUserId());
            }
            if(!StringUtils.isEmpty(sh.getTranDate())) {
                ClientRequestCommonData.attachmentData.put("TRAN_DATE", sh.getTranDate());
            }

        }

        if(ClientRequestCommonData.attachmentData.size() > 0) {
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("THREAD_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("THREAD_NO", ClientRequestCommonData.attachmentData.get("THREAD_NO"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("TRAN_TIMESTAMP"))) {
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_TIMESTAMP", ClientRequestCommonData.attachmentData.get("TRAN_TIMESTAMP"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("USER_LANG"))) {
                keyValueAnnotation = KeyValueAnnotation.create("USER_LANG", ClientRequestCommonData.attachmentData.get("USER_LANG"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("SEQ_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SEQ_NO", ClientRequestCommonData.attachmentData.get("SEQ_NO"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("PROGRAM_ID"))) {
                keyValueAnnotation = KeyValueAnnotation.create("PROGRAM_ID", ClientRequestCommonData.attachmentData.get("PROGRAM_ID"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("SOURCE_BRANCH_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_BRANCH_NO", ClientRequestCommonData.attachmentData.get("SOURCE_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("DEST_BRANCH_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("DEST_BRANCH_NO", ClientRequestCommonData.attachmentData.get("DEST_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("SERVICE_CODE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SERVICE_CODE", ClientRequestCommonData.attachmentData.get("SERVICE_CODE"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("MESSAGE_TYPE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_TYPE", ClientRequestCommonData.attachmentData.get("MESSAGE_TYPE"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("MESSAGE_CODE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_CODE", ClientRequestCommonData.attachmentData.get("MESSAGE_CODE"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("TRAN_MODE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_MODE", ClientRequestCommonData.attachmentData.get("TRAN_MODE"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("SOURCE_TYPE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_TYPE", ClientRequestCommonData.attachmentData.get("SOURCE_TYPE"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("BRANCH_ID"))) {
                keyValueAnnotation = KeyValueAnnotation.create("BRANCH_ID", ClientRequestCommonData.attachmentData.get("BRANCH_ID"));
                annotations.add(keyValueAnnotation);
            }

            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("USER_ID"))) {
                keyValueAnnotation = KeyValueAnnotation.create("USER_ID", ClientRequestCommonData.attachmentData.get("USER_ID"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("TRAN_DATE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_DATE", ClientRequestCommonData.attachmentData.get("TRAN_DATE"));
                annotations.add(keyValueAnnotation);
            }



        }
        return annotations;
        //return Collections.singletonList(KeyValueAnnotation.create("url", RpcContext.getContext().getUrl().toString()));
    }


    public Endpoint serverAddress() {
        //kxw todos
        /*InetSocketAddress inetSocketAddress = RpcContext.getContext().getRemoteAddress();
        String ipAddr = RpcContext.getContext().getUrl().getIp();
        String serverName = serverNameProvider.resolveServerName(RpcContext.getContext());
        System.out.println("server name.....:" + serverName);
        return Endpoint.create(serverName, IPConversion.convertToInt(ipAddr),inetSocketAddress.getPort());*/
        return null;
    }



}
