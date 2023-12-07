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
                    "getArmorCutoutNoCull"
            },
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinAllEntityLayers(Identifier value) {
        return etf$getETFVariantOf(value);
    }

//    @ModifyVariable(
//            method = "getEntitySolid",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEyes",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer2(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }

//    @ModifyVariable(
//            method = "getEnergySwirl",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer3(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityAlpha",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer4(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getItemEntityTranslucentCull",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer5(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityCutout",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer6(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer7(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityCutoutNoCullZOffset(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer8(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityDecal",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer9(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityNoOutline",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer10(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntitySmoothCutout",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer11(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityTranslucent(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer12(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityTranslucentCull",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer13(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getEntityTranslucentEmissive(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer14(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
//
//    @ModifyVariable(
//            method = "getArmorCutoutNoCull",
//            at = @At(value = "HEAD"),
//            index = 0, argsOnly = true)
//    private static Identifier etf$mixinLayer15(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }


}
