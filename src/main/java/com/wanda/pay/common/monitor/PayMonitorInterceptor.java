package com.wanda.pay.common.monitor;

import com.wanda.pay.common.monitor.annotation.PayMonitorEntity;
import com.wanda.pay.monitor.client.monitor.PayMonitorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付监控拦截器.拦截配置的controller方法，更具注解打埋点.
 * <p>
 * Created by tangxuanli on 17/3/16.
 */
public class PayMonitorInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            PayMonitorEntity entity = handlerMethod.getMethodAnnotation(PayMonitorEntity.class);
            if (entity != null) {
                PayMonitorClient client = PayMonitorClient.newInstance(entity.k1(), entity.k2(), entity.k3());
                //qps+1
                client.qps();
                PayMonitorContext.current().setClient(client);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //记录埋点
        PayMonitorClient client = PayMonitorContext.current().getClient();
        if (client != null) {
            //记录平均响应时间
            client.rt();
            //记录成功率
            Object responseObj = PayMonitorContext.current().getResponse();
            if (responseObj != null && responseObj instanceof MonitorBaseResponse) {
                MonitorBaseResponse responseBean = (MonitorBaseResponse) responseObj;
                //记录总数
                client.sr_incrTotal();
                if (responseBean.isSuccess()) {
                    //记录成功数
                    client.sr_incrSuccess();
                }
            }
            //释放
            PayMonitorContext.release();
        }
    }
}
