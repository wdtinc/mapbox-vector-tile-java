package com.wdtinc.mapbox_vector_tile.util;

import java.util.Map;

/**
 * Mimic future JDK capabilities for backwards compatibility.
 */
public final class JdkUtils {

    private JdkUtils() {}

    /**
     * Backwards compatible {@link Map#putIfAbsent} to support &lt; Android API 24.
     *
     * If the specified key is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     *         {@code null} if there was no mapping for the key.
     */
    public static <K,V> V putIfAbsent(Map<K,V> map, K key, V value) {
        V val = map.get(key);
        if (val == null) {
            val = map.put(key, value);
        }
        return val;
    }

    /**
     * This method mimics the behavior of Objects.requireNonNull method to allow
     * Android API level 15 backward compatibility.
     *
     * @param object
     * @return object
     * @throws NullPointerException if object is null
     */
    public static <T> T requireNonNull(T object) {
        if (object == null)
            throw new NullPointerException();
        return object;
    }

}
