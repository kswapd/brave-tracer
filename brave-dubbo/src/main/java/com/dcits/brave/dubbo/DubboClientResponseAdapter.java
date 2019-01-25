package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.brave.filters.BraveTracerFilter;
import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by chenjg on 16/7/24.
 */
public class DubboClientResponseAdapter implements ClientResponseAdapter {

    private Result rpcResult ;
    private Invocation invocation;

    private Exception exception;

    public DubboClientResponseAdapter(Exception exception) {
        this.exception = exception;
    }



    public DubboClientResponseAdapter(Result rpcResult, Invocation invocation) {
        this.rpcResult = rpcResult;
        this.invocation = invocation;
    }


    public Collection<KeyValueAnnotation> responseAnnotations() {
        List<KeyValueAnnotation> annotations = new ArrayList<KeyValueAnnotation>();


        if(exception != null){
            KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("exception",exception.getMessage());
            annotations.add(keyValueAnnotation);
        }else{
            if(rpcResult.hasException()){
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("exception",rpcResult.getException().getMessage());
                annotations.add(keyValueAnnotation);
            }else{
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("status","success");
                annotations.add(keyValueAnnotation);

            }
        }
        if(invocation.getMethodName() != null && invocation.getMethodName().equals("process")){
            String jsonStr = BraveTracerFilter.getObjectJsonStr(rpcResult.getResult());
            if(jsonStr != null){
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("RESPONSE_INFO",jsonStr);
                annotations.add(keyValueAnnotation);
            }
        }else if(invocation.getMethodName() != null &&  invocation.getMethodName().equals("$invoke")){
            String jsonStr = BraveTracerFilter.getObjectMapStr(rpcResult.getResult());
            if(jsonStr != null){
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("RESPONSE_INFO",jsonStr);
                annotations.add(keyValueAnnotation);
            }
        }
        return annotations;
    }

}
