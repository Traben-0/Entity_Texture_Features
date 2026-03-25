package traben.entity_texture_features.mixin.mixins.mods.iris;
//#if !IRIS || MC == 12002 || MC < 12000 || MC >= 26.1
//$$ import net.minecraft.client.Minecraft;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Pseudo
//$$ @Mixin(CancelTarget.class)
//$$ public class MixinInnerWrappedRenderType {}
//#else
import net.irisshaders.iris.layer.InnerWrappedRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;

import java.util.Optional;


/**
 * Required in case Iris wraps an instance of {@link ETFRenderLayerWithTexture}
 * <p>
 * This is assumed to be required, whereas I know {@link MixinOuterWrappedRenderType} is required.
 */
@Pseudo
@Mixin(value = InnerWrappedRenderType.class)
public abstract class MixinInnerWrappedRenderType implements ETFRenderLayerWithTexture {


    @Shadow
            //#if !FABRIC && MC < 12100
            //$$ (remap = false)
            //#endif
    public abstract RenderType unwrap();

    @Override
    public Optional<ResourceLocation> etf$getId() {
        if (unwrap() instanceof ETFRenderLayerWithTexture etf)
            return etf.etf$getId();
        return Optional.empty();
    }
}
//#endif
