package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static traben.entity_texture_features.client.utils.ETFPlayerSkinUtils.UUID_PLAYER_HAS_EMISSIVE_CAPE;
import static traben.entity_texture_features.client.utils.ETFPlayerSkinUtils.UUID_PLAYER_HAS_ENCHANT_CAPE;

@Mixin(CapeFeatureRenderer.class)
public abstract class MixinCapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MixinCapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",
                    shift = At.Shift.BEFORE), cancellable = true)
    private void etf$injected(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        //valueMain= null;
        //if (abstractClientPlayerEntity.canRenderCapeTexture() && !abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE) && abstractClientPlayerEntity.getCapeTexture() != null) {
        //    ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
        //   if (!itemStack.isOf(Items.ELYTRA)) {

        UUID id = abstractClientPlayerEntity.getUuid();
        Identifier cape = abstractClientPlayerEntity.getCapeTexture();
        if (cape != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(cape));
            (this.getContextModel()).renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
            if (UUID_PLAYER_HAS_EMISSIVE_CAPE.containsKey(id)) {
                if (UUID_PLAYER_HAS_EMISSIVE_CAPE.getBoolean(id)) {
                    VertexConsumer emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(cape.toString().replace(".png", "_e.png"))));
                    (this.getContextModel()).renderCape(matrixStack, emissiveVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                }
            }
            if (UUID_PLAYER_HAS_ENCHANT_CAPE.containsKey(id)) {
                if (UUID_PLAYER_HAS_ENCHANT_CAPE.getBoolean(id)) {
                    VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(new Identifier(cape.toString().replace(".png", "_enchant.png"))), false, true);
                    //VertexConsumer enchantVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier( cape.toString().replace(".png","_e.png"))));
                    (this.getContextModel()).renderCape(matrixStack, enchantVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                }
            }
        }
        matrixStack.pop();
        ci.cancel();
        //    }
        //}
    }

//    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/PlayerEntityModel;renderCape(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"),
//            index = 1)
//    private VertexConsumer mixin(VertexConsumer value) {
//
//        return  valueMain!= null ? valueMain : value;
//    }
//
//    private VertexConsumer valueMain= null;

}


