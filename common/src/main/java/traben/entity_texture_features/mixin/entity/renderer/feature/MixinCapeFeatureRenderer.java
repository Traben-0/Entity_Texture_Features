package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@Mixin(CapeFeatureRenderer.class)
public abstract class MixinCapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    //private static final Identifier dev_cape = new Identifier(MOD_ID, "textures/capes/etf.png");
    @Unique
    private static final Identifier dev_cape_e = new Identifier(MOD_ID, "textures/capes/etf_e.png");
    //private static final Identifier wife_cape = new Identifier(MOD_ID, "textures/capes/wife.png");
    @Unique
    AbstractClientPlayerEntity etf$player = null;

    @SuppressWarnings("unused")
    public MixinCapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/PlayerEntityModel;renderCape(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    shift = At.Shift.AFTER))
    private void etf$injected(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        //custom rendering required as ETF uses a different render layer to allow transparent capes
        // the return of getCapeTexture() from abstract client is ignored here, but it was required for enabling capes to render for those players and also for elytras
        ETFPlayerTexture playerTexture = ETFManager.getInstance().getPlayerTexture(abstractClientPlayerEntity, abstractClientPlayerEntity.getSkinTextures().texture());
        //can load these textures
        if (ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric")) {
            if (playerTexture != null && playerTexture.capeType != ETFConfigScreenSkinTool.CapeType.NONE) {
                playerTexture.renderFeatures(matrixStack, vertexConsumerProvider, i, this.getContextModel());
            } else if (abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Dev)) {
                VertexConsumer emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(dev_cape_e));
                (this.getContextModel()).renderCape(matrixStack, emissiveVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV);
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At("HEAD"))
    private void injected(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$player = abstractClientPlayerEntity;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"),
            index = 0
    )
    private RenderLayer mixin(RenderLayer layer) {
        if (etf$player != null)
            return RenderLayer.getEntityTranslucent(etf$player.getSkinTextures().capeTexture());
        return layer;
    }

}


