package traben.entity_texture_features.mixin.client.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
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
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;
import traben.entity_texture_features.config.ETFConfig;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
            String fileString = getTexture(livingEntity).getPath();
            //System.out.println(fileString);
            if (ETFConfigData.enableRandomTextures && UUID_randomTextureSuffix.containsKey(id)) {
                if (UUID_randomTextureSuffix.get(id) != 0 && optifineOldOrVanilla.containsKey(fileString)) {
                    fileString = returnOptifineOrVanillaPath(fileString, UUID_randomTextureSuffix.get(id), "");
                }
            }
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
                    boolean found = false;
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
            renderSkinFeatures(id,livingEntity, a, g, matrixStack, vertexConsumerProvider, i);
        }
        //potion effects
        if (ETFConfigData.enchantedPotionEffects != ETFConfig.enchantedPotionEffectsEnum.NONE
                //&&  !livingEntity.getStatusEffects().isEmpty()
                && !livingEntity.getActiveStatusEffects().isEmpty()
                && !livingEntity.hasStatusEffect(StatusEffects.INVISIBILITY)
        ) {
            renderPotion(livingEntity, matrixStack, vertexConsumerProvider);
        }

        //randomly mark texture for rechecking randomized by UUID
        long randomizer = ETFConfigData.textureUpdateFrequency* 25L;
        if (livingEntity.world.isClient()
                && System.currentTimeMillis() % randomizer == Math.abs(id.hashCode()) % randomizer
        ) {
            if (hasUpdatableRandomCases.containsKey(id)) {
                if (hasUpdatableRandomCases.get(id)
                        && !UUID_entityAwaitingDataClearing.containsKey(id)) {
                    UUID_entityAwaitingDataClearing.put(id, System.currentTimeMillis());
                }
            }else{
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
        T entity = (T)inEntity;
        Identifier vanilla = getTexture(entity);
        String path = vanilla.getPath();
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
                                    testCases(path, id, entity);
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
                            testCases(path, id, entity);
                            //if all failed set to vanilla
                            if (!UUID_randomTextureSuffix.containsKey(id)) {
                                //System.out.println("Entity Texture Features - optifine properties failed to assign texture. setting "+entity.getEntityName()+" to vanilla texture");
                                UUID_randomTextureSuffix.put(id, 0);
                            }
                            if (!UUID_entityAlreadyCalculated.contains(id)) {
                                UUID_entityAlreadyCalculated.add(id);
                            }
                        }
                        if (UUID_randomTextureSuffix.get(id) == 0) {
                            return returnBlinkIdOrGiven( entity, vanilla.getPath(), id);
                        } else {
                            return returnBlinkIdOrGiven( entity, returnOptifineOrVanillaIdentifier(path, UUID_randomTextureSuffix.get(id)).getPath(), id);
                        }

                    } else {//true random assign
                        hasUpdatableRandomCases.put(id, false);
                        if (Texture_TotalTrueRandom.get(path) > 0) {
                            if (!UUID_randomTextureSuffix.containsKey(id)) {
                                int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();
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
                                return returnBlinkIdOrGiven( entity, vanilla.getPath(), id);
                            } else {
                                return returnBlinkIdOrGiven( entity, returnOptifineOrVanillaPath(path, UUID_randomTextureSuffix.get(id), ""), id);
                            }
                        } else {
                            return returnBlinkIdOrGiven(entity, vanilla.getPath(), id);
                        }
                    }

                } catch (Exception e) {
                    modMessage(e.toString(), false);
                    return returnBlinkIdOrGiven( entity, vanilla.getPath(), id);
                }
            }
        } else {
            if (!UUID_playerHasFeatures.containsKey(id) && !UUID_playerSkinDownloadedYet.containsKey(id)) {
                checkPlayerForSkinFeatures(id,entity);
            }
            if (UUID_playerSkinDownloadedYet.get(id)) {
                if (UUID_playerHasFeatures.get(id)) {

                    return returnBlinkIdOrGiven(entity, SKIN_NAMESPACE + id + ".png", id, true);
                } else {
                    return vanilla;
                }
            }
        }
        return returnBlinkIdOrGiven(entity, vanilla.getPath(), id);
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
                    int blinkTimeVariedByUUID = id.hashCode() < 0 ? -id.hashCode() : id.hashCode() % ETFConfigData.blinkFrequency;
                    if (blinkTimeVariedByUUID < 2) blinkTimeVariedByUUID = 2;
                    if (blinkTimeVariedByUUID > ETFConfigData.blinkFrequency - 2)
                        blinkTimeVariedByUUID = ETFConfigData.blinkFrequency - 2;


                    if (timer >= blinkTimeVariedByUUID - 1 && timer <= blinkTimeVariedByUUID + 1) {
                        //System.out.println("blinking");
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
                (  (ETFConfigData.skinFeaturesEnabled
                && UUID_playerTransparentSkinId.containsKey(id)
                && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (entity.isTeammate(MinecraftClient.getInstance().player)
                || entity.getScoreboardTeam() == null))
                    )? UUID_playerTransparentSkinId.get(id):  getTexture(entity))
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

    private void renderSkinFeatures(UUID id, T player, float a, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        //skin http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888

        if (!UUID_playerHasFeatures.containsKey(id) && !UUID_playerSkinDownloadedYet.containsKey(id)) {
            //check for mark
            checkPlayerForSkinFeatures(id,player);
        }
        if (UUID_playerSkinDownloadedYet.get(id)) {
            if (UUID_playerHasFeatures.get(id)) {
                //perform texture features

                if (UUID_playerHasEnchant.get(id)) {
                    Identifier enchant = new Identifier(SKIN_NAMESPACE + id + "_enchant.png");
                    VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(enchant), false, true);
                    this.getModel().render(matrixStack, enchantVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);
                }
                if (UUID_playerHasEmissive.get(id)) {
                    Identifier emissive = new Identifier(SKIN_NAMESPACE + id + "_e.png");
                    VertexConsumer emissVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(emissive, true));
                    this.getModel().render(matrixStack, emissVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }//else if (player.world.getTime() % 512 == 1){
            //randomly recheck skin incase of HTTP protocol error
            //checkPlayerForSkinFeatures(id,player );
            //}
        }
    }

    private void checkPlayerForSkinFeatures(UUID id, T player) {
        //if on an enemy team option to disable skin features loading
        if(ETFConfigData.skinFeaturesEnabled
            &&(ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (player.isTeammate(MinecraftClient.getInstance().player)
                || player.getScoreboardTeam() == null))
        ) {
            UUID_playerSkinDownloadedYet.put(id,false);
            getSkin(id,player);
        }
    }
    private void skinLoaded(NativeImage skin,UUID id, T player){
        UUID_playerSkinDownloadedYet.put(id,true);
            if (skin != null) {
                if (skin.getColor(1, 16) == -16776961 &&
                        skin.getColor(0, 16) == -16777089 &&
                        skin.getColor(0, 17) == -16776961 &&
                        skin.getColor(2, 16) == -16711936 &&
                        skin.getColor(3, 16) == -16744704 &&
                        skin.getColor(3, 17) == -16711936 &&
                        skin.getColor(0, 18) == -65536 &&
                        skin.getColor(0, 19) == -8454144 &&
                        skin.getColor(1, 19) == -65536 &&
                        skin.getColor(3, 18) == -1 &&
                        skin.getColor(2, 19) == -1 &&
                        skin.getColor(3, 18) == -1
                ) {
                    //this has texture features
                    modMessage("Found Player {" + id + "} with texture features in skin.", false);
                    UUID_playerHasFeatures.put(id, true);
                    //find what features

                    //check for transparency options

                    if (ETFConfigData.skinFeaturesEnableTransparency) {
                        //if (skin.getColor(52, 17) == -65281) {
                            if (canTransparentSkin(skin)) {
                                Identifier transId = new Identifier(SKIN_NAMESPACE + id + "_transparent.png");
                                UUID_playerTransparentSkinId.put(id,transId);
                                registerNativeImageToIdentifier(skin, transId.getPath());

                            } else {
                                modMessage("Skin was too transparent or had other problems",false);
                            }
                        //}
                    }

                    NativeImage check = getEnchantedTexture(id, skin);
                    if (check != null) {
                        registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_enchant.png");
                    }

                    check = getEmissiveTexture(id, skin);
                    if (check != null) {
                        registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_e.png");
                    }
                    //pink = -65281, blue = -256
                    //blink 1 frame if either pink or blue optional
                    if (skin.getColor(52, 16) == -65281 || skin.getColor(52, 16) == -256) {
                        UUID_HasBlink.put(id, true);
                        registerNativeImageToIdentifier(returnBlinkFace(skin, false), SKIN_NAMESPACE + id + "_blink.png");
                    } else {
                        UUID_HasBlink.put(id, false);
                    }
                    //blink is 2 frames with blue optional
                    if (skin.getColor(52, 16) == -256) {
                        UUID_HasBlink2.put(id, true);
                        registerNativeImageToIdentifier(returnBlinkFace(skin, true), SKIN_NAMESPACE + id + "_blink2.png");
                    } else {
                        UUID_HasBlink2.put(id, false);
                    }

                } else {
                    //modMessage("Player has no texture features", false);
                    UUID_playerHasFeatures.put(id, false);
                }
            } else { //http failed
                //modMessage("Player has no texture features", false);
                //UUID_playerHasFeatures.put(id, false);

                UUID_playerHasFeatures.put(id, false);
            }
        }



    private void registerNativeImageToIdentifier(NativeImage img, String identifierPath) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        NativeImageBackedTexture bob = new NativeImageBackedTexture(img);
        textureManager.registerTexture(new Identifier(identifierPath), bob);

    }

    private int countTransparentInBox(NativeImage img,int x1,int y1,int x2,int y2) {
        int counter = 0;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                //if (img.getOpacity(x, y) != -1) {
                    //ranges from  0 to 127  then wraps around negatively -127 to -1  totalling 0 to 255
                    int i = img.getOpacity(x, y);
                    if (i < 0){
                        i += 256;
                    }
                    //adjusted to 0 to 256
                    counter +=i;
                    //System.out.println("opacity["+x+","+y+"]= "+i+",  total"+count);
                //}
            }
        }
        return counter;
    }

    private boolean canTransparentSkin(NativeImage skin){
        int countTransparent=0;
        //map of bottom skin layer in cubes
        countTransparent += countTransparentInBox(skin,8,0,23,15);
        countTransparent += countTransparentInBox(skin,0,20,55,31);
        countTransparent += countTransparentInBox(skin,0,8,7,15);
        countTransparent += countTransparentInBox(skin,24,8,31,15);
        countTransparent += countTransparentInBox(skin,0,16,11,19);
        countTransparent += countTransparentInBox(skin,20,16,35,19);
        countTransparent += countTransparentInBox(skin,44,16,51,19);
        countTransparent += countTransparentInBox(skin,20,48,27,51);
        countTransparent += countTransparentInBox(skin,36,48,43,51);
        countTransparent += countTransparentInBox(skin,16,52,47,63);
        //do not allow skins under 40% ish total opacity
        //1648 is total pixels that are not allowed transparent by vanilla
        int average = (countTransparent/1648); // should be 0 to 256
        //System.out.println("averages"+average +"-"+100);
        return average >= 100;
    }

    private NativeImage returnBlinkFace(NativeImage baseSkin, boolean isSecondFrame) {
        NativeImage texture = new NativeImage(64, 64, false);
//        for (int x = 0; x <= 63; x++) {
//            for (int y = 0; y <= 63; y++) {
//                texture.setColor(x,y,0);
//            }
//        }
        texture.copyFrom(baseSkin);
        if (isSecondFrame) {
            //copy face 2
            for (int x = 24; x <= 31; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x - 16, y + 8, baseSkin.getColor(x, y));
                }
            }
            //copy face overlay 2
            for (int x = 56; x <= 63; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x - 16, y + 8, baseSkin.getColor(x, y));
                }
            }
        } else {
            //copy face
            for (int x = 0; x <= 7; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x + 8, y + 8, baseSkin.getColor(x, y));
                }
            }
            //copy face overlay
            for (int x = 32; x <= 39; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x + 8, y + 8, baseSkin.getColor(x, y));
                }
            }
        }
        return texture;
    }

    private NativeImage getEnchantedTexture(UUID id, NativeImage baseSkin) {
        NativeImage check = returnMatchPixels(baseSkin, 56, 24, 63, 31);
        UUID_playerHasEnchant.put(id, check != null);
        return check;
    }

    private NativeImage getEmissiveTexture(UUID id, NativeImage baseSkin) {
        NativeImage check = returnMatchPixels(baseSkin, 56, 16, 63, 23);
        UUID_playerHasEmissive.put(id, check != null);
        return check;
    }

    @Nullable
    private NativeImage returnMatchPixels(NativeImage baseSkin, int x1, int y1, int x2, int y2) {
        ArrayList<Integer> matchColors = new ArrayList<>();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (baseSkin.getOpacity(x, y) != 0 && !matchColors.contains(baseSkin.getColor(x, y))) {
                    matchColors.add(baseSkin.getColor(x, y));
                }
            }
        }
        if (matchColors.size() == 0) {
            return null;
        } else {
            NativeImage texture = new NativeImage(64, 64, false);
            texture.copyFrom(baseSkin);
            for (int x = 0; x <= 63; x++) {
                for (int y = 0; y <= 63; y++) {
                    if (!matchColors.contains(baseSkin.getColor(x, y))) {
                        texture.setColor(x, y, 0);
                    }
                }
            }
            return texture;
        }

    }

    private void getSkin(UUID id, T player) {
        NativeImage retreivedSkin = null;
        try {
            String url = "";
            PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(id);
            GameProfile gameProfile = playerListEntry.getProfile();
            PropertyMap texturesMap = gameProfile.getProperties();
            Collection<Property> properties = texturesMap.get("textures");
            for (Property p :
                    properties) {
                //System.out.println(p.getValue());
                /*
0{
1  "timestamp" : 1645524822329,
2  "profileId" : "fd22e573178c415a94fee476b328abfd",
3  "profileName" : "Benjamin",
4  "textures" : {
5    "SKIN" : {
6      "url" : "http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888"
    },
    "CAPE" : {
      "url" : "http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933"
    }
  }
}
                */
                byte[] result = Base64.getDecoder().decode(p.getValue());
                url = new String(result);
                url = url.split("SKIN")[1];
                url = url.split("http")[1];
                url = url.split("\"")[0];
                url = "http" + url.trim();

                //System.out.println(url);

            }

//            HttpURLConnection httpURLConnection = null;
//
//            try {
//                httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.setDoOutput(false);
//                httpURLConnection.connect();
//                if (httpURLConnection.getResponseCode() / 100 == 2) {
//                    InputStream inputStream;
//
//                    inputStream = httpURLConnection.getInputStream();
//
//                    retreivedSkin = this.loadTexture(inputStream);
//                    //NativeImage nativeImge = this.loadTexture(inputStream);
//                    //if (nativeImge != null) {
//                    //    UUID_playerSkins.put(id,nativeImge);
//                    //}
//                }
//            } catch (Exception var6) {
//                System.out.println(var6);
//            } finally {
//                if (httpURLConnection != null) {
//                    httpURLConnection.disconnect();
//                }
//
//            }
            String finalUrl = url;
            //LOGGER.debug("Downloading http texture from {} to {}", this.url, this.cacheFile);
            //return;
            //LOGGER.error("Couldn't download http texture", var6);
            //return;
            //                    if (httpURLConnection != null) {
            //                        httpURLConnection.disconnect();
            //                    }
            CompletableFuture<?> loader = CompletableFuture.runAsync(() -> {
                HttpURLConnection httpURLConnection = null;
                //LOGGER.debug("Downloading http texture from {} to {}", this.url, this.cacheFile);

                try {
                    httpURLConnection = (HttpURLConnection) (new URL(finalUrl)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() / 100 == 2) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        MinecraftClient.getInstance().execute(() -> {
                            NativeImage nativeImage = this.loadTexture(inputStream);
                            if (nativeImage != null) {
                                skinLoaded(nativeImage, id, player);
                            } else {
                                modMessage("Player skin {" + player.getDisplayName().getString() + "} unavailable for feature check", false);
                                UUID_playerHasFeatures.put(id, false);

                            }
                            if (UUID_HTTPtoDisconnect.containsKey(id)) {
                                UUID_HTTPtoDisconnect.get(id).disconnect();
                                UUID_HTTPtoDisconnect.remove(id);
                            }
                        });
                        //return;
                    }
                } catch (Exception var6) {
                    //LOGGER.error("Couldn't download http texture", var6);
                    //return;
                } finally {
//                    if (httpURLConnection != null) {
//                        httpURLConnection.disconnect();
//                    }
                    UUID_HTTPtoDisconnect.put(id, httpURLConnection);
                }

            }, Util.getMainWorkerExecutor());

        }catch(Exception e){
            //return null;
        }
        //can return null
        //return retreivedSkin;
    }

    private NativeImage loadTexture(InputStream stream) {
        NativeImage nativeImage = null;

        try {
            nativeImage = NativeImage.read(stream);

        } catch (Exception var4) {
            System.out.println("failed 165165651"+var4);
        }

        return nativeImage;
    }
}


