package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Saddleable;
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
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(SaddleFeatureRenderer.class)
public abstract class MixinSaddleFeatureRenderer<T extends Entity & Saddleable, M extends EntityModel<T>> {


    @Unique
    ETFEntityWrapper etf$entity = null;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;
    @Final
    @Shadow
    private M model;


    @Inject(
            method = "Lnet/minecraft/client/render/entity/feature/SaddleFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        etf$entity = new ETFEntityWrapper(entity);

    }

    @ModifyArg(
            method = "Lnet/minecraft/client/render/entity/feature/SaddleFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnAlteredTexture(Identifier texture) {


        entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(texture, etf$entity, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveArmour);
        return entity_texture_features$thisETFTexture.getTextureIdentifier(etf$entity);

    }

    @Inject(method = "Lnet/minecraft/client/render/entity/feature/SaddleFeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/Entity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void etf$applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();

//        matrixStack.push();
//        matrixStack.translate(3,0,3);
        if (entity_texture_features$thisETFTexture != null) entity_texture_features$thisETFTexture.renderEmissive(matrices, vertexConsumers, model);
//        matrixStack.pop();
    }

}


