package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.utils.ETFVertexConsumer;

@Mixin(VertexConsumerProvider.Immediate.class)
public class MixinVertexConsumerProvider_Immediate {

//todo modifiable render layer
//    @ModifyVariable(
//            method = "getBuffer",
//            at = @At(value = "HEAD"),
//            index = 1, argsOnly = true)
//    private RenderLayer etf$injectIntoGetBuffer2(RenderLayer value) {
//        VertexConsumerProvider.Immediate self = (VertexConsumerProvider.Immediate)(Object)this;
//        ETFRenderContext.setCurrentProvider(self);
//        return ETFRenderContext.processVertexConsumer(self, value);
//    }

//    @Inject(
//            method = "getBuffer",
//            at = @At(value = "HEAD"))
//    private void etf$injectIntoGetBuffer(RenderLayer renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
//        VertexConsumerProvider.Immediate self = (VertexConsumerProvider.Immediate)(Object)this;
//        ETFRenderContext.setCurrentProvider(self);
//        ETFRenderContext.processVertexConsumer(self, renderLayer);
//    }

    @Inject(
            method = "getBuffer",
            at = @At(value = "RETURN"))
    private void etf$injectIntoGetBufferReturn(RenderLayer renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumerProvider.Immediate self = (VertexConsumerProvider.Immediate)(Object)this;
//        ETFRenderContext.setCurrentProvider(self);
//        ETFRenderContext.processVertexConsumer(self, renderLayer);
        //need to store etf texture of consumer and original render layer
        //store provider as well for future actions
        ((ETFVertexConsumer)cir.getReturnValue()).etf$initETFVertexConsumer(self,renderLayer);
    }

}
