package com.dcits.brave.dubbo.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenjg on 16/8/22.
 */
public class ClientRequestCommonData{
    //public static Map<String,String> attachmentData= new ConcurrentHashMap<String,String>();
    public static ThreadLocal<Map<String,String>> attachmentData= new ThreadLocal<>();
}
