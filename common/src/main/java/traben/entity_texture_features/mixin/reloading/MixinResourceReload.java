package traben.entity_texture_features.mixin.reloading;

#if MC > MC_20_1
import net.minecraft.client.Minecraft;
#else
import net.minecraft.client.ResourceLoadStateTracker;
#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

#if MC > MC_20_1
@Mixin(Minecraft.class)
#else
@Mixin(ResourceLoadStateTracker.class)
#endif
public abstract class MixinResourceReload {


    #if MC > MC_20_1
    @Inject(method = "onResourceLoadFinished", at = @At("HEAD"))
    #else
    @Inject(method = "finishReload", at = @At("HEAD"))
    #endif
    private void etf$injected(CallbackInfo ci) {
        ETFUtils2.logMessage("reloading ETF data.");
        ETFManager.resetInstance();
    }
}

