package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
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
import org.springframework.util.StringUtils;

/**
 * Created by chenjg on 16/7/24.
 */
public class DubboClientRequestAdapter implements ClientRequestAdapter {
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
        if (spanId == null) {
            RpcContext.getContext().setAttachment("sampled", "0");

        }else{

            RpcContext.getContext().setAttachment("traceId", IdConversion.convertToString(spanId.traceId));
            RpcContext.getContext().setAttachment("spanId", IdConversion.convertToString(spanId.spanId));
            if (spanId.nullableParentId() != null) {
                RpcContext.getContext().setAttachment("parentId", IdConversion.convertToString(spanId.parentId));
            }
        }
    }


    public Collection<KeyValueAnnotation> requestAnnotations() {
        List<KeyValueAnnotation> annotations = new ArrayList();
        KeyValueAnnotation keyValueAnnotation;
        if(RpcContext.getContext().getMethodName().equals("process")){
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




           /* RpcContext.getContext().setAttachment("THREAD_NO",sh.getThreadNo());
            RpcContext.getContext().setAttachment("TRAN_TIMESTAMP",sh.getTranTimestamp());
            RpcContext.getContext().setAttachment("USER_LANG",sh.getUserLang());
            RpcContext.getContext().setAttachment("SEQ_NO",sh.getSeqNo());
            RpcContext.getContext().setAttachment("PROGRAM_ID",sh.getProgramId());
            RpcContext.getContext().setAttachment("SOURCE_BRANCH_NO",sh.getSourceBranchNo());
            RpcContext.getContext().setAttachment("DEST_BRANCH_NO",sh.getDestBranchNo());
            RpcContext.getContext().setAttachment("SERVICE_CODE",sh.getServiceCode());
            RpcContext.getContext().setAttachment("MESSAGE_TYPE",sh.getMessageType());
            RpcContext.getContext().setAttachment("MESSAGE_CODE",sh.getMessageCode());
            RpcContext.getContext().setAttachment("TRAN_MODE",sh.getTranMode());
            RpcContext.getContext().setAttachment("SOURCE_TYPE",sh.getSourceType());
            RpcContext.getContext().setAttachment("BRANCH_ID",sh.getBranchId());
            RpcContext.getContext().setAttachment("USER_ID",sh.getUserId());
            RpcContext.getContext().setAttachment("TRAN_DATE",sh.getTranDate());*/


            /*keyValueAnnotation=  KeyValueAnnotation.create("THREAD_NO",sh.getThreadNo());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("TRAN_TIMESTAMP",sh.getTranTimestamp());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("USER_LANG",sh.getUserLang());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("SEQ_NO",sh.getSeqNo());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("PROGRAM_ID",sh.getProgramId());
            annotations.add(keyValueAnnotation);

            keyValueAnnotation=  KeyValueAnnotation.create("SOURCE_BRANCH_NO",sh.getSourceBranchNo());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("DEST_BRANCH_NO",sh.getDestBranchNo());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("SERVICE_CODE",sh.getServiceCode());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("MESSAGE_TYPE",sh.getMessageType());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("MESSAGE_CODE",sh.getMessageCode());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("TRAN_MODE",sh.getTranMode());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("SOURCE_TYPE",sh.getSourceType());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("BRANCH_ID",sh.getBranchId());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("USER_ID",sh.getUserId());
            annotations.add(keyValueAnnotation);
            keyValueAnnotation=  KeyValueAnnotation.create("TRAN_DATE",sh.getTranDate());
            annotations.add(keyValueAnnotation);*/


        }else{

           // if(!StringUtils.isEmpty(ClientRequestCommonData.attachmentData.get("THREAD_NO"))) {
            /*if(ClientRequestCommonData.attachmentData.size() > 0) {
                
                keyValueAnnotation = KeyValueAnnotation.create("THREAD_NO", ClientRequestCommonData.attachmentData.get("THREAD_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_TIMESTAMP", ClientRequestCommonData.attachmentData.get("TRAN_TIMESTAMP"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("USER_LANG", ClientRequestCommonData.attachmentData.get("USER_LANG"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("SEQ_NO", ClientRequestCommonData.attachmentData.get("SEQ_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("PROGRAM_ID", ClientRequestCommonData.attachmentData.get("PROGRAM_ID"));
                annotations.add(keyValueAnnotation);

                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_BRANCH_NO", ClientRequestCommonData.attachmentData.get("SOURCE_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("DEST_BRANCH_NO", ClientRequestCommonData.attachmentData.get("DEST_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("SERVICE_CODE", ClientRequestCommonData.attachmentData.get("SERVICE_CODE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_TYPE", ClientRequestCommonData.attachmentData.get("MESSAGE_TYPE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_CODE", ClientRequestCommonData.attachmentData.get("MESSAGE_CODE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_MODE", ClientRequestCommonData.attachmentData.get("TRAN_MODE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_TYPE", ClientRequestCommonData.attachmentData.get("SOURCE_TYPE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("BRANCH_ID", ClientRequestCommonData.attachmentData.get("BRANCH_ID"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("USER_ID", ClientRequestCommonData.attachmentData.get("USER_ID"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_DATE", ClientRequestCommonData.attachmentData.get("TRAN_DATE"));
                annotations.add(keyValueAnnotation);



            }*/



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
