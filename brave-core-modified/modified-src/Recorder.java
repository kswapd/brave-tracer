//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.kristofa.brave;

import com.github.kristofa.brave.AnnotationSubmitter.Clock;
import com.github.kristofa.brave.internal.DefaultSpanCodec;
import com.github.kristofa.brave.internal.V2SpanConverter;
import com.google.auto.value.AutoValue;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.BinaryAnnotation;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;
import java.util.List;
import zipkin2.reporter.Reporter;

abstract class Recorder implements Clock {
    Recorder() {
    }

    abstract void name(Span var1, String var2);

    abstract void start(Span var1, long var2);

    abstract void annotate(Span var1, long var2, String var4);

    abstract void address(Span var1, String var2, Endpoint var3);

    abstract void tag(Span var1, String var2, String var3);

    abstract void finish(Span var1, long var2);

    abstract void flush(Span var1);

    @AutoValue
    abstract static class Default extends Recorder {
        Default() {
        }

        abstract Endpoint localEndpoint();

        abstract Clock clock();

        abstract Reporter<zipkin2.Span> reporter();

        public long currentTimeMicroseconds() {
            return this.clock().currentTimeMicroseconds();
        }

        void name(Span span, String name) {
            synchronized(span) {
                span.setName(name);
            }
        }

        void start(Span span, long timestamp) {
            synchronized(span) {
                span.setTimestamp(timestamp);
            }
        }

        void annotate(Span span, long timestamp, String value) {
            Annotation annotation = Annotation.create(timestamp, value, this.localEndpoint());
            synchronized(span) {
                span.addToAnnotations(annotation);
            }
        }

        void address(Span span, String key, Endpoint endpoint) {
            BinaryAnnotation address = BinaryAnnotation.address(key, endpoint);
            synchronized(span) {
                span.addToBinary_annotations(address);
            }
        }

        void tag(Span span, String key, String value) {
            BinaryAnnotation ba = BinaryAnnotation.create(key, value, this.localEndpoint());
            synchronized(span) {
                span.addToBinary_annotations(ba);
            }
        }

        void finish(Span span, long timestamp) {

            synchronized(span) {
                int i;
                int length;
                Long startTimestamp = span.getTimestamp();
                if (startTimestamp != null) {
                    span.setDuration(Math.max(1L, timestamp - startTimestamp));
                }


               
           
                if (span.isShared()) {
                    i = 0;

                    for(length = span.getAnnotations().size(); i < length; ++i) {
                        if (((Annotation)span.getAnnotations().get(i)).value.equals("sr")) {
                            span.setTimestamp((Long)null);
                            break;
                        }
                    }
                }
            

                List<zipkin2.Span> toReport = V2SpanConverter.fromSpan(DefaultSpanCodec.toZipkin(span));
                i = 0;

                for(length = toReport.size(); i < length; ++i) {
                    this.reporter().report((zipkin2.Span)toReport.get(i));
                }
            }
        }

        void flush(Span span) {
            int i;
            int length;
            synchronized(span) {
                if (span.isShared()) {
                    i = 0;

                    for(length = span.getAnnotations().size(); i < length; ++i) {
                        if (((Annotation)span.getAnnotations().get(i)).value.equals("sr")) {
                            span.setTimestamp((Long)null);
                            break;
                        }
                    }
                }
                List<zipkin2.Span> toReport = V2SpanConverter.fromSpan(DefaultSpanCodec.toZipkin(span));
                i = 0;

                for(length = toReport.size(); i < length; ++i) {
                    this.reporter().report((zipkin2.Span)toReport.get(i));
                }
            }

            

        }
    }
}
