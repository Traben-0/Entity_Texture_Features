package traben.entity_texture_features.mixin.mods.skin_layers;

import dev.tr7zw.skinlayers.render.CustomizableModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.mixin.MixinModelPart;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

/**
 * this is a copy of {@link MixinModelPart}
 */
@Pseudo
@Mixin(value = CustomizableModelPart.class)// implements Mesh
public abstract class MixinMesh {

    @Shadow public abstract void render(ModelPart vanillaModel, MatrixStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha);

    @Inject(method = "render(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$findOutIfInitialModelPart(ModelPart vanillaModel, MatrixStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        ETFRenderContext.incrementCurrentModelPartDepth();
//        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$doEmissive(ModelPart vanillaModel, MatrixStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        //run code if this is the initial topmost rendered part
        if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
        } else {
            if (ETFRenderContext.isCurrentlyRenderingEntity()
                    && vertexConsumer instanceof ETFVertexConsumer etfVertexConsumer) {
                ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
                if(texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                    VertexConsumerProvider provider = etfVertexConsumer.etf$getProvider();
                    RenderLayer layer = etfVertexConsumer.etf$getRenderLayer();
                    if (provider != null && layer != null) {
                        //attempt special renders as eager OR checks
                        ETFUtils2.RenderMethodForOverlay renderer = (a, b)-> render(vanillaModel,poseStack,a,b, overlay, red, green, blue, alpha);
                        if (ETFUtils2.renderEmissive(texture, provider, renderer) |
                                ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
                            //reset render layer stuff behind the scenes if special renders occurred
                            provider.getBuffer(layer);
                        }
                    }
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
//            ETFRenderContext.preventTexturePatching();
        }
    }

//    @Unique
//    private boolean etf$renderEmissive(ModelPart vanillaModel,ETFTexture texture, VertexConsumerProvider provider, MatrixStack matrices, int overlay) {
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
//                render(vanillaModel, matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay,1f,1f,1f,1f);
//            ETFRenderContext.endSpecialRenderOverlayPhase();
//            return true;
//        }
//        return false;
//    }
//
//    @Unique
//    private boolean etf$renderEnchanted(ModelPart vanillaModel,ETFTexture texture, VertexConsumerProvider provider, MatrixStack matrices, int light, int overlay) {
//        //attempt enchanted render
//        Identifier enchanted = texture.getEnchantIdentifierOfCurrentState();
//        if (enchanted != null) {
//            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
//            ETFRenderContext.preventRenderLayerTextureModify();
//                VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
//            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();
//
//            ETFRenderContext.startSpecialRenderOverlayPhase();
//                render(vanillaModel,matrices, enchantedVertex, light, overlay,1f,1f,1f,1f);
//            ETFRenderContext.endSpecialRenderOverlayPhase();
//            return true;
//        }
//        return false;
//    }


}
