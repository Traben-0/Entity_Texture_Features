package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;


@Mixin(RenderLayer.class)
public abstract class MixinRenderLayer {


    @Unique
    private static Identifier etf$getETFVariantOf(Identifier identifier) {

        if (ETFRenderContext.getCurrentEntity() == null
                || !ETFRenderContext.isAllowedToRenderLayerTextureModify())
            return identifier;


        ETFManager.TextureSource source;
        if (ETFRenderContext.isRenderingFeatures()) {
            source = ETFManager.TextureSource.ENTITY_FEATURE;//this is still needed to speed up some feature renderers
        } else if (ETFRenderContext.getCurrentEntity().etf$isBlockEntity()) {
            source = ETFManager.TextureSource.BLOCK_ENTITY;//todo still needed in rewrite?
        } else {
            source = ETFManager.TextureSource.ENTITY;
        }
        Identifier modified = ETFManager.getInstance().getETFTextureVariant(identifier, ETFRenderContext.getCurrentEntity(), source)
                .getTextureIdentifier(ETFRenderContext.getCurrentEntity());
        //noinspection ConstantValue
        return modified == null ? identifier : modified;
    }

    @ModifyVariable(
            method = "getEntitySolid",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEyes",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer2(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEnergySwirl",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer3(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityAlpha",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer4(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getItemEntityTranslucentCull",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer5(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityCutout",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer6(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer7(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityCutoutNoCullZOffset(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer8(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityDecal",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer9(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityNoOutline",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer10(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntitySmoothCutout",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer11(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityTranslucent(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer12(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityTranslucentCull",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer13(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getEntityTranslucentEmissive(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer14(Identifier value) {
        return etf$getETFVariantOf(value);
    }

    @ModifyVariable(
            method = "getArmorCutoutNoCull",
            at = @At(value = "HEAD"),
            index = 0, argsOnly = true)
    private static Identifier etf$mixinLayer15(Identifier value) {
        return etf$getETFVariantOf(value);
    }

//    @ModifyVariable(
//            method = "get",
//            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"),
//            index = 0,
//            argsOnly = true)
//    private static Identifier etf$mixinLayer15(Identifier value) {
//        return etf$getETFVariantOf(value);
//    }
}
