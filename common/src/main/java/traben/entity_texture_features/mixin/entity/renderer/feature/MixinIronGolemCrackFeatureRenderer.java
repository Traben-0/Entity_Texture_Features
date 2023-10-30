package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
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

@Mixin(IronGolemCrackFeatureRenderer.class)
public abstract class MixinIronGolemCrackFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {


    @Unique
    ETFEntityWrapper etf$entity = null;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;

    @SuppressWarnings("unused")
    public MixinIronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/IronGolemEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/IronGolemCrackFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IronGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, IronGolemEntity.Crack crack, Identifier identifier) {
        //UUID id = livingEntity.getUuid();
        if (entity_texture_features$thisETFTexture != null)
            entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, this.getContextModel());
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/IronGolemEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IronGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$entity = new ETFEntityWrapper(ironGolemEntity);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/IronGolemEntity;FFFFFF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/feature/IronGolemCrackFeatureRenderer;renderModel(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFF)V")
            , index = 1)
    private Identifier etf$returnAlteredTexture(Identifier texture) {

        entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(texture, etf$entity, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveMobs);
        return entity_texture_features$thisETFTexture.getTextureIdentifier(etf$entity);
    }
}


