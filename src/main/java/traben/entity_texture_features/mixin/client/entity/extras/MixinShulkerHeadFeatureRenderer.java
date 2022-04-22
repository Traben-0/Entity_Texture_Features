package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ShulkerHeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETFUtils;

@Mixin(ShulkerHeadFeatureRenderer.class)
public abstract class MixinShulkerHeadFeatureRenderer extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {


    public MixinShulkerHeadFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/ShulkerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    shift = At.Shift.AFTER))
    private void etf$applyRenderFeatures(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ShulkerEntity shulkerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFUtils.renderEmissiveModel(matrixStack, vertexConsumerProvider, etf$returnAlteredTexture(ShulkerEntityRenderer.getTexture(shulkerEntity.getColor())), (this.getContextModel()), false);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/ShulkerEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ShulkerEntity shulkerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$shulker = shulkerEntity;
    }

    ShulkerEntity etf$shulker = null;

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/ShulkerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnAlteredTexture(Identifier texture) {
        return ETFUtils.generalReturnAlreadySetAlteredTexture(texture, etf$shulker);
    }
}
