package com.github.kristofa.brave.dubbo.support;

import com.alibaba.dubbo.rpc.RpcContext;
import com.github.kristofa.brave.dubbo.DubboSpanNameProvider;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenjg on 16/8/22.
 */
public class ClientRequestCommonData{
    public static Map<String,String> attachmentData= new ConcurrentHashMap<String,String>();
}
