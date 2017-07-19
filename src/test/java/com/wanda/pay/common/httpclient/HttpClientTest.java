package com.wanda.pay.common.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.wanda.pay.common.config.Config;
import com.wanda.pay.common.helper.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by robin on 16/12/22.
 */
public class HttpClientTest {
    private static final int CASE_COUNT = 100;
    @Test
    public void testDoGet() throws Exception {
        try {
            String url = "http://api.sit.ffan.com/pay-account/v1/fullBanks?bankCode=ICBC";
            String response = PayHttpClient.doGet(url, 20000);
            System.out.println(response);
        } catch (Exception e) {

        }

    }

    @Test
    public void testMultiThreadDoGet() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        final CountDownLatch countDownLatch = new CountDownLatch(CASE_COUNT);
        final AtomicInteger successCounter = new AtomicInteger(0);
        for (int i = 0; i < CASE_COUNT; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    String url = "http://api.sit.ffan.com/pay-account/v1/fullBanks?bankCode=ICBC";
                    try {
//                        String response = PayHttpClient.doGet(url);
                        String response = PayHttpClientPoolManager.getInstance().doGet(url);
                        if (StringUtils.isNotBlank(response)) {
                            JsonNode json = JsonHelper.toTree(response);
                            if (json != null) {
                                JsonNode status = json.get("status");
                                if (status != null && "200".equals(status.toString())) {
                                    System.out.println(successCounter.incrementAndGet());
                                }
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        countDownLatch.countDown();

                    }
                }
            });
        }
        countDownLatch.await();
        System.out.println("Success: " + successCounter.get());
    }
}
