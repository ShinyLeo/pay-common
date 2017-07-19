package com.wanda.pay.common.helper;

import java.util.*;

/**
 * Created by robin on 16/12/22.
 */
public class CollectionHelper {
    /**
     * 判断集合是否不为空
     *
     * @param collection
     * @return
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.size() <= 0;
    }

    /**
     * 过滤出集合中元素指定字段的值
     *
     * @param list
     * @param adapter
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T, S> List<S> filter(List<T> list, CollectionAdapter<T, S> adapter) {
        List<S> ret = new ArrayList<>();
        if (isNotEmpty(list)) {
            for (T t : list) {
                ret.add(adapter.adapt(t));
            }
        }
        return ret;
    }

    /**
     * 将集合转换成以集合元素指定字段为Key的Map
     *
     * @param list
     * @param adapter
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T, S> Map<S, T> convertList2Map(List<T> list, CollectionAdapter<T, S> adapter) {
        Map<S, T> map = new HashMap<>();
        if (isNotEmpty(list)) {
            for (T t : list) {
                map.put(adapter.adapt(t), t);
            }
        }
        return map;
    }
}
