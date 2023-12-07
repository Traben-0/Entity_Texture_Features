package traben.entity_texture_features.compat;

import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerTexture;

//class exists to introduce extra, ETF interacting, child functionality to 3d Skin layer mod featureRenderers
//this will replace the featureRender added by 3d skin layers mod
//
// as long as inheritance remains valid this should remain future-proof
public class ETF3DBodyLayerFeatureRenderer extends BodyLayerFeatureRenderer {
    private VertexConsumerProvider thisProvider = null;

    public ETF3DBodyLayerFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> renderLayerParent) {
        super(renderLayerParent);
    }

    // simply captures vertexConsumerProvider
    @Override
    public void render(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, AbstractClientPlayerEntity player, float f, float g, float h, float j, float k, float l) {
        thisProvider = multiBufferSource;
        super.render(poseStack, multiBufferSource, i, player, f, g, h, j, k, l);
    }

    // simply defers the parent renderLayers method until ETF has had a chance to change the vertex consumer
    @Override
    public void renderLayers(AbstractClientPlayerEntity abstractClientPlayer, PlayerSettings settings, MatrixStack matrixStack, VertexConsumer vertices, int light, int overlay) {
        ETFPlayerTexture thisETF = ETFManager.getInstance().getPlayerTexture(abstractClientPlayer, abstractClientPlayer.getSkinTextures().texture());
        if (thisETF != null) {
            Identifier skin = thisETF.getBaseTextureIdentifierOrNullForVanilla(abstractClientPlayer);
            if (skin != null) {
                vertices = thisProvider.getBuffer(RenderLayer.getEntityTranslucent(skin));
            }
            // mesh may be slightly incorrect if texture has changed significantly
            // only minor changes expected, typically to the face for blinking
            super.renderLayers(abstractClientPlayer, settings, matrixStack, vertices, light, overlay);

            // further meshes are correct as emissive and enchant are only overlays
            Identifier emissiveSkin = thisETF.getBaseTextureEmissiveIdentifierOrNullForNone();
            if (emissiveSkin != null) {
                vertices = thisProvider.getBuffer(RenderLayer.getEntityTranslucent(emissiveSkin));
                super.renderLayers(abstractClientPlayer, settings, matrixStack, vertices, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay);
            }
            Identifier enchantSkin = thisETF.getBaseTextureEnchantIdentifierOrNullForNone();
            if (enchantSkin != null) {
                vertices = ItemRenderer.getArmorGlintConsumer(thisProvider, RenderLayer.getArmorCutoutNoCull(enchantSkin), false, true);
                super.renderLayers(abstractClientPlayer, settings, matrixStack, vertices, light, overlay);
            }
        } else {
            super.renderLayers(abstractClientPlayer, settings, matrixStack, vertices, light, overlay);
        }
    }
}
