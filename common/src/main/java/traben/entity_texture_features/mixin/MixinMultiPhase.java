package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

import java.util.Optional;


@Pseudo
@Mixin(value = RenderLayer.MultiPhase.class)
public abstract class MixinMultiPhase implements ETFRenderLayerWithTexture {


    @Shadow @Final public RenderLayer.MultiPhaseParameters phases;

    @Override
    public Optional<Identifier> etf$getId() {
        return phases.texture.getId();
    }
}
