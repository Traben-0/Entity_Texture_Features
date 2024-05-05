package traben.entity_texture_features.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.HttpTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.player.ETFPlayerTexture;


@Mixin(HttpTexture.class)
public abstract class MixinPlayerSkinTexture {


    @Inject(method = "setNoAlpha", cancellable = true, at = @At("HEAD"))
    private static void etf$cancelling(final NativeImage image, final int x1, final int y1, final int x2, final int y2, final CallbackInfo ci) {
        if (ETF.config().getConfig() != null) {
            var mode = ETF.config().getConfig().skinTransparencyMode;

            if (mode == ETFConfig.SkinTransparencyMode.ETF_SKINS_ONLY && ETFPlayerTexture.remappingETFSkin) {
                ci.cancel();
            } else if (mode == ETFConfig.SkinTransparencyMode.ALL) {
                ci.cancel();
            }
        }
    }
}


