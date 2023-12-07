package traben.entity_texture_features.features.player;

import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class ETFPlayerFeatureRenderer<T extends PlayerEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {
    static private final Identifier VILLAGER_TEXTURE = new Identifier("textures/entity/villager/villager.png");
    protected final ETFPlayerSkinHolder skinHolder;


    protected static final ModelPart villagerNose = getModelData(new Dilation(0)).getRoot().getChild("nose").createPart(64, 64);
    protected static final ModelPart textureNose = getModelData(new Dilation(0)).getRoot().getChild("textureNose").createPart(8, 8);
    protected static final ModelPart jacket = getModelData(new Dilation(0)).getRoot().getChild("jacket").createPart(64, 64);
    protected static final ModelPart fatJacket = getModelData(new Dilation(0)).getRoot().getChild("fatJacket").createPart(64, 64);

    //public boolean sneaking;
    public ETFPlayerFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
        this.skinHolder = context instanceof ETFPlayerSkinHolder holder ? holder : null;

//        ModelPartData data = getModelData(new Dilation(0)).getRoot();
//        this.jacket = data.getChild("jacket").createPart(64, 64);
//        this.fatJacket = data.getChild("fatJacket").createPart(64, 64);
//        this.villagerNose = data.getChild("nose").createPart(64, 64); //23   15
//        this.textureNose = data.getChild("textureNose").createPart(8, 8); //23   15
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 12.5F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE);
        modelPartData.addChild("fatJacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 12.5F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F).add(0.5F)), ModelTransform.NONE);
        modelPartData.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
        modelPartData.addChild("textureNose", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -8.0F, -8.0F, 0.0F, 8.0F, 4.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
        return modelData;
    }

    public static void renderSkullFeatures(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, SkullBlockEntityModel skullModel, ETFPlayerTexture playerTexture, float yaw) {
        ETFRenderContext.preventRenderLayerTextureModify();
        ETFRenderContext.startSpecialRenderOverlayPhase();

        if (playerTexture.hasVillagerNose || playerTexture.texturedNoseIdentifier != null) {
            villagerNose.yaw = yaw * 0.017453292F;
            villagerNose.pitch = 0;
            villagerNose.pivotY = 0;
            textureNose.yaw = yaw * 0.017453292F;
            textureNose.pitch = 0;
            textureNose.pivotY = 0;
            renderNose(matrixStack, vertexConsumerProvider, light, playerTexture);
        }
//        ETFPlayerFeatureRenderer.renderEmmisive(matrixStack, vertexConsumerProvider, playerTexture, skullModel);
        ETFPlayerFeatureRenderer.renderEnchanted(matrixStack, vertexConsumerProvider, light, playerTexture, skullModel);

        ETFRenderContext.endSpecialRenderOverlayPhase();
        ETFRenderContext.allowRenderLayerTextureModify();
    }

    private static void renderEmmisive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, ETFPlayerTexture playerTexture, Model model) {
        if (playerTexture.hasEmissives && playerTexture.etfTextureOfFinalBaseSkin != null) {
            playerTexture.etfTextureOfFinalBaseSkin.renderEmissive(matrixStack, vertexConsumerProvider, model);
        }
    }

    private static void renderEnchanted(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, ETFPlayerTexture playerTexture, Model model) {
        if (playerTexture.hasEnchant && playerTexture.baseEnchantIdentifier != null && playerTexture.etfTextureOfFinalBaseSkin != null) {
            VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(
                    switch (playerTexture.etfTextureOfFinalBaseSkin.currentTextureState) {
                        case BLINK, BLINK_PATCHED, APPLY_BLINK -> playerTexture.baseEnchantBlinkIdentifier;
                        case BLINK2, BLINK2_PATCHED, APPLY_BLINK2 -> playerTexture.baseEnchantBlink2Identifier;
                        default -> playerTexture.baseEnchantIdentifier;
                    }), false, true);
            model.render(matrixStack, enchantVert, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

        if (ETFConfigData.skinFeaturesEnabled && skinHolder != null) {
            ETFRenderContext.preventRenderLayerTextureModify();

            ETFPlayerTexture playerTexture = skinHolder.etf$getETFPlayerTexture();
            if (playerTexture != null && playerTexture.hasFeatures) {

                renderFeatures(matrices, vertexConsumers, light, getContextModel(), playerTexture);
            }

            ETFRenderContext.allowRenderLayerTextureModify();
        }
    }

    public void renderFeatures(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, M model, ETFPlayerTexture playerTexture) {
        if (playerTexture.canUseFeaturesForThisPlayer()) {
            ETFRenderContext.startSpecialRenderOverlayPhase();

            if (playerTexture.hasVillagerNose || playerTexture.texturedNoseIdentifier != null) {
                villagerNose.copyTransform(model.head);
                textureNose.copyTransform(model.head);
                renderNose(matrixStack, vertexConsumerProvider, light, playerTexture);
            }
            renderCoat(matrixStack, vertexConsumerProvider, light, playerTexture, model);

//            ETFPlayerFeatureRenderer.renderEmmisive(matrixStack, vertexConsumerProvider, playerTexture, model);
            ETFPlayerFeatureRenderer.renderEnchanted(matrixStack, vertexConsumerProvider, light, playerTexture, model);

            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    private static void renderNose(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, ETFPlayerTexture playerTexture) {
        if (playerTexture.hasVillagerNose) {
//            villagerNose.copyTransform(model.head);
            if (playerTexture.noseType == ETFConfigScreenSkinTool.NoseType.VILLAGER_TEXTURED) {
                VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(playerTexture.etfTextureOfFinalBaseSkin.getTextureIdentifier(null)));
                villagerNose.render(matrixStack, villagerVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                playerTexture.etfTextureOfFinalBaseSkin.renderEmissive(matrixStack, vertexConsumerProvider, villagerNose);
            } else {
                VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(VILLAGER_TEXTURE));
                villagerNose.render(matrixStack, villagerVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        } else if (playerTexture.texturedNoseIdentifier != null) {
//            textureNose.copyTransform(model.head);
            VertexConsumer noseVertex = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(playerTexture.texturedNoseIdentifier));
            textureNose.render(matrixStack, noseVertex, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            if (playerTexture.texturedNoseIdentifierEmissive != null) {
//                textureNose.copyTransform(model.head);
                VertexConsumer noseVertex_e;
                if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                    noseVertex_e = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(playerTexture.texturedNoseIdentifierEmissive, true));
                } else {
                    noseVertex_e = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(playerTexture.texturedNoseIdentifierEmissive));
                }
                textureNose.render(matrixStack, noseVertex_e, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (playerTexture.texturedNoseIdentifierEnchanted != null) {
//                textureNose.copyTransform(model.head);
                VertexConsumer noseVertex_ench = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(playerTexture.texturedNoseIdentifierEnchanted), false, true);
                textureNose.render(matrixStack, noseVertex_ench, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderCoat(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, ETFPlayerTexture playerTexture, M model) {
        ItemStack armour = playerTexture.player.etf$getInventory().getArmorStack(1);
        if (playerTexture.coatIdentifier != null &&
                playerTexture.player.etf$isPartVisible(PlayerModelPart.JACKET) &&
                !(armour.isOf(Items.CHAINMAIL_LEGGINGS) ||
                        armour.isOf(Items.LEATHER_LEGGINGS) ||
                        armour.isOf(Items.DIAMOND_LEGGINGS) ||
                        armour.isOf(Items.GOLDEN_LEGGINGS) ||
                        armour.isOf(Items.IRON_LEGGINGS) ||
                        armour.isOf(Items.NETHERITE_LEGGINGS))
        ) {
            //String coat = ETFPlayerSkinUtils.SKIN_NAMESPACE + id + "_coat.png";

            if (playerTexture.hasFatCoat) {
                fatJacket.copyTransform(model.jacket);
            } else {
                jacket.copyTransform(model.jacket);
            }
            //perform texture features
            VertexConsumer coatVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(playerTexture.coatIdentifier));
            matrixStack.push();
            if (playerTexture.hasFatCoat) {
                fatJacket.render(matrixStack, coatVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                jacket.render(matrixStack, coatVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            if (playerTexture.coatEnchantedIdentifier != null) {
                VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(playerTexture.coatEnchantedIdentifier), false, true);
                if (playerTexture.hasFatCoat) {
                    fatJacket.render(matrixStack, enchantVert, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
                } else {
                    jacket.render(matrixStack, enchantVert, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1f);
                }
            }


            if (playerTexture.coatEmissiveIdentifier != null) {
                VertexConsumer emissiveVert;// = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                    emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(playerTexture.coatEmissiveIdentifier, true));
                } else {
                    emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(playerTexture.coatEmissiveIdentifier));
                }

                if (playerTexture.hasFatCoat) {
                    fatJacket.render(matrixStack, emissiveVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    jacket.render(matrixStack, emissiveVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }

            matrixStack.pop();
        }
    }

}
