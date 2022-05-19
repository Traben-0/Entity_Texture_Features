package traben.entity_texture_features.client.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static traben.entity_texture_features.client.ETFClient.*;

//contains all methods and utilities for player skin features to prevent bloat in the regular ETFUtils file
public class ETFPlayerSkinUtils {


    public static void forceResetAllDataOfPlayerUUID(UUID id) {
        UUID_NEXT_BLINK_TIME.remove(id);
        UUID_PLAYER_HAS_FEATURES.remove(id);
        UUID_PLAYER_HAS_ENCHANT.remove(id);
        UUID_PLAYER_HAS_EMISSIVE.remove(id);
        UUID_PLAYER_TRANSPARENT_SKIN_ID.remove(id);
        UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.remove(id);
        UUID_PLAYER_HAS_COAT.remove(id);
        UUID_PLAYER_HAS_FAT_COAT.remove(id);
        UUID_PLAYER_HAS_VILLAGER_NOSE.remove(id);
        UUID_PLAYER_HAS_CAPE.remove(id);
        UUID_PLAYER_HAS_CUSTOM_CAPE.remove(id);
        UUID_PLAYER_LAST_SKIN_CHECK.remove(id);
        UUID_PLAYER_LAST_SKIN_CHECK_COUNT.remove(id);
    }


    public static void checkPlayerForSkinFeatures(UUID id, PlayerEntity player) {
        //if on an enemy team option to disable skin features loading
        if (ETFConfigData.skinFeaturesEnabled
                && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (player.isTeammate(MinecraftClient.getInstance().player)
                || player.getScoreboardTeam() == null))
        ) {
            // skip if tried to recently
            if (UUID_PLAYER_LAST_SKIN_CHECK.containsKey(id)) {
                if (UUID_PLAYER_LAST_SKIN_CHECK.get(id) + 6000 > System.currentTimeMillis()) {
                    return;
                }
            }
            UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.put(id, false);
            UUID_PLAYER_HAS_CAPE.put(id, ((AbstractClientPlayerEntity) player).canRenderCapeTexture());
            getSkin(player);
        }
    }


    private static void getSkin(PlayerEntity player) {
        UUID id = player.getUuid();


        try {
            boolean hasCape = ((AbstractClientPlayerEntity) player).canRenderCapeTexture();
            String url = "";
            String capeurl = null;
            @SuppressWarnings("ConstantConditions")
            PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(id);
            @SuppressWarnings("ConstantConditions")
            GameProfile gameProfile = playerListEntry.getProfile();
            PropertyMap texturesMap = gameProfile.getProperties();
            Collection<Property> properties = texturesMap.get("textures");
            for (Property p :
                    properties) {
                JsonObject props = JsonParser.parseString(new String(Base64.getDecoder().decode((p.getValue())))).getAsJsonObject();
                url = ((JsonObject) ((JsonObject) props.get("textures")).get("SKIN")).get("url").getAsString();
                //le cape
                if (hasCape) {
                    try {
                        capeurl = ((JsonObject) ((JsonObject) props.get("textures")).get("CAPE")).get("url").getAsString();
                    } catch (Exception e) {
                        //modMessage("no cape",false);
                    }
                }

                break;
            }


                /*System.out.println(p.getValue());
{
  "timestamp" : 1645524822329,
  "profileId" : "fd22e573178c415a94fee476b328abfd",
  "profileName" : "Benjamin",
  "textures" : {
    "SKIN" : {
      "url" : "http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888"
    },
    "CAPE" : {
      "url" : "http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933"
    }
  }
}
                */

            downloadImageFromUrl(player, url, "VANILLA_SKIN", capeurl);
        } catch (Exception e) {
            skinFailed(id);
        }

    }

    private static void downloadImageFromUrl(PlayerEntity player, String url, @SuppressWarnings("SameParameterValue") String sendFileToMethodKey) {
        downloadImageFromUrl(player, url, sendFileToMethodKey, null, false);
    }

    private static void downloadImageFromUrl(PlayerEntity player, String url, @SuppressWarnings("SameParameterValue") String sendFileToMethodKey, String url2) {
        downloadImageFromUrl(player, url, sendFileToMethodKey, url2, false);
    }

    private static void downloadImageFromUrl(PlayerEntity player, String url, String sendFileToMethodKey, @Nullable String url2, @SuppressWarnings("SameParameterValue") boolean isFile) {
        try {
            //required for vanilla cape
            boolean do2urls = url2 != null;
            CompletableFuture.runAsync(() -> {
                HttpURLConnection httpURLConnection = null;
                HttpURLConnection httpURLConnection2 = null;
                try {
                    httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.connect();
                    if (do2urls) {
                        httpURLConnection2 = (HttpURLConnection) (new URL(url2)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                        httpURLConnection2.setDoInput(true);
                        httpURLConnection2.setDoOutput(false);
                        httpURLConnection2.connect();
                    }
                    boolean newHas2 = do2urls && httpURLConnection2.getResponseCode() / 100 == 2;
                    if (httpURLConnection.getResponseCode() / 100 == 2) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        InputStream inputStreamCape = null;
                        if (newHas2) {
                            inputStreamCape = httpURLConnection2.getInputStream();
                        }
                        InputStream finalInputStreamCape = inputStreamCape;
                        MinecraftClient.getInstance().execute(() -> {
                            if (isFile) {
                                String read;
                                try {
                                    read = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                } catch (Exception e) {
                                    read = null;
                                }
                                directFileFromUrlToMethod(read, sendFileToMethodKey);
                            } else {
                                NativeImage one = loadTexture(inputStream);
                                NativeImage two = null;
                                if (newHas2) {
                                    two = loadTexture(finalInputStreamCape);
                                }
                                if (one != null) {
                                    directImageFromUrlToMethod(player, one, sendFileToMethodKey, two);
                                } else {
                                    //modMessage("downloading image failed", false);
                                    skinFailed(player.getUuid());
                                }
                            }
                            if (URL_HTTP_TO_DISCONNECT_1.containsKey(url)) {
                                if (URL_HTTP_TO_DISCONNECT_1.get(url) != null) {
                                    URL_HTTP_TO_DISCONNECT_1.get(url).disconnect();
                                }
                                URL_HTTP_TO_DISCONNECT_1.remove(url);
                            }
                            if (URL_HTTP_TO_DISCONNECT_2.containsKey(url2)) {
                                if (URL_HTTP_TO_DISCONNECT_2.get(url2) != null) {
                                    URL_HTTP_TO_DISCONNECT_2.get(url2).disconnect();
                                }
                                URL_HTTP_TO_DISCONNECT_2.remove(url2);
                            }
                        });

                    }
                } catch (Exception var6) {
                    URL_HTTP_TO_DISCONNECT_1.put(url, httpURLConnection);
                    URL_HTTP_TO_DISCONNECT_2.put(url2, httpURLConnection2);
                } finally {
                    URL_HTTP_TO_DISCONNECT_1.put(url, httpURLConnection);
                    URL_HTTP_TO_DISCONNECT_2.put(url2, httpURLConnection2);
                }

            }, Util.getMainWorkerExecutor());

        } catch (Exception e) {
            //
        }
    }

    private static void directFileFromUrlToMethod(String fileString, String sendFileToMethodKey) {
        //switch
        if (fileString != null) {
            if (sendFileToMethodKey.equals("etf$CAPE")) {
                System.out.println(fileString);
            }
        }

    }

    private static void skinFailed(UUID id) {
        UUID_PLAYER_LAST_SKIN_CHECK.put(id, System.currentTimeMillis());
        if (!UUID_PLAYER_LAST_SKIN_CHECK_COUNT.containsKey(id)) {
            UUID_PLAYER_LAST_SKIN_CHECK_COUNT.put(id, 0);
        } else {
            UUID_PLAYER_LAST_SKIN_CHECK_COUNT.put(id, UUID_PLAYER_LAST_SKIN_CHECK_COUNT.get(id) + 1);
        }

        //modMessage("Player skin {" + name + "} unavailable for feature check. try number "+UUID_playerLastSkinCheckCount.get(id)+". Reason failed = "+(reason+1), false);
        ///give up after a few checks
        if (UUID_PLAYER_LAST_SKIN_CHECK_COUNT.get(id) > 5) {
            UUID_PLAYER_HAS_FEATURES.put(id, false);
        }
        UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.remove(id);
    }

    private static void directImageFromUrlToMethod(PlayerEntity player, NativeImage image, String sendFileToMethodKey, @Nullable NativeImage image2) {
        //switch
        UUID id = player.getUuid();
        if (sendFileToMethodKey.equals("VANILLA_SKIN")) {
            if (image != null) {
                skinLoaded(player, image, image2);
            } else {
                //modMessage("Player skin {" + player.getName().getString() + "} unavailable for feature check", false);
                skinFailed(id);
            }
        } else if (sendFileToMethodKey.equals("THIRD_PARTY_CAPE")) {
            if (image != null) {
                //optifine resizes them for space cause expensive servers I guess
                if (image.getWidth() % image.getHeight() != 0) {
                    ETFUtils.registerNativeImageToIdentifier(resizeOptifineImage(image), SKIN_NAMESPACE + id + "_cape.png");
                } else {
                    ETFUtils.registerNativeImageToIdentifier(image, SKIN_NAMESPACE + id + "_cape.png");
                }
                UUID_PLAYER_HAS_CUSTOM_CAPE.put(id, true);
            } else {
                ETFUtils.logMessage("Player skin {" + player.getName().getString() + "} no THIRD_PARTY_CAPE Found", false);
                //registerNativeImageToIdentifier(getNativeImageFromID(new Identifier("etf:capes/error.png")), SKIN_NAMESPACE + id + "_cape.png");
                UUID_PLAYER_HAS_CUSTOM_CAPE.put(id, false);
            }

        }
    }

    private static NativeImage resizeOptifineImage(NativeImage image) {
        int newWidth = 64;
        while (newWidth < image.getWidth()) {
            newWidth = newWidth + newWidth;
        }
        int newHeight = newWidth / 2;
        NativeImage resizedImage = ETFUtils.emptyNativeImage(newWidth, newHeight);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resizedImage.setColor(x, y, image.getColor(x, y));
            }
        }
        return resizedImage;
    }

    private static NativeImage loadTexture(InputStream stream) {
        NativeImage nativeImage = null;

        try {
            nativeImage = NativeImage.read(stream);

        } catch (Exception var4) {
            //modMessage("failed 165165651" + var4, false);
        }

        return nativeImage;
    }


    private static int getSkinPixelColourToNumber(int color) {
        //            pink   cyan     red       green      brown    blue     orange     yellow
        //colours = -65281, -256, -16776961, -16711936, -16760705, -65536, -16744449, -14483457
        return switch (color) {
            case -65281 -> 1;
            case -256 -> 2;
            case -16776961 -> 3;
            case -16711936 -> 4;
            case -16760705 -> 5;
            case -65536 -> 6;
            case -16744449 -> 7;
            case -14483457 -> 8;
            case -12362096 -> 666; //villager nose color
            default -> 0;
        };
    }

    private static void skinLoaded(PlayerEntity player, NativeImage skin, @Nullable NativeImage cape) {
        UUID id = player.getUuid();
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
                ETFUtils.logMessage("Found Player {" + id + "} with texture features in skin.", false);
                UUID_PLAYER_HAS_FEATURES.put(id, true);
                //find what features
                //pink = -65281, blue = -256
                //            pink   cyan     red       green      brown    blue     orange     yellow
                //colours = -65281, -256, -16776961, -16711936, -16760705, -65536, -16744449, -14483457

                //check Choice Box
                int[] choiceBoxChoices = {getSkinPixelColourToNumber(skin.getColor(52, 16)),
                        getSkinPixelColourToNumber(skin.getColor(52, 17)),
                        getSkinPixelColourToNumber(skin.getColor(52, 18)),
                        getSkinPixelColourToNumber(skin.getColor(52, 19)),
                        getSkinPixelColourToNumber(skin.getColor(53, 16)),
                        getSkinPixelColourToNumber(skin.getColor(53, 17))};

                //villager nose check
                boolean noseUpper = (getSkinPixelColourToNumber(skin.getColor(43, 13)) == 666 && getSkinPixelColourToNumber(skin.getColor(44, 13)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(43, 14)) == 666 && getSkinPixelColourToNumber(skin.getColor(44, 14)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(43, 15)) == 666 && getSkinPixelColourToNumber(skin.getColor(44, 15)) == 666);
                boolean noseLower = (getSkinPixelColourToNumber(skin.getColor(11, 13)) == 666 && getSkinPixelColourToNumber(skin.getColor(12, 13)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(11, 14)) == 666 && getSkinPixelColourToNumber(skin.getColor(12, 14)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(11, 15)) == 666 && getSkinPixelColourToNumber(skin.getColor(12, 15)) == 666);
                if (noseUpper) {
                    deletePixels(skin, 43, 13, 44, 15);
                }
                UUID_PLAYER_HAS_VILLAGER_NOSE.put(id, noseLower || noseUpper);

                //check for coat bottom
                //pink to copy coat    light blue to remove from legs
                NativeImage coatSkin = null;
                int controllerCoat = choiceBoxChoices[1];
                if (controllerCoat >= 1 && controllerCoat <= 8) {
                    int lengthOfCoat = choiceBoxChoices[2] - 1;
                    Identifier coatID = new Identifier(SKIN_NAMESPACE + id + "_coat.png");
                    coatSkin = getOrRemoveCoatTexture(skin, lengthOfCoat, controllerCoat >= 5);
                    ETFUtils.registerNativeImageToIdentifier(coatSkin, coatID.toString());
                    UUID_PLAYER_HAS_COAT.put(id, true);
                    if (controllerCoat == 2 || controllerCoat == 4 || controllerCoat == 6 || controllerCoat == 8) {
                        //delete original pixel from skin
                        deletePixels(skin, 4, 32, 7, 35);
                        deletePixels(skin, 4, 48, 7, 51);
                        deletePixels(skin, 0, 36, 15, 36 + lengthOfCoat);
                        deletePixels(skin, 0, 52, 15, 52 + lengthOfCoat);
                    }
                    //red or green make fat coat
                    UUID_PLAYER_HAS_FAT_COAT.put(id, controllerCoat == 3 || controllerCoat == 4 || controllerCoat == 7 || controllerCoat == 8);


                } else {
                    UUID_PLAYER_HAS_COAT.put(id, false);
                }
                //check for transparency options
                //System.out.println("about to check");
                if (ETFConfigData.skinFeaturesEnableTransparency) {
                    if (canTransparentSkin(skin)) {
                        Identifier transId = new Identifier(SKIN_NAMESPACE + id + "_transparent.png");
                        UUID_PLAYER_TRANSPARENT_SKIN_ID.put(id, transId);
                        ETFUtils.registerNativeImageToIdentifier(skin, transId.toString());

                    } else {
                        ETFUtils.logMessage("Skin was too transparent or had other problems", false);
                    }
                }

                //blink
                NativeImage blinkSkinFile = null;
                NativeImage blinkSkinFile2 = null;
                int blinkChoice = choiceBoxChoices[0];
                if (blinkChoice >= 1 && blinkChoice <= 5) {
                    //check if lazy blink
                    PATH_HAS_BLINK_TEXTURE.put(SKIN_NAMESPACE + id + ".png", true);
                    if (blinkChoice <= 2) {
                        //blink 1 frame if either pink or blue optional
                        blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("face1"), 1, getSkinPixelBounds("face3"));

                        ETFUtils.registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");

                        //blink is 2 frames with blue optional
                        if (blinkChoice == 2) {
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("face2"), 1, getSkinPixelBounds("face4"));
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", true);
                            ETFUtils.registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        } else {
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", false);
                        }
                    } else {//optimized blink
                        int eyeHeightTopDown = choiceBoxChoices[3];
                        if (eyeHeightTopDown > 8 || eyeHeightTopDown < 1) {
                            eyeHeightTopDown = 1;
                        }
                        //optimized 1p high eyes
                        if (blinkChoice == 3) {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEyeSmall"), eyeHeightTopDown);

                            ETFUtils.registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");

                        } else if (blinkChoice == 4) {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", true);

                            ETFUtils.registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                            ETFUtils.registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        } else /*if( blinkChoice == 5)*/ {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", true);
                            ETFUtils.registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                            ETFUtils.registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        }
                    }


                }
                if (!PATH_HAS_BLINK_TEXTURE.containsKey(SKIN_NAMESPACE + id + ".png")) {
                    PATH_HAS_BLINK_TEXTURE.put(SKIN_NAMESPACE + id + ".png", false);
                }
                if (!PATH_HAS_BLINK_TEXTURE_2.containsKey(SKIN_NAMESPACE + id + ".png")) {
                    PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", false);
                }

                //check for cape recolor
                int capeChoice1 = choiceBoxChoices[4];
                // custom cape data experiment
                // https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe
                //downloadImageFromUrl(player, "https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe", "etf$CAPE",null,true);
                if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666) {
                    switch (capeChoice1) {
                        case 1 -> //custom in skin
                                cape = returnCustomTexturedCape(skin);
                        case 2 -> {
                            cape = null;
                            // minecraft capes mod
                            //https://minecraftcapes.net/profile/fd22e573178c415a94fee476b328abfd/cape/
                            downloadImageFromUrl(player, "https://minecraftcapes.net/profile/" + player.getUuidAsString().replace("-", "") + "/cape/", "THIRD_PARTY_CAPE");
                        }
                        case 3 -> {
                            cape = null;
                            //  https://optifine.net/capes/Benjamin.png
                            downloadImageFromUrl(player, "https://optifine.net/capes/" + player.getName().getString() + ".png", "THIRD_PARTY_CAPE");
                        }
                        case 666 -> cape = ETFUtils.getNativeImageFromID(new Identifier("etf:capes/error.png"));
                        default -> {
                            //cape = getNativeImageFromID(new Identifier("etf:capes/blank.png"));
                        }
                    }
                }
                if (cape != null) {
                    if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666) {//custom chosen
                        ETFUtils.registerNativeImageToIdentifier(cape, SKIN_NAMESPACE + id + "_cape.png");
                        UUID_PLAYER_HAS_CUSTOM_CAPE.put(id, true);
                    }
                }
                if (!UUID_PLAYER_HAS_CUSTOM_CAPE.containsKey(id)) {
                    UUID_PLAYER_HAS_CUSTOM_CAPE.put(id, false);
                }


                //check for marker choices
                //  1 = Emissives,  2 = Enchanted
                List<Integer> markerChoices = List.of(getSkinPixelColourToNumber(skin.getColor(1, 17)),
                        getSkinPixelColourToNumber(skin.getColor(1, 18)),
                        getSkinPixelColourToNumber(skin.getColor(2, 17)),
                        getSkinPixelColourToNumber(skin.getColor(2, 18)));

                //enchanted
                UUID_PLAYER_HAS_ENCHANT.put(id, markerChoices.contains(2));
                if (markerChoices.contains(2)) {
                    int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(2) + 1));
                    NativeImage check = returnMatchPixels(skin, boxChosenBounds);
                    if (check != null) {
                        ETFUtils.registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_enchant.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            ETFUtils.registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_enchant.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            ETFUtils.registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_enchant.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(coatSkin, boxChosenBounds);
                            ETFUtils.registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_coat_enchant.png");
                        }

                        // NativeImage checkCape = returnMatchPixels(skin, boxChosenBounds,cape);
                        // registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCape, this::emptyNativeImage), SKIN_NAMESPACE + id + "_cape_enchant.png");

                    } else {
                        UUID_PLAYER_HAS_ENCHANT.put(id, false);
                    }

                }
                //emissives
                UUID_PLAYER_HAS_EMISSIVE.put(id, markerChoices.contains(1));
                if (markerChoices.contains(1)) {
                    int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(1) + 1));
                    NativeImage check = returnMatchPixels(skin, boxChosenBounds);
                    if (check != null) {
                        ETFUtils.registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_e.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            ETFUtils.registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            ETFUtils.registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(coatSkin, boxChosenBounds);
                            ETFUtils.registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_coat_e.png");
                        }

                        //  NativeImage checkCape = returnMatchPixels(skin, boxChosenBounds,cape);
                        // registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCape, this::emptyNativeImage), SKIN_NAMESPACE + id + "_cape_e.png");

                    } else {
                        UUID_PLAYER_HAS_EMISSIVE.put(id, false);
                    }

                }

            } else {
                UUID_PLAYER_HAS_FEATURES.put(id, false);
                // System.out.println("worked but no features");
            }
        } else { //http failed
            //UUID_playerHasFeatures.put(id, false);
            skinFailed(id);
        }
        UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.put(id, true);
    }


    private static int[] getSkinPixelBounds(String choiceKey) {
        return switch (choiceKey) {
            case "marker1" -> new int[]{56, 16, 63, 23};
            case "marker2" -> new int[]{56, 24, 63, 31};
            case "marker3" -> new int[]{56, 32, 63, 39};
            case "marker4" -> new int[]{56, 40, 63, 47};
            case "optimizedEyeSmall" -> new int[]{12, 16, 19, 16};
            case "optimizedEye2High" -> new int[]{12, 16, 19, 17};
            case "optimizedEye2High_second" -> new int[]{12, 18, 19, 19};
            case "optimizedEye4High" -> new int[]{12, 16, 19, 19};
            case "optimizedEye4High_second" -> new int[]{36, 16, 43, 19};
            case "face1" -> new int[]{0, 0, 7, 7};
            case "face2" -> new int[]{24, 0, 31, 7};
            case "face3" -> new int[]{32, 0, 39, 7};
            case "face4" -> new int[]{56, 0, 63, 7};
            case "cape1" -> new int[]{12, 32, 19, 35};
            case "cape2" -> new int[]{36, 32, 43, 35};
            case "cape3" -> new int[]{12, 48, 19, 51};
            case "cape4" -> new int[]{28, 48, 35, 51};
            case "cape5.1" -> new int[]{44, 48, 45, 51};
            case "cape5.2" -> new int[]{46, 48, 47, 51};
            case "cape5.3" -> new int[]{48, 48, 49, 51};
            case "cape5.4" -> new int[]{50, 48, 51, 51};
            case "capeVertL" -> new int[]{1, 1, 1, 16};
            case "capeVertR" -> new int[]{10, 1, 10, 16};
            case "capeHorizL" -> new int[]{1, 1, 10, 1};
            case "capeHorizR" -> new int[]{1, 16, 10, 16};
            default -> new int[]{0, 0, 0, 0};
        };
    }


    private static NativeImage returnCustomTexturedCape(NativeImage skin) {
        NativeImage cape = ETFUtils.emptyNativeImage(64, 32);
        NativeImage elytra = ETFUtils.getNativeImageFromID(new Identifier("etf:capes/public static _elytra.png"));
        if (elytra == null) {
            elytra = ETFUtils.getNativeImageFromID(new Identifier("textures/entity/elytra.png"));
        }//not else
        if (elytra != null) {
            cape.copyFrom(elytra);
        }
        copyToPixels(skin, cape, getSkinPixelBounds("cape1"), 1, 1);
        copyToPixels(skin, cape, getSkinPixelBounds("cape1"), 12, 1);
        copyToPixels(skin, cape, getSkinPixelBounds("cape2"), 1, 5);
        copyToPixels(skin, cape, getSkinPixelBounds("cape2"), 12, 5);
        copyToPixels(skin, cape, getSkinPixelBounds("cape3"), 1, 9);
        copyToPixels(skin, cape, getSkinPixelBounds("cape3"), 12, 9);
        copyToPixels(skin, cape, getSkinPixelBounds("cape4"), 1, 13);
        copyToPixels(skin, cape, getSkinPixelBounds("cape4"), 12, 13);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.1"), 9, 1);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.1"), 20, 1);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.2"), 9, 5);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.2"), 20, 5);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.3"), 9, 9);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.3"), 20, 9);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.4"), 9, 13);
        copyToPixels(skin, cape, getSkinPixelBounds("cape5.4"), 20, 13);

        copyToPixels(cape, cape, getSkinPixelBounds("capeVertL"), 0, 1);
        copyToPixels(cape, cape, getSkinPixelBounds("capeVertR"), 11, 1);
        copyToPixels(cape, cape, getSkinPixelBounds("capeHorizL"), 1, 0);
        copyToPixels(cape, cape, getSkinPixelBounds("capeHorizR"), 11, 0);

        return cape;
    }

    private static NativeImage getOrRemoveCoatTexture(NativeImage skin, int lengthOfCoat, boolean ignoreTopTexture) {

        NativeImage coat = new NativeImage(64, 64, false);
        coat.fillRect(0, 0, 64, 64, 0);

        //top
        if (!ignoreTopTexture) {
            copyToPixels(skin, coat, 4, 32, 7, 35 + lengthOfCoat, 20, 32);
            copyToPixels(skin, coat, 4, 48, 7, 51 + lengthOfCoat, 24, 32);
        }
        //sides
        copyToPixels(skin, coat, 0, 36, 7, 36 + lengthOfCoat, 16, 36);
        copyToPixels(skin, coat, 12, 36, 15, 36 + lengthOfCoat, 36, 36);
        copyToPixels(skin, coat, 4, 52, 15, 52 + lengthOfCoat, 24, 36);
        //ENCHANT AND EMISSIVES
        copyToPixels(skin, coat, 56, 16, 63, 47, 0, 0);
        return coat;

    }

    // modifiers are distance from x1,y1 to copy
    private static void copyToPixels(NativeImage source, NativeImage dest, int[] bounds, int copyToX, int CopyToY) {
        copyToPixels(source, dest, bounds[0], bounds[1], bounds[2], bounds[3], copyToX, CopyToY);
    }

    private static void copyToPixels(NativeImage source, NativeImage dest, int x1, int y1, int x2, int y2, int copyToX, int copyToY) {
        int copyToXRelative = copyToX - x1;
        int copyToYRelative = copyToY - y1;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                dest.setColor(x + copyToXRelative, y + copyToYRelative, source.getColor(x, y));
            }
        }
    }

    private static void deletePixels(NativeImage source, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                source.setColor(x, y, 0);
            }
        }
    }


    private static int countTransparentInBox(NativeImage img, int x1, int y1, int x2, int y2) {
        int counter = 0;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                //ranges from  0 to 127  then wraps around negatively -127 to -1  totalling 0 to 255
                int i = img.getOpacity(x, y);
                if (i < 0) {
                    i += 256;
                }
                //adjusted to 0 to 256
                counter += i;

            }
        }
        return counter;
    }

    private static boolean canTransparentSkin(NativeImage skin) {
        if (ETFConfigData.skinFeaturesEnableFullTransparency) {
            return true;
        } else {
            int countTransparent = 0;
            //map of bottom skin layer
            countTransparent += countTransparentInBox(skin, 8, 0, 23, 15);
            countTransparent += countTransparentInBox(skin, 0, 20, 55, 31);
            countTransparent += countTransparentInBox(skin, 0, 8, 7, 15);
            countTransparent += countTransparentInBox(skin, 24, 8, 31, 15);
            countTransparent += countTransparentInBox(skin, 0, 16, 11, 19);
            countTransparent += countTransparentInBox(skin, 20, 16, 35, 19);
            countTransparent += countTransparentInBox(skin, 44, 16, 51, 19);
            countTransparent += countTransparentInBox(skin, 20, 48, 27, 51);
            countTransparent += countTransparentInBox(skin, 36, 48, 43, 51);
            countTransparent += countTransparentInBox(skin, 16, 52, 47, 63);
            //do not allow skins under 40% ish total opacity
            //1648 is total pixels that are not allowed transparent by vanilla
            int average = (countTransparent / 1648); // should be 0 to 256
            //System.out.println("average ="+average);
            return average >= 100;
        }
    }

    private static NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown) {
        return returnOptimizedBlinkFace(baseSkin, eyeBounds, eyeHeightFromTopDown, null);
    }

    private static NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown, int[] secondLayerBounds) {
        NativeImage texture = new NativeImage(64, 64, false);
        texture.copyFrom(baseSkin);
        //copy face
        copyToPixels(baseSkin, texture, eyeBounds, 8, 8 + (eyeHeightFromTopDown - 1));
        //copy face overlay
        if (secondLayerBounds != null) {
            copyToPixels(baseSkin, texture, secondLayerBounds, 40, 8 + (eyeHeightFromTopDown - 1));
        }
        return texture;
    }

    @Nullable
    private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck) {
        return returnMatchPixels(baseSkin, boundsToCheck, null);
    }

    @Nullable
    private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck, @SuppressWarnings("SameParameterValue") @Nullable NativeImage second) {
        if (baseSkin == null) return null;

        boolean secondImage = second != null;
        Set<Integer> matchColors = new HashSet<>();
        for (int x = boundsToCheck[0]; x <= boundsToCheck[2]; x++) {
            for (int y = boundsToCheck[1]; y <= boundsToCheck[3]; y++) {
                if (baseSkin.getOpacity(x, y) != 0) {
                    matchColors.add(baseSkin.getColor(x, y));
                }
            }
        }
        if (matchColors.size() == 0) {
            return null;
        } else {
            NativeImage texture = !secondImage ? new NativeImage(64, 64, false) : new NativeImage(64, 32, false);
            if (!secondImage) {
                texture.copyFrom(baseSkin);
            } else {
                texture.copyFrom(second);
            }
            for (int x = 0; x < texture.getWidth(); x++) {
                for (int y = 0; y < texture.getHeight(); y++) {
                    if (!matchColors.contains(baseSkin.getColor(x, y))) {
                        texture.setColor(x, y, 0);
                    }
                }
            }
            return texture;
        }

    }


}
