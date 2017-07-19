package com.wanda.pay.common.httpclient;

import lombok.Getter;

/**
 * @Description
 * @Author liangrun
 * @Create 2017-01-04 08:50
 * @Version 1.0
 */
public class PayHttpClientException extends Exception {
    @Getter
    private int code;

    public PayHttpClientException(PayHttpClientExceptionEnum error) {
        super(error.getDesc());
        this.code = error.getCode();
    }

    public PayHttpClientException(PayHttpClientExceptionEnum error, String msg) {
        super(msg);
        this.code = error.getCode();
    }

    public PayHttpClientException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
