package com.dcits.brave.dubbo.support;

import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.brave.dubbo.DubboSpanNameProvider;

/**
 * Created by chenjg on 16/8/22.
 */
public class DefaultSpanNameProvider implements DubboSpanNameProvider {
    private static final String invokeStr = "$invoke";
    @Override
    public String resolveSpanName(RpcContext rpcContext) {

        String className = rpcContext.getUrl().getPath();
        String simpleName = className.substring(className.lastIndexOf(".")+1);
        String method = rpcContext.getMethodName();

        if(method.equals(invokeStr)){
            method = "process";
        }
        return simpleName+"."+method;

    }
}
