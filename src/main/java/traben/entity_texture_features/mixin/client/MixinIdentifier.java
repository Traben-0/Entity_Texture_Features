package traben.entity_texture_features.mixin.client;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;


@Mixin(Identifier.class)
public abstract class MixinIdentifier {
    @Shadow
    public abstract String toString();

    @Inject(method = "isPathValid", cancellable = true, at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void etf$illegalPathOverride(String path, CallbackInfoReturnable<Boolean> cir, int i) {
        if (ETFConfigData != null) {
            if (ETFConfigData.allowIllegalTexturePaths) {
                if (!cir.getReturnValue()) {
                    //only allow the fix for images. the only real use case is for weird emissive suffixes
                    //property files should not have a use case here
                    if (path.endsWith(".png")) { //|| path.endsWith(".properties")) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}


