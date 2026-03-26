package traben.entity_texture_features.features.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

//#if MC >= 12111
//$$ import net.minecraft.client.model.object.skull.SkullModelBase;
//$$ import net.minecraft.client.model.player.PlayerModel;
//#endif

//#if MC >= 12105
import net.minecraft.world.entity.EquipmentSlot;
//#endif
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFUtils2;

//#if MC >= 12109
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

public class ETFPlayerFeatureRenderer<T extends AvatarRenderState, M extends PlayerModel> extends RenderLayer<T, M> {
//#elseif MC >= 12103
//$$ import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//$$ public class ETFPlayerFeatureRenderer<T extends PlayerRenderState, M extends PlayerModel> extends RenderLayer<T, M> {
//#else
//$$ import net.minecraft.world.entity.player.Player;
//$$
//$$ public class ETFPlayerFeatureRenderer<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M> {
//#endif
    protected static final ModelPart villagerNose = getModelData(new CubeDeformation(0)).getRoot().getChild("nose").bake(64, 64);
    protected static final ModelPart textureNose = getModelData(new CubeDeformation(0)).getRoot().getChild("textureNose").bake(8, 8);
    protected static final ModelPart jacket = getModelData(new CubeDeformation(0)).getRoot().getChild("jacket").bake(64, 64);
    protected static final ModelPart fatJacket = getModelData(new CubeDeformation(0)).getRoot().getChild("fatJacket").bake(64, 64);
    static private final ResourceLocation VILLAGER_TEXTURE = ETFUtils2.res("textures/entity/villager/villager.png");
    protected final ETFPlayerSkinHolder skinHolder;


    //public boolean sneaking;
    public ETFPlayerFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
        this.skinHolder = context instanceof ETFPlayerSkinHolder holder ? holder : null;
    }

    public static MeshDefinition getModelData(CubeDeformation dilation) {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 12.5F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.extend(0.25F)), PartPose.ZERO);
        modelPartData.addOrReplaceChild("fatJacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 12.5F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.extend(0.25F).extend(0.5F)), PartPose.ZERO);
        modelPartData.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        modelPartData.addOrReplaceChild("textureNose", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -8.0F, -8.0F, 0.0F, 8.0F, 4.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        return modelData;
    }

    public static void renderSkullFeatures(PoseStack matrixStack,
                                           //#if MC >= 12109
                                           SubmitNodeCollector vertexConsumerProvider,
                                           //#else
                                           //$$ MultiBufferSource vertexConsumerProvider,
                                           //#endif
                                           int light, SkullModelBase skullModel, ETFPlayerTexture playerTexture, float yaw) {
        ETFRenderContext.preventRenderLayerTextureModify();
        ETFRenderContext.startSpecialRenderOverlayPhase();

        if (playerTexture.hasVillagerNose || playerTexture.texturedNoseIdentifier != null) {
            villagerNose.yRot = yaw * 0.017453292F;
            villagerNose.xRot = 0;
            villagerNose.y = 0;
            textureNose.yRot = yaw * 0.017453292F;
            textureNose.xRot = 0;
            textureNose.y = 0;
            renderNose(matrixStack, vertexConsumerProvider, light, playerTexture);
        }
//        ETFPlayerFeatureRenderer.renderEmmisive(matrixStack, vertexConsumerProvider, playerTexture, skullModel);
        ETFPlayerFeatureRenderer.renderEnchanted(matrixStack, vertexConsumerProvider, light, playerTexture, skullModel);

        ETFRenderContext.endSpecialRenderOverlayPhase();
        ETFRenderContext.allowRenderLayerTextureModify();
    }

//    private static void renderEmmisive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, ETFPlayerTexture playerTexture, Model model) {
//        if (playerTexture.hasEmissives && playerTexture.etfTextureOfFinalBaseSkin != null) {
//            playerTexture.etfTextureOfFinalBaseSkin.renderEmissive(matrixStack, vertexConsumerProvider, model);
//        }
//    }

    private static void renderEnchanted(PoseStack matrixStack,
                                        //#if MC >= 12109
                                        SubmitNodeCollector submit,
                                        //#else
                                        //$$ MultiBufferSource vertexConsumerProvider,
                                        //#endif
                                        int light, ETFPlayerTexture playerTexture, Model model) {
        //#if MC >= 12109
        if (playerTexture.hasEnchant && playerTexture.baseEnchantIdentifier != null && playerTexture.etfTextureOfFinalBaseSkin != null) {
            ETFUtils2.submitEnchantedModelPart(matrixStack, submit, light, model.root(),playerTexture.baseEnchantIdentifier);
        }
        //#else
        //$$ if (playerTexture.hasEnchant && playerTexture.baseEnchantIdentifier != null && playerTexture.etfTextureOfFinalBaseSkin != null) {
        //$$     VertexConsumer enchantVert = ItemRenderer.getArmorFoilBuffer(vertexConsumerProvider, RenderType.armorCutoutNoCull(
        //$$             switch (playerTexture.etfTextureOfFinalBaseSkin.currentTextureState) {
        //$$                 case BLINK, BLINK_PATCHED, APPLY_BLINK -> playerTexture.baseEnchantBlinkIdentifier;
        //$$                 case BLINK2, BLINK2_PATCHED, APPLY_BLINK2 -> playerTexture.baseEnchantBlink2Identifier;
        //$$                 default -> playerTexture.baseEnchantIdentifier;
        //$$             }),
        //$$             //#if MC < 12100
        //$$             //$$ false,
        //$$             //#endif
        //$$             true);
        //$$    model.renderToBuffer(matrixStack, enchantVert, light, OverlayTexture.NO_OVERLAY
        //$$             //#if MC < 12100
        //$$             //$$ , 1F, 1F, 1F, 1F
        //$$             //#endif
        //$$             );
        //$$ }
        //#endif
    }

    private static void renderNose(PoseStack matrixStack,
                                   //#if MC >= 12109
                                   SubmitNodeCollector submit,
                                   //#else
                                   //$$ MultiBufferSource vertexConsumerProvider,
                                   //#endif
                                   int light, ETFPlayerTexture playerTexture) {
        if (playerTexture.hasVillagerNose) {
//            villagerNose.copyTransform(model.head);
            if (playerTexture.noseType == ETFConfigScreenSkinTool.NoseType.VILLAGER_TEXTURED || playerTexture.noseType == ETFConfigScreenSkinTool.NoseType.VILLAGER_TEXTURED_REMOVE) {

                var type =
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .entityTranslucent(playerTexture.etfTextureOfFinalBaseSkin.getTextureIdentifier(null));
                //#if MC >= 12109
                submit.submitModelPart(villagerNose, matrixStack, type,  light, OverlayTexture.NO_OVERLAY, null);
                var emissive = playerTexture.etfTextureOfFinalBaseSkin.getEmissiveRenderLayer(null);
                if (emissive != null) {
                    submit.submitModelPart(villagerNose, matrixStack, emissive,  ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, null);
                }
                //#else
                //$$ VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(type);
                //$$ villagerNose.render(matrixStack, villagerVert, light, OverlayTexture.NO_OVERLAY
                //$$         //#if MC < 12100
                //$$         //$$ , 1F, 1F, 1F, 1F
                //$$         //#endif
                //$$ );
                //$$ playerTexture.etfTextureOfFinalBaseSkin.renderEmissive(matrixStack, vertexConsumerProvider, villagerNose);
                //#endif
            } else {
                //#if MC >= 12109
                submit.submitModelPart(villagerNose, matrixStack,
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .entitySolid(VILLAGER_TEXTURE),  light, OverlayTexture.NO_OVERLAY, null);
                //#else
                //$$ VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(RenderType.entitySolid(VILLAGER_TEXTURE));
                //$$ villagerNose.render(matrixStack, villagerVert, light, OverlayTexture.NO_OVERLAY
                //$$         //#if MC < 12100
                //$$         //$$ , 1F, 1F, 1F, 1F
                //$$         //#endif
                //$$ );
                //#endif
            }
        } else if (playerTexture.texturedNoseIdentifier != null) {
//            textureNose.copyTransform(model.head);
            //#if MC >= 12109
            ETFUtils2.submitModelPart(matrixStack, submit, light,
                    textureNose,
                    playerTexture.texturedNoseIdentifier,
                    playerTexture.texturedNoseIdentifierEmissive,
                    playerTexture.texturedNoseIdentifierEnchanted
            );
            //#else
            //$$ VertexConsumer noseVertex = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(playerTexture.texturedNoseIdentifier));
            //$$ textureNose.render(matrixStack, noseVertex, light, OverlayTexture.NO_OVERLAY
            //$$         //#if MC < 12100
            //$$         //$$ , 1F, 1F, 1F, 1F
            //$$         //#endif
            //$$ );
            //$$ if (playerTexture.texturedNoseIdentifierEmissive != null) {
//                textureNose.copyTransform(model.head);
            //$$     VertexConsumer noseVertex_e;
            //$$     if (ETFManager.getEmissiveMode() == ETFConfig.EmissiveRenderModes.BRIGHT) {
            //$$         noseVertex_e = vertexConsumerProvider.getBuffer(RenderType.beaconBeam(playerTexture.texturedNoseIdentifierEmissive, true));
            //$$     } else {
            //$$         noseVertex_e = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(playerTexture.texturedNoseIdentifierEmissive));
            //$$     }
            //$$     textureNose.render(matrixStack, noseVertex_e, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY
            //$$             //#if MC < 12100
            //$$             //$$ , 1F, 1F, 1F, 1F
            //$$             //#endif
            //$$     );
            //$$ }
            //$$ if (playerTexture.texturedNoseIdentifierEnchanted != null) {
//                textureNose.copyTransform(model.head);
            //$$     VertexConsumer noseVertex_ench = ItemRenderer.getArmorFoilBuffer(vertexConsumerProvider, RenderType.armorCutoutNoCull(playerTexture.texturedNoseIdentifierEnchanted),
                        //#if MC < 12100
                        //$$ false,
                        //#endif
            //$$             true);
            //$$     textureNose.render(matrixStack, noseVertex_ench, light, OverlayTexture.NO_OVERLAY
            //$$             //#if MC < 12100
            //$$             //$$ , 1F, 1F, 1F, 1F
            //$$             //#endif
            //$$     );
            //$$ }
            //#endif
        }
    }




    //#if MC >= 12109
    @Override
        public void submit(final PoseStack matrices, final SubmitNodeCollector submit, final int light, final T entityRenderState, final float f, final float g) {
    //#elseif MC >= 12103
    //$$ @Override
    //$$ public void render(final PoseStack matrices, final MultiBufferSource submit, final int light, final T entityRenderState, final float f, final float g) {
    //#else
    //$$     @Override
    //$$     public void render(PoseStack matrices, MultiBufferSource submit, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    //#endif
        if (ETF.config().getConfig().skinFeaturesEnabled && skinHolder != null) {
            ETFRenderContext.preventRenderLayerTextureModify();

            ETFPlayerTexture playerTexture = skinHolder.etf$getETFPlayerTexture();
            if (playerTexture != null && playerTexture.hasFeatures) {

                renderFeatures(matrices, submit,
                        //#if MC >= 12109
                        entityRenderState,
                        //#endif
                        light, getParentModel(), playerTexture);
            }

            ETFRenderContext.allowRenderLayerTextureModify();
        }
    }

    public void renderFeatures(PoseStack matrixStack,
                               //#if MC >= 12109
                               SubmitNodeCollector vertexConsumerProvider,
                               T entityRenderState,
                               //#else
                               //$$ MultiBufferSource vertexConsumerProvider,
                               //#endif
                               int light, M model, ETFPlayerTexture playerTexture) {
        if (playerTexture.canUseFeaturesForThisPlayer()) {
            ETFRenderContext.startSpecialRenderOverlayPhase();

            if (playerTexture.hasVillagerNose || playerTexture.texturedNoseIdentifier != null) {
                matrixStack.pushPose();
                //#if MC >= 12109
                var head = model.head.getInitialPose();
                villagerNose.loadPose(head);
                textureNose.loadPose(head);
                model.head.translateAndRotate(matrixStack);
                //#else
                //$$ villagerNose.copyFrom(model.head);
                //$$ textureNose.copyFrom(model.head);
                //#endif
                renderNose(matrixStack, vertexConsumerProvider, light, playerTexture);
                matrixStack.popPose();
            }
            matrixStack.pushPose();
            renderCoat(matrixStack, vertexConsumerProvider,
                    //#if MC >= 12109
                    entityRenderState,
                    //#endif
                    light, playerTexture, model);
            matrixStack.popPose();

//            ETFPlayerFeatureRenderer.renderEmmisive(matrixStack, vertexConsumerProvider, playerTexture, model);
            //ETFPlayerFeatureRenderer.renderEnchanted(matrixStack, vertexConsumerProvider, light, playerTexture, model);

            ETFRenderContext.endSpecialRenderOverlayPhase();
        }
    }

    private void renderCoat(PoseStack matrixStack,
                            //#if MC >= 12109
                            SubmitNodeCollector submit,
                            T entityRenderState,
                            //#else
                            //$$ MultiBufferSource vertexConsumerProvider,
                            //#endif
                            int light, ETFPlayerTexture playerTexture, M model) {
        ItemStack armour = playerTexture.player.etf$getInventory()
                //#if MC >= 12105
                .getItem(EquipmentSlot.LEGS.getIndex(36));
                //#else
                //$$ .getArmor(1);
                //#endif
        if (playerTexture.coatIdentifier != null &&
                //#if MC >= 12109
                entityRenderState.showJacket &&
                //#else
                //$$ playerTexture.player.etf$isPartVisible(PlayerModelPart.JACKET) &&
                //#endif
                //#if MC >= 12006
                !(armour.is(ItemTags.LEG_ARMOR)) //todo check all versions for the else, might have just been written before i learned about tags
                //#else
                        //$$     !(armour.is(Items.CHAINMAIL_LEGGINGS) ||
                        //$$             armour.is(Items.LEATHER_LEGGINGS) ||
                        //$$         armour.is(Items.DIAMOND_LEGGINGS) ||
                        //$$         armour.is(Items.GOLDEN_LEGGINGS) ||
                        //$$         armour.is(Items.IRON_LEGGINGS) ||
                        //$$         armour.is(Items.NETHERITE_LEGGINGS))
                //#endif
        ) {
            //String coat = ETFPlayerSkinUtils.SKIN_NAMESPACE + id + "_coat.png";
            //#if MC >= 12109
            var part = playerTexture.hasFatCoat ? fatJacket : jacket;
            part.loadPose(model.body.getInitialPose());
            model.body.translateAndRotate(matrixStack);

            ETFUtils2.submitModelPart(matrixStack, submit, light,
                    part,
                    playerTexture.coatIdentifier,
                    playerTexture.coatEmissiveIdentifier,
                    playerTexture.coatEnchantedIdentifier
            );
            //#else
            //$$ if (playerTexture.hasFatCoat) {
            //$$     fatJacket.copyFrom(model.jacket);
            //$$ } else {
            //$$     jacket.copyFrom(model.jacket);
            //$$ }
            //$$ //perform texture features
            //$$ VertexConsumer coatVert = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(playerTexture.coatIdentifier));
            //$$ matrixStack.pushPose();
            //$$ if (playerTexture.hasFatCoat) {
            //$$     fatJacket.render(matrixStack, coatVert, light, OverlayTexture.NO_OVERLAY
            //$$             //#if MC < 12100
            //$$             //$$ , 1F, 1F, 1F, 1F
            //$$             //#endif
            //$$     );
            //$$ } else {
            //$$     jacket.render(matrixStack, coatVert, light, OverlayTexture.NO_OVERLAY
            //$$             //#if MC < 12100
            //$$             //$$ , 1F, 1F, 1F, 1F
            //$$             //#endif
            //$$     );
            //$$ }
            //$$ if (playerTexture.coatEnchantedIdentifier != null) {
            //$$     VertexConsumer enchantVert = ItemRenderer.getArmorFoilBuffer(vertexConsumerProvider, RenderType.armorCutoutNoCull(playerTexture.coatEnchantedIdentifier),
            //$$             //#if MC < 12100
            //$$             //$$ false,
            //$$             //#endif
            //$$             true);
            //$$     if (playerTexture.hasFatCoat) {
            //$$         fatJacket.render(matrixStack, enchantVert, light, OverlayTexture.NO_OVERLAY
            //$$                 //#if MC < 12100
            //$$                 //$$ , 1F, 1F, 1F, 1F
            //$$                 //#endif
            //$$         );
            //$$     } else {
            //$$         jacket.render(matrixStack, enchantVert, light, OverlayTexture.NO_OVERLAY
            //$$                 //#if MC < 12100
            //$$                 //$$ , 1F, 1F, 1F, 1F
            //$$                 //#endif
            //$$         );
            //$$     }
            //$$ }
            //$$ if (playerTexture.coatEmissiveIdentifier != null) {
            //$$     VertexConsumer emissiveVert;// = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
            //$$     if (ETFManager.getEmissiveMode() == ETFConfig.EmissiveRenderModes.BRIGHT) {
            //$$         emissiveVert = vertexConsumerProvider.getBuffer(RenderType.beaconBeam(playerTexture.coatEmissiveIdentifier, true));
            //$$     } else {
            //$$         emissiveVert = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(playerTexture.coatEmissiveIdentifier));
            //$$     }
            //$$     if (playerTexture.hasFatCoat) {
            //$$         fatJacket.render(matrixStack, emissiveVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY
            //$$                 //#if MC < 12100
            //$$                 //$$ , 1F, 1F, 1F, 1F
            //$$                 //#endif
            //$$         );
            //$$     } else {
            //$$         jacket.render(matrixStack, emissiveVert, ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY
            //$$                 //#if MC < 12100
            //$$                 //$$ , 1F, 1F, 1F, 1F
            //$$                 //#endif
            //$$         );
            //$$     }
            //$$ }
            //$$ matrixStack.popPose();
            //#endif
        }
    }

}
