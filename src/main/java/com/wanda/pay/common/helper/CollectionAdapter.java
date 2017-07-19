package com.wanda.pay.common.helper;

/**
 * @Description
 * @Author liangrun
 * @Create 2016-12-30 08:44
 * @Version 1.0
 */
public interface CollectionAdapter<T, S> {
    public S adapt(T t);
}
