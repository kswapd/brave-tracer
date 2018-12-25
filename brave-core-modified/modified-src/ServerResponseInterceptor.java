package com.github.kristofa.brave;

import com.github.kristofa.brave.internal.Util;
import java.util.logging.Logger;

@Deprecated
public class ServerResponseInterceptor
{
  private static final Logger LOGGER = Logger.getLogger(ServerResponseInterceptor.class.getName());
  private final ServerTracer serverTracer;
  
  public ServerResponseInterceptor(ServerTracer serverTracer)
  {
    this.serverTracer = ((ServerTracer)Util.checkNotNull(serverTracer, "Null serverTracer", new Object[0]));
  }
  
  public void handle(ServerResponseAdapter adapter)
  {
    LOGGER.fine("Sending server send.");
    try
    {
      for (KeyValueAnnotation annotation : adapter.responseAnnotations()) {
        this.serverTracer.submitBinaryAnnotation(annotation.getKey(), annotation.getValue());
      }
      this.serverTracer.setServerSend();
    }
    finally
    {
      //this.serverTracer.clearCurrentSpan();
    }
  }
}
