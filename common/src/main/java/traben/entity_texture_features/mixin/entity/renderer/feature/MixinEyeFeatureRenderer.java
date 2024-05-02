package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(EyesLayer.class)
public abstract class MixinEyeFeatureRenderer {

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            index = 0
    )
    private RenderType etf$allowModifiableEyes(RenderType layer) {
        //the eye texture render layers are hard coded in vanilla and do not recalculate each time
        if (layer instanceof ETFRenderLayerWithTexture etf && etf.etf$getId().isPresent()) {
            ResourceLocation id = etf.etf$getId().get();
            ResourceLocation variant = ETFUtils2.getETFVariantNotNullForInjector(id);
            if (!id.equals(variant)) {
                //if there is a variant then lets send a layer with it
                boolean allowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
                ETFRenderContext.preventRenderLayerTextureModify();

                RenderType layer2 = RenderType.eyes(variant);

                if (allowed) ETFRenderContext.allowRenderLayerTextureModify();

                return layer2;
            }
        }
        //no need to variate so lets just send the hard coded final layer
        return layer;
    }

}


