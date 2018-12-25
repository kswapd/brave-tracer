package com.dcits.brave.dubbo.support;

import com.alibaba.dubbo.rpc.RpcContext;
import com.dcits.brave.dubbo.DubboSpanNameProvider;

/**
 * Created by chenjg on 16/8/22.
 */
public class DefaultSpanNameProvider implements DubboSpanNameProvider {
    @Override
    public String resolveSpanName(RpcContext rpcContext) {
        String className = rpcContext.getUrl().getPath();
        String simpleName = className.substring(className.lastIndexOf(".")+1);
        String method = rpcContext.getMethodName();
        return simpleName+"."+method;

    }
}
