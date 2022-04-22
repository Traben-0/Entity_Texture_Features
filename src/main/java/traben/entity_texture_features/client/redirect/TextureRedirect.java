package traben.entity_texture_features.client.redirect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public interface TextureRedirect {
    Identifier redirect(Identifier originalTextureId, LivingEntity livingEntity);
}
