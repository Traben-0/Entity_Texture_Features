package traben.entity_texture_features.utils;

import traben.entity_texture_features.ETF;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ETFLruCache<X, Y> extends LinkedHashMap<X, Y> {

    final int capacity;

    public ETFLruCache() {
        super(2048, 0.75f, true);
        this.capacity = 2048;
    }

    public ETFLruCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<X, Y> eldest) {
        double sizeModifier = Math.max(1, ETF.config().getConfig().advanced_IncreaseCacheSizeModifier);
        return size() >= capacity * sizeModifier;
    }

    public void removeEntryOnly(X key) {
        remove(key);
    }

    protected Y defaultReturn = null;

    @Override
    public Y get(Object key) {
        return super.getOrDefault(key, defaultReturn);
    }

    public void defaultReturnValue(Y value) {
        defaultReturn = value;
    }

    public static class UUIDBoolean extends ETFLruCache<UUID, Boolean> {
        public UUIDBoolean() {
            defaultReturnValue(false);
        }
        public UUIDBoolean(int capacity) {
            super(capacity);
            defaultReturnValue(false);
        }
    }
    public static class UUIDInteger extends ETFLruCache<UUID, Integer> {
        public UUIDInteger() {
            defaultReturnValue(-1);
        }
        public UUIDInteger(int capacity) {
            super(capacity);
            defaultReturnValue(-1);
        }
    }
}
