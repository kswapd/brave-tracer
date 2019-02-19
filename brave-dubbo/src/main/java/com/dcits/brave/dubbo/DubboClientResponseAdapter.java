package com.dcits.brave.dubbo;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSONArray;
import com.dcits.brave.filters.BraveTracerFilter;
import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
                Map map = BraveTracerFilter.getObjectMap(rpcResult.getResult());
                if(map.get("retJsonObject") != null ){

                    String code = (String)((Map)(((JSONArray)map.get("retJsonObject")).get(0))).get("retCode");
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RETCODE",code);
                    annotations.add(kCode);

                    String msg = (String)((Map)(((JSONArray)map.get("retJsonObject")).get(0))).get("retMsg");
                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RETMSG",msg);
                    annotations.add(kMsg);
                }
            }
        }else if(invocation.getMethodName() != null &&  invocation.getMethodName().equals("$invoke")){
            String jsonStr = BraveTracerFilter.getObjectMapStr(rpcResult.getResult());
            if(jsonStr != null){
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("RESPONSE_INFO",jsonStr);
                annotations.add(keyValueAnnotation);
                Map map = (Map)rpcResult.getResult();
                if(map.get("retJsonObject") != null ){
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RETCODE",(String)((Map)map.get("retJsonObject")).get("retCode"));
                    annotations.add(kCode);

                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RETMSG",(String)((Map)map.get("retJsonObject")).get("retMsg"));
                    annotations.add(kCode);
                }
            }
        }
        return annotations;
    }

}
