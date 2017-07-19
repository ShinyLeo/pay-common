package com.wanda.pay.common.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by robin on 16/12/22.
 */
@Slf4j
public class URLEncodeHelper {
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static String encode(String value) {
        return encode(value, DEFAULT_CHARSET);
    }

    public static String encode(String value, String charset) {
        try {
            return URLEncoder.encode(value, charset);
        } catch (UnsupportedEncodingException e) {
            log.warn("Encode [{}] with [{}] error", value, charset);
            return value;
        }
    }
}
