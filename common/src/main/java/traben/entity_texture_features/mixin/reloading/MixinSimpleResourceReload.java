package traben.entity_texture_features.mixin.reloading;

import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.texture_handlers.ETFManager;


@Mixin(SimpleResourceReload.class)
public abstract class MixinSimpleResourceReload {

    private static boolean etf$falseAfterFirstRun = true;

    @Inject(method = "Lnet/minecraft/resource/SimpleResourceReload;getProgress()F", at = @At("RETURN"))
    private void etf$injected(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValue() == 1.0) {
            if (etf$falseAfterFirstRun) {
                etf$falseAfterFirstRun = false;
                ETFManager.resetInstance();
            }
        } else {
            etf$falseAfterFirstRun = true;
        }
    }
}

