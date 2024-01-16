package traben.entity_texture_features.utils;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

public interface ETFVertexConsumer {

    @Nullable
    ETFTexture etf$getETFTexture();
    @Nullable
    VertexConsumerProvider etf$getProvider();
    @Nullable
    RenderLayer etf$getRenderLayer();

    void etf$initETFVertexConsumer(VertexConsumerProvider provider, RenderLayer renderLayer);
}
