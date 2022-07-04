package traben.entity_texture_features.utils;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ETFCacheKey(UUID uuid, Identifier identifier) {

    //don't need to create new object every time
    private static final Identifier NULL_IDENTIFIER = new Identifier("etf:null");

    public ETFCacheKey(UUID uuid, @Nullable Identifier identifier) {
        this.uuid = uuid;
        this.identifier = identifier == null ? NULL_IDENTIFIER : identifier;
    }

    public UUID getMobUUID() {
        return this.uuid;
    }


}
