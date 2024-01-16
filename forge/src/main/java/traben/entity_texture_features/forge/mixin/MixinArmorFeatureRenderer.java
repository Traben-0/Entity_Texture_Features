package traben.entity_texture_features.forge.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreen;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;


@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;

    @SuppressWarnings("unused")
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$markNotToChange(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.preventRenderLayerTextureModify();
        ETFRenderContext.allowTexturePatching();
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "RETURN"))
    private void etf$markAllowedToChange(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.preventTexturePatching();
    }


    @ModifyArg(method = "renderModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changeTexture(Identifier texture) {
        if(ETFConfig.getInstance().enableArmorAndTrims) {
            entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTextureNoVariation(texture);
            //noinspection ConstantConditions
            if (entity_texture_features$thisETFTexture != null) {
                entity_texture_features$thisETFTexture.reRegisterBaseTexture();
                return entity_texture_features$thisETFTexture.getTextureIdentifier(null);
            }
        }
        return texture;
    }

    @Inject(method = "renderModel",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack arg, VertexConsumerProvider arg2, int i, boolean bl, Model arg3, float f, float g, float h, Identifier armorResource, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (entity_texture_features$thisETFTexture != null && ETFConfig.getInstance().enableEmissiveTextures) {
            Identifier emissive = entity_texture_features$thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = arg2.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                //}
                ETFRenderContext.startSpecialRenderOverlayPhase();
                arg3.render(arg, textureVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
                ETFRenderContext.endSpecialRenderOverlayPhase();
            }
        }

    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$cancelIfUi(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (MinecraftClient.getInstance() != null) {
            if (MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreen) {
                //cancel armour rendering
                ci.cancel();
            }
        }
    }

}


