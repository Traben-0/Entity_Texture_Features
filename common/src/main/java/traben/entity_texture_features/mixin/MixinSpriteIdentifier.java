package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

import java.util.function.Function;

@Mixin(SpriteIdentifier.class)
public class MixinSpriteIdentifier {
    @Inject(method = "getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$modifyIfRequired(VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory, CallbackInfoReturnable<VertexConsumer> cir) {

        if (cir.getReturnValue() instanceof SpriteTexturedVertexConsumer spriteTexturedVertexConsumer) {
            Identifier rawId = spriteTexturedVertexConsumer.sprite.getContents().getId();

            //infer actual texture
            Identifier actualTexture;
            if (rawId.toString().endsWith(".png")) {
                actualTexture = rawId;
            } else {
                //todo check all block entities follow this logic? i know chests, shulker boxes, and beds do
                actualTexture = new Identifier(rawId.getNamespace(), "textures/" + rawId.getPath() + ".png");
            }


            ETFTexture texture = ETFManager.getInstance().getETFTextureVariant(actualTexture, ETFRenderContext.getCurrentEntity());

            //if texture is emissive or a variant then replace with a non sprite vertex consumer like regular entities
            if (texture.getVariantNumber() != 0 || texture.isEmissive() || texture.isEnchanted()) {
                ETFRenderContext.preventRenderLayerTextureModify();
                RenderLayer layer = layerFactory.apply(texture.thisIdentifier);
                ETFRenderContext.allowRenderLayerTextureModify();
                if(layer != null) {
                    VertexConsumer consumer = vertexConsumers.getBuffer(layer);
                    if (consumer != null) {
                        cir.setReturnValue(consumer);
                    }
                }
            }
        }
    }

}
