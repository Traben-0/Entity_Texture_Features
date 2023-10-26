package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(VillagerClothingFeatureRenderer.class)
public abstract class MixinVillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat> extends FeatureRenderer<T, M> {

    @Unique
    ETFEntityWrapper etf$villager = null;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;

    @SuppressWarnings("unused")
    public MixinVillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$villager = new ETFEntityWrapper(livingEntity);
    }

    @Inject(method = "findTexture",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$returnAlteredTexture(String keyType, Identifier keyId, CallbackInfoReturnable<Identifier> cir) {
        if (etf$villager != null) {
            entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(cir.getReturnValue(), etf$villager, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs);
            //just in case
            //noinspection ConstantConditions
            if (entity_texture_features$thisETFTexture != null)
                cir.setReturnValue(entity_texture_features$thisETFTexture.getTextureIdentifier(etf$villager));
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/VillagerClothingFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V",
                    shift = At.Shift.AFTER, ordinal = 0))
    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {

        if (entity_texture_features$thisETFTexture != null)
            entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, (this.getContextModel()));
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/VillagerClothingFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V",
                    shift = At.Shift.AFTER, ordinal = 1))
    private void etf$applyEmissive2(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {

        if (entity_texture_features$thisETFTexture != null)
            entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, (this.getContextModel()));
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/VillagerClothingFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V",
                    shift = At.Shift.AFTER, ordinal = 2))
    private void etf$applyEmissive3(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {

        if (entity_texture_features$thisETFTexture != null)
            entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, (this.getContextModel()));
    }

}
