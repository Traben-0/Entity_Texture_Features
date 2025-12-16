package traben.entity_texture_features.mixin.mixins;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

import java.util.Optional;

//#if MC >= 12111
//$$ import net.minecraft.client.renderer.rendertype.RenderSetup;
//$$ @Pseudo
//$$ @Mixin(value = RenderType.class)
//$$ public abstract class MixinMultiPhase implements ETFRenderLayerWithTexture {
//$$
//$$     @Shadow @Final private RenderSetup state;
//$$
//$$     @Override
//$$     public Optional<Identifier> etf$getId() {
//$$         var optional = state.textures.values().stream().findFirst();
//$$         return optional.map(it -> it.location());
//$$     }
//$$ }
//$$
//#else
@Pseudo
@Mixin(value = RenderType.CompositeRenderType.class)
public abstract class MixinMultiPhase implements ETFRenderLayerWithTexture {


    @Shadow
    @Final
    public RenderType.CompositeState state;

    @Override
    public Optional<ResourceLocation> etf$getId() {
        return state.textureState.cutoutTexture();
    }
}
//#endif
