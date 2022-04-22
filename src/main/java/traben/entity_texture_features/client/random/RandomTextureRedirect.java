package traben.entity_texture_features.client.random;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.redirect.TextureRedirect;

public class RandomTextureRedirect implements TextureRedirect {
    @Override
    public Identifier redirect(Identifier originalTextureId, LivingEntity livingEntity) {
        return null;
    }
}
