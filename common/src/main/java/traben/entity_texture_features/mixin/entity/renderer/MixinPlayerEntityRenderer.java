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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.mod_compat.ETF3DSkinLayersUtil;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    @Unique
    public int entity_texture_features$timerBeforeTrySkin = 200;

    @SuppressWarnings("unused")
    public MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "renderArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/PlayerEntityModel;setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
                    shift = At.Shift.AFTER), cancellable = true)
    private void etf$redirectNicely(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        arm.pitch = 0.0F;
        sleeve.pitch = 0.0F;
        //I haven't nailed down exactly why, but it cannot attempt to grab the skin until a bit of time has passed
        if (entity_texture_features$timerBeforeTrySkin > 0) {
            entity_texture_features$timerBeforeTrySkin--;
        } else {
            if (ETFConfigData.skinFeaturesEnabled) {
                ETFPlayerTexture thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(player, player.getSkinTextures().texture());
                if (thisETFPlayerTexture != null) {
                    Identifier etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
                    if (etfTexture != null) {
                        VertexConsumer vc1 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(etfTexture));
                        arm.render(matrices, vc1, light, OverlayTexture.DEFAULT_UV);
                        sleeve.render(matrices, vc1, light, OverlayTexture.DEFAULT_UV);
                        if (ETFVersionDifferenceHandler.isThisModLoaded("skinlayers") || ETFVersionDifferenceHandler.isThisModLoaded("skinlayers3d")) {
                            try {
                                // handler class is only ever accessed if the mod is present
                                // prevents NoClassDefFoundError
                                //noinspection DataFlowIssue
                                ETF3DSkinLayersUtil.renderHand((PlayerEntityRenderer) ((Object) this), matrices, vc1, light, player, arm, sleeve);
                            } catch (Exception e) {
                                ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
                            } catch (NoClassDefFoundError error) {
                                // Should never be thrown
                                // unless a significant change if skin layers mod
                                ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
                            }
                        }

                        Identifier emissive = thisETFPlayerTexture.getBaseTextureEmissiveIdentifierOrNullForNone();
                        if (emissive != null) {
                            VertexConsumer vc2 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(emissive));
                            arm.render(matrices, vc2, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, vc2, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV);
                            if (ETFVersionDifferenceHandler.isThisModLoaded("skinlayers") || ETFVersionDifferenceHandler.isThisModLoaded("skinlayers3d")) {
                                try {
                                    // handler class is only ever accessed if the mod is present
                                    // prevents NoClassDefFoundError

                                    //noinspection DataFlowIssue
                                    ETF3DSkinLayersUtil.renderHand((PlayerEntityRenderer) ((Object) this), matrices, vc2, light, player, arm, sleeve);
                                } catch (Exception e) {
                                    ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
                                } catch (NoClassDefFoundError error) {
                                    // Should never be thrown
                                    // unless a significant change if skin layers mod
                                    ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
                                }
                            }
                        }
                        if (thisETFPlayerTexture.baseEnchantIdentifier != null) {
                            VertexConsumer vc3 = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(thisETFPlayerTexture.baseEnchantIdentifier), false, true);
                            arm.render(matrices, vc3, light, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, vc3, light, OverlayTexture.DEFAULT_UV);
                            if (ETFVersionDifferenceHandler.isThisModLoaded("skinlayers") || ETFVersionDifferenceHandler.isThisModLoaded("skinlayers3d")) {
                                try {
                                    // handler class is only ever accessed if the mod is present
                                    // prevents NoClassDefFoundError
                                    //noinspection DataFlowIssue
                                    ETF3DSkinLayersUtil.renderHand((PlayerEntityRenderer) ((Object) this), matrices, vc3, light, player, arm, sleeve);
                                } catch (Exception e) {
                                    ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
                                } catch (NoClassDefFoundError error) {
                                    // Should never be thrown
                                    // unless a significant change if skin layers mod
                                    ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
                                }
                            }
                        }
                        //don't further render vanilla arms
                        ci.cancel();
                    }
                }
            }
        }
        //else vanilla render
    }
}