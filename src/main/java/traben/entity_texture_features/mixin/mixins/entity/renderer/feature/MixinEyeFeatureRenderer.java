package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETF.EYES_FEATURE_LIGHT_VALUE;

@Mixin(EyesLayer.class)
public abstract class MixinEyeFeatureRenderer {

    //#if MC>=12109
    @ModifyExpressionValue(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EyesLayer;renderType()Lnet/minecraft/client/renderer/RenderType;"))
    //#else
    //$$ @ModifyArg(
    //$$         method = "render",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
    //$$         index = 0
    //$$ )
    //#endif
    private RenderType etf$allowModifiableEyes(RenderType layer) {
        //the eye texture render layers are hard coded in vanilla and do not recalculate each time
        if (layer instanceof ETFRenderLayerWithTexture etf && etf.etf$getId().isPresent()) {
            ResourceLocation id = etf.etf$getId().get();
            ResourceLocation variant = ETFUtils2.getETFVariantNotNullForInjector(id);
            if (!id.equals(variant)) {
                //if there is a variant then lets send a layer with it
                ETFState.pushRenderLayerModifyState(false);

                RenderType layer2 =
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .eyes(variant);

                ETFState.popRenderLayerModifyState();

                return layer2;
            }
        }
        //no need to variate so lets just send the hard coded final layer
        return layer;
    }

    @SuppressWarnings("SameReturnValue")
    @ModifyVariable(method =
            //#if MC >= 12109
            "submit"
            //#else
            //$$ "render"
            //#endif
            , at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private int emf$markEyeLight(int i) {
        return EYES_FEATURE_LIGHT_VALUE;
    }

}


