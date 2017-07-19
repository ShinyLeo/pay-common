package com.wanda.pay.common.redis;

import com.wanda.pay.monitor.client.loggers.MonitorLogMgnt;
import lombok.Data;
import lombok.Synchronized;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.core.RList;
import org.redisson.core.RLock;
import org.redisson.core.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tangxuanli on 17/3/31.
 */
public class RedisManager {

    static Logger logger = LoggerFactory.getLogger(RedisManager.class);
    private static String host = "";
    private static int port = 0;
    private static int poolSize = 5;
    private static Redisson redisson = null;
    private static RedisManager redisManager = null;

    static {
        logger.info("start to load RedisManager.....");
        Properties properties = new Properties();
        InputStream inputStream = new BufferedInputStream(MonitorLogMgnt.class.getClassLoader().getResourceAsStream("common_redis.properties"));
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("common_redis.properties not exists in classpath.");
            logger.error("", e);
        }
        Config config = new Config();
        host = properties.getProperty("redis.host");
        port = NumberUtils.toInt(properties.getProperty("redis.port"));
        poolSize = NumberUtils.toInt(properties.getProperty("redis.pool.size"));
        logger.info("host:{},port:{},poolSize:{}", host, port, poolSize);
        if (StringUtils.isBlank(host)) {
            logger.error("common_redis.properties not exists in classpath, redis.host/redis.port/redis.pool.size should be config");
        } else {
            config.useMasterSlaveConnection().setMasterAddress(host.trim() + ":" + port).setMasterConnectionPoolSize(poolSize);
            redisson = Redisson.create(config);
        }
    }

    @Synchronized
    public static RedisManager getInstance() {
        if (redisManager == null) {
            redisManager = new RedisManager();
        }
        return redisManager;
    }

    /**
     * get by key
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> Result<T> get(String key) {
        Result<T> result = new Result();
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            result.setData(rList.get(0));
        } else {
            result.setSuccess(false);
            result.setMsg("no data");
        }
        return result;
    }


    /**
     * set key/value
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Result<T> set(String key, T value) {
        Result<T> result = new Result();
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            rList.clear();
        }
        rList.add(value);
        result.setData(value);
        return result;
    }

    /**
     * set key/value in expire seconds
     *
     * @param key
     * @param value
     * @param expire seconds.
     * @param <T>
     * @return
     */
    public <T> Result<T> set(String key, T value, int expire) {
        Result<T> result = new Result();
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            rList.clear();
        }
        rList.add(value);
        rList.expireAt(new Date(System.currentTimeMillis() + expire * 1000));
        return result;
    }

    public <T> Result<List<T>> getList(String key) {
        Result<List<T>> result = new Result();
        List<T> list = new ArrayList<>();
        result.setData(list);
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            for (T t : rList) {
                result.getData().add(t);
            }
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setMsg("no data");
        }
        return result;
    }

    public <T> Result<List<T>> setList(String key, List<T> list) {
        Result<List<T>> result = new Result();
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            rList.clear();
            rList.addAll(list);
        } else {
            rList.addAll(list);
        }
        result.setData(list);
        return result;
    }

    public <T> Result<List<T>> setList(String key, List<T> list, int exprie) {
        Result<List<T>> result = new Result();
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            rList.clear();
            rList.addAll(list);
        } else {
            rList.addAll(list);
        }
        rList.expireAt(new Date(System.currentTimeMillis() + exprie * 1000));
        result.setData(list);
        return result;
    }

    public <T> Result<Map<String, T>> getMap(String key) {
        Result<Map<String, T>> result = new Result<>();
        RMap<String, T> ret = redisson.getMap(key);
        result.setData(ret);
        return result;
    }

    public <T> Result<Map<String, T>> setMap(String key, Map<String, T> map) {
        Result<Map<String, T>> result = new Result<>();
        RMap<String, T> ret = redisson.getMap(key);
        ret.putAll(map);
        result.setData(ret);
        return result;
    }

    public <T> Result<Map<String, T>> setMap(String key, String field, T value) {
        Result<Map<String, T>> result = new Result<>();
        RMap<String, T> ret = redisson.getMap(key);
        ret.put(field, value);
        result.setData(ret);
        return result;
    }

    public <T> Result<Map<String, T>> setMap(String key, Map<String, T> map, int exprie) {
        Result<Map<String, T>> result = new Result<>();
        RMap<String, T> ret = redisson.getMap(key);
        ret.putAll(map);
        ret.expireAt(new Date(System.currentTimeMillis() + exprie * 1000));
        result.setData(ret);
        return result;
    }

    public <T> Result<Map<String, T>> deleteMap(String key, String field) {
        Result<Map<String, T>> result = new Result<>();
        RMap<String, T> ret = redisson.getMap(key);
        ret.remove(field);
        result.setData(ret);
        return result;
    }

    /**
     * list 新增
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Result<T> add(String key, T value) {
        Result<T> result = new Result();
        result.setSuccess(true);
        RList<T> rList = redisson.getList(key);
        if (rList != null && rList.size() > 0) {
            boolean flag = false;
            for (T t : rList) {
                if (t.equals(value)) {
                    flag = true;
                }
            }
            if (!flag) {
                rList.add(value);
            }
        } else {
            rList.add(value);
        }
        return result;
    }


    /**
     * delete by key
     *
     * @param key
     * @param <T>
     * @return
     */
    public void remove(String key) {
        redisson.getKeys().delete(key);
    }


    /**
     * 等待 <waitSeconds>秒，如果没有获取到锁那么返回0，否则返回1
     *
     * @param key
     * @param waitSeconds
     * @param expireSeconds
     * @return
     */
    public int lock(String key, Long waitSeconds, Long expireSeconds) {
        RLock rLock = redisson.getLock(LOCK_PREFIX + key);
        try {
            if (rLock.tryLock(waitSeconds, expireSeconds, TimeUnit.SECONDS)) {
                return 1;
            }
        } catch (InterruptedException e) {
            logger.error("e", e);
        }
        return 0;
    }


    /**
     * 在释放锁的时候调用
     * try{
     * int ret= redisService.lock(...)
     * }finally{
     * redisService.unlock(...);
     * }
     *
     * @param key
     */
    public void unlock(String key) {
        RLock rLock = redisson.getLock(LOCK_PREFIX + key);
        if (rLock.isLocked()) {
            rLock.forceUnlock();
        }
    }

    private static final String LOCK_PREFIX = "redis_lock_prefix_";

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }


    @Data
    public static class Result<T> {
        private static final String RESULT_SUCCESS = "SUCCESS";
        private boolean success = true;
        private String msg = RESULT_SUCCESS;
        private T data;
    }
}
