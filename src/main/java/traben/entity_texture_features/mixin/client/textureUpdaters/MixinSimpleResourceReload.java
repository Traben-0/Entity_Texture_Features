package traben.entity_texture_features.mixin.client.textureUpdaters;

import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.utils.ETFUtils;


@Mixin(SimpleResourceReload.class)
public abstract class MixinSimpleResourceReload {

    private static boolean etf$falseAfterFirstRun = true;

    @Inject(method = "getProgress", at = @At("RETURN"))
    private void etf$injected(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValue() == 1.0) {
            if (etf$falseAfterFirstRun) {
                etf$falseAfterFirstRun = false;
                ETFUtils.resetAllETFEntityData();
            }
        } else {
            etf$falseAfterFirstRun = true;
        }
    }
}
/*
below inject location insufficient as it does not allow emissive suffix loading to happen correctly
there is likely another better location but the above guarantees 100% reload completion and 1.18.1 support
this triggers twice in some scenarios, like first boot, but performs very little code.

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinReloadableResourceManagerImpl {
    @Inject(method = "method_29491", at = @At("HEAD"))
    private static void etf$updateVisuals(CallbackInfoReturnable<String> cir) {
        ETFUtils.resetAllETFEntityData();
    }
}
*/
