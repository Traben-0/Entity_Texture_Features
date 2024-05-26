package traben.entity_texture_features.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

/**
 * This allows a Double VertexConsumer to be used as an ETFVertexConsumer
 * mostly used for enchanted rendered things such as elytra to allow emissives to work correctly
 */
@Mixin(VertexMultiConsumer.Double.class)
public class MixinVertexMultiConsumer$Double implements ETFVertexConsumer {


    @Shadow @Final private VertexConsumer first;

    @Shadow @Final private VertexConsumer second;

    @Override
    public ETFTexture etf$getETFTexture() {
        if (second instanceof ETFVertexConsumer etfSecond) {
            return etfSecond.etf$getETFTexture();
        }
        if (first instanceof ETFVertexConsumer etfFirst) {
            return etfFirst.etf$getETFTexture();
        }
        return null;
    }

    @Override
    public MultiBufferSource etf$getProvider() {
        if (second instanceof ETFVertexConsumer etfSecond) {
            return etfSecond.etf$getProvider();
        }
        if (first instanceof ETFVertexConsumer etfFirst) {
            return etfFirst.etf$getProvider();
        }
        return null;
    }

    @Override
    public RenderType etf$getRenderLayer() {
        if (second instanceof ETFVertexConsumer etfSecond) {
            return etfSecond.etf$getRenderLayer();
        }
        if (first instanceof ETFVertexConsumer etfFirst) {
            return etfFirst.etf$getRenderLayer();
        }
        return null;
    }

    @Override
    public void etf$initETFVertexConsumer(MultiBufferSource provider, RenderType renderLayer) {

    }
}
