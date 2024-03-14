package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Inject(method = "getLight", at = @At(value = "RETURN"), cancellable = true)
    private void etf$vanillaLightOverrideCancel(T entity, float tickDelta, CallbackInfoReturnable<Integer> cir) {
        //if need to override vanilla brightness behaviour
        //change return with overridden light value still respecting higher block and sky lights
        cir.setReturnValue(ETF.config().getConfig().getLightOverride(cir.getReturnValue()));
    }
}