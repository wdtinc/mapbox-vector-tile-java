package com.wdtinc.mapbox_vector_tile.build;

import java.util.Map;

public class Util {

    private Util() {}

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
}
