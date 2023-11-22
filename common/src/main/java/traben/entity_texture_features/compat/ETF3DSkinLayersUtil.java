package traben.entity_texture_features.compat;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import dev.tr7zw.skinlayers.renderlayers.BodyLayerFeatureRenderer;
import dev.tr7zw.skinlayers.renderlayers.HeadLayerFeatureRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import traben.entity_texture_features.utils.ETFUtils2;

// compatibility class for the wonderful 3D skin layers mod by @tr9zw to utilise ETF skin features
public abstract class ETF3DSkinLayersUtil {
    // method is called from MixinLivingEntityRenderer as a super of the playerEntityRenderer
    //
    // method is handled in this class separately to prevent any loading attempt of 3D skin layer mod classes without the mod being present
    //
    // this appears to be the easiest way to add to the 3D skin layer mod's featureRenderers while also utilising
    // the mods code to do all the setup required for correct rendering
    //
    // this method is future-proof only with the expectation that the 2 ETF3D featureRenderer's inheritance remains valid

    // checks whether the provided featureRenderer is one of the 3D skin layer feature renderers
    public static boolean canReplace(FeatureRenderer<?, ?> feature) {
        return feature instanceof BodyLayerFeatureRenderer || feature instanceof HeadLayerFeatureRenderer;
    }

    // returns a replacement ETF child featureRenderer of the 3D skin layer featureRenderer
    public static FeatureRenderer<?, ?> getReplacement(FeatureRenderer<?, ?> feature, FeatureRendererContext<?, ?> context) {
        try {
            if (feature instanceof HeadLayerFeatureRenderer) {
                //noinspection unchecked
                return new ETF3DHeadLayerFeatureRenderer((FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) context);
            } else if (feature instanceof BodyLayerFeatureRenderer) {
                //noinspection unchecked
                return new ETF3DBodyLayerFeatureRenderer((FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) context);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
        }
        ETFUtils2.logError("Failed in ETF's 3D skin layers mod compatibility.");
        return feature;
    }

    // todo ensure rendering method is kept up to date with how 3D skin layers mod does it
    // copy of 3D skin layers hand rendering code as ETF needs to be able to render it at will with a custom VertexConsumer
    public static void renderHand(PlayerEntityRenderer instance, MatrixStack poseStack, VertexConsumer vertexConsumer, int i, AbstractClientPlayerEntity abstractClientPlayer, ModelPart arm, ModelPart sleeve) {
        try {
            // try contains hand render code
            boolean rightSleeve = instance.getModel().leftSleeve != sleeve;
            if (rightSleeve) {
                if (!SkinLayersModBase.config.enableRightSleeve) {
                    return;
                }
            } else if (!SkinLayersModBase.config.enableLeftSleeve) {
                return;
            }

            sleeve.visible = false;
            if (abstractClientPlayer.isPartVisible(rightSleeve ? PlayerModelPart.RIGHT_SLEEVE : PlayerModelPart.LEFT_SLEEVE)) {
                PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
                float pixelScaling = 1.1F;
                float armHeightScaling = 1.1F;
                boolean thinArms = ((PlayerEntityModelAccessor) instance.getModel()).hasThinArms();
                if (SkinUtil.setup3dLayers(abstractClientPlayer, settings, thinArms, instance.getModel())) {
                    Mesh part = sleeve == instance.getModel().leftSleeve ? settings.getLeftArmMesh() : settings.getRightArmMesh();
                    part.copyFrom(arm);
                    poseStack.push();
                    poseStack.scale(pixelScaling, armHeightScaling, pixelScaling);
                    boolean left = sleeve == instance.getModel().leftSleeve;
                    float x = left ? 5.0F : -5.0F;
                    float y = 1.4F;
                    if (!thinArms) {
                        if (left) {
                            x = (float) ((double) x + 0.4);
                        } else {
                            x = (float) ((double) x - 0.4);
                        }
                    }

                    part.setPosition(x, y, 0.0F);
                    part.render(poseStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                    part.setPosition(0.0F, 0.0F, 0.0F);
                    part.setRotation(0.0F, 0.0F, 0.0F);
                    poseStack.pop();
                }
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod hand compatibility: " + e);
        } catch (NoClassDefFoundError error) {
            // Should never be thrown
            // unless a significant change if skin layers mod
            ETFUtils2.logError("Error with ETF's 3D skin layers mod hand compatibility: " + error);
        }
    }


// initial testing method
//    public static void tryRenderWithETFFeatures(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, FeatureRenderer featureRenderer, AbstractClientPlayerEntity entity, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float k, float m, float l, float n, float o) {
//        if (featureRenderer instanceof HeadLayerFeatureRenderer) {
//            new ETF3DHeadLayerFeatureRenderer<LivingEntity, EntityModel<T>>(context).render(matrixStack, vertexConsumerProvider, i, entity, o, n, g, l, k, m);
//        } else if (featureRenderer instanceof BodyLayerFeatureRenderer) {
//            new ETF3DBodyLayerFeatureRenderer(context).render(matrixStack, vertexConsumerProvider, i, entity, o, n, g, l, k, m);
//        }
//    }
}
