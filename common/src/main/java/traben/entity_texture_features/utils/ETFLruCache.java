package traben.entity_texture_features.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.ETFManager;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class ETFLruCache<X, Y> {

    //cache with lru functionality
    final Object2ObjectLinkedOpenHashMap<X, Y> cache;
    final int capacity = 2048;

//    public ETFLruCache(int upperCapacity) {
//        capacity = upperCapacity;
//        this.cache = new Object2ObjectLinkedOpenHashMap<>();//( 64 +(int)(capacity * (ETFConfigData.advanced_IncreaseCacheSizeModifier > 1 ? ETFConfigData.advanced_IncreaseCacheSizeModifier : 1))));
//
//        //this.capacity = capacity - 1;
//    }

    public ETFLruCache() {
        this.cache = new InternalCache<>();

    }

    public boolean containsKey(X key) {
        return this.cache.containsKey(key);
    }

    @Nullable
    public Y get(X key) {
        return cache.getAndMoveToFirst(key);

    }

    public void put(X key, Y value) {
        if (cache.size() >= capacity * (ETFConfigData.advanced_IncreaseCacheSizeModifier > 1 ? ETFConfigData.advanced_IncreaseCacheSizeModifier : 1)) {
            X lastKey = cache.lastKey();
            if (!lastKey.equals(key)) {
                if (lastKey instanceof ETFCacheKey ETFKey) {
                    ETFManager.getInstance().removeThisEntityDataFromAllStorage(ETFKey);
                }
                cache.remove(lastKey);
            }
        }
        cache.putAndMoveToFirst(key, value);
    }

//    public void clearCache() {
//        cache.clear();
//    }


    public int size() {
        return cache.size();
    }

    public void removeEntryOnly(X key) {
        cache.remove(key);
    }

    @Override
    public String toString() {
        return cache.toString();
    }

    private static class InternalCache<X, Y> extends Object2ObjectLinkedOpenHashMap<X, Y> {
        {
            defRetValue = null;
        }
    }
}
