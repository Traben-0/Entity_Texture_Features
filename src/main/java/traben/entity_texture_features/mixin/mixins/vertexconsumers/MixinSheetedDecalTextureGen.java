package traben.entity_texture_features.mixin.mixins.vertexconsumers;

import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;
import traben.entity_texture_features.utils.URenderTypeToVertexConsumer;

@Mixin(SheetedDecalTextureGenerator.class)
public class MixinSheetedDecalTextureGen implements ETFVertexConsumer {

    @Shadow
    @Final
    private VertexConsumer delegate;

    @Unique
    private @Nullable ETFVertexConsumer etfDelegate() {
        if (delegate instanceof ETFVertexConsumer etfDelegate) {
            return etfDelegate;
        } else {
            return null;
        }
    }

    @Override
    public ETFTexture etf$getETFTexture() {
        var etf = etfDelegate();
        if (etf != null) {
            return etf.etf$getETFTexture();
        }
        return null;
    }

    @Override
    public URenderTypeToVertexConsumer etf$getProvider() {
        var etf = etfDelegate();
        if (etf != null) {
            return etf.etf$getProvider();
        }
        return null;
    }

    @Override
    public RenderType etf$getRenderLayer() {
        var etf = etfDelegate();
        if (etf != null) {
            return etf.etf$getRenderLayer();
        }
        return null;
    }

    @Override
    public void etf$initETFVertexConsumer(URenderTypeToVertexConsumer provider, RenderType renderLayer) {
        var etf = etfDelegate();
        if (etf != null) {
            etf.etf$initETFVertexConsumer(provider, renderLayer);
        }
    }
}
