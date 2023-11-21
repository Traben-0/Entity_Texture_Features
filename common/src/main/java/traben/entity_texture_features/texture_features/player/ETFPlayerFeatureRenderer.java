package traben.entity_texture_features.texture_features.player;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import traben.entity_texture_features.texture_features.ETFRenderContext;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class ETFPlayerFeatureRenderer<T extends PlayerEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T,M> {

    protected final ETFPlayerSkinHolder skinHolder;

    public ETFPlayerFeatureRenderer(FeatureRendererContext<T,M> context) {
        super(context);
        this.skinHolder = context instanceof ETFPlayerSkinHolder holder ? holder : null;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(ETFConfigData.skinFeaturesEnabled && skinHolder != null) {
            ETFPlayerTexture playerTexture = skinHolder.etf$getETFPlayerTexture();
            if (playerTexture != null && playerTexture.hasFeatures) {
                M model = getContextModel();

                ETFRenderContext.preventRenderLayerTextureModify();
                playerTexture.renderFeatures(matrices,vertexConsumers,light,model);
                ETFRenderContext.allowRenderLayerTextureModify();
            }
        }
    }
}
