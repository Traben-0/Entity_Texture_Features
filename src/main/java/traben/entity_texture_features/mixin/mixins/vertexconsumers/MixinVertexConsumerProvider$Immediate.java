package traben.entity_texture_features.mixin.mixins.vertexconsumers;

//#if MC >= 26.2
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
//$$ import net.minecraft.client.renderer.rendertype.RenderType;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$ import traben.entity_texture_features.compat.SodiumGetBufferInjector;
//$$ import traben.entity_texture_features.features.state.ETFState;
//$$ import traben.entity_texture_features.utils.URenderTypeToVertexConsumer;
//$$
//$$ @Mixin(value = RenderTypeFeatureRenderer.class, priority = 800)
//$$ public class MixinVertexConsumerProvider$Immediate {
//$$
//$$
//$$     @ModifyVariable(
//$$             method = "getVertexBuilder",
//$$             at = @At(value = "HEAD"),
//$$             index = 1, argsOnly = true)
//$$     private RenderType etf$modifyRenderLayer(RenderType value) {
//$$         return ETFState.modifyRenderLayerIfRequired(value);
//$$     }
//$$
//$$
//$$     @Inject(
//$$             method = "getVertexBuilder",
//$$             at = @At(value = "RETURN"))
//$$     private void etf$injectIntoGetBufferReturn(RenderType renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
//$$         var returned = cir.getReturnValue();
//$$         var uSource = new URenderTypeToVertexConsumer((RenderTypeFeatureRenderer) (Object) this);
//$$         ETFState.insertETFDataIntoVertexConsumer(uSource, renderLayer, returned);
//$$         //quarantined class to contain all sodium interaction
//$$         //sodium ExtendedBufferBuilder classes contain a delegate that must instead have the above data passed into
//$$         SodiumGetBufferInjector.inject(uSource, renderLayer, returned);
//$$     }
//$$
//$$ }
//#else
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.compat.SodiumGetBufferInjector;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.utils.URenderTypeToVertexConsumer;

@Mixin(value = MultiBufferSource.BufferSource.class, priority = 800)
public class MixinVertexConsumerProvider$Immediate {


    @ModifyVariable(
            method = "getBuffer",
            at = @At(value = "HEAD"),
            index = 1, argsOnly = true)
    private RenderType etf$modifyRenderLayer(RenderType value) {
        return ETFState.modifyRenderLayerIfRequired(value);
    }


    @Inject(
            method = "getBuffer",
            at = @At(value = "RETURN"))
    private void etf$injectIntoGetBufferReturn(RenderType renderLayer, CallbackInfoReturnable<VertexConsumer> cir) {
        var returned = cir.getReturnValue();
        ETFState.insertETFDataIntoVertexConsumer(new URenderTypeToVertexConsumer((MultiBufferSource) this), renderLayer, returned);
        //quarantined class to contain all sodium interaction
        //sodium ExtendedBufferBuilder classes contain a delegate that must instead have the above data passed into
        SodiumGetBufferInjector.inject(new URenderTypeToVertexConsumer((MultiBufferSource) this), renderLayer, returned);
    }

}
//#endif
