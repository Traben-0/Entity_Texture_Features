package traben.entity_texture_features.utils;

import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;

import java.util.UUID;

public class EntityBooleanLRU extends Object2BooleanLinkedOpenHashMap<UUID> {
    {
        defaultReturnValue(false);
    }

    final int capacity;

    public EntityBooleanLRU(int capacity) {
        this.capacity = capacity;
    }

    public EntityBooleanLRU() {
        this.capacity = 2048;
    }

    @Override
    public boolean put(UUID uuid, boolean v) {
        if (size() >= capacity) {
            UUID lastKey = lastKey();
            if (!lastKey.equals(uuid)) {
                removeBoolean(lastKey);
            }
        }
        return this.putAndMoveToFirst(uuid, v);
    }
}
