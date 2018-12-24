package com.github.kristofa.brave;

import com.github.kristofa.brave.internal.Util;
import com.twitter.zipkin.gen.Span;
import java.util.logging.Logger;

@Deprecated
public class ServerRequestInterceptor
{
  private static final Logger LOGGER = Logger.getLogger(ServerRequestInterceptor.class.getName());
  private final ServerTracer serverTracer;
  
  public ServerRequestInterceptor(ServerTracer serverTracer)
  {
    this.serverTracer = ((ServerTracer)Util.checkNotNull(serverTracer, "Null serverTracer", new Object[0]));
  }
  
  public void handle(ServerRequestAdapter adapter)
  {
    //this.serverTracer.clearCurrentSpan();
    TraceData traceData = adapter.getTraceData();
    
    Boolean sample = traceData.getSample();
    if (Boolean.FALSE.equals(sample))
    {
      this.serverTracer.setStateNoTracing();
      LOGGER.fine("Received indication that we should NOT trace."); return;
    }
    Span span;
    if (traceData.getSpanId() != null)
    {
      LOGGER.fine("Received span information as part of request.");
      
      span = this.serverTracer.spanFactory().joinSpan(traceData.getSpanId());
    }
    else
    {
      LOGGER.fine("Received no span state.");
      span = this.serverTracer.spanFactory().nextSpan(null);
    }
    SpanId context = Brave.context(span);
    if (!context.sampled().booleanValue())
    {
      LOGGER.fine("Trace is unsampled.");
      this.serverTracer.setStateNoTracing();
      return;
    }
    this.serverTracer.setStateCurrentTrace(span, adapter.getSpanName());
    
    this.serverTracer.setServerReceived();
    for (KeyValueAnnotation annotation : adapter.requestAnnotations()) {
      this.serverTracer.submitBinaryAnnotation(annotation.getKey(), annotation.getValue());
    }
  }
}
