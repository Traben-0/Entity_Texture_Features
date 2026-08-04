package traben.entity_texture_features.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;
import com.mojang.blaze3d.vertex.BufferBuilder;

import java.util.Optional;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import traben.entity_texture_features.utils.URenderTypeToVertexConsumer;

@Mixin(BufferBuilder.class)
public class MixinBufferBuilder implements ETFVertexConsumer {

    @Unique
    private URenderTypeToVertexConsumer etf$provider = null;
    @Unique
    private RenderType etf$renderLayer = null;
    @Unique
    private ETFTexture etf$ETFTexture = null;

    @Override
    public ETFTexture etf$getETFTexture() {
        return etf$ETFTexture;
    }

    @Override
    public URenderTypeToVertexConsumer etf$getProvider() {
        return etf$provider;
    }

    @Override
    public RenderType etf$getRenderLayer() {
        return etf$renderLayer;
    }

    @Override
    public void etf$initETFVertexConsumer(URenderTypeToVertexConsumer provider, RenderType renderLayer) {
        etf$provider = provider;

        etf$renderLayer = renderLayer;

        //todo sprites give atlas texture here
        if (renderLayer instanceof ETFRenderLayerWithTexture etfRenderLayerWithTexture) {
            Optional<ResourceLocation> possibleId = etfRenderLayerWithTexture.etf$getId();
            possibleId.ifPresent(identifier -> etf$ETFTexture = ETFManager.getInstance().getETFTextureNoVariation(identifier));
        }
//        else {
//            etf$ETFTexture = null;
//        }
    }
}
