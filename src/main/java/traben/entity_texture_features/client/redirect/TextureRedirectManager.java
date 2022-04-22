package traben.entity_texture_features.client.redirect;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class TextureRedirectManager {
    private static final Reference2ReferenceOpenHashMap<Identifier, TextureRedirect> REDIRECT_MAP = new Reference2ReferenceOpenHashMap<>();

    public static void addRedirect(Identifier originalTextureId, TextureRedirect textureRedirect) {
        REDIRECT_MAP.put(originalTextureId, textureRedirect);
    }

    public static Identifier redirect(Identifier originalTextureId, @Nullable LivingEntity livingEntity) {
        if(REDIRECT_MAP.containsKey(originalTextureId)) {
            return REDIRECT_MAP.get(originalTextureId).redirect(originalTextureId, livingEntity);
        }

        return originalTextureId;
    }
}
