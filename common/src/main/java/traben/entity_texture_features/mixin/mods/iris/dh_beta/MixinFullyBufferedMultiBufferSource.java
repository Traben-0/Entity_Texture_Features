package traben.entity_texture_features.mixin.mods.iris.dh_beta;

import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.mixin.MixinVertexConsumerProvider$Immediate;

/**
 * this is a copy of {@link MixinVertexConsumerProvider$Immediate} but for iris's
 * custom entity {@link VertexConsumerProvider}
 * <p>
 * this should have no negative impact on iris's render process, other than of course adding more code that needs to run
 */
@Pseudo
@Mixin(FullyBufferedMultiBufferSource.class)
public class MixinFullyBufferedMultiBufferSource {


    @ModifyVariable(
            method = "getBuffer",
            at = @At(value = "HEAD"),
            index = 1, argsOnly = true)
    private RenderLayer etf$modifyRenderLayer(RenderLayer value) {
        RenderLayer newLayer = ETFRenderContext.modifyRenderLayerIfRequired(value);
        return newLayer == null ? value : newLayer;
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
