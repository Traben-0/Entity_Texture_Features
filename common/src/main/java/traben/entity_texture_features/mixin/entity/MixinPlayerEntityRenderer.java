package traben.entity_texture_features.mixin.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public int timerBeforeTrySkin = 200;

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
        if (timerBeforeTrySkin > 0) {
            timerBeforeTrySkin--;
        } else {
            if (ETFConfigData.skinFeaturesEnabled) {
                ETFPlayerTexture thisETFPlayerTexture = ETFManager.getPlayerTexture(player);
                if (thisETFPlayerTexture != null) {
                    Identifier etfTexture = thisETFPlayerTexture.getBaseTextureIdentifierOrNullForVanilla(player);
                    if (etfTexture != null) {
                        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(etfTexture)), light, OverlayTexture.DEFAULT_UV);
                        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(etfTexture)), light, OverlayTexture.DEFAULT_UV);
                        Identifier emissive = thisETFPlayerTexture.getBaseTextureEmissiveIdentifierOrNullForNone();
                        if (emissive != null) {
                            arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(emissive)), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(emissive)), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                        }
                        if (thisETFPlayerTexture.baseEnchantIdentifier != null) {
                            arm.render(matrices, ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(thisETFPlayerTexture.baseEnchantIdentifier), false, true), 15728640, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(thisETFPlayerTexture.baseEnchantIdentifier), false, true), 15728640, OverlayTexture.DEFAULT_UV);
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