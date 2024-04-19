package traben.entity_texture_features.mixin.mods.iris.dh_beta;

import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

import java.util.Optional;


/**
 * Required in case Iris wraps an instance of {@link ETFRenderLayerWithTexture}
 */
@Pseudo
@Mixin(value = OuterWrappedRenderType.class)
public abstract class MixinOuterWrappedRenderType implements ETFRenderLayerWithTexture {


    @Shadow
    public abstract RenderLayer unwrap();

    @Override
    public Optional<Identifier> etf$getId() {
        if (unwrap() instanceof ETFRenderLayerWithTexture etf)
            return etf.etf$getId();
        return Optional.empty();
    }
}
