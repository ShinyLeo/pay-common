package com.wanda.pay.common.trace;

import com.wanda.pay.common.constants.PayConstants;
import com.wanda.pay.common.helper.JsonHelper;
import com.wanda.pay.common.helper.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by robin on 16/12/22.
 */
@Slf4j
public class TraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long start = System.currentTimeMillis();
        TraceContext.current().setStartTime(start);

        String traceId = request.getParameter(PayConstants.KEY_TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = StringHelper.randomTimestampStr(start, 7);
        }
        TraceContext.current().setTraceId(traceId);

        String spanId = request.getParameter(PayConstants.KEY_SPAN_ID);
        if (StringUtils.isBlank(spanId)) {
            spanId = "0";
        }
        TraceContext.current().setSpanId(spanId);

        log.info("****************Request start uri:{}-{}, params:{}, [traceId:{}], [spanId:{}]****************", request.getMethod(), request.getRequestURI(), JsonHelper.toJson(request.getParameterMap()), traceId, spanId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long start = TraceContext.current().getStartTime();
        long cost = System.currentTimeMillis() - start;
        log.info("****************Request completed response:{}, cost:{}ms, [traceId:{}], [spanId:{}]{}****************", TraceContext.current().getResponse(), cost, TraceContext.current().getTraceId(), TraceContext.current().getSpanId(), cost > 1000 ? ", _slow_request_" : "");
        TraceContext.release();
    }
}
