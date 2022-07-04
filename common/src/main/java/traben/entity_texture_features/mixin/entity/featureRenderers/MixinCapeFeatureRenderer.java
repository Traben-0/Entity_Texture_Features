package traben.entity_texture_features.mixin.entity.featureRenderers;

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
import traben.entity_texture_features.ETFCrossPlatformHandler;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@Mixin(CapeFeatureRenderer.class)
public abstract class MixinCapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MixinCapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;",
                    shift = At.Shift.BEFORE), cancellable = true)
    private void etf$injected(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        //custom rendering required as ETF uses a different render layer to allow transparent capes
        // the return of getCapeTexture() from abstract client is ignored here, but it was required for enabling capes to render for those players and also for elytras
        ETFPlayerTexture playerTexture = ETFManager.getPlayerTexture(abstractClientPlayerEntity);
        if (playerTexture != null) {
            playerTexture.renderCapeAndFeatures(matrixStack, vertexConsumerProvider, i, this.getContextModel());
        }
        if (ETFCrossPlatformHandler.isFabric() == ETFCrossPlatformHandler.isThisModLoaded("fabric")) {
            if (abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Dev)) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(MOD_ID, "textures/capes/dev.png")));
                (this.getContextModel()).renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                VertexConsumer emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(MOD_ID, "textures/capes/dev_e.png")));
                (this.getContextModel()).renderCape(matrixStack, emissiveVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
            } else if (abstractClientPlayerEntity.getUuid().equals(ETFPlayerTexture.Wife)) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(MOD_ID, "textures/capes/wife.png")));
                (this.getContextModel()).renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
            }
        }
        matrixStack.pop();
        ci.cancel();
    }


}


