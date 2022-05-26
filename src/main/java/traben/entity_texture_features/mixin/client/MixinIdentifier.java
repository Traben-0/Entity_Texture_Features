package traben.entity_texture_features.mixin.client;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(Identifier.class)
public abstract class MixinIdentifier {

    @Inject(method = "isPathValid", cancellable = true, at = @At("RETURN"))
    private static void etf$illegalPathOverride(String path, CallbackInfoReturnable<Boolean> cir) {
        if (ETFConfigData != null) {
            if (ETFConfigData.allowIllegalTexturePaths) {
                if (!cir.getReturnValue() && path != null) {
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


