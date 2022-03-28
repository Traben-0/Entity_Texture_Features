package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;
import traben.entity_texture_features.client.customPlayerFeatureModel;
import traben.entity_texture_features.config.ETFConfig;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class MIX_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>, ETF_METHODS {
    @Shadow
    public abstract M getModel();

    private final customPlayerFeatureModel customPlayerModel = new customPlayerFeatureModel<>();

    protected MIX_LivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);

    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER)
    )
    private void ETF_applyRenderFeatures(T livingEntity, float a, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        UUID id = livingEntity.getUuid();
        if (!(livingEntity instanceof PlayerEntity)) {

            Identifier texture = ETF_returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity);
            ETF_GeneralEmissiveRender(matrixStack, vertexConsumerProvider, texture, this.getModel());

        } else if (ETFConfigData.skinFeaturesEnabled) { // is a player
            ETF_renderSkinFeatures(id, (PlayerEntity) livingEntity, matrixStack, vertexConsumerProvider, i);
        }
        //potion effects
        if (ETFConfigData.enchantedPotionEffects != ETFConfig.enchantedPotionEffectsEnum.NONE
                && !livingEntity.getActiveStatusEffects().isEmpty()
                && !livingEntity.hasStatusEffect(StatusEffects.INVISIBILITY)
        ) {
            ETF_renderPotion(livingEntity, matrixStack, vertexConsumerProvider);
        }

        //randomly mark texture for rechecking randomized by UUID
        if (ETFConfigData.enableCustomTextures && ETFConfigData.textureUpdateFrequency_V2 != ETFConfig.updateFrequency.Never) {

            int delay = switch (ETFConfigData.textureUpdateFrequency_V2) {
                case Fast -> 5;
                case Slow -> 80;
                case Instant -> 1;
                default -> -1;
            };
            long randomizer = delay * 20L;
            if (livingEntity.world.isClient()
                    && System.currentTimeMillis() % randomizer == Math.abs(id.hashCode()) % randomizer
            ) {
                if (!ETF_UUID_entityAwaitingDataClearing.containsKey(id)) {
                    ETF_UUID_entityAwaitingDataClearing.put(id, System.currentTimeMillis());
                }
                if (ETF_UUID_randomTextureSuffix2.containsKey(id)) {
                    if (!ETF_UUID_entityAwaitingDataClearing2.containsKey(id)) {
                        ETF_UUID_entityAwaitingDataClearing2.put(id, System.currentTimeMillis());
                    }
                }
            }
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier ETF_returnAlteredTexture(@SuppressWarnings("rawtypes") LivingEntityRenderer instance, Entity inEntity) {
        @SuppressWarnings("unchecked")
        T entity = (T) inEntity;
        Identifier vanilla = getTexture(entity);

        //this is to support inspectio or other abstract rendering mods
        if (inEntity.getBlockStateAtPos() == null) {
            return vanilla;
        } else if (inEntity.getBlockStateAtPos().isOf(Blocks.VOID_AIR)) {
            return vanilla;
        }


        String path = vanilla.toString();
        UUID id = entity.getUuid();

        if (!(entity instanceof PlayerEntity)) {
            if (ETFConfigData.enableCustomTextures) {
                try {
                    if (!ETF_PATH_OptifineOrTrueRandom.containsKey(path)) {
                        ETF_processNewRandomTextureCandidate(path);
                    }
                    if (ETF_PATH_OptifineOrTrueRandom.containsKey(path)) {
                        //if needs to check if change required
                        if (ETF_UUID_entityAwaitingDataClearing.containsKey(id)) {
                            if (ETF_UUID_randomTextureSuffix.containsKey(id)) {
                                if (!ETF_UUID_hasUpdatableRandomCases.containsKey(id)) {
                                    ETF_UUID_hasUpdatableRandomCases.put(id, true);
                                }
                                if (ETF_UUID_hasUpdatableRandomCases.get(id)) {
                                    //skip a few ticks
                                    //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                                    if (ETF_UUID_entityAwaitingDataClearing.get(id) + 100 < System.currentTimeMillis()) {
                                        if (ETF_PATH_OptifineOrTrueRandom.get(path)) {
                                            //if (UUID_randomTextureSuffix.containsKey(id)) {
                                            int hold = ETF_UUID_randomTextureSuffix.get(id);
                                            ETF_resetSingleData(id);
                                            ETF_testCases(path, id, entity, true, ETF_UUID_randomTextureSuffix, ETF_UUID_hasUpdatableRandomCases);
                                            //if didnt change keep the same
                                            if (!ETF_UUID_randomTextureSuffix.containsKey(id)) {
                                                ETF_UUID_randomTextureSuffix.put(id, hold);
                                            }
                                            //}
                                        }//else here would do something for true random but no need really - may optimise this

                                        ETF_UUID_entityAwaitingDataClearing.remove(id);
                                    }
                                } else {
                                    ETF_UUID_entityAwaitingDataClearing.remove(id);
                                }
                            }

                        }
                        if (ETF_PATH_OptifineOrTrueRandom.get(path)) {//optifine random
                            //if it doesn't have a random already assign one
                            if (!ETF_UUID_randomTextureSuffix.containsKey(id)) {
                                ETF_testCases(path, id, entity, false);
                                //if all failed set to vanilla
                                if (!ETF_UUID_randomTextureSuffix.containsKey(id)) {
                                    ETF_UUID_randomTextureSuffix.put(id, 0);
                                }
                                ETF_UUID_entityAlreadyCalculated.add(id);
                            }
                            // System.out.println("suffix was ="+UUID_randomTextureSuffix.get(id));
                            if (ETF_UUID_randomTextureSuffix.get(id) == 0) {
                                if (!ETF_PATH_HasOptifineDefaultReplacement.containsKey(vanilla.toString())) {
                                    ETF_PATH_HasOptifineDefaultReplacement.put(vanilla.toString(), ETF_isExistingFile(ETF_returnOptifineOrVanillaIdentifier(path)));
                                }
                                if (ETF_PATH_HasOptifineDefaultReplacement.get(vanilla.toString())) {
                                    return ETF_returnBlinkIdOrGiven(entity, ETF_returnOptifineOrVanillaIdentifier(path).toString(), id);
                                } else {
                                    return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                                }

                            } else {
                                return ETF_returnBlinkIdOrGiven(entity, ETF_returnOptifineOrVanillaIdentifier(path, ETF_UUID_randomTextureSuffix.get(id)).toString(), id);
                            }

                        } else {//true random assign
                            ETF_UUID_hasUpdatableRandomCases.put(id, false);
                            if (ETF_PATH_TotalTrueRandom.get(path) > 0) {
                                if (!ETF_UUID_randomTextureSuffix.containsKey(id)) {
                                    int randomReliable = Math.abs(id.hashCode());
                                    randomReliable %= ETF_PATH_TotalTrueRandom.get(path);
                                    randomReliable++;
                                    if (randomReliable == 1 && ETF_PATH_ignoreOnePNG.get(path)) {
                                        randomReliable = 0;
                                    }
                                    ETF_UUID_randomTextureSuffix.put(id, randomReliable);
                                    ETF_UUID_entityAlreadyCalculated.add(id);
                                }
                                if (ETF_UUID_randomTextureSuffix.get(id) == 0) {
                                    return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                                } else {
                                    return ETF_returnBlinkIdOrGiven(entity, ETF_returnOptifineOrVanillaPath(path, ETF_UUID_randomTextureSuffix.get(id), ""), id);
                                }
                            } else {
                                return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                            }
                        }
                    } else {
                        ETF_modMessage("not random", false);
                        return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                    }

                } catch (Exception e) {
                    ETF_modMessage(e.toString(), false);
                    return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                }
            }
        } else { // is player
            if (ETFConfigData.skinFeaturesEnabled) {
                if (ETF_timerBeforeTrySkin > 0) {
                    ETF_timerBeforeTrySkin--;
                } else {
                    if (!ETF_UUID_playerHasFeatures.containsKey(id) && !ETF_UUID_playerSkinDownloadedYet.containsKey(id)) {
                        ETF_checkPlayerForSkinFeatures(id, (PlayerEntity) entity);
                    }
                    if (ETF_UUID_playerSkinDownloadedYet.containsKey(id) && ETF_UUID_playerHasFeatures.containsKey(id)) {
                        if (ETF_UUID_playerSkinDownloadedYet.get(id)) {
                            if (ETF_UUID_playerHasFeatures.get(id)) {
                                return ETF_returnBlinkIdOrGiven(entity, ETF_SKIN_NAMESPACE + id + ".png", id, true);
                            } else {
                                return vanilla;
                            }
                        }
                    }
                }
            }
        }

        return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
    }


    private void ETF_renderPotion(T livingEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        VertexConsumer textureVert;
        switch (ETFConfigData.enchantedPotionEffects) {
            case ENCHANTED -> {
                textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(ETF_returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity)), false, true);
                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
            }
            case GLOWING -> {
                textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(ETF_returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity), true));
                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
            }
            case CREEPER_CHARGE -> {
                int f = (int) ((float) livingEntity.world.getTime() / 10);
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEnergySwirl(new Identifier("textures/entity/creeper/creeper_armor.png"), f * 0.01F % 1.0F, f * 0.01F % 1.0F));
                matrixStack.scale(1.1f, 1.1f, 1.1f);
                this.getModel().render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 0.5F, 0.5F, 0.5F, 0.5F);
                matrixStack.scale(1f, 1f, 1f);
            }
        }
    }

    //double it
    private int ETF_timerBeforeTrySkin = 400;

    private void ETF_renderSkinFeatures(UUID id, PlayerEntity player, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        //skin http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888

        //test area


        /////////////////////////////////////////////
        String skinPossiblyBlinking = ETF_returnAlteredTexture((LivingEntityRenderer) (Object) this, player).toString();
        if (skinPossiblyBlinking.contains("_transparent")) {
            skinPossiblyBlinking = skinPossiblyBlinking.replace("_transparent", "");
        }
        if (ETF_timerBeforeTrySkin > 0) {
            ETF_timerBeforeTrySkin--;
        } else {
            if (!ETF_UUID_playerHasFeatures.containsKey(id) && !ETF_UUID_playerSkinDownloadedYet.containsKey(id)) {
                //check for mark
                ETF_checkPlayerForSkinFeatures(id, player);
            }
            if (ETF_UUID_playerHasFeatures.containsKey(id) && ETF_UUID_playerSkinDownloadedYet.containsKey(id)) {
                if (ETF_UUID_playerSkinDownloadedYet.get(id)) {
                    if (ETF_UUID_playerHasFeatures.get(id)) {

                        //villager nose
                        if (ETF_UUID_playerHasVillagerNose.get(id)) {

                            customPlayerModel.nose.copyTransform(((PlayerEntityModel) this.getModel()).head);
                            Identifier villager = new Identifier("textures/entity/villager/villager.png");
                            VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(villager));
                            customPlayerModel.nose.render(matrixStack, villagerVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        }

                        //coat features
                        ItemStack armour = player.getInventory().getArmorStack(1);
                        if (ETF_UUID_playerHasCoat.get(id) &&
                                player.isPartVisible(PlayerModelPart.JACKET) &&
                                !(armour.isOf(Items.CHAINMAIL_LEGGINGS) ||
                                        armour.isOf(Items.LEATHER_LEGGINGS) ||
                                        armour.isOf(Items.DIAMOND_LEGGINGS) ||
                                        armour.isOf(Items.GOLDEN_LEGGINGS) ||
                                        armour.isOf(Items.IRON_LEGGINGS) ||
                                        armour.isOf(Items.NETHERITE_LEGGINGS))
                        ) {
                            String coat = ETF_SKIN_NAMESPACE + id + "_coat.png";

                            if (ETF_UUID_playerHasFatCoat.get(id)) {
                                customPlayerModel.fatJacket.copyTransform(((PlayerEntityModel) this.getModel()).jacket);
                            } else {
                                customPlayerModel.jacket.copyTransform(((PlayerEntityModel) this.getModel()).jacket);
                            }
                            //perform texture features
                            VertexConsumer coatVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(coat)));
                            matrixStack.push();


                            if (ETF_UUID_playerHasFatCoat.get(id)) {
                                customPlayerModel.fatJacket.render(matrixStack, coatVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            } else {
                                customPlayerModel.jacket.render(matrixStack, coatVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            }
                            if (ETF_UUID_playerHasEnchant.get(id)) {
                                Identifier enchant = new Identifier(coat.replace(".png", "_enchant.png"));
                                VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(enchant), false, true);

                                if (ETF_UUID_playerHasFatCoat.get(id)) {
                                    customPlayerModel.fatJacket.render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                                } else {
                                    customPlayerModel.jacket.render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                                }
                            }
                            if (ETF_UUID_playerHasEmissive.get(id)) {
                                Identifier emissive = new Identifier(coat.replace(".png", "_e.png"));
                                VertexConsumer emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                                if (ETFConfigData.doShadersEmissiveFix) {
                                    matrixStack.scale(1.01f, 1.01f, 1.01f);
                                }
                                if (ETF_UUID_playerHasFatCoat.get(id)) {
                                    customPlayerModel.fatJacket.render(matrixStack, emissVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                                } else {
                                    customPlayerModel.jacket.render(matrixStack, emissVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                                }
                                if (ETFConfigData.doShadersEmissiveFix) {
                                    matrixStack.scale(1f, 1f, 1f);
                                }

                            }
                            matrixStack.pop();
                        }

                        //perform texture features
                        if (ETF_UUID_playerHasEnchant.get(id)) {
                            Identifier enchant = skinPossiblyBlinking.contains(".png") ?
                                    new Identifier(skinPossiblyBlinking.replace(".png", "_enchant.png")) :
                                    new Identifier(ETF_SKIN_NAMESPACE + id + "_enchant.png");
                            VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(enchant), false, true);
                            this.getModel().render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                        }
                        if (ETF_UUID_playerHasEmissive.get(id)) {
                            Identifier emissive = skinPossiblyBlinking.contains(".png") ?
                                    new Identifier(skinPossiblyBlinking.replace(".png", "_e.png")) :
                                    new Identifier(ETF_SKIN_NAMESPACE + id + "_e.png");
                            VertexConsumer emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                            if (ETFConfigData.doShadersEmissiveFix) {
                                matrixStack.scale(1.01f, 1.01f, 1.01f);
                                this.getModel().render(matrixStack, emissVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                                matrixStack.scale(1f, 1f, 1f);
                            } else {
                                this.getModel().render(matrixStack, emissVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            }

                        }
                    }
                }
            }

        }
    }

}


