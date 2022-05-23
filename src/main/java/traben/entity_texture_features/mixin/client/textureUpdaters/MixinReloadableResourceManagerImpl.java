package traben.entity_texture_features.mixin.client.textureUpdaters;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.utils.ETFUtils;

//better reload mixin location thanks to #Maximum#8760
@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinReloadableResourceManagerImpl {
    @Inject(method = "method_29491", at = @At("HEAD"))
    private static void etf$updateVisuals(CallbackInfoReturnable<String> cir) {
        ETFUtils.resetAllETFEntityData();
    }


}

