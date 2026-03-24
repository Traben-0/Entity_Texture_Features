package traben.entity_texture_features.mixin.mixins.mods.sodium;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12105 || !SODIUM
// todo seems to be no longer needed as sodium mixins to Cube now
import traben.entity_texture_features.mixin.CancelTarget;

@Mixin(value = CancelTarget.class)
public abstract class MixinModelPartSodium { }
//#else
//$$ import com.mojang.blaze3d.vertex.BufferBuilder;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
//$$ import net.minecraft.client.model.geom.ModelPart;
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.ModifyVariable;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_texture_features.features.ETFRenderContext;
//$$ import traben.entity_texture_features.features.texture_handlers.ETFTexture;
//$$ import traben.entity_texture_features.mixin.mixins.MixinModelPart;
//$$ import traben.entity_texture_features.utils.ETFUtils2;
//$$ import traben.entity_texture_features.utils.ETFVertexConsumer;
//$$
//$$ /**
//$$  * this is a copy of {@link MixinModelPart} but for sodium's alternative model part render method
//$$  * <p>
//$$  * this should have no negative impact on sodium's render process, other than of course adding more code that needs to run
//$$  */
//$$ @Pseudo
//$$ @Mixin(targets = "me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer")
//$$
//$$ public abstract class MixinModelPartSodium {
//$$
//$$     @SuppressWarnings("EmptyMethod")
//$$     @Shadow
            //#if !FABRIC && MC < 12100
            //$$ (remap = false)
            //#endif
//$$     public static void render(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color) {
//$$     }
//$$
//$$     @Inject(method = "render",
//$$             at = @At(value = "HEAD"))
//$$     private static void etf$findOutIfInitialModelPart(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci) {
//$$         ETFRenderContext.incrementCurrentModelPartDepth();
//$$     }
//$$
//$$     @Unique
//$$     private static boolean once = true;
//$$
//$$     @Unique
//$$     private static VertexBufferWriter etf$convertOrLog(VertexConsumer consumer) {
//$$         if (consumer instanceof VertexBufferWriter writer) {
//$$             if (writer.canUseIntrinsics()) {
//$$                 return writer;
//$$             }
//$$         }
//$$         if (once) {
//$$             once = false;
//$$             ETFUtils2.logWarn("Bad consumer for sodium MixinModelPartSodium");
//$$         }
//$$         return null;
//$$      }
//$$
//$$     @Inject(method = "render",
//$$             at = @At(value = "RETURN"))
//$$     private static void etf$doEmissiveIfInitialPart(PoseStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci) {
//$$         //run code if this is the initial topmost rendered part
//$$         if (ETFRenderContext.getCurrentModelPartDepth() > 1) {
//$$             ETFRenderContext.decrementCurrentModelPartDepth();
//$$         } else {
//$$             if (ETFRenderContext.isCurrentlyRenderingEntity()
//$$                     && writer instanceof ETFVertexConsumer etfVertexConsumer) {
//$$                 ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
//$$                 if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
//$$                     MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
//$$                     RenderType layer = etfVertexConsumer.etf$getRenderLayer();
//$$                     if (provider != null && layer != null) {
//$$                         //attempt special renders as eager OR checks
//$$                         ETFUtils2.RenderMethodForOverlay renderer = (a, b) -> {
//$$                             VertexBufferWriter a2 = etf$convertOrLog(a);
//$$                             if (a2 == null) {
//$$                                 return;
//$$                             }
//$$                             render(matrixStack, a2, part, b, overlay, color);
//$$                         };
//$$                         if (ETFUtils2.renderEmissive(texture, provider, renderer) |
//$$                                 ETFUtils2.renderEnchanted(texture, provider, light, renderer)) {
//$$                             //reset render layer stuff behind the scenes if special renders occurred
//$$                             //todo check the 1.21 stuff is needed for sodium
                            //#if MC < 12100
                            //$$ provider.getBuffer(layer);
                            //#endif
//$$                         }
//$$                     }
//$$                 }
//$$             }
//$$             //ensure model count is reset
//$$             ETFRenderContext.resetCurrentModelPartDepth();
//$$         }
//$$     }
//$$ //todo check the 1.21 stuff is needed for sodium
    //#if MC >= 12100
    //$$
    //$$
    //$$     @ModifyVariable(method = "render",
    //$$             at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$     private static VertexBufferWriter etf$modify(final VertexBufferWriter value) {
    //$$         if (value instanceof BufferBuilder builder && !builder.building){
    //$$             if (value instanceof ETFVertexConsumer etf
    //$$                     && etf.etf$getRenderLayer() != null
    //$$                     && etf.etf$getProvider() != null){
    //$$                 var a = etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer());
    //$$                 VertexBufferWriter a2 = etf$convertOrLog(a);
    //$$                 if (a2 != null) {
    //$$                     return a2;
    //$$                 }
    //$$             }
    //$$         }
    //$$         return value;
    //$$     }
    //$$
    //#endif
//$$ }
//#endif
