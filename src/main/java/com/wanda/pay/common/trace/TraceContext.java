package com.wanda.pay.common.trace;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by robin on 16/12/22.
 */
@Data
public class TraceContext<T> {
    private long startTime;
    private long endTime;
    private String traceId;
    private AtomicInteger spanCounter = new AtomicInteger(0);
    private String spanId;
    private T response;


    private static ThreadLocal<TraceContext> threadLocal = new ThreadLocal<TraceContext>() {
        @Override
        protected TraceContext initialValue() {
            return new TraceContext();
        }
    };

    public static void release() {
        threadLocal.remove();
    }

    public static TraceContext current() {
        return threadLocal.get();
    }

    public String getChildSpanId() {
        return (StringUtils.isBlank(spanId) ? "0" : spanId) + "." + spanCounter.addAndGet(1);
    }
}
