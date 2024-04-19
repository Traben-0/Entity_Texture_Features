package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(EyesFeatureRenderer.class)
public abstract class MixinEyeFeatureRenderer {

    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"),
            index = 0
    )
    private RenderLayer etf$allowModifiableEyes(RenderLayer layer) {
        //the eye texture render layers are hard coded in vanilla and do not recalculate each time
        if (layer instanceof ETFRenderLayerWithTexture etf && etf.etf$getId().isPresent()) {
            Identifier id = etf.etf$getId().get();
            Identifier variant = ETFUtils2.getETFVariantNotNullForInjector(id);
            if (!id.equals(variant)) {
                //if there is a variant then lets send a layer with it
                boolean allowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
                ETFRenderContext.preventRenderLayerTextureModify();

                RenderLayer layer2 = RenderLayer.getEyes(variant);

                if (allowed) ETFRenderContext.allowRenderLayerTextureModify();

                return layer2;
            }
        }
        //no need to variate so lets just send the hard coded final layer
        return layer;
    }

}


