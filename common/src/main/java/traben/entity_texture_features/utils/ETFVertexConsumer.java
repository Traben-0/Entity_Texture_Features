package traben.entity_texture_features.utils;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
    MultiBufferSource etf$getProvider();

    @Nullable
    RenderType etf$getRenderLayer();

    void etf$initETFVertexConsumer(MultiBufferSource provider, RenderType renderLayer);
}
