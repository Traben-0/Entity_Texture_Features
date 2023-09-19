package traben.entity_texture_features.mixin.accessor;

import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerSkinProvider.FileCache.class)
public interface FileCacheAccessor {
    @Accessor
    TextureManager getTextureManager();
}
