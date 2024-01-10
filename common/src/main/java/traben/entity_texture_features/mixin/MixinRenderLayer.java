package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;


@Mixin(RenderLayer.class)
public abstract class MixinRenderLayer {


    @Unique
    private static Identifier etf$getETFVariantOf(Identifier identifier) {
        //do not modify texture
        if (ETFRenderContext.getCurrentEntity() == null
                || !ETFRenderContext.isAllowedToRenderLayerTextureModify())
            return identifier;

        //get etf modified texture
        ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(identifier, ETFRenderContext.getCurrentEntity());
        if(ETFRenderContext.isAllowedToPatch()){
            etfTexture.assertPatchedTextures();
        }
        Identifier modified = etfTexture.getTextureIdentifier(ETFRenderContext.getCurrentEntity());

        //check not null just to be safe, it shouldn't be however
        //noinspection ConstantValue
        return modified == null ? identifier : modified;
    }

    @ModifyVariable(
            method ={
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
                    "getEntityTranslucentEmissive(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
                    "getArmorCutoutNoCull",
                    "getEntityShadow"
            },
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinAllEntityLayers(Identifier value) {
        return etf$getETFVariantOf(value);
    }





}
