package traben.entity_texture_features.mixin.client.entity;

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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(PlayerEntityRenderer.class)
public abstract class MIX_PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements ETF_METHODS {
    public int timerBeforeTrySkin = 80;

    public MIX_PlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Redirect(method = "renderArm",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",ordinal = 0)
            )
    private void injected3(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        //do nothing
    }

    @Inject(method = "renderArm", at = @At(value = "TAIL"))
    private void renderSkinFeaturesOverlays(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        //I haven't nailed down exactly why, but it cannot attempt to grab the skin until a bit of time has passed
        if (timerBeforeTrySkin > 0) {
            arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
            sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
            timerBeforeTrySkin--;
        }else {
            try {
                UUID id = player.getUuid();

                if (!UUID_playerHasFeatures.containsKey(id) && !UUID_playerSkinDownloadedYet.containsKey(id)) {
                    //check for mark
                    checkPlayerForSkinFeatures(id, player);
                }
                if (UUID_playerSkinDownloadedYet.get(id)) {
                    if (UUID_playerHasFeatures.get(id)) {
                        if (ETFConfigData.skinFeaturesEnableTransparency
                                && UUID_playerTransparentSkinId.containsKey(id)) {
                            arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(UUID_playerTransparentSkinId.get(id))), light, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                        } else {
                            arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                        }
                        if (UUID_playerHasEmissive.get(id)) {
                            arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(SKIN_NAMESPACE + id + "_e.png"))), 15728640, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(SKIN_NAMESPACE + id + "_e.png"))), 15728640, OverlayTexture.DEFAULT_UV);
                        }
                        if (UUID_playerHasEnchant.get(id)) {
                            arm.render(matrices, ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(new Identifier(SKIN_NAMESPACE + id + "_enchant.png")), false, true), 15728640, OverlayTexture.DEFAULT_UV);
                            sleeve.render(matrices, ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(new Identifier(SKIN_NAMESPACE + id + "_enchant.png")), false, true), 15728640, OverlayTexture.DEFAULT_UV);
                        }
                    } else {
                        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                    }
                } else {
                    arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                    sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
                }
            } catch (Exception e) {
                modMessage(e.toString(), false);
            }
        }
    }

}
