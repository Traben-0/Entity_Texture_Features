package traben.entity_texture_features.utils;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ETFCacheKey {

    public final UUID uuid;
    public final Identifier identifier;

    //don't need to create new object every time
    private static final Identifier NULL_IDENTIFIER = new Identifier("etf:null");

    public ETFCacheKey(UUID uuid, @Nullable Identifier identifier) {
        this.uuid = uuid;
        this.identifier = identifier == null ? NULL_IDENTIFIER : identifier;
    }

    public UUID getMobUUID() {
        return this.uuid;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode() + identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ETFCacheKey){
            return ((ETFCacheKey) o).uuid.equals(this.uuid) && ((ETFCacheKey) o).identifier.equals(this.identifier);
        }
        return false;
    }
}
