package traben.entity_texture_features.mixin.mods.imediatelyfast;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.raphimc.immediatelyfast.feature.core.BatchableImmediate;
import net.raphimc.immediatelyfast.feature.core.ImmediateAdapter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.compat.SodiumGetBufferInjector;
import traben.entity_texture_features.features.ETFRenderContext;

@Pseudo
@Mixin(value = ImmediateAdapter.class, priority = 800)
public class MixinBatchableImmediate {


    @Unique
    private Boolean etf$notBatchableClass = null;

    @ModifyVariable(
            method = "getBuffer",
            at = @At(value = "HEAD"),
            index = 1, argsOnly = true)
    private RenderLayer etf$modifyRenderLayer(RenderLayer value) {
        if (etf$notBatchableClass()) return value;
        return ETFRenderContext.modifyRenderLayerIfRequired(value);
    }


    @Unique
    private boolean etf$notBatchableClass() {
        if (etf$notBatchableClass == null) {
            Class<?> cl = getClass();
            etf$notBatchableClass = !cl.equals(BatchableImmediate.class);
        }
        return etf$notBatchableClass;
    }

    @Inject(
            method = "getBuffer",
            at = @At(value = "RETURN"))
    private void etf$injectIntoGetBufferReturn(RenderLayer renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
        if (etf$notBatchableClass()) return;

        var returned = cir.getReturnValue();
        ETFRenderContext.insertETFDataIntoVertexConsumer((VertexConsumerProvider) this, renderLayer, returned);

        //quarantined class to contain all sodium interaction
        //sodium ExtendedBufferBuilder classes contain a delegate that must instead have the above data passed into
        SodiumGetBufferInjector.inject((VertexConsumerProvider) this, renderLayer, returned);
    }

}
