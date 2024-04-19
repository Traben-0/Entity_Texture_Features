package traben.entity_texture_features.utils;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

/**
 * The interface Etf vertex consumer.
 * <p>
 * this turns any implemented vertex consumers into data holders to pass in etf context to assist entity feature rendering.
 * this has additional functionality for emf where the context may change through the iteration of each model part where
 * custom texture overrides exist.
 */
public interface ETFVertexConsumer {

    @Nullable
    ETFTexture etf$getETFTexture();

    @Nullable
    VertexConsumerProvider etf$getProvider();

    @Nullable
    RenderLayer etf$getRenderLayer();

    void etf$initETFVertexConsumer(VertexConsumerProvider provider, RenderLayer renderLayer);
}
