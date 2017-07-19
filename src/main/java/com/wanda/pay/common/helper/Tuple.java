package com.wanda.pay.common.helper;

import lombok.Data;

/**
 * @Description
 * @Author liangrun
 * @Create 2016-12-27 14:59
 * @Version 1.0
 */
@Data
public class Tuple<A, B, C> {
    private A first;
    private B second;
    private C third;

    public Tuple() {
    }

    public Tuple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
