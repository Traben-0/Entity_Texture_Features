package traben.entity_texture_features.client.emissive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.redirect.TextureRedirect;

public class EmissivePatchedTextureRedirect implements TextureRedirect {
    private final Identifier emissivePatchedTextureId;

    public EmissivePatchedTextureRedirect(Identifier emissivePatchedTextureId) {
        this.emissivePatchedTextureId = emissivePatchedTextureId;
    }

    @Override
    public Identifier redirect(Identifier originalTextureId, LivingEntity livingEntity) {
        return emissivePatchedTextureId;
    }
}
