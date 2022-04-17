package traben.entity_texture_features.client.random;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFUtils;
import traben.entity_texture_features.client.emissive.EmissiveSuffixLoader;

public class RandomTextureLoader {
    private static final Reference2BooleanOpenHashMap<Identifier> RANDOM_TEXTURE_CACHE = new Reference2BooleanOpenHashMap<>();

    @Nullable
    public static Identifier getOrLoad(Identifier normalTextureId, Entity entity) {
        // if (entity == null) return normalTextureId;

        // if (!RANDOM_TEXTURE_CACHE.containsKey(normalTextureId)) {
        //     //creates and sets emissive for texture if it exists
        //     String normalTextureIdString = normalTextureId.toString();

        //     for (String emissiveSuffix : EmissiveSuffixLoader.getEmissiveSuffixes()) {
        //         Identifier emissiveTextureId = new Identifier(normalTextureIdString.toString().substring(0, normalTextureIdString.lastIndexOf(".png")) + emissiveSuffix + ".png");

        //         if (ETFUtils.isExistingNativeImageFile(emissiveTextureId)) {
        //             RANDOM_TEXTURE_CACHE.put(normalTextureId, emissiveTextureId);
        //         } else {
        //             return null;
        //         }
        //     }
        // } else {

        // }

        return null;
    }

    public static void clearCache() {
        RANDOM_TEXTURE_CACHE.clear();
    }
}
