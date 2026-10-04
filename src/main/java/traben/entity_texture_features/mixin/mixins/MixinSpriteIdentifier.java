package traben.entity_texture_features.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import traben.entity_texture_features.utils.ETFUtils2;
//#if MC >= 26.1
//$$ @Mixin(net.minecraft.client.resources.model.sprite.SpriteId.class)
//#else
@Mixin(Material.class)
//#endif
public class MixinSpriteIdentifier {
    //TODO really needs a look at
//#if MC < 26.2
    @Unique
    private static final Map<ResourceLocation, ResourceLocation> etf$ACTUAL_TEXTURES = new ConcurrentHashMap<>();

    @Unique
    private static boolean etf$isStandardPath(String path) {
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= '0' && c <= '9')
                    && c != '_' && c != '-' && c != '.' && c != '/') {
                return false;
            }
        }
        return true;
    }

    @Inject(method =
            //#if MC >= 26.1
            //$$ {
            //$$     "buffer(Lnet/minecraft/client/resources/model/sprite/SpriteGetter;Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            //$$     "buffer(Lnet/minecraft/client/resources/model/sprite/SpriteGetter;Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            //$$ },
            //#elseif MC >= 12109
            "buffer(Lnet/minecraft/client/resources/model/MaterialSet;Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            //#else
            //$$ "buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            //#endif
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$modifyIfRequired(CallbackInfoReturnable<VertexConsumer> cir,
                                      @Local(argsOnly = true) net.minecraft.client.renderer.MultiBufferSource vertexConsumers,
                                      @Local(argsOnly = true) Function<ResourceLocation, RenderType> layerFactory) {

        if (cir.getReturnValue() instanceof SpriteCoordinateExpander spriteTexturedVertexConsumer) {
            ResourceLocation rawId = spriteTexturedVertexConsumer.sprite.contents().name();

            //infer actual texture
            ResourceLocation actualTexture = etf$ACTUAL_TEXTURES.get(rawId);
            if (actualTexture == null) {
                if (rawId.toString().endsWith(".png")) {
                    actualTexture = rawId;
                } else {
                    //todo check all block entities follow this logic? i know chests, shulker boxes, and beds do
                    actualTexture = ETFUtils2.res(rawId.getNamespace(), "textures/" + rawId.getPath() + ".png");
                    // Nonstandard paths depend on the current illegal-path support setting.
                    if (etf$isStandardPath(rawId.getPath())) {
                        etf$ACTUAL_TEXTURES.put(rawId, actualTexture);
                    }
                }
            }


            ETFTexture texture = ETFManager.getInstance().getETFTextureVariant(actualTexture, ETFState.state());

            //if texture is emissive or a variant then replace with a non sprite vertex consumer like regular entities
            if (!actualTexture.equals(texture.thisIdentifier) || texture.isEmissive() || texture.isEnchanted()) {
                ETFState.pushRenderLayerModifyState(false);
                RenderType layer = layerFactory.apply(texture.thisIdentifier);
                ETFState.popRenderLayerModifyState();
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
