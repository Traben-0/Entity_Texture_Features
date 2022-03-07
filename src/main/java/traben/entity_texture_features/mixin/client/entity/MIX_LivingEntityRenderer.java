package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;
import traben.entity_texture_features.config.ETFConfig;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class MIX_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>, ETF_METHODS {
    @Shadow
    public abstract M getModel();


    protected MIX_LivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void applyRenderFeatures(T livingEntity, float a, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        UUID id = livingEntity.getUuid();
        if (!(livingEntity instanceof PlayerEntity)) {

            String fileString = returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity).toString();

            if (ETFConfigData.enableEmissiveTextures) {
                if (Texture_Emissive.containsKey(fileString)) {
                    if (Texture_Emissive.get(fileString) != null) {
                        //VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(Texture_Emissive.get(fileString)));
                        VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString), true));
                        //one check most efficient instead of before and after applying
                        if (ETFConfigData.doShadersEmissiveFix) {
                            matrixStack.scale(1.01f, 1.01f, 1.01f);
                            this.getModel().render(matrixStack
                                    , textureVert
                                    , 15728640
                                    , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            matrixStack.scale(1f, 1f, 1f);
                        } else {
                            this.getModel().render(matrixStack
                                    , textureVert
                                    , 15728640
                                    , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        }
                    }
                } else {//creates and sets emissive for texture if it exists
                    Identifier fileName_e;
                    for (String suffix1 :
                            emissiveSuffix) {
                        fileName_e = new Identifier(fileString.replace(".png", suffix1 + ".png"));
                        if (isExistingFile(fileName_e)) {
                            VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(fileName_e, true));
                            Texture_Emissive.put(fileString, fileName_e);
                            //one check most efficient instead of before and after applying
                            if (ETFConfigData.doShadersEmissiveFix) {
                                matrixStack.scale(1.01f, 1.01f, 1.01f);
                                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                                matrixStack.scale(1f, 1f, 1f);
                            } else {
                                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            }
                            break;
                        }
                    }
                    if (!Texture_Emissive.containsKey(fileString)) {
                        Texture_Emissive.put(fileString, null);
                    }
                }
            }
        } else if (ETFConfigData.skinFeaturesEnabled) { // is a player
            renderSkinFeatures(id, livingEntity, matrixStack, vertexConsumerProvider);
        }
        //potion effects
        if (ETFConfigData.enchantedPotionEffects != ETFConfig.enchantedPotionEffectsEnum.NONE
                && !livingEntity.getActiveStatusEffects().isEmpty()
                && !livingEntity.hasStatusEffect(StatusEffects.INVISIBILITY)
        ) {
            renderPotion(livingEntity, matrixStack, vertexConsumerProvider);
        }

        //randomly mark texture for rechecking randomized by UUID
        long randomizer = ETFConfigData.textureUpdateFrequency * 20L;
        if (livingEntity.world.isClient()
                && System.currentTimeMillis() % randomizer == Math.abs(id.hashCode()) % randomizer
        ) {
            if (hasUpdatableRandomCases.containsKey(id)) {
                if (hasUpdatableRandomCases.get(id)
                        && !UUID_entityAwaitingDataClearing.containsKey(id)) {
                    UUID_entityAwaitingDataClearing.put(id, System.currentTimeMillis());
                }
            } else {
                if (!UUID_entityAwaitingDataClearing.containsKey(id)) {
                    UUID_entityAwaitingDataClearing.put(id, System.currentTimeMillis());
                }
            }
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier returnAlteredTexture(@SuppressWarnings("rawtypes") LivingEntityRenderer instance, Entity inEntity) {
        @SuppressWarnings("unchecked")
        T entity = (T) inEntity;
        Identifier vanilla = getTexture(entity);
        String path = vanilla.toString();
        UUID id = entity.getUuid();
        if (!(entity instanceof PlayerEntity)) {
            if (ETFConfigData.enableRandomTextures) {
                try {
                    if (!Texture_OptifineOrTrueRandom.containsKey(path)) {
                        processNewRandomTextureCandidate(path);
                    }
                    //if needs to check if change required
                    if (UUID_entityAwaitingDataClearing.containsKey(id)) {
                        if (!hasUpdatableRandomCases.containsKey(id)) {
                            //modMessage("Error - mob will no longer have texture updated",false);
                            hasUpdatableRandomCases.put(id, false);
                            UUID_entityAwaitingDataClearing.remove(id);
                        }
                        if (hasUpdatableRandomCases.get(id)) {
                            //skip a few ticks
                            //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                            if ((UUID_entityAwaitingDataClearing.get(id) / 100) + 1 < (System.currentTimeMillis() / 100)) {
                                if (Texture_OptifineOrTrueRandom.get(path)) {
                                    int hold = UUID_randomTextureSuffix.get(id);
                                    resetSingleData(id);
                                    testCases(path, id, entity,true);
                                    //if didnt change keep the same
                                    if (!UUID_randomTextureSuffix.containsKey(id)) {
                                        UUID_randomTextureSuffix.put(id, hold);
                                    }
                                }//else here would do something for true random but no need really - may optimise this
                                UUID_entityAwaitingDataClearing.remove(id);
                            }

                        } else {
                            UUID_entityAwaitingDataClearing.remove(id);
                        }

                    }
                    if (Texture_OptifineOrTrueRandom.get(path)) {//optifine random
                        //if it doesn't have a random already assign one
                        if (!UUID_randomTextureSuffix.containsKey(id)) {
                            testCases(path, id, entity,false);
                            //if all failed set to vanilla
                            if (!UUID_randomTextureSuffix.containsKey(id)) {
                                UUID_randomTextureSuffix.put(id, 0);
                            }
                            if (!UUID_entityAlreadyCalculated.contains(id)) {
                                UUID_entityAlreadyCalculated.add(id);
                            }
                        }
                        if (UUID_randomTextureSuffix.get(id) == 0) {
                            return returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        } else {
                            return returnBlinkIdOrGiven(entity, returnOptifineOrVanillaIdentifier(path, UUID_randomTextureSuffix.get(id)).toString(), id);
                        }

                    } else {//true random assign
                        hasUpdatableRandomCases.put(id, false);
                        if (Texture_TotalTrueRandom.get(path) > 0) {
                            if (!UUID_randomTextureSuffix.containsKey(id)) {
                                int randomReliable = Math.abs(id.hashCode());
                                randomReliable %= Texture_TotalTrueRandom.get(path);
                                randomReliable++;
                                if (randomReliable == 1 && ignoreOnePNG.get(path)) {
                                    randomReliable = 0;
                                }
                                UUID_randomTextureSuffix.put(id, randomReliable);
                                if (!UUID_entityAlreadyCalculated.contains(id)) {
                                    UUID_entityAlreadyCalculated.add(id);
                                }
                            }
                            if (UUID_randomTextureSuffix.get(id) == 0) {
                                return returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                            } else {
                                return returnBlinkIdOrGiven(entity, returnOptifineOrVanillaPath(path, UUID_randomTextureSuffix.get(id), ""), id);
                            }
                        } else {
                            return returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        }
                    }

                } catch (Exception e) {
                    modMessage(e.toString(), false);
                    return returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                }
            }
        } else {
            if (!UUID_playerHasFeatures.containsKey(id) && !UUID_playerSkinDownloadedYet.containsKey(id)) {
                checkPlayerForSkinFeatures(id, (PlayerEntity)entity);
            }
            if (UUID_playerSkinDownloadedYet.get(id)) {
                if (UUID_playerHasFeatures.get(id)) {

                    return returnBlinkIdOrGiven(entity, SKIN_NAMESPACE + id + ".png", id, true);
                } else {
                    return vanilla;
                }
            }
        }
        return returnBlinkIdOrGiven(entity, vanilla.toString(), id);
    }

    private Identifier returnBlinkIdOrGiven(T entity, String givenTexturePath, UUID id) {
        return returnBlinkIdOrGiven(entity, givenTexturePath, id, false);
    }

    private Identifier returnBlinkIdOrGiven(T entity, String givenTexturePath, UUID id, boolean isPlayer) {
        if (ETFConfigData.enableBlinking) {
            if (!UUID_HasBlink.containsKey(id)) {
                //check for blink textures
                UUID_HasBlink.put(id, isExistingFile(new Identifier(givenTexturePath.replace(".png", "_blink.png"))));
                UUID_HasBlink2.put(id, isExistingFile(new Identifier(givenTexturePath.replace(".png", "_blink2.png"))));
            }
            if (UUID_HasBlink.get(id)) {
                if (entity.getPose() == EntityPose.SLEEPING) {
                    return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                }
                //force eyes closed if blinded
                else if (entity.hasStatusEffect(StatusEffects.BLINDNESS)) {
                    return new Identifier(givenTexturePath.replace(".png", (UUID_HasBlink2.get(id) ? "_blink2.png" : "_blink.png")));
                } else {
                    //do regular blinking
                    long timer = entity.world.getTime() % ETFConfigData.blinkFrequency;
                    int blinkTimeVariedByUUID = Math.abs(id.hashCode()) % ETFConfigData.blinkFrequency;
                    if (blinkTimeVariedByUUID < 2) blinkTimeVariedByUUID = 2;
                    if (blinkTimeVariedByUUID > ETFConfigData.blinkFrequency - 2)
                        blinkTimeVariedByUUID = ETFConfigData.blinkFrequency - 2;


                    if (timer >= blinkTimeVariedByUUID - 1 && timer <= blinkTimeVariedByUUID + 1) {
                        if (UUID_HasBlink2.get(id)) {
                            if (timer == blinkTimeVariedByUUID) {
                                return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                            }
                            return new Identifier(givenTexturePath.replace(".png", "_blink2.png"));
                        } else if (!(timer > blinkTimeVariedByUUID)) {
                            return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                        }
                    }
                }
            }
        }
        return isPlayer ?
                ((ETFConfigData.skinFeaturesEnabled
                        && UUID_playerTransparentSkinId.containsKey(id)
                        && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                        || (entity.isTeammate(MinecraftClient.getInstance().player)
                        || entity.getScoreboardTeam() == null))
                ) ? UUID_playerTransparentSkinId.get(id) : getTexture(entity))
                : new Identifier(givenTexturePath);
    }

    private void renderPotion(T livingEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        VertexConsumer textureVert;
        switch (ETFConfigData.enchantedPotionEffects) {
            case ENCHANTED -> {
                textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity)), false, true);
                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
            }
            case GLOWING -> {
                textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity), true));
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

    private void renderSkinFeatures(UUID id, T player, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        //skin http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888

        String skinPossiblyBlinking = returnAlteredTexture((LivingEntityRenderer)(Object) this,player).toString();
        if (skinPossiblyBlinking.contains("_transparent")){
            skinPossiblyBlinking = skinPossiblyBlinking.replace("_transparent","");
        }
        if (!UUID_playerHasFeatures.containsKey(id) && !UUID_playerSkinDownloadedYet.containsKey(id)) {
            //check for mark
            checkPlayerForSkinFeatures(id, (PlayerEntity)player);
        }
        if (UUID_playerSkinDownloadedYet.get(id)) {
            if (UUID_playerHasFeatures.get(id)) {
                //perform texture features
                if (UUID_playerHasEnchant.get(id)) {
                    Identifier enchant = skinPossiblyBlinking.contains(".png")?
                            new Identifier(skinPossiblyBlinking.replace(".png", "_enchant.png")):
                            new Identifier(SKIN_NAMESPACE + id + "_enchant.png");
                    VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(enchant), false, true);
                    this.getModel().render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                }
                if (UUID_playerHasEmissive.get(id)) {
                    Identifier emissive = skinPossiblyBlinking.contains(".png")?
                            new Identifier(skinPossiblyBlinking.replace(".png", "_e.png")):
                            new Identifier(SKIN_NAMESPACE + id + "_e.png");
                    VertexConsumer emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                    this.getModel().render(matrixStack, emissVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }










}


