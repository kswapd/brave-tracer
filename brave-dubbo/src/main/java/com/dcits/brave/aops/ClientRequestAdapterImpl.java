package com.dcits.brave.aops;

import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.SpanId;
import com.twitter.zipkin.gen.Endpoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kongxiangwen on 9/17/18 w:38.
 */
class ClientRequestAdapterImpl implements ClientRequestAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ClientRequestAdapterImpl.class);
	String spanName;
	SpanId spanId;
	Map<String,String> tagMap = new HashMap<>();

	ClientRequestAdapterImpl(String spanName){
		this.spanName = spanName;
	}
	ClientRequestAdapterImpl(String spanName, Map<String,String> tags){
		this.spanName = spanName;
		this.tagMap.putAll(tags);

	}


	public SpanId getSpanId() {
		return spanId;
	}


	public String getSpanName() {
		return this.spanName;
	}


	public void addSpanIdToRequest(SpanId spanId) {
		//记录传输到远程服务
		//System.out.println(spanId);
		if (spanId != null) {
			this.spanId = spanId;
			logger.debug(String.format("ClientRequestAdapterImpl:addSpanIdToRequest:trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));
		}else {
			logger.debug(String.format("ClientRequestAdapterImpl:addSpanIdToRequest: null"));
		}

	}


	public Collection<KeyValueAnnotation> requestAnnotations() {
		Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
		/*KeyValueAnnotation kv = KeyValueAnnotation.create("client-request", "111111");
		collection.add(kv);*/
		KeyValueAnnotation keyValueAnnotation = KeyValueAnnotation.create("LOCAL_SPAN", "true");
		collection.add(keyValueAnnotation);
		if(tagMap.size() > 0){
			for(Entry<String,String> entry:tagMap.entrySet()){
				KeyValueAnnotation anno = KeyValueAnnotation.create(entry.getKey(), entry.getValue());
				collection.add(anno);
			}
		}
		return collection;
	}


	public Endpoint serverAddress() {
		return null;
	}

}
