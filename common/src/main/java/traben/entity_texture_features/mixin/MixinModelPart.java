package traben.entity_texture_features.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.ETFRenderContext;

@Mixin(ModelPart.class)
public abstract class MixinModelPart {
    @Shadow public abstract void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$findOutIfInitialModelPart(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
//        if(ETFRenderContext.getCurrentTopPart() == null){
//            ETFRenderContext.setCurrentTopPart(this);
//        }
        ETFRenderContext.incrementCurrentModelPartDepth();
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$doEmissiveIfInitialPart(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        //run code if this is the initial topmost rendered part
        if(ETFRenderContext.getCurrentModelPartDepth() != 1){
            ETFRenderContext.decrementCurrentModelPartDepth();
        }else{
            if(ETFRenderContext.isRenderReady()) {
                //attempt emissive render
                Identifier emissive = ETFRenderContext.getCurrentETFTexture().getEmissiveIdentifierOfCurrentState();
                if (emissive != null) {

                    ETFRenderContext.preventRenderLayerTextureModify();

                    VertexConsumer emissiveConsumer = ETFRenderContext.getCurrentProvider().getBuffer(
                            ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT
                                    && ETFRenderContext.getCurrentEntity().getBlockEntity() == null ?
                                RenderLayer.getBeaconBeam(emissive,true):
                                RenderLayer.getEntityTranslucentCull(emissive));

                    ETFRenderContext.allowRenderLayerTextureModify();

                    render(matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, red, green, blue, alpha);
                }

                //attempt enchanted render
                Identifier enchanted = ETFRenderContext.getCurrentETFTexture().getEnchantIdentifierOfCurrentState();
                if(enchanted != null){
                    ETFRenderContext.preventRenderLayerTextureModify();
                    VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(ETFRenderContext.getCurrentProvider(), RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
                    ETFRenderContext.allowRenderLayerTextureModify();

                    render(matrices, enchantedVertex, light, overlay, red, green, blue, alpha);
                }

                if(enchanted != null || emissive != null){
                    //reset whatever render layer statics this establishes
                    ETFRenderContext.getCurrentProvider().getBuffer(ETFRenderContext.getCurrentRenderLayer());
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }




}
