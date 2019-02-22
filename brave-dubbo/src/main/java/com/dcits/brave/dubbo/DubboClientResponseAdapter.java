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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenjg on 16/7/24.
 */
public class DubboClientResponseAdapter implements ClientResponseAdapter {

    private Result rpcResult ;
    private Invocation invocation;

    private Exception exception;
    private static  Pattern pattern_ret = Pattern.compile("\"retCode\"\\s*:\\s*\"(.*?)\\s*\",.*retMsg\"\\s*:\\s*\"(.*?)\\s*\"");
    private static  Pattern pattern_status = Pattern.compile("\"retStatus\"\\s*:\\s*\"(.*?)\\s*\"");
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
                KeyValueAnnotation kStatus=  KeyValueAnnotation.create("status","failed");
                annotations.add(kStatus);
            }else{
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("status","success");
                annotations.add(keyValueAnnotation);

            }
        }
        if(invocation != null && invocation.getMethodName() != null && invocation.getMethodName().equals("process")){
            String jsonStr = BraveTracerFilter.getObjectJsonStr(rpcResult.getResult());
            if(jsonStr != null){
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("RESPONSE_INFO",jsonStr);
                annotations.add(keyValueAnnotation);
                /*Map<String,Object> map = BraveTracerFilter.getObjectMap(rpcResult.getResult());
                if(map.get("rs") != null ){

                    String code = (String)((Map)(((JSONArray)((Map)map.get("rs")).get("ret")).get(0))).get("retCode");
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RET_CODE",code);
                    annotations.add(kCode);

                    String msg = (String)((Map)(((JSONArray)((Map)map.get("rs")).get("ret")).get(0))).get("retMsg");
                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RET_MSG",msg);
                    annotations.add(kMsg);
                }
                if(map.get("retStatus") != null){
                    String retStatus = (String)map.get("retStatus");
                    KeyValueAnnotation kStatus=  KeyValueAnnotation.create("RET_STATUS",retStatus);
                    annotations.add(kStatus);

                }*/

                Matcher matcher = pattern_ret.matcher(jsonStr);
                if(matcher.find() && matcher.groupCount() > 1) {
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RET_CODE",matcher.group(1));
                    annotations.add(kCode);
                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RET_MSG",matcher.group(2));
                    annotations.add(kMsg);
                }
                matcher = pattern_status.matcher(jsonStr);
                if(matcher.find()) {
                    KeyValueAnnotation kStatus=  KeyValueAnnotation.create("RET_STATUS",matcher.group(1));
                    annotations.add(kStatus);
                }



            }
        }else if(invocation != null && invocation.getMethodName() != null &&  invocation.getMethodName().equals("$invoke")){
            String jsonStr = BraveTracerFilter.getObjectMapStr(rpcResult.getResult());
            if(jsonStr != null){
                KeyValueAnnotation keyValueAnnotation=  KeyValueAnnotation.create("RESPONSE_INFO",jsonStr);
                annotations.add(keyValueAnnotation);
                /*Map map = (Map)rpcResult.getResult();
                if(map.get("retJsonObject") != null ){
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RETCODE",(String)((Map)map.get("retJsonObject")).get("retCode"));
                    annotations.add(kCode);

                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RETMSG",(String)((Map)map.get("retJsonObject")).get("retMsg"));
                    annotations.add(kCode);
                }*/
                Matcher matcher = pattern_ret.matcher(jsonStr);
                if(matcher.find() && matcher.groupCount() > 1) {
                    KeyValueAnnotation kCode=  KeyValueAnnotation.create("RET_CODE",matcher.group(1));
                    annotations.add(kCode);
                    KeyValueAnnotation kMsg=  KeyValueAnnotation.create("RET_MSG",matcher.group(2));
                    annotations.add(kMsg);
                }
                matcher = pattern_status.matcher(jsonStr);
                if(matcher.find()) {
                    KeyValueAnnotation kStatus=  KeyValueAnnotation.create("RET_STATUS",matcher.group(1));
                    annotations.add(kStatus);
                }
            }
        }
        return annotations;
    }

}
