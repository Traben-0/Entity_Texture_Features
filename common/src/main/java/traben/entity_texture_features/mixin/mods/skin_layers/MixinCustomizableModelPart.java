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
import traben.entity_texture_features.ETF;
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
public abstract class MixinCustomizableModelPart {

    @Shadow public abstract void render(ModelPart vanillaModel, MatrixStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha);

    @Inject(method = "render(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$findOutIfInitialModelPart(ModelPart vanillaModel, MatrixStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if(ETF.config().getConfig().use3DSkinLayerPatch) {
            ETFRenderContext.incrementCurrentModelPartDepth();
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$doEmissive(ModelPart vanillaModel, MatrixStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if(ETF.config().getConfig().use3DSkinLayerPatch) {
            //run code if this is the initial topmost rendered part
            if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
                ETFRenderContext.decrementCurrentModelPartDepth();
            } else {
                if (ETFRenderContext.isCurrentlyRenderingEntity()
                        && vertexConsumer instanceof ETFVertexConsumer etfVertexConsumer) {
                    ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
                    if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                        VertexConsumerProvider provider = etfVertexConsumer.etf$getProvider();
                        RenderLayer layer = etfVertexConsumer.etf$getRenderLayer();
                        if (provider != null && layer != null) {
                            //attempt special renders as eager OR checks
                            ETFUtils2.RenderMethodForOverlay renderer = (a, b) -> render(vanillaModel, poseStack, a, b, overlay, red, green, blue, alpha);
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
            }
        }
    }




}
