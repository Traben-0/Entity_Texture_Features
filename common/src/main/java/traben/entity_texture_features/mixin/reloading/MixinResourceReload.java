package traben.entity_texture_features.mixin.reloading;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;


@Mixin(MinecraftClient.class)
public abstract class MixinResourceReload {


    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void etf$injected(CallbackInfoReturnable<Float> cir) {
        ETFUtils2.logMessage("reloading ETF data.");
        ETFManager.resetInstance();

    }
}

