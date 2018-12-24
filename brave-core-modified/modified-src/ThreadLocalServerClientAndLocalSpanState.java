package com.github.kristofa.brave;

import com.github.kristofa.brave.internal.Nullable;
import com.github.kristofa.brave.internal.Util;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;
import java.net.InetAddress;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public final class ThreadLocalServerClientAndLocalSpanState
  implements ServerClientAndLocalSpanState
{
  private static final ThreadLocal<Deque> currentServerSpan = new ThreadLocal<Deque>()
  {
    @Override
    protected Deque initialValue() {
      return new LinkedBlockingDeque();
    }
  };
  private static final InheritableThreadLocal<Deque> currentClientSpan =
          new InheritableThreadLocal<Deque>() {
            @Override
            protected Deque initialValue() {
              return new LinkedBlockingDeque();
            }
          };

  private static final InheritableThreadLocal<Deque> currentLocalSpan =
          new InheritableThreadLocal<Deque>() {
            @Override
            protected Deque initialValue() {
              return new LinkedBlockingDeque();
            }
          };

  private final Endpoint endpoint;
  
  public static void clear()
  {
    currentServerSpan.remove();
    currentClientSpan.remove();
    currentLocalSpan.remove();
  }
  
  @Deprecated
  public ThreadLocalServerClientAndLocalSpanState(InetAddress ip, int port, String serviceName)
  {
    this(InetAddressUtilities.toInt((InetAddress)Util.checkNotNull(ip, "ip address must be specified.", new Object[0])), port, serviceName);
  }
  
  public ThreadLocalServerClientAndLocalSpanState(int ip, int port, String serviceName)
  {
    this(Endpoint.builder().ipv4(ip).port(port).serviceName(serviceName).build());
  }
  
  public ThreadLocalServerClientAndLocalSpanState(Endpoint endpoint)
  {
    Util.checkNotNull(endpoint, "endpoint must be specified.", new Object[0]);
    Util.checkNotBlank(endpoint.service_name, "Service name must be specified.", new Object[0]);
    this.endpoint = endpoint;
  }

  public ServerSpan getCurrentServerSpan()
  {
    //return (ServerSpan)currentServerSpan.get();
    if(currentServerSpan.get().size() == 0){
      return ServerSpan.EMPTY;
    }else {
      return (ServerSpan) currentServerSpan.get().getLast();
    }
  }

  public void setCurrentServerSpan(@Nullable ServerSpan span)
  {
    if (span == null || span.getSpan()==null) {
      if(currentServerSpan.get().size() > 0) {
        currentServerSpan.get().removeLast();
      }
    } else {
      currentServerSpan.get().addLast(span);
    }
  }
  
  public Endpoint endpoint()
  {
    return this.endpoint;
  }

  @Nullable
  public Span getCurrentClientSpan()
  {
    if(currentClientSpan.get().size() == 0){
      return null;
    }else {
      return (Span) currentClientSpan.get().getLast();
    }
  }

  public void setCurrentClientSpan(@Nullable Span span)
  {
    if (span == null) {
      if(currentClientSpan.get().size() > 0) {
        currentClientSpan.get().removeLast();
      }
    }else{
      currentClientSpan.get().addLast(span);
    }
  }
  
  @Nullable
  public Boolean sample()
  {
    return ((ServerSpan)currentServerSpan.get()).getSample();
  }

  @Nullable
  public Span getCurrentLocalSpan()
  {
    if(currentLocalSpan.get().size() == 0){
      return null;
    }else {
      return (Span) currentLocalSpan.get().getLast();
    }
  }

  public void setCurrentLocalSpan(@Nullable Span span)
  {
    if (span == null) {
      if(currentLocalSpan.get().size() > 0) {
        currentLocalSpan.get().removeLast();
      }
    } else {
      currentLocalSpan.get().addLast(span);
    }
  }
}
