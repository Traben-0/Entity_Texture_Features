package traben.entity_texture_features.mixin.reloading;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFManager;


@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("TAIL"))
    private void etf$injected(CallbackInfo ci) {
        ETFManager.resetInstance();
    }
}

