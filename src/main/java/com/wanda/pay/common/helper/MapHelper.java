package com.wanda.pay.common.helper;

import java.util.Map;

/**
 * Created by robin on 16/12/22.
 */
public class MapHelper {
    /**
     * 判断Map是否不为空
     * @param map
     * @return
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    /**
     * 判断Map是否为空
     * @param map
     * @return
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.size() <= 0;
    }
}
