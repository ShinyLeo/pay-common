package com.wanda.pay.common.monitor;

import com.wanda.pay.monitor.client.monitor.PayMonitorClient;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by tangxuanli on 17/3/16.
 */

public class PayMonitorContext<T> {
    @Getter
    @Setter
    private Long startTime;
    @Getter
    @Setter
    private PayMonitorClient client;

    @Getter
    @Setter
    private T response;

    private static ThreadLocal<PayMonitorContext> threadLocal = new ThreadLocal<PayMonitorContext>() {
        @Override
        protected PayMonitorContext initialValue() {
            return new PayMonitorContext();
        }
    };


    /**
     * 释放资源
     */
    public static void release() {
        threadLocal.remove();
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public static PayMonitorContext current() {
        return threadLocal.get();
    }

}
