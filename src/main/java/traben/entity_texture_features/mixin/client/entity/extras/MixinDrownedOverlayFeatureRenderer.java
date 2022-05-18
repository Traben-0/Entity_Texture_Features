package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;
import static traben.entity_texture_features.client.ETFClient.PATH_IS_EXISTING_FEATURE;

@Mixin(DrownedOverlayFeatureRenderer.class)
public abstract class MixinDrownedOverlayFeatureRenderer<T extends DrownedEntity> extends FeatureRenderer<T, DrownedEntityModel<T>> {


    public MixinDrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/DrownedEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T drownedEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$entity = drownedEntity;
    }

    private DrownedEntity etf$entity = null;

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/DrownedEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/DrownedOverlayFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V"))
    private Identifier etf$returnAlteredTexture(Identifier texture) {
        if (ETFConfigData.enableCustomTextures) {
            String check = ETFUtils.generalReturnAlreadySetAlteredTexture(texture, etf$entity).toString();
            //System.out.println("check=" + check);
            if (!PATH_IS_EXISTING_FEATURE.containsKey(check)) {
                PATH_IS_EXISTING_FEATURE.put(check, ETFUtils.isExistingNativeImageFile(new Identifier(check)));
            }
            if (PATH_IS_EXISTING_FEATURE.get(check)) {
                return new Identifier(check);
            }
        }

        return texture;
    }

    @Final
    @Shadow
    private static Identifier SKIN;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/DrownedEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/DrownedOverlayFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T drownedEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();

        ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$returnAlteredTexture(SKIN), model);

    }

    @Final
    @Shadow
    private DrownedEntityModel<T> model;

}


