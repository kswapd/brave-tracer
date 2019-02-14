package com.dcits.brave.aops;

import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.SpanId;
import com.twitter.zipkin.gen.Endpoint;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kongxiangwen on 9/17/18 w:38.
 */
class ClientRequestAdapterImpl implements ClientRequestAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ClientRequestAdapterImpl.class);
	String spanName;
	SpanId spanId;

	ClientRequestAdapterImpl(String spanName){
		this.spanName = spanName;
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
		KeyValueAnnotation kv = KeyValueAnnotation.create("client-request", "111111");
		collection.add(kv);
		return collection;
	}


	public Endpoint serverAddress() {
		return null;
	}

}
