package traben.entity_texture_features.texture_features.player;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.ETFRenderContext;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class ETFPlayerFeatureRenderer<T extends PlayerEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T,M> {

    public ETFPlayerFeatureRenderer(FeatureRendererContext<T,M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(ETFConfigData.skinFeaturesEnabled) {
            Identifier texture = getTexture(entity);
            ETFPlayerTexture playerTexture = ETFManager.getInstance().getPlayerTexture(entity, texture);
            if (playerTexture != null && playerTexture.hasFeatures) {
                M model = getContextModel();

                ETFRenderContext.preventRenderLayerTextureModify();
                playerTexture.renderFeatures(matrices,vertexConsumers,light,model);
                ETFRenderContext.allowRenderLayerTextureModify();
            }
        }
    }
}
