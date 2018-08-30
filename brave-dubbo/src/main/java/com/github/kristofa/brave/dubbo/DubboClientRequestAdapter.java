package com.github.kristofa.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.galaxy.base.data.BaseRequest;
import com.dcits.galaxy.base.data.ISysHead;
import com.dcits.galaxy.base.data.SysHead;
import com.github.kristofa.brave.*;
import com.github.kristofa.brave.dubbo.support.DefaultClientNameProvider;
import com.github.kristofa.brave.dubbo.support.DefaultServerNameProvider;
import com.github.kristofa.brave.dubbo.support.DefaultSpanNameProvider;
import com.github.kristofa.brave.internal.Nullable;
import com.twitter.zipkin.gen.Endpoint;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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



            RpcContext.getContext().setAttachment("THREAD_NO",sh.getThreadNo());
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
            RpcContext.getContext().setAttachment("TRAN_DATE",sh.getTranDate());


            keyValueAnnotation=  KeyValueAnnotation.create("THREAD_NO",sh.getThreadNo());
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
            annotations.add(keyValueAnnotation);


        }else{
            if(!StringUtils.isEmpty(RpcContext.getContext().getAttachment("THREAD_NO"))) {

                keyValueAnnotation = KeyValueAnnotation.create("THREAD_NO", RpcContext.getContext().getAttachment("THREAD_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_TIMESTAMP", RpcContext.getContext().getAttachment("TRAN_TIMESTAMP"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("USER_LANG", RpcContext.getContext().getAttachment("USER_LANG"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("SEQ_NO", RpcContext.getContext().getAttachment("SEQ_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("PROGRAM_ID", RpcContext.getContext().getAttachment("PROGRAM_ID"));
                annotations.add(keyValueAnnotation);

                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_BRANCH_NO", RpcContext.getContext().getAttachment("SOURCE_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("DEST_BRANCH_NO", RpcContext.getContext().getAttachment("DEST_BRANCH_NO"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("SERVICE_CODE", RpcContext.getContext().getAttachment("SERVICE_CODE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_TYPE", RpcContext.getContext().getAttachment("MESSAGE_TYPE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("MESSAGE_CODE", RpcContext.getContext().getAttachment("MESSAGE_CODE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_MODE", RpcContext.getContext().getAttachment("TRAN_MODE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("SOURCE_TYPE", RpcContext.getContext().getAttachment("SOURCE_TYPE"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("BRANCH_ID", RpcContext.getContext().getAttachment("BRANCH_ID"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("USER_ID", RpcContext.getContext().getAttachment("USER_ID"));
                annotations.add(keyValueAnnotation);
                keyValueAnnotation = KeyValueAnnotation.create("TRAN_DATE", RpcContext.getContext().getAttachment("TRAN_DATE"));
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
