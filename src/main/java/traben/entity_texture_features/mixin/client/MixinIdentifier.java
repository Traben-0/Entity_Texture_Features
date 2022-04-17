package traben.entity_texture_features.mixin.client;

import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;


@Mixin(Identifier.class)
public class MixinIdentifier {
    @Inject(method = "isPathValid", cancellable = true, at = @At("RETURN"))
    private static void etf$allowIllegalPaths(String path, CallbackInfoReturnable<Boolean> cir) {
        if (ETFConfigData != null) {
            if (ETFConfigData.allowIllegalTexturePaths) {
                if (!cir.getReturnValue()) {
                    //only allow the fix for images. the only real use case is for weird emissive suffixes
                    //property files should not have a use case here
                    if (path.endsWith(".png")) { //|| path.endsWith(".properties")) {
                        ETFUtils.modWarn(String.format("Encountered broken path: %s", path), false);
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
