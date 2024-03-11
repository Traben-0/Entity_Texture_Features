package traben.entity_texture_features.compat;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import dev.tr7zw.skinlayers.SkinUtil;
import dev.tr7zw.skinlayers.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.skinlayers.accessor.PlayerSettings;
import dev.tr7zw.skinlayers.api.Mesh;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;

// compatibility class for the wonderful 3D skin layers mod by @tr9zw to utilise ETF skin features
public abstract class ETF3DSkinLayersUtil {


    // todo ensure rendering method is kept up to date with how 3D skin layers mod does it
    // copy of 3D skin layers hand rendering code as ETF needs to be able to render it at will with a custom VertexConsumer
    public static void renderHand(PlayerEntityRenderer instance, MatrixStack poseStack, VertexConsumer vertexConsumer, int i, AbstractClientPlayerEntity abstractClientPlayer, ModelPart arm, ModelPart sleeve)
            throws NoClassDefFoundError {
        boolean rightSleeve;
        label59:
        {
            rightSleeve = (instance.getModel()).leftSleeve != sleeve;
            if (rightSleeve) {
                if (SkinLayersModBase.config.enableRightSleeve) {
                    break label59;
                }
            } else if (SkinLayersModBase.config.enableLeftSleeve) {
                break label59;
            }

            return;
        }

        sleeve.visible = false;
        if (abstractClientPlayer.isPartVisible(rightSleeve ? PlayerModelPart.RIGHT_SLEEVE : PlayerModelPart.LEFT_SLEEVE)) {
            PlayerSettings settings = (PlayerSettings) abstractClientPlayer;
            float armHeightScaling = 1.1F;
            boolean thinArms = ((PlayerEntityModelAccessor) instance.getModel()).hasThinArms();
            if (SkinUtil.setup3dLayers(abstractClientPlayer, settings, thinArms, instance.getModel())) {
                Mesh part = sleeve == (instance.getModel()).leftSleeve ? settings.getLeftArmMesh() : settings.getRightArmMesh();
                part.copyFrom(arm);
                poseStack.push();
                poseStack.scale(SkinLayersModBase.config.firstPersonPixelScaling, armHeightScaling, SkinLayersModBase.config.firstPersonPixelScaling);
                boolean left = sleeve == (instance.getModel()).leftSleeve;
                float x = left ? 5.0F : -5.0F;
                float y = 1.4F;
                double scaleOffset = ((double) SkinLayersModBase.config.firstPersonPixelScaling - 1.1) * 5.0;
                if (left) {
                    x = (float) ((double) x - scaleOffset);
                } else {
                    x = (float) ((double) x + scaleOffset);
                }

                if (!thinArms) {
                    if (left) {
                        x = (float) ((double) x + 0.45);
                    } else {
                        x = (float) ((double) x - 0.45);
                    }
                }

                part.setPosition(x, y, 0.0F);
                part.render(poseStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                part.setPosition(0.0F, 0.0F, 0.0F);
                part.setRotation(0.0F, 0.0F, 0.0F);
                poseStack.pop();
            }
        }
    }
}
