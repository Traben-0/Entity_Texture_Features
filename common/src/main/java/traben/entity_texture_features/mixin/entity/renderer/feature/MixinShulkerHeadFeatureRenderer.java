package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ShulkerHeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ShulkerHeadFeatureRenderer.class)
public abstract class MixinShulkerHeadFeatureRenderer extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {


    @Unique
    ETFEntityWrapper etf$shulker = null;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;

    @SuppressWarnings("unused")
    public MixinShulkerHeadFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/ShulkerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    shift = At.Shift.AFTER))
    private void etf$applyRenderFeatures(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ShulkerEntity shulkerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {

        if (entity_texture_features$thisETFTexture != null)
            entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, (this.getContextModel()));
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/ShulkerEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ShulkerEntity shulkerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$shulker = new ETFEntityWrapper(shulkerEntity);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/ShulkerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$returnAlteredTexture(Identifier texture) {

        entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(texture, etf$shulker, ETFManager.TextureSource.ENTITY, ETFConfigData.removePixelsUnderEmissiveMobs);
        return entity_texture_features$thisETFTexture.getTextureIdentifier(etf$shulker);
    }
}
