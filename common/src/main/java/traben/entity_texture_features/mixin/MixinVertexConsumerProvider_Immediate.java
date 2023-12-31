package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFRenderContext;

@Mixin(VertexConsumerProvider.Immediate.class)
public class MixinVertexConsumerProvider_Immediate {


    @ModifyVariable(
            method = "getBuffer",
            at = @At(value = "HEAD"),
            index = 1, argsOnly = true)
    private RenderLayer etf$modifyRenderLayer(RenderLayer value) {
        return ETFRenderContext.modifyRenderLayerIfRequired(value);
    }


    @Inject(
            method = "getBuffer",
            at = @At(value = "RETURN"))
    private void etf$injectIntoGetBufferReturn(RenderLayer renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
        ETFRenderContext.insertETFDataIntoVertexConsumer(
                (VertexConsumerProvider) this,
                renderLayer,
                cir.getReturnValue());
    }

}
