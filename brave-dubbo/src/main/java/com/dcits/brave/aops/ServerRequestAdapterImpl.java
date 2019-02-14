package com.dcits.brave.aops;

import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerRequestAdapter;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.TraceData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServerRequestAdapterImpl implements ServerRequestAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ServerRequestAdapterImpl.class);
   Random randomGenerator = new Random();
   SpanId spanId;
   String spanName;

   ServerRequestAdapterImpl(String spanName){
	   this.spanName = spanName;
	   long startId = randomGenerator.nextLong();
	   SpanId spanId = SpanId.builder().spanId(startId).traceId(startId).parentId(startId).build();
	   this.spanId = spanId;
	   logger.debug(String.format("ServerRequestAdapterImpl:trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

   }

   ServerRequestAdapterImpl(String spanName, SpanId spanId){
	   this.spanName = spanName;
	   this.spanId = spanId;
   }


   public TraceData getTraceData() {
	   if (this.spanId != null) {
		   logger.debug(String.format("ServerRequestAdapterImpl:getTraceData trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

		   return TraceData.builder().spanId(this.spanId).build();
	   }
	   logger.debug(String.format("ServerRequestAdapterImpl:getTraceData generate trace_id=%s, parent_id=%s, span_id=%s", Long.toHexString(spanId.traceId),  Long.toHexString(spanId.parentId),  Long.toHexString(spanId.spanId)));

	   long startId = randomGenerator.nextLong();
	   SpanId spanId = SpanId.builder().spanId(startId).traceId(startId).parentId(startId).build();
	   return TraceData.builder().spanId(spanId).build();
   }


   public String getSpanName() {
	   return spanName;
   }


   public Collection<KeyValueAnnotation> requestAnnotations() {
	   Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
	   KeyValueAnnotation kv = KeyValueAnnotation.create("server-request", "222222");
	   collection.add(kv);
	   return collection;
   }

}
