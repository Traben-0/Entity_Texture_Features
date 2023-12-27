package traben.entity_texture_features.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

import java.util.Optional;

@Mixin(BufferBuilder.class)
public class MixinBufferBuilder implements ETFVertexConsumer {

    @Unique
    VertexConsumerProvider etf$provider = null;
    @Unique
    RenderLayer etf$renderLayer = null;
    @Unique
    ETFTexture etf$ETFTexture = null;
    @Override
    public ETFTexture etf$getETFTexture() {
        return etf$ETFTexture;
    }

    @Override
    public VertexConsumerProvider etf$getProvider() {
        return etf$provider;
    }

    @Override
    public RenderLayer etf$getRenderLayer() {
        return etf$renderLayer;
    }

    @Override
    public void etf$initETFVertexConsumer(VertexConsumerProvider provider, RenderLayer renderLayer) {
        etf$provider = provider;

        etf$renderLayer = renderLayer;

        //todo sprites give atlas texture here
        if (renderLayer instanceof RenderLayer.MultiPhase multiPhase) {
            Optional<Identifier> possibleId = multiPhase.phases.texture.getId();
            possibleId.ifPresent(identifier -> etf$ETFTexture = ETFManager.getInstance().getETFTextureNoVariation(identifier));
        }
    }
}
