package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dcits.brave.filters.BraveTracerFilter;
import com.dcits.galaxy.base.data.BaseRequest;
import com.dcits.galaxy.base.data.ISysHead;
import com.github.kristofa.brave.*;
import com.dcits.brave.dubbo.support.ClientRequestCommonData;
import com.dcits.brave.dubbo.support.DefaultClientNameProvider;
import com.dcits.brave.dubbo.support.DefaultServerNameProvider;
import com.dcits.brave.dubbo.support.DefaultSpanNameProvider;
import com.github.kristofa.brave.internal.Nullable;
import com.google.common.collect.Maps;
import com.twitter.zipkin.gen.Endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
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


        /*public static final String BASE_REQUEST_CLASS = BaseRequest.class.getName();
        Map<String, Object> reqMap = (Map<String, Object>) invocation.getRequest();
        String out = (String) process.$invoke(PROCESS_METHOD,
                new String[]{BASE_REQUEST_CLASS},
                new Object[]{JSON.toJSONString(reqMap)});*/
        /*if(methodName.equals("$invoke")){
            String genClassName = null;
            String genMethod = null;
            BaseRequest br = null;

            genMethod  = (String)RpcContext.getContext().getArguments()[0];
            if(RpcContext.getContext().getArguments()[1] instanceof Object[]){
                genClassName = (String)((Object[])RpcContext.getContext().getArguments()[1])[0];
            }
            if(RpcContext.getContext().getArguments()[2] instanceof Object[]){
                Map<String, Object> genParamMap = (Map<String, Object>)((Object[])RpcContext.getContext().getArguments()[2])[0];
                JSONObject jobj = JSONObject.parseObject(JSON.toJSONString(genParamMap));
                br = (BaseRequest)JSONObject.toJavaObject(jobj, BaseRequest.class);
            }
            //JSON.parse
        }*/


        if(methodName.equals("process")){
            BaseRequest br = null;//(BaseRequest)RpcContext.getContext().getArguments()[0];
            if(ClientRequestCommonData.attachmentData.get() == null) {
                ClientRequestCommonData.attachmentData.set(new HashMap<String,String>());
            }
            ClientRequestCommonData.attachmentData.get().clear();
            Map<String, Object> map = Maps.newHashMap();
            if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[0] != null) {
                br = (BaseRequest) RpcContext.getContext().getArguments()[0];
                String jsonStr = BraveTracerFilter.getObjectJsonStr(RpcContext.getContext().getArguments()[0]);
                if(jsonStr != null){
                    ClientRequestCommonData.attachmentData.get().put("REQUEST_INFO", jsonStr);
                }

                ISysHead sh = br.getSysHead();


                if (!StringUtils.isEmpty(sh.getThreadNo())) {
                    ClientRequestCommonData.attachmentData.get().put("THREAD_NO", sh.getThreadNo());
                }
                if (!StringUtils.isEmpty(sh.getTranTimestamp())) {
                    ClientRequestCommonData.attachmentData.get().put("TRAN_TIMESTAMP", sh.getTranTimestamp());
                }
                if (!StringUtils.isEmpty(sh.getUserLang())) {
                    ClientRequestCommonData.attachmentData.get().put("USER_LANG", sh.getUserLang());
                }
                if (!StringUtils.isEmpty(sh.getSeqNo())) {
                    ClientRequestCommonData.attachmentData.get().put("SEQ_NO", sh.getSeqNo());
                }
                if (!StringUtils.isEmpty(sh.getProgramId())) {
                    ClientRequestCommonData.attachmentData.get().put("PROGRAM_ID", sh.getProgramId());
                }

                if (!StringUtils.isEmpty(sh.getSourceBranchNo())) {
                    ClientRequestCommonData.attachmentData.get().put("SOURCE_BRANCH_NO", sh.getSourceBranchNo());
                }
                if (!StringUtils.isEmpty(sh.getDestBranchNo())) {
                    ClientRequestCommonData.attachmentData.get().put("DEST_BRANCH_NO", sh.getDestBranchNo());
                }
                if (!StringUtils.isEmpty(sh.getServiceCode())) {
                    ClientRequestCommonData.attachmentData.get().put("SERVICE_CODE", sh.getServiceCode());
                }
                if (!StringUtils.isEmpty(sh.getMessageType())) {
                    ClientRequestCommonData.attachmentData.get().put("MESSAGE_TYPE", sh.getMessageType());
                }
                if (!StringUtils.isEmpty(sh.getMessageCode())) {
                    ClientRequestCommonData.attachmentData.get().put("MESSAGE_CODE", sh.getMessageCode());
                }
                if (!StringUtils.isEmpty(sh.getTranMode())) {
                    ClientRequestCommonData.attachmentData.get().put("TRAN_MODE", sh.getTranMode());
                }
                if (!StringUtils.isEmpty(sh.getSourceType())) {
                    ClientRequestCommonData.attachmentData.get().put("SOURCE_TYPE", sh.getSourceType());
                }
                if (!StringUtils.isEmpty(sh.getBranchId())) {
                    ClientRequestCommonData.attachmentData.get().put("BRANCH_ID", sh.getBranchId());
                }
                if (!StringUtils.isEmpty(sh.getUserId())) {
                    ClientRequestCommonData.attachmentData.get().put("USER_ID", sh.getUserId());
                }
                if (!StringUtils.isEmpty(sh.getTranDate())) {
                    ClientRequestCommonData.attachmentData.get().put("TRAN_DATE", sh.getTranDate());
                }

            }

        }else if(methodName.equals("$invoke")){
            BaseRequest br = null;//(BaseRequest)RpcContext.getContext().getArguments()[0];
            if(ClientRequestCommonData.attachmentData.get() == null) {
                ClientRequestCommonData.attachmentData.set(new HashMap<String,String>());

            }
            ClientRequestCommonData.attachmentData.get().clear();
            Map<String, Object> map = Maps.newHashMap();
            if (RpcContext.getContext().getArguments() != null && RpcContext.getContext().getArguments()[2] != null) {
                //br = (BaseRequest) RpcContext.getContext().getArguments()[0];
                String jsonStr = BraveTracerFilter.getObjectMapStr(((Object[])(RpcContext.getContext().getArguments()[2]))[0]);
                if(jsonStr != null){
                    ClientRequestCommonData.attachmentData.get().put("REQUEST_INFO", jsonStr);
                }

                Map params = (Map)((Object[])(RpcContext.getContext().getArguments()[2]))[0];
                Map sysHead = null;
                if(params.get("sysHead") != null){
                    sysHead = (Map)params.get("sysHead");
                }


                if (!StringUtils.isEmpty(sysHead.get("threadNo"))) {
                    ClientRequestCommonData.attachmentData.get().put("THREAD_NO", (String)sysHead.get("threadNo"));
                }
                if (!StringUtils.isEmpty(sysHead.get("tranTimestamp"))) {
                    ClientRequestCommonData.attachmentData.get().put("TRAN_TIMESTAMP", (String)sysHead.get("tranTimestamp"));
                }
                if (!StringUtils.isEmpty(sysHead.get("userLang"))) {
                    ClientRequestCommonData.attachmentData.get().put("USER_LANG", (String)sysHead.get("userLang"));
                }
                if (!StringUtils.isEmpty(sysHead.get("seqNo"))) {
                    ClientRequestCommonData.attachmentData.get().put("SEQ_NO", (String)sysHead.get("seqNo"));
                }
                if (!StringUtils.isEmpty(sysHead.get("programId"))) {
                    ClientRequestCommonData.attachmentData.get().put("PROGRAM_ID", (String)sysHead.get("programId"));
                }

                if (!StringUtils.isEmpty(sysHead.get("sourceBranchNo"))) {
                    ClientRequestCommonData.attachmentData.get().put("SOURCE_BRANCH_NO", (String)sysHead.get("sourceBranchNo"));
                }
                if (!StringUtils.isEmpty(sysHead.get("destBranchNo"))) {
                    ClientRequestCommonData.attachmentData.get().put("DEST_BRANCH_NO", (String)sysHead.get("destBranchNo"));
                }
                if (!StringUtils.isEmpty(sysHead.get("serviceCode"))) {
                    ClientRequestCommonData.attachmentData.get().put("SERVICE_CODE", (String)sysHead.get("serviceCode"));
                }
                if (!StringUtils.isEmpty(sysHead.get("messageType"))) {
                    ClientRequestCommonData.attachmentData.get().put("MESSAGE_TYPE", (String)sysHead.get("messageType"));
                }
                if (!StringUtils.isEmpty(sysHead.get("messageCode"))) {
                    ClientRequestCommonData.attachmentData.get().put("MESSAGE_CODE", (String)sysHead.get("messageCode"));
                }
                if (!StringUtils.isEmpty(sysHead.get("tranMode"))) {
                    ClientRequestCommonData.attachmentData.get().put("TRAN_MODE", (String)sysHead.get("tranMode"));
                }
                if (!StringUtils.isEmpty(sysHead.get("sourceType"))) {
                    ClientRequestCommonData.attachmentData.get().put("SOURCE_TYPE", (String)sysHead.get("sourceType"));
                }
                if (!StringUtils.isEmpty(sysHead.get("branchId"))) {
                    ClientRequestCommonData.attachmentData.get().put("BRANCH_ID", (String)sysHead.get("branchId"));
                }
                if (!StringUtils.isEmpty(sysHead.get("userId"))) {
                    ClientRequestCommonData.attachmentData.get().put("USER_ID", (String)sysHead.get("userId"));
                }
                if (!StringUtils.isEmpty(sysHead.get("tranDate"))) {
                    ClientRequestCommonData.attachmentData.get().put("TRAN_DATE", (String)sysHead.get("tranDate"));
                }
            }

        }

        if(ClientRequestCommonData.attachmentData.get() != null && ClientRequestCommonData.attachmentData.get().size() > 0) {

            if (!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("REQUEST_INFO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("REQUEST_INFO", (String)ClientRequestCommonData.attachmentData.get().get("REQUEST_INFO"));
                annotations.add(keyValueAnnotation);
            }


            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("THREAD_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("THREAD_NO", ClientRequestCommonData.attachmentData.get().get("THREAD_NO"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("TRAN_TIMESTAMP"))) {
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_TIMESTAMP", ClientRequestCommonData.attachmentData.get().get("TRAN_TIMESTAMP"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("USER_LANG"))) {
                keyValueAnnotation = KeyValueAnnotation.create("USER_LANG", ClientRequestCommonData.attachmentData.get().get("USER_LANG"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("SEQ_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SEQ_NO", ClientRequestCommonData.attachmentData.get().get("SEQ_NO"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("PROGRAM_ID"))) {
                keyValueAnnotation = KeyValueAnnotation.create("PROGRAM_ID", ClientRequestCommonData.attachmentData.get().get("PROGRAM_ID"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("SOURCE_BRANCH_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_BRANCH_NO", ClientRequestCommonData.attachmentData.get().get("SOURCE_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("DEST_BRANCH_NO"))) {
                keyValueAnnotation = KeyValueAnnotation.create("DEST_BRANCH_NO", ClientRequestCommonData.attachmentData.get().get("DEST_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("SERVICE_CODE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SERVICE_CODE", ClientRequestCommonData.attachmentData.get().get("SERVICE_CODE"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("MESSAGE_TYPE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_TYPE", ClientRequestCommonData.attachmentData.get().get("MESSAGE_TYPE"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("MESSAGE_CODE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_CODE", ClientRequestCommonData.attachmentData.get().get("MESSAGE_CODE"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("TRAN_MODE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_MODE", ClientRequestCommonData.attachmentData.get().get("TRAN_MODE"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("SOURCE_TYPE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_TYPE", ClientRequestCommonData.attachmentData.get().get("SOURCE_TYPE"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("BRANCH_ID"))) {
                keyValueAnnotation = KeyValueAnnotation.create("BRANCH_ID", ClientRequestCommonData.attachmentData.get().get("BRANCH_ID"));
                annotations.add(keyValueAnnotation);
            }
            
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("USER_ID"))) {
                keyValueAnnotation = KeyValueAnnotation.create("USER_ID", ClientRequestCommonData.attachmentData.get().get("USER_ID"));
                annotations.add(keyValueAnnotation);
            }
            if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get().get("TRAN_DATE"))) {
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_DATE", ClientRequestCommonData.attachmentData.get().get("TRAN_DATE"));
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
