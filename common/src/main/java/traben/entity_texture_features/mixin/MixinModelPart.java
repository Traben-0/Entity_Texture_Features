package traben.entity_texture_features.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.mixin.mods.sodium.MixinModelPartSodium;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

/**
 * this method figures out if a {@link ModelPart} is the top level of the children tree being rendered,
 * then applies overlay rendering like emissives and enchanted pixels.
 * <p>
 * this is copied in {@link MixinModelPartSodium} for sodium's alternative model part render method.
 * <p>
 * the priority is set so this method will never run before sodium cancels the vanilla rendering code.
 */
@Mixin(value = ModelPart.class, priority = 2000)
public abstract class MixinModelPart {
    @Shadow
    public abstract void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$findOutIfInitialModelPart(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        ETFRenderContext.incrementCurrentModelPartDepth();
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$doEmissiveIfInitialPart(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        //run code if this is the initial topmost rendered part
        if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
        } else {
            //top level model so try special rendering
            if (ETFRenderContext.isCurrentlyRenderingEntity()
                    && vertices instanceof ETFVertexConsumer etfVertexConsumer) {
                ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
                //is etf texture not null and does it special render?
                if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                    VertexConsumerProvider provider = etfVertexConsumer.etf$getProvider();
                    //very important this is captured before doing the special renders as they can potentially modify
                    //the same ETFVertexConsumer down stream
                    RenderLayer layer = etfVertexConsumer.etf$getRenderLayer();
                    //are these render required objects valid?
                    if (provider != null && layer != null) {
                        //attempt special renders as eager OR checks
                        ETFUtils2.RenderMethodForOverlay renderer = (a, b) -> render(matrices, a, b, overlay, red, green, blue, alpha);
                        if (ETFUtils2.renderEmissive(texture, provider, renderer) |
                                ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
                            //reset render layer stuff behind the scenes if special renders occurred
                            //this will also return ETFVertexConsumer held data to normal if the same ETFVertexConsumer
                            //was previously affected by a special render
                            provider.getBuffer(layer);
                        }
                    }
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }

//    @Unique
//    private boolean etf$renderEmissive(ETFTexture texture, VertexConsumerProvider provider, MatrixStack matrices, int overlay, float red, float green, float blue, float alpha) {
//        Identifier emissive = texture.getEmissiveIdentifierOfCurrentState();
//        if (emissive != null) {
//            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
//            ETFRenderContext.preventRenderLayerTextureModify();
//
//            boolean textureIsAllowedBrightRender = ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT
//                    && ETFRenderContext.getCurrentEntity().etf$canBeBright();// && !ETFRenderContext.getCurrentETFTexture().isPatched_CurrentlyOnlyArmor();
//
//            VertexConsumer emissiveConsumer = provider.getBuffer(
//                    textureIsAllowedBrightRender ?
//                            RenderLayer.getBeaconBeam(emissive, true) :
//                            ETFRenderContext.getCurrentEntity().etf$isBlockEntity() ?
//                                    RenderLayer.getEntityTranslucentCull(emissive) :
//                                    RenderLayer.getEntityTranslucent(emissive));
//
//            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();
//
//            ETFRenderContext.startSpecialRenderOverlayPhase();
//                render(matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, red, green, blue, alpha);
//            ETFRenderContext.endSpecialRenderOverlayPhase();
//            return true;
//        }
//        return false;
//    }
//
//    @Unique
//    private boolean etf$renderEnchanted(ETFTexture texture, VertexConsumerProvider provider, MatrixStack matrices, int light, int overlay, float red, float green, float blue, float alpha) {
//        //attempt enchanted render
//        Identifier enchanted = texture.getEnchantIdentifierOfCurrentState();
//        if (enchanted != null) {
//            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
//            ETFRenderContext.preventRenderLayerTextureModify();
//                VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
//            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();
//
//            ETFRenderContext.startSpecialRenderOverlayPhase();
//                render(matrices, enchantedVertex, light, overlay, red, green, blue, alpha);
//            ETFRenderContext.endSpecialRenderOverlayPhase();
//            return true;
//        }
//        return false;
//    }


}
