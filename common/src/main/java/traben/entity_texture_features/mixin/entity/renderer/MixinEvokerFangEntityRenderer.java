package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EvokerFangsEntityRenderer;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(EvokerFangsEntityRenderer.class)
public abstract class MixinEvokerFangEntityRenderer extends EntityRenderer<EvokerFangsEntity> {

    @Final
    @Shadow
    private static Identifier TEXTURE;

    @Unique
    private ETFEntityWrapper etf$entity = null;
    @Final
    @Shadow
    private EvokerFangsEntityModel<EvokerFangsEntity> model;

    @SuppressWarnings("unused")
    protected MixinEvokerFangEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/mob/EvokerFangsEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(EvokerFangsEntity evokerFangsEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        etf$entity = new ETFEntityWrapper(evokerFangsEntity);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/mob/EvokerFangsEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer etf$returnAlteredTexture(RenderLayer renderLayer) {

        if (ETFConfigData.enableCustomTextures) {
            try {

                Identifier alteredTexture = ETFManager.getInstance().getETFTexture(TEXTURE, (etf$entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs).getTextureIdentifier(etf$entity);
                RenderLayer layerToReturn;

                if (ETFManager.getInstance().ENTITY_TYPE_RENDER_LAYER.containsKey(etf$entity.getType())) {
                    //Identifier identifier = this.getTexture(entity);
                    int choice = ETFManager.getInstance().ENTITY_TYPE_RENDER_LAYER.getInt(etf$entity.getType());
                    //noinspection EnhancedSwitchMigration
                    switch (choice) {
                        case 1:
                            layerToReturn = (RenderLayer.getEntityTranslucent(alteredTexture));
                            break;
                        case 2:
                            layerToReturn = (RenderLayer.getEntityTranslucentCull(alteredTexture));
                            break;
                        case 3:
                            layerToReturn = (RenderLayer.getEndGateway());
                            break;
                        case 4:
                            layerToReturn = (RenderLayer.getOutline(alteredTexture));
                            break;
                        default:
                            layerToReturn = (null);
                            break;
                    }
                } else {
                    layerToReturn = RenderLayer.getEntityCutoutNoCull(alteredTexture);
                }

                if (layerToReturn != null) return layerToReturn;

            } catch (Exception e) {
                ETFUtils2.logError(e.toString(), false);
            }
        }

        return renderLayer;
    }

    @Inject(method = "render(Lnet/minecraft/entity/mob/EvokerFangsEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EvokerFangsEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissive(EvokerFangsEntity evokerFangsEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci, float h, float j, float k, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();
        ETFManager.getInstance().getETFTexture(TEXTURE, (etf$entity), ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs).renderEmissive(matrixStack, vertexConsumerProvider, model);

    }

}


