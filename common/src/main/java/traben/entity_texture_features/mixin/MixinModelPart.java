package traben.entity_texture_features.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
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
        ETFRenderContext.decrementCurrentModelPartDepth();
        if(ETFRenderContext.getCurrentModelPartDepth() == 0){// ETFRenderContext.getCurrentTopPart() == this){
            //ETFRenderContext.setCurrentTopPart(null);
            if(ETFRenderContext.isRenderReady()) {
                Identifier emissive = ETFRenderContext.getCurrentETFTexture().getEmissiveIdentifierOfCurrentState();
                if (emissive != null) {
                   //VertexConsumer emissiveConsumer = ETFRenderContext.getCurrentProvider().getBuffer(etf$ENTITY_TRANSLUCENT_CULL_Z_OFFSET.apply(emissive));//RenderLayer.getEntityTranslucentCull(emissive));
                    VertexConsumer emissiveConsumer = ETFRenderContext.getCurrentProvider()
                            .getBuffer(RenderLayer.getEntityTranslucentCull(emissive));//todo options


                    //ensure this doesnt trigger again
                    ETFRenderContext.incrementCurrentModelPartDepth();
                    render(matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, red, green, blue, alpha);

                    //reset whatever render layer statics this establishes
                    ETFRenderContext.getCurrentProvider().getBuffer(ETFRenderContext.getCurrentRenderLayer());

                    //that should be it ???
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }




}
