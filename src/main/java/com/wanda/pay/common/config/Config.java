package com.wanda.pay.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Description
 * @Author liangrun
 * @Create 2017-01-03 16:02
 * @Version 1.0
 */
@Slf4j
public final class Config {
    private static final String CONFIG_PATH_DEFAULT = "/common_default.properties";
    private static final String CONFIG_PATH_CUSTOM = "/common_custom.properties";

    /**
     * 连接池大小，缺省为100个连接
     */
    public static final int DEFAULT_MAX_TOTAL;
    /**
     * 每个Host最大连接数，缺省为20个连接
     */
    public static final int DEFAULT_MAX_PER_ROUTE;
    /**
     * 闲置连接超时时间, 缺省为120秒钟
     */
    public static final int DEFAULT_IDLE_TIMEOUT;
    /**
     * 清理闲置连接间隔, 缺省为5秒钟
     */
    public static final int DEFAULT_CLEAR_IDLE_INTERVAL;
    /**
     * 连接超时时间，缺省为20秒钟
     */
    public static final int DEFAULT_CONNECTIONT_IMEOUT;
    /**
     * 响应超时时间, 缺省为20秒钟
     */
    public static final int DEFAULT_SO_TIMEOUT;
    /**
     * 调用者身份
     */
    public static final String DEFAULT_UNIN_SOURCE;

    /**
     * 线程池大小，默认为CPU核数 * 2
     */
    public static final int DEFAULT_THREAD_POOL_SIZE;

    static {
        Properties defaultProperties = new Properties();
        Properties customProperties = new Properties();

        InputStream defaultIS = null;

        try {
            defaultIS = Config.class.getResourceAsStream(CONFIG_PATH_DEFAULT);
            if (defaultIS != null) {
                defaultProperties.load(defaultIS);
            }
        } catch (Exception e) {
            log.warn("Load default common config file error, will use default common config");
        } finally {
            try {
                if (defaultIS != null) {
                    defaultIS.close();
                }
            } catch (Exception e) {
            }
        }
        InputStream customIS = null;
        try {
            customIS = Config.class.getResourceAsStream(CONFIG_PATH_CUSTOM);
            if (customIS != null) {
                customProperties.load(customIS);
            }
        } catch (Exception e) {
            log.warn("Load custom common config file error, will use default common config");
        } finally {
            try {
                if (customIS != null) {
                    customIS.close();
                }
            } catch (Exception e) {
            }
        }

        String maxTotal = customProperties.getProperty("httpclient.pool.max.total", "");
        if (StringUtils.isBlank(maxTotal)) {
            maxTotal = defaultProperties.getProperty("httpclient.pool.max.total", "");
        }
        DEFAULT_MAX_TOTAL = NumberUtils.toInt(maxTotal, 100);

        String maxPerRoute = customProperties.getProperty("httpclient.pool.max.per.route", "");
        if (StringUtils.isBlank(maxPerRoute)) {
            maxPerRoute = defaultProperties.getProperty("httpclient.pool.max.per.route", "");
        }
        DEFAULT_MAX_PER_ROUTE = NumberUtils.toInt(maxPerRoute, 20);

        String idleTimeout = customProperties.getProperty("httpclient.pool.idle.timeout", "");
        if (StringUtils.isBlank(idleTimeout)) {
            idleTimeout = defaultProperties.getProperty("httpclient.pool.idle.timeout", "");
        }
        DEFAULT_IDLE_TIMEOUT = NumberUtils.toInt(idleTimeout, 120000);

        String idleClearIterval = customProperties.getProperty("httpclient.pool.idle.clear.interval", "");
        if (StringUtils.isBlank(idleClearIterval)) {
            idleClearIterval = defaultProperties.getProperty("httpclient.pool.idle.clear.interval", "");
        }
        DEFAULT_CLEAR_IDLE_INTERVAL = NumberUtils.toInt(idleClearIterval, 5000);

        String connTimeout = customProperties.getProperty("httpclient.conn.timeout", "");
        if (StringUtils.isBlank(connTimeout)) {
            connTimeout = defaultProperties.getProperty("httpclient.conn.timeout", "");
        }
        DEFAULT_CONNECTIONT_IMEOUT = NumberUtils.toInt(connTimeout, 20000);

        String soTimeout = customProperties.getProperty("httpclient.socket.timeout", "");
        if (StringUtils.isBlank(soTimeout)) {
            soTimeout = defaultProperties.getProperty("httpclient.socket.timeout", "");
        }
        DEFAULT_SO_TIMEOUT = NumberUtils.toInt(soTimeout, 20000);

        String uninSource = customProperties.getProperty("caller.unin.source", "");
        if (StringUtils.isBlank(uninSource)) {
            uninSource = defaultProperties.getProperty("caller.unin.source", "");
        }
        DEFAULT_UNIN_SOURCE = uninSource;

        DEFAULT_THREAD_POOL_SIZE = NumberUtils.toInt(customProperties.getProperty("thread.pool.size", ""), Runtime.getRuntime().availableProcessors() * 2);
    }
}
