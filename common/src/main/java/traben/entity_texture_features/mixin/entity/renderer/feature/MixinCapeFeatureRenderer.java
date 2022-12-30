package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreenSkinTool;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@Mixin(CapeFeatureRenderer.class)
public abstract class MixinCapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier dev_cape = new Identifier(MOD_ID, "textures/capes/dev.png");
    private static final Identifier dev_cape_e = new Identifier(MOD_ID, "textures/capes/dev_e.png");
    private static final Identifier wife_cape = new Identifier(MOD_ID, "textures/capes/wife.png");

    @SuppressWarnings("unused")
    public MixinCapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",
                    shift = At.Shift.BEFORE), cancellable = true)
    private void etf$injected(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        //custom rendering required as ETF uses a different render layer to allow transparent capes
        // the return of getCapeTexture() from abstract client is ignored here, but it was required for enabling capes to render for those players and also for elytras
        ETFPlayerTexture playerTexture = ETFManager.getInstance().getPlayerTexture(abstractClientPlayerEntity, abstractClientPlayerEntity.getSkinTexture());
        boolean cancelVanillaRender = false;
        if (playerTexture != null) {
            cancelVanillaRender = playerTexture.hasCustomCape();
            if ((abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Dev) || abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Wife))
                    && playerTexture.capeType == ETFConfigScreenSkinTool.CapeType.NONE
                    && ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric")) {
                cancelVanillaRender = false;
            }
            if (cancelVanillaRender) {
                playerTexture.renderCapeAndFeatures(matrixStack, vertexConsumerProvider, i, this.getContextModel());
            }
        }
        if (!cancelVanillaRender && ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric")) {
            if (abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Dev)) {
                cancelVanillaRender = true;
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(dev_cape));
                (this.getContextModel()).renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                VertexConsumer emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(dev_cape_e));
                (this.getContextModel()).renderCape(matrixStack, emissiveVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
            } else if (abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Wife)) {
                cancelVanillaRender = true;
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(wife_cape));
                (this.getContextModel()).renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
            }
        }
        if (cancelVanillaRender) {
            matrixStack.pop();
            ci.cancel();
        }
    }


}


