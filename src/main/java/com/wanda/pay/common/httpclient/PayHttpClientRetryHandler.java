package com.wanda.pay.common.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.NoHttpResponseException;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * @Description
 * @Author liangrun
 * @Create 2017-02-28 11:00
 * @Version 1.0
 */
@Slf4j
public class PayHttpClientRetryHandler extends DefaultHttpRequestRetryHandler {
    public PayHttpClientRetryHandler() {
        super(1, false);
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount > this.getRetryCount()) {
            return false;
        }
        if (exception instanceof NoHttpResponseException) {
            log.info("NoHttpResponse retry");
            return true;
        }
        return false;
    }
}
