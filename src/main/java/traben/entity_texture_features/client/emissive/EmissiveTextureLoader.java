package traben.entity_texture_features.client.emissive;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class EmissiveTextureLoader {
    private static final Reference2ReferenceOpenHashMap<Identifier, Identifier> EMISSIVE_TEXTURE_CACHE = new Reference2ReferenceOpenHashMap<>();

    private static final ResourceManager RESOURCE_MANAGER = MinecraftClient.getInstance().getResourceManager();

    @Nullable
    public static Identifier getOrLoad(Identifier normalTextureId) {
        if (!EMISSIVE_TEXTURE_CACHE.containsKey(normalTextureId)) {
            String normalTextureIdString = normalTextureId.toString();

            for (String emissiveSuffix : EmissiveSuffixLoader.getEmissiveSuffixes()) {
                Identifier emissiveTextureId = new Identifier(normalTextureIdString.toString().substring(0, normalTextureIdString.lastIndexOf(".png")) + emissiveSuffix + ".png");

                if (RESOURCE_MANAGER.containsResource(emissiveTextureId)) {
                    EMISSIVE_TEXTURE_CACHE.put(normalTextureId, emissiveTextureId);
                } else {
                    EMISSIVE_TEXTURE_CACHE.put(normalTextureId, null);
                }
            }
        }

        return EMISSIVE_TEXTURE_CACHE.get(normalTextureId);
    }

    public static void clearCache() {
        EMISSIVE_TEXTURE_CACHE.clear();
    }
}
