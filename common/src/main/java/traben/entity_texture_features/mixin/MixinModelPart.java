package traben.entity_texture_features.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;

/**
 * this method figures out if a {@link ModelPart} is the top level of the children tree being rendered,
 * then applies overlay rendering like emissives and enchanted pixels.
 * <p>
 * this is copied in {@link MixinModelPartSodium} for sodium's alternative model part render method.
 * <p>
 * the priority is required so this method will never mixin before sodium.
 */
@Mixin(value = ModelPart.class, priority = 99999999)
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
            if (ETFRenderContext.isRenderReady()) {
                //attempt special renders as eager OR checks
                if (etf$renderEmissive(matrices, overlay, red, green, blue, alpha) |
                        etf$renderEnchanted(matrices, light, overlay, red, green, blue, alpha)) {
                        //reset render layer stuff behind the scenes if special renders occurred
                    ETFRenderContext.getCurrentProvider().getBuffer(ETFRenderContext.getCurrentRenderLayer());
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }

    @Unique
    private boolean etf$renderEmissive(MatrixStack matrices, int overlay, float red, float green, float blue, float alpha) {
        Identifier emissive = ETFRenderContext.getCurrentETFTexture().getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();

            boolean textureIsAllowedBrightRender = ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT
                    && ETFRenderContext.getCurrentEntity().etf$canBeBright();// && !ETFRenderContext.getCurrentETFTexture().isPatched_CurrentlyOnlyArmor();

            VertexConsumer emissiveConsumer = ETFRenderContext.getCurrentProvider().getBuffer(
                    textureIsAllowedBrightRender ?
                            RenderLayer.getBeaconBeam(emissive, true) :
                            ETFRenderContext.getCurrentEntity().etf$isBlockEntity() ?
                                    RenderLayer.getEntityTranslucentCull(emissive) :
                                    RenderLayer.getEntityTranslucent(emissive));

            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            ETFRenderContext.startSpecialRenderOverlayPhase();
                render(matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, red, green, blue, alpha);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    @Unique
    private boolean etf$renderEnchanted(MatrixStack matrices, int light, int overlay, float red, float green, float blue, float alpha) {
        //attempt enchanted render
        Identifier enchanted = ETFRenderContext.getCurrentETFTexture().getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
                VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(ETFRenderContext.getCurrentProvider(), RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            ETFRenderContext.startSpecialRenderOverlayPhase();
                render(matrices, enchantedVertex, light, overlay, red, green, blue, alpha);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }


}
