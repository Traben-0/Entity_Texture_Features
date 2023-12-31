package traben.entity_texture_features.mixin.mods.skin_layers;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Pseudo
@Mixin(BodyLayerFeatureRenderer.class)
public abstract class Mixin3DSkinLayersBody {


    @Shadow public abstract void renderLayers(AbstractClientPlayerEntity abstractClientPlayer, PlayerSettings settings, MatrixStack matrixStack, VertexConsumer vertices, int light, int overlay);

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/tr7zw/skinlayers/renderlayers/BodyLayerFeatureRenderer;renderLayers(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Ldev/tr7zw/skinlayers/accessor/PlayerSettings;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    shift = At.Shift.AFTER))
    private void etf$renderETFHead(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, AbstractClientPlayerEntity player, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (ETFConfigData != null && ETFConfigData.use3DSkinLayerPatch) {
            ETFPlayerTexture thisETF = ETFManager.getInstance().getPlayerTexture(player, player.getSkinTextures().texture());
            if (thisETF != null && thisETF.hasFeatures) {
                int overlay = LivingEntityRenderer.getOverlay(player, 0.0F);
                Identifier emissiveSkin = thisETF.getBaseTextureEmissiveIdentifierOrNullForNone();
                if (emissiveSkin != null) {
                    VertexConsumer emissive = multiBufferSource.getBuffer(RenderLayer.getEntityTranslucentCull(emissiveSkin));
                    renderLayers(player, (PlayerSettings) player, poseStack, emissive, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay);
                }
                Identifier enchantSkin = thisETF.getBaseTextureEnchantIdentifierOrNullForNone();
                if (enchantSkin != null) {
                    VertexConsumer enchanted = ItemRenderer.getArmorGlintConsumer(multiBufferSource, RenderLayer.getArmorCutoutNoCull(enchantSkin), false, true);
                    renderLayers(player, (PlayerSettings) player, poseStack, enchanted, i, overlay);
                }
            }
        }
    }
}


