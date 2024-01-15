package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.compat.ETF3DSkinLayersUtil;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.player.ETFPlayerFeatureRenderer;
import traben.entity_texture_features.features.player.ETFPlayerSkinHolder;
import traben.entity_texture_features.features.player.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements ETFPlayerSkinHolder {


    @Unique
    ETFPlayerTexture etf$ETFPlayerTexture = null;

    @SuppressWarnings("unused")
    public MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void etf$addFeatures(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        PlayerEntityRenderer self = (PlayerEntityRenderer) ((Object) this);
        this.addFeature(new ETFPlayerFeatureRenderer<>(self));
    }



    /*
     * For some reason cancelling in this way is the only way to get this working
     * */
    @Inject(method = "renderArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/PlayerEntityModel;setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
                    shift = At.Shift.AFTER), cancellable = true)
    private void etf$redirectNicely(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        if (ETFConfig.getInstance().skinFeaturesEnabled) {
            ETFPlayerTexture thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(player, player.getSkinTextures().texture());
            if (thisETFPlayerTexture != null && thisETFPlayerTexture.hasFeatures) {
                Identifier etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
                if (etfTexture != null) {
                    ETFRenderContext.preventRenderLayerTextureModify();

                    arm.pitch = 0.0F;
                    sleeve.pitch = 0.0F;

                    VertexConsumer vc1 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(etfTexture));
                    etf$renderOnce(matrices,vc1,light,player,arm,sleeve);

                    ETFRenderContext.startSpecialRenderOverlayPhase();
                    Identifier emissive = thisETFPlayerTexture.getBaseTextureEmissiveIdentifierOrNullForNone();
                    if (emissive != null) {
                        VertexConsumer vc2 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(emissive));
                        etf$renderOnce(matrices,vc2,ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE,player,arm,sleeve);
                    }
                    if (thisETFPlayerTexture.baseEnchantIdentifier != null) {
                        VertexConsumer vc3 = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(thisETFPlayerTexture.baseEnchantIdentifier), false, true);
                        etf$renderOnce(matrices,vc3,light,player,arm,sleeve);
                    }
                    ETFRenderContext.endSpecialRenderOverlayPhase();

                    ETFRenderContext.allowRenderLayerTextureModify();
                    //don't further render vanilla arms
                    ci.cancel();
                }
            }
        }

    }


    @Unique
    private void etf$renderOnce(MatrixStack matrixStack, VertexConsumer consumer, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve){
        arm.render(matrixStack, consumer, light, OverlayTexture.DEFAULT_UV);
        sleeve.render(matrixStack, consumer, light, OverlayTexture.DEFAULT_UV);
        if (ETFClientCommon.SKIN_LAYERS_DETECTED && ETFConfig.getInstance().use3DSkinLayerPatch) {
            try {
                // handler class is only ever accessed if the mod is present
                // prevents NoClassDefFoundError
                //noinspection DataFlowIssue
                ETF3DSkinLayersUtil.renderHand((PlayerEntityRenderer) ((Object) this), matrixStack, consumer, light, player, arm, sleeve);
            } catch (Exception e) {
                //ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
            } catch (NoClassDefFoundError error) {
                // Should never be thrown
                // unless a significant change if skin layers mod
                ETFUtils2.logError("Error with ETF's 3D skin layers mod hand compatibility: " + error);
                error.printStackTrace();
                //prevent further attempts
                ETFClientCommon.SKIN_LAYERS_DETECTED = false;
            }
        }
    }



    @Inject(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfoReturnable<Identifier> cir) {
        if (ETFConfig.getInstance().skinFeaturesEnabled) {
            etf$ETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(abstractClientPlayerEntity, cir.getReturnValue());
            if (etf$ETFPlayerTexture != null && etf$ETFPlayerTexture.hasFeatures) {
                Identifier texture = etf$ETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(abstractClientPlayerEntity);
                if (texture != null) {
                    System.out.println(etf$ETFPlayerTexture.etfTextureOfFinalBaseSkin);
                    cir.setReturnValue(texture);
                }
            }
        } else {
            etf$ETFPlayerTexture = null;
        }
    }

    @Override
    public @Nullable ETFPlayerTexture etf$getETFPlayerTexture() {
        return etf$ETFPlayerTexture;
    }
}