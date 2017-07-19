package com.wanda.pay.common.helper;

import lombok.Data;

/**
 * Created by tangxuanli on 15/12/2.
 */
@Data
public class Pair<T, S> {
    private T first;
    private S second;

    public Pair() {

    }


    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }
}
