package traben.entity_texture_features.mixin.client;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;


@Mixin(Identifier.class)
public class MIX_Identifier {
    @Inject(method = "isPathValid", cancellable = true, at = @At("RETURN"))
    private static void ETF_illegalPathOverride(CallbackInfoReturnable<Boolean> cir) {
        if (ETFConfigData != null) {
            if (ETFConfigData.allowIllegalTexturePaths) {
                if (!cir.getReturnValue()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}


