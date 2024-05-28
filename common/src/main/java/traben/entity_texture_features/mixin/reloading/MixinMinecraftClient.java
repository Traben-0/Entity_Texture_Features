package traben.entity_texture_features.mixin.reloading;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFManager;


@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    #if MC > MC_20_1
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    #else
    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    #endif
    private void etf$injected(CallbackInfo ci) {
        ETFManager.resetInstance();
    }
}

