package com.github.nicholasmaven.expression.constant;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 通用的枚举辅助类, 用于根据属性/名称反查. 该类内部会有缓存
 *
 * @author mawen
 */
@SuppressWarnings({"unchecked"})
public class EnumUtils {

    private static final Map<String, Map<Object, ? extends Enum<?>>> CACHE = new ConcurrentHashMap<>();
    private static final String RESERVED_ENUM_NAME = "0_name";

    /**
     * @param clazz  枚举类型
     * @param property 枚举的属性名
     * @param value  要查找的值
     */
    public static <E extends Enum<E>, T> E lookupByProperty(Class<E> clazz, String property, Function<E, T> function,
            T value) {
        Assert.notNull(clazz, "class is null");
        if (property.equals(RESERVED_ENUM_NAME)) {
            throw new IllegalArgumentException("property conflicts with reserved enum name");
        }

        String key = clazz.getName().concat(":").concat(property);
        Map<Object, E> map = (Map<Object, E>) CACHE.computeIfAbsent(key, (Function<String, Map<Object, E>>) s -> {
            Map<Object, E> indexes = new HashMap<>();
            for (E e : clazz.getEnumConstants()) {
                indexes.put(function.apply(e), e);
            }
            return indexes;
        });
        return map.get(value);
    }

    public static <E extends Enum<E>, T> E lookupByEnumName(Class<E> clazz, T value) {
        Assert.notNull(clazz, "class is null");

        String key = clazz.getName().concat(":" + RESERVED_ENUM_NAME);
        Map<Object, E> map = (Map<Object, E>) CACHE.computeIfAbsent(key, (Function<String, Map<Object, E>>) s -> {
            Map<Object, E> indexes = new HashMap<>();
            for (E e : clazz.getEnumConstants()) {
                indexes.put(e.name(), e);
            }
            return indexes;
        });
        return map.get(value);
    }
}
