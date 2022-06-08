package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(WardenFeatureRenderer.class)
public abstract class MixinWardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>> extends FeatureRenderer<T, M>{


    @Shadow @Final private Identifier texture;

    public MixinWardenFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/WardenEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();
        ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$returnAlteredTexture(this.texture), (this.getContextModel()));
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$entity = wardenEntity;
    }

    WardenEntity etf$entity = null;

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucentEmissive(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnAlteredTexture(Identifier texture) {

        if (ETFConfigData.enableCustomTextures) {
            return ETFUtils.generalReturnAlteredFeatureTextureOrOriginal(texture, etf$entity);
        }
        return texture;
    }
}


