package com.wanda.pay.common.httpclient;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Description
 * @Author liangrun
 * @Create 2017-01-04 08:51
 * @Version 1.0
 */
@RequiredArgsConstructor
public enum PayHttpClientExceptionEnum {
    INVALID_STATUS(9991, "非法响应状态"),
    CONN_TIMEOUT(9997, "连接超时"),
    SO_TIMEOUT(9998, "会话超时"),
    SYSTEM_ERROR(9999, "系统错误");

    @Getter
    private final int code;
    @Getter
    private final String desc;
}
