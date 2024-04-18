package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_texture_features.utils.ETFUtils2;


@Mixin(RenderLayer.class)
public abstract class MixinRenderLayer {


    @ModifyVariable(
            method = {
                    "getEntitySolid",
                    "getEyes",
                    "getEnergySwirl",
                    "getEntityAlpha",
                    "getItemEntityTranslucentCull",
                    "getEntityCutout",
                    "getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
                    "getEntityCutoutNoCullZOffset(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
                    "getEntityDecal",
                    "getEntityNoOutline",
                    "getEntitySmoothCutout",
                    "getEntityTranslucent(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
                    "getEntityTranslucentCull",
                    "getArmorCutoutNoCull",
                    "getEntityShadow"
            },
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinAllEntityLayers(Identifier value) {
        return ETFUtils2.getETFVariantNotNullForInjector(value);
    }

}
