package traben.entity_texture_features.client.emissive;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFUtils;
import traben.entity_texture_features.client.redirect.TextureRedirectManager;

public class EmissiveTextureLoader {
    private static final Reference2ReferenceOpenHashMap<Identifier, Identifier> EMISSIVE_TEXTURE_CACHE = new Reference2ReferenceOpenHashMap<>();
    private static final ReferenceLinkedOpenHashSet<Identifier> FIXED_TEXTURES = new ReferenceLinkedOpenHashSet<>();

    private static final ResourceManager RESOURCE_MANAGER = MinecraftClient.getInstance().getResourceManager();

    @Nullable
    public static Identifier cacheOrGet(Identifier normalTextureId) {
        if (!EMISSIVE_TEXTURE_CACHE.containsKey(normalTextureId)) {
            String normalTextureIdString = normalTextureId.toString();

            for (String emissiveSuffix : EmissiveSuffixLoader.getEmissiveSuffixes()) {
                Identifier emissiveTextureId = new Identifier(normalTextureIdString.substring(0, normalTextureIdString.lastIndexOf(".png")) + emissiveSuffix + ".png");

                if (RESOURCE_MANAGER.containsResource(emissiveTextureId)) {
                    EMISSIVE_TEXTURE_CACHE.put(normalTextureId, emissiveTextureId);
                } else {
                    EMISSIVE_TEXTURE_CACHE.put(normalTextureId, null);
                }
            }
        }

        return EMISSIVE_TEXTURE_CACHE.get(normalTextureId);
    }

    public static void patchTextureForEmissives(Identifier originalTextureId) {
        if (FIXED_TEXTURES.contains(originalTextureId)) {
            String originalTextureIdString = originalTextureId.toString();

            for (String emissiveSuffix : EmissiveSuffixLoader.getEmissiveSuffixes()) {
                Identifier emissiveTextureId = new Identifier(originalTextureIdString.substring(0, originalTextureIdString.lastIndexOf(".png")) + emissiveSuffix + ".png");

                if (RESOURCE_MANAGER.containsResource(emissiveTextureId)) {
                    if (emissiveTextureId != null) {
                        NativeImage emissiveTexture = ETFUtils.loadTextureFromId(RESOURCE_MANAGER, emissiveTextureId);
                        NativeImage normalTextureCopy = ETFUtils.loadTextureFromId(RESOURCE_MANAGER, originalTextureId);

                        try {
                            if (emissiveTexture.getWidth() == normalTextureCopy.getWidth() && emissiveTexture.getHeight() == normalTextureCopy.getHeight()) {
                                for (int x = 0; x < normalTextureCopy.getWidth(); x++) {
                                    for (int y = 0; y < normalTextureCopy.getHeight(); y++) {
                                        if (emissiveTexture.getOpacity(x, y) != 0) {
                                            normalTextureCopy.setColor(x, y, 0);
                                        }
                                    }
                                }
                                //no errors and fully replaced
                                FIXED_TEXTURES.add(originalTextureId);

                                Identifier emissivePatchedTextureId = new Identifier(originalTextureIdString.substring(0, originalTextureIdString.lastIndexOf(".png")) + "_etf_patched" + ".png");
                                ETFUtils.registerTexture(normalTextureCopy, emissivePatchedTextureId);
                                TextureRedirectManager.addRedirect(originalTextureId, new EmissivePatchedTextureRedirect(emissivePatchedTextureId));

                                return;
                            }
                        } catch (NullPointerException ignored) {}
                    }

                    break;
                }
            }
        }
    }

    public static void clearCache() {
        EMISSIVE_TEXTURE_CACHE.clear();
        FIXED_TEXTURES.clear();
    }
}
