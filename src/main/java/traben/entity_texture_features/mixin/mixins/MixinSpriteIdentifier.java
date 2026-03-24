package traben.entity_texture_features.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.function.Function;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(Material.class)
public class MixinSpriteIdentifier {

    //#if MC >= 26.1
    //$$ @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = "sprite", at = @At(value = "RETURN"))
    //$$ private Identifier etf$modifyIfRequired(Identifier original) {
    //$$
    //$$     //infer actual texture
    //$$     Identifier actualTexture;
    //$$     if (original.toString().endsWith(".png")) {
    //$$         actualTexture = original;
    //$$     } else {
    //$$         //todo check all block entities follow this logic? i know chests, shulker boxes, and beds do
    //$$         actualTexture = ETFUtils2.res(original.getNamespace(), "textures/" + original.getPath() + ".png");
    //$$     }
    //$$
    //$$     ETFTexture texture = ETFManager.getInstance().getETFTextureVariant(actualTexture, ETFRenderContext.getCurrentEntityState());
    //$$
    //$$     //if texture is emissive or a variant then replace with a non sprite vertex consumer like regular entities
    //$$     if (!actualTexture.equals(texture.thisIdentifier) || texture.isEmissive() || texture.isEnchanted()) {
    //$$         return texture.thisIdentifier;
    //$$     }
    //$$     return original;
    //$$ }
    //#else
    @Inject(method =
            //#if MC >= 12109
            "buffer(Lnet/minecraft/client/resources/model/MaterialSet;Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            //#else
            //$$ "buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            //#endif
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$modifyIfRequired(CallbackInfoReturnable<VertexConsumer> cir,
                                      @Local(argsOnly = true) MultiBufferSource vertexConsumers,
                                      @Local(argsOnly = true) Function<ResourceLocation, RenderType> layerFactory) {

        if (cir.getReturnValue() instanceof SpriteCoordinateExpander spriteTexturedVertexConsumer) {
            ResourceLocation rawId = spriteTexturedVertexConsumer.sprite.contents().name();

            //infer actual texture
            ResourceLocation actualTexture;
            if (rawId.toString().endsWith(".png")) {
                actualTexture = rawId;
            } else {
                //todo check all block entities follow this logic? i know chests, shulker boxes, and beds do
                actualTexture = ETFUtils2.res(rawId.getNamespace(), "textures/" + rawId.getPath() + ".png");
            }


            ETFTexture texture = ETFManager.getInstance().getETFTextureVariant(actualTexture, ETFRenderContext.getCurrentEntityState());

            //if texture is emissive or a variant then replace with a non sprite vertex consumer like regular entities
            if (!actualTexture.equals(texture.thisIdentifier) || texture.isEmissive() || texture.isEnchanted()) {
                ETFRenderContext.preventRenderLayerTextureModify();
                RenderType layer = layerFactory.apply(texture.thisIdentifier);
                ETFRenderContext.allowRenderLayerTextureModify();
                if (layer != null) {
                    VertexConsumer consumer = vertexConsumers.getBuffer(layer);
                    //noinspection ConstantValue
                    if (consumer != null) {
                        cir.setReturnValue(consumer);
                    }
                }
            }
        }
    }
    //#endif

}
