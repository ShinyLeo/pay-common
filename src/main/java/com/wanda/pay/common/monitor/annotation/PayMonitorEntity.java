package com.wanda.pay.common.monitor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tangxuanli on 17/3/16.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PayMonitorEntity {

    /**
     * 监控维度1
     *
     * @return
     */
    public String k1();

    /**
     * 监控维度1
     *
     * @return
     */
    public String k2() default "";

    /**
     * 监控维度1
     *
     * @return
     */
    public String k3() default "";

}
