package traben.entity_texture_features.mixin.accessor;

import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerSkinProvider.class)
public interface PlayerSkinProviderAccessor {

    @Accessor
    PlayerSkinProvider.FileCache getSkinCache();

    @Accessor
    PlayerSkinProvider.FileCache getCapeCache();
}
