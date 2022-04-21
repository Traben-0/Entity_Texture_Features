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
import net.minecraft.entity.mob.ShulkerEntity;
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
import traben.entity_texture_features.client.CustomPlayerFeatureModel;
import traben.entity_texture_features.client.ETFUtils;
import traben.entity_texture_features.config.ETFConfig;

import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.*;
import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

@SuppressWarnings("rawtypes")
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow
    public abstract M getModel();

    private final CustomPlayerFeatureModel customPlayerModel = new CustomPlayerFeatureModel<>();

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);

    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER)
    )
    private void etf$applyRenderFeatures(T livingEntity, float a, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        UUID id = livingEntity.getUuid();
        if (!(livingEntity instanceof PlayerEntity)) {

            Identifier texture = etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity);
            ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, texture, this.getModel());

        } else if (ETFConfigData.skinFeaturesEnabled) { // is a player
            etf$renderSkinFeatures(id, (PlayerEntity) livingEntity, matrixStack, vertexConsumerProvider, i);
        }
        //potion effects
        if (ETFConfigData.enchantedPotionEffects != ETFConfig.enchantedPotionEffectsEnum.NONE
                && !livingEntity.getActiveStatusEffects().isEmpty()
                && !livingEntity.hasStatusEffect(StatusEffects.INVISIBILITY)
        ) {
            etf$renderPotion(livingEntity, matrixStack, vertexConsumerProvider);
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
                if (!UUID_ENTITY_AWAITING_DATA_CLEARING.containsKey(id)) {
                    UUID_ENTITY_AWAITING_DATA_CLEARING.put(id, System.currentTimeMillis());
                }
                if (UUID_RANDOM_TEXTURE_SUFFIX_2.containsKey(id)) {
                    if (!UUID_ENTITY_AWAITING_DATA_CLEARING_2.containsKey(id)) {
                        UUID_ENTITY_AWAITING_DATA_CLEARING_2.put(id, System.currentTimeMillis());
                    }
                }
            }
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier etf$returnAlteredTexture(@SuppressWarnings("rawtypes") LivingEntityRenderer instance, Entity inEntity) {
        @SuppressWarnings("unchecked")
        T entity = (T) inEntity;
        Identifier textureIdentifier = getTexture(entity);

        //this is to support inspectio or other abstract rendering mods
        //checks a few possibilities to detect if the displayed entity is actually in a special rendering window
        if (inEntity.getBlockStateAtPos() == null) {
            return textureIdentifier;
        } else if (inEntity.getBlockStateAtPos().isOf(Blocks.VOID_AIR)) {
            //used to trigger inspecios inventory renderer
            // an unintended but acceptable result is an entity in the void will render as vanilla always
            return textureIdentifier;
        } else if (inEntity.getBlockPos() == null) {
            return textureIdentifier;
        } else if (inEntity.getBlockPos().getX() == 0 &&
                inEntity.getBlockPos().getY() == 0 &&
                inEntity.getBlockPos().getZ() == 0) {
            //triggers inspecio inventory renderer
            // an unintended but acceptable result is an entity at 0,0,0 will render as vanilla always
            return textureIdentifier;
        }


        Identifier originalIdentifierToBeUsedIfChanged = null;
        String texturePath = textureIdentifier.toString();

        UUID id = entity.getUuid();

        //debug printing
        if (UUID_DEBUG_EXPLAINATION_MARKER.contains(id)) {
            System.out.println("entity Data:\nTexture=" + texturePath);

            if (PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(texturePath))
                System.out.println("is properties or random=" + PATH_OPTIFINE_OR_JUST_RANDOM.get(texturePath));
            if (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(texturePath))
                System.out.println("path_0123=" + PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.get(texturePath));
            if (PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.containsKey(texturePath))
                System.out.println("amount of properties=" + PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.get(texturePath).size());
            if (PATH_TOTAL_TRUE_RANDOM.containsKey(texturePath))
                System.out.println("total true random=" + PATH_TOTAL_TRUE_RANDOM.get(texturePath));
            if (UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id))
                System.out.println("Random=" + UUID_RANDOM_TEXTURE_SUFFIX.get(id));

            UUID_DEBUG_EXPLAINATION_MARKER.remove(id);
        }


        if (!(entity instanceof PlayerEntity)) {
            if (entity instanceof ShulkerEntity) {
                //set to use vanilla shulker properties if color has been changed
                //setting the below will trigger a return to the original coloured shulker if no random is applied
                originalIdentifierToBeUsedIfChanged = new Identifier(texturePath);
                texturePath = "minecraft:textures/entity/shulker/shulker.png";
                textureIdentifier = new Identifier(texturePath);
            }
            if (ETFConfigData.enableCustomTextures) {
                try {
                    // return via general method but also apply the original texture check if return is unalterred
                    Identifier check = ETFUtils.generalProcessAndReturnAlteredTexture(textureIdentifier, entity);
                    //System.out.println("texture="+check.toString());
                    if (originalIdentifierToBeUsedIfChanged != null && check.toString().equals(textureIdentifier.toString())) {
                        return ETFUtils.returnBlinkIdOrGiven(entity, originalIdentifierToBeUsedIfChanged.toString(), id);
                    } else {
                        return check;
                    }
                } catch (Exception e) {
                    ETFUtils.modMessage(e.toString(), false);
                }
            }
        } else { // is player
            if (ETFConfigData.skinFeaturesEnabled) {
                if (etf$timerBeforeTrySkin > 0) {
                    etf$timerBeforeTrySkin--;
                } else {

                    if (!UUID_PLAYER_HAS_FEATURES.containsKey(id) && !UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.containsKey(id)) {
                        ETFUtils.checkPlayerForSkinFeatures(id, (PlayerEntity) entity);
                    }
                    if (UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.containsKey(id) && UUID_PLAYER_HAS_FEATURES.containsKey(id)) {
                        if (UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.get(id)) {
                            if (UUID_PLAYER_HAS_FEATURES.get(id)) {
                                return ETFUtils.returnBlinkIdOrGiven(entity, SKIN_NAMESPACE + id + ".png", id, true);
                            } else {
                                return textureIdentifier;
                            }
                        }
                    }
                }
            }
        }
        //return original if it was changed and should be set back to original
        if (originalIdentifierToBeUsedIfChanged == null) {
            return ETFUtils.returnBlinkIdOrGiven(entity, textureIdentifier.toString(), id);
        }else{
            return ETFUtils.returnBlinkIdOrGiven(entity, originalIdentifierToBeUsedIfChanged.toString(), id);
        }
    }


    private void etf$renderPotion(T livingEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        VertexConsumer textureVert;
        switch (ETFConfigData.enchantedPotionEffects) {
            case ENCHANTED -> {
                textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity)), false, true);
                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
            }
            case GLOWING -> {
                //textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity), true));
                if (ETFConfigData.fullBrightEmissives) {
                    textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity), true));
                } else {
                    textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, livingEntity)));
                }

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
    private int etf$timerBeforeTrySkin = 400;

    private void etf$renderSkinFeatures(UUID id, PlayerEntity player, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        //skin http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888

        //test area


        /////////////////////////////////////////////
        String skinPossiblyBlinking = etf$returnAlteredTexture((LivingEntityRenderer) (Object) this, player).toString();
        if (skinPossiblyBlinking.contains("_transparent")) {
            skinPossiblyBlinking = skinPossiblyBlinking.replace("_transparent", "");
        }
        if (etf$timerBeforeTrySkin > 0) {
            etf$timerBeforeTrySkin--;
        } else {
            if (!UUID_PLAYER_HAS_FEATURES.containsKey(id) && !UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.containsKey(id)) {
                //check for mark
                ETFUtils.checkPlayerForSkinFeatures(id, player);
            }
            if (UUID_PLAYER_HAS_FEATURES.containsKey(id) && UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.containsKey(id)) {
                if (UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.get(id)) {
                    if (UUID_PLAYER_HAS_FEATURES.get(id)) {

                        //villager nose
                        if (UUID_PLAYER_HAS_VILLAGER_NOSE.get(id)) {

                            customPlayerModel.nose.copyTransform(((PlayerEntityModel) this.getModel()).head);
                            Identifier villager = new Identifier("textures/entity/villager/villager.png");
                            VertexConsumer villagerVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(villager));
                            customPlayerModel.nose.render(matrixStack, villagerVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        }

                        //coat features
                        ItemStack armour = player.getInventory().getArmorStack(1);
                        if (UUID_PLAYER_HAS_COAT.get(id) &&
                                player.isPartVisible(PlayerModelPart.JACKET) &&
                                !(armour.isOf(Items.CHAINMAIL_LEGGINGS) ||
                                        armour.isOf(Items.LEATHER_LEGGINGS) ||
                                        armour.isOf(Items.DIAMOND_LEGGINGS) ||
                                        armour.isOf(Items.GOLDEN_LEGGINGS) ||
                                        armour.isOf(Items.IRON_LEGGINGS) ||
                                        armour.isOf(Items.NETHERITE_LEGGINGS))
                        ) {
                            String coat = SKIN_NAMESPACE + id + "_coat.png";

                            if (UUID_PLAYER_HAS_FAT_COAT.get(id)) {
                                customPlayerModel.fatJacket.copyTransform(((PlayerEntityModel) this.getModel()).jacket);
                            } else {
                                customPlayerModel.jacket.copyTransform(((PlayerEntityModel) this.getModel()).jacket);
                            }
                            //perform texture features
                            VertexConsumer coatVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(coat)));
                            matrixStack.push();


                            if (UUID_PLAYER_HAS_FAT_COAT.get(id)) {
                                customPlayerModel.fatJacket.render(matrixStack, coatVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            } else {
                                customPlayerModel.jacket.render(matrixStack, coatVert, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            }
                            if (UUID_PLAYER_HAS_ENCHANT.get(id)) {
                                Identifier enchant = new Identifier(coat.replace(".png", "_enchant.png"));
                                VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(enchant), false, true);

                                if (UUID_PLAYER_HAS_FAT_COAT.get(id)) {
                                    customPlayerModel.fatJacket.render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                                } else {
                                    customPlayerModel.jacket.render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                                }
                            }
                            if (UUID_PLAYER_HAS_EMISSIVE.get(id)) {
                                Identifier emissive = new Identifier(coat.replace(".png", "_e.png"));
                                VertexConsumer emissVert;// = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                                if (ETFConfigData.fullBrightEmissives) {
                                    emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                                } else {
                                    emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(emissive));
                                }
                                if (ETFConfigData.doShadersEmissiveFix) {
                                    matrixStack.scale(1.01f, 1.01f, 1.01f);
                                }
                                if (UUID_PLAYER_HAS_FAT_COAT.get(id)) {
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
                        if (UUID_PLAYER_HAS_ENCHANT.get(id)) {
                            Identifier enchant = skinPossiblyBlinking.contains(".png") ?
                                    new Identifier(skinPossiblyBlinking.replace(".png", "_enchant.png")) :
                                    new Identifier(SKIN_NAMESPACE + id + "_enchant.png");
                            VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(enchant), false, true);
                            this.getModel().render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                        }
                        if (UUID_PLAYER_HAS_EMISSIVE.get(id)) {
                            Identifier emissive = skinPossiblyBlinking.contains(".png") ?
                                    new Identifier(skinPossiblyBlinking.replace(".png", "_e.png")) :
                                    new Identifier(SKIN_NAMESPACE + id + "_e.png");
                            VertexConsumer emissVert;// = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                            if (ETFConfigData.fullBrightEmissives) {
                                emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                            } else {
                                emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(emissive));
                            }
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


