package traben.entity_texture_features.features.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.config.screens.skin.ETFConfigScreenSkinTool;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.mixin.accessor.FileCacheAccessor;
import traben.entity_texture_features.mixin.accessor.PlayerSkinProviderAccessor;
import traben.entity_texture_features.mixin.accessor.PlayerSkinTextureAccessor;
import traben.entity_texture_features.utils.ETFUtils2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;


@SuppressWarnings("EnhancedSwitchMigration")
public class ETFPlayerTexture {

    public static final String SKIN_NAMESPACE = "etf_skin";
    public static final UUID Dev = UUID.fromString("fd22e573-178c-415a-94fe-e476b328abfd");
    //public static final UUID Dev2 = UUID.fromString("bc2d6979-ddde-4452-8c7d-caefa4aceb01");
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static final UUID Wife = UUID.fromString("cab7d2e2-519f-4b34-afbd-b65f4542b8a1");
    public static NativeImage clientPlayerOriginalSkinImageForTool = null;
    public Identifier baseEnchantIdentifier = null;
    public Identifier baseEnchantBlinkIdentifier = null;
    public Identifier baseEnchantBlink2Identifier = null;
//    public Identifier etfCapeIdentifier = null;
    public Identifier texturedNoseIdentifier = null;
    public Identifier texturedNoseIdentifierEmissive = null;
    public Identifier texturedNoseIdentifierEnchanted = null;
    public boolean hasVillagerNose = false;
    public boolean hasFeatures = false;
    public int coatStyle = 0;
    public int coatLength = 1;
    public int blinkType = 0;
    public int blinkHeight = 1;
    //public boolean THIS_SKIN_IS_IN_EDITOR = false;

//    public ETFTexture etfCape = null;
    public boolean hasEmissives = false;
    public boolean hasEnchant = false;
    //provides emissive patching and blinking functionality
    //all ETFPlayerTexture needs to do is build those textures and register them before this ETFTexture is made, and it will auto locate and apply them
    public ETFTexture etfTextureOfFinalBaseSkin;
//    public ETFConfigScreenSkinTool.CapeType capeType = ETFConfigScreenSkinTool.CapeType.NONE;
    public ETFConfigScreenSkinTool.NoseType noseType = ETFConfigScreenSkinTool.NoseType.NONE;
    public ETFPlayerEntity player;
    public boolean wasForcedSolid = false;
    //private boolean allowThisETFBaseSkin = true;
    Identifier coatIdentifier = null;
    Identifier coatEmissiveIdentifier = null;
    Identifier coatEnchantedIdentifier = null;
    boolean hasFatCoat = false;
    private boolean isTextureReady = false;
    //private boolean hasVanillaCape = false;
    private NativeImage originalSkin;
    private NativeImage originalCape;
    private int[] enchantCapeBounds = null;
    private int[] emissiveCapeBounds = null;
//    private Identifier etfCapeEmissiveIdentifier = null;
//    private Identifier etfCapeEnchantedIdentifier = null;
    private Identifier normalVanillaSkinIdentifier = null;


    public ETFPlayerTexture(ETFPlayerEntity player, Identifier rendererGivenSkin) {
        //initiate texture download as we need unprocessed texture from the skin server
        this.player = player;
        this.normalVanillaSkinIdentifier = rendererGivenSkin;
        //triggerSkinDownload();
        if (player instanceof PlayerEntity) {
            //normal player entity
            checkTexture(false);
        } else {
            //create a player texture for a player head block
            //this can have a historic skin and thus should not be driven by the actual players uuid
            //use the historic skin resource and ensure we do not bother to load a current skin for the player
            try {

                PlayerSkinTexture skin = (PlayerSkinTexture) ((FileCacheAccessor) ((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getSkinCache()).getTextureManager().getOrDefault(rendererGivenSkin, null);
                FileInputStream fileInputStream = new FileInputStream(((PlayerSkinTextureAccessor) skin).getCacheFile());
                NativeImage vanilla = NativeImage.read(fileInputStream);
                //System.out.println((vanilla != null) +" skin");
                fileInputStream.close();
                originalSkin = ETFUtils2.emptyNativeImage(64, 64);
                originalSkin.copyFrom(vanilla);
                vanilla.close();

                //originalSkin = ETFUtils2.getNativeImageElseNull(rendererGivenSkin);
                checkTexture(true);
            } catch (Exception e) {
                //e.printStackTrace();
                skinFailed();
            }
        }
    }

    //THIS REPRESENTS A NON FEATURED SKIN
    // must still create an object as the identifier is important to detect skin changes from other mods
    private ETFPlayerTexture(Identifier rendererGivenSkin) {
        this.player = null;
        this.normalVanillaSkinIdentifier = rendererGivenSkin;
    }

    public ETFPlayerTexture() {
        //THIS_SKIN_IS_IN_EDITOR = true;
        //exists only for tool
        this.player = (ETFPlayerEntity) MinecraftClient.getInstance().player;
        assert player != null;
        if (player.etf$getEntity() != null) {
            assert MinecraftClient.getInstance().player != null;
            NativeImage skin = ETFUtils2.getNativeImageElseNull(MinecraftClient.getInstance().player.getSkinTextures().texture());
            if (skin != null) {
                clientPlayerOriginalSkinImageForTool = skin;
                changeSkinToThisForTool(skin);
                return;
            }
        }

        ETFUtils2.logError("ETFPlayerTexture went wrong");
    }

    @Nullable
    private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck) {
        return returnMatchPixels(baseSkin, boundsToCheck, null);
    }

    // returns a native image with only pixels that match those contained in the boundsToCheck region of baseSkin
    // if second is not null it will return an altered version of that instead of baseSkin
    // will also return null if there is nothing to check or no matching pixels
    @Nullable
    private static NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck, @Nullable NativeImage second) {
        if (baseSkin == null || boundsToCheck == null) return null;

        boolean hasSecondImageToBeUsedAsBase = second != null;
        Set<Integer> matchColors = new HashSet<>();
        for (int x = boundsToCheck[0]; x <= boundsToCheck[2]; x++) {
            for (int y = boundsToCheck[1]; y <= boundsToCheck[3]; y++) {
                if (baseSkin.getOpacity(x, y) != 0) {
                    matchColors.add(baseSkin.getColor(x, y));
                }
            }
        }
        if (matchColors.isEmpty()) {
            return null;
        } else {
            NativeImage texture;
            if (!hasSecondImageToBeUsedAsBase) {
                texture = new NativeImage(baseSkin.getWidth(), baseSkin.getHeight(), false);
                texture.copyFrom(baseSkin);
            } else {
                texture = new NativeImage(second.getWidth(), second.getHeight(), false);
                texture.copyFrom(second);
            }
            for (int x = 0; x < texture.getWidth(); x++) {
                for (int y = 0; y < texture.getHeight(); y++) {
                    if (!matchColors.contains(texture.getColor(x, y))) {
                        texture.setColor(x, y, 0);
                    }
                }
            }
            return returnNullIfEmptyImage(texture);
        }

    }

    @Nullable
    private static NativeImage returnNullIfEmptyImage(NativeImage imageToCheck) {
        boolean foundAPixel = false;
        upper:
        for (int x = 0; x < imageToCheck.getWidth(); x++) {
            for (int y = 0; y < imageToCheck.getHeight(); y++) {
                if (imageToCheck.getColor(x, y) != 0) {
                    foundAPixel = true;
                    break upper;
                }
            }
        }
        return foundAPixel ? imageToCheck : null;
    }

    private static NativeImage returnCustomTexturedCape(NativeImage skin) {
        NativeImage cape = ETFUtils2.emptyNativeImage(64, 32);
        NativeImage elytra = ETFUtils2.getNativeImageElseNull(new Identifier("textures/entity/elytra.png"));
        if (elytra == null || elytra.getWidth() != 64 || elytra.getHeight() != 32) {
            elytra = ETFUtils2.getNativeImageElseNull(new Identifier(MOD_ID, "textures/capes/default_elytra.png"));
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
            case "cape5" -> new int[]{44, 48, 51, 51};
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

    private static void setNotTransparentInBox(NativeImage img, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                //ranges from  0 to 127  then wraps around negatively -127 to -1  totalling 0 to 255
                if (img.getOpacity(x, y) != -1) {
                    int col = img.getColor(x, y);
                    //set colour to not be transparent
                    img.setColor(x, y, ColorHelper.Argb.getArgb(
                            -1,
                            ColorHelper.Argb.getRed(col),
                            ColorHelper.Argb.getGreen(col),
                            ColorHelper.Argb.getBlue(col)
                    ));
                }
            }
        }
    }

    private static void parseSkinTransparency(NativeImage skin, boolean forceSolidSkin) {
        if (forceSolidSkin || !ETFConfigData.skinFeaturesEnableTransparency) {
            forceSolidLowerSkin(skin);
            return;
        }
        if (!ETFConfigData.skinFeaturesEnableFullTransparency) {
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
            boolean isSkinMoreThan40PercentOpaque = average >= 100;
            if (!isSkinMoreThan40PercentOpaque) {
                forceSolidLowerSkin(skin);
            }
        }
    }

    private static void forceSolidLowerSkin(NativeImage skin) {
        try {
            setNotTransparentInBox(skin, 8, 0, 23, 15);
            setNotTransparentInBox(skin, 0, 20, 55, 31);
            setNotTransparentInBox(skin, 0, 8, 7, 15);
            setNotTransparentInBox(skin, 24, 8, 31, 15);
            setNotTransparentInBox(skin, 0, 16, 11, 19);
            setNotTransparentInBox(skin, 20, 16, 35, 19);
            setNotTransparentInBox(skin, 44, 16, 51, 19);
            setNotTransparentInBox(skin, 20, 48, 27, 51);
            setNotTransparentInBox(skin, 36, 48, 43, 51);
            setNotTransparentInBox(skin, 16, 52, 47, 63);
        } catch (Exception ignored) {
        }
    }
//    private static boolean isSkinNotTooTransparent(NativeImage skin) {
//        if (ETFConfigData.skinFeaturesEnableFullTransparency) {
//            return true;
//        } else {
//            int countTransparent = 0;
//            //map of bottom skin layer
//            countTransparent += countTransparentInBox(skin, 8, 0, 23, 15);
//            countTransparent += countTransparentInBox(skin, 0, 20, 55, 31);
//            countTransparent += countTransparentInBox(skin, 0, 8, 7, 15);
//            countTransparent += countTransparentInBox(skin, 24, 8, 31, 15);
//            countTransparent += countTransparentInBox(skin, 0, 16, 11, 19);
//            countTransparent += countTransparentInBox(skin, 20, 16, 35, 19);
//            countTransparent += countTransparentInBox(skin, 44, 16, 51, 19);
//            countTransparent += countTransparentInBox(skin, 20, 48, 27, 51);
//            countTransparent += countTransparentInBox(skin, 36, 48, 43, 51);
//            countTransparent += countTransparentInBox(skin, 16, 52, 47, 63);
//            //do not allow skins under 40% ish total opacity
//            //1648 is total pixels that are not allowed transparent by vanilla
//            int average = (countTransparent / 1648); // should be 0 to 256
//            //System.out.println("average ="+average);
//            return average >= 100;
//        }
//    }

    private static NativeImage getCoatTexture(NativeImage skin, int lengthOfCoat, boolean ignoreTopTexture) {

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
//        //ENCHANT AND EMISSIVES
//        copyToPixels(skin, coat, 56, 16, 63, 47, 0, 0);
        return coat;

    }

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

    public static int getSkinPixelColourToNumber(int color) {
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


    public boolean isCorrectObjectForThisSkin(Identifier check) {
        return check.equals(normalVanillaSkinIdentifier);
    }

//    public boolean hasCustomCape() {
//        return etfCape != null;
//    }

    @Nullable
    public Identifier getBaseTextureIdentifierOrNullForVanilla(PlayerEntity player) {
        return getBaseTextureIdentifierOrNullForVanilla((ETFPlayerEntity) player);
    }

    @Nullable
    public Identifier getBaseTextureIdentifierOrNullForVanilla(ETFPlayerEntity player) {
        this.player = player;//refresh player data
        if (etfTextureOfFinalBaseSkin != null) {
            if (/*allowThisETFBaseSkin &&*/ canUseFeaturesForThisPlayer()) {
                return etfTextureOfFinalBaseSkin.getTextureIdentifier(player);
            } else if (ETFConfigData.tryETFTransparencyForAllSkins) {
                return etfTextureOfFinalBaseSkin.getTextureIdentifier(player);
            }
        }
        return null;
    }

    @Nullable
    public Identifier getBaseHeadTextureIdentifierOrNullForVanilla() {
        if (etfTextureOfFinalBaseSkin != null) {
            if (/*allowThisETFBaseSkin &&*/ canUseFeaturesForThisPlayer()) {
                return etfTextureOfFinalBaseSkin.getTextureIdentifier(null);
            } else if (ETFConfigData.tryETFTransparencyForAllSkins) {
                return etfTextureOfFinalBaseSkin.getTextureIdentifier(null);
            }
        }
        return null;
    }

    @Nullable
    public Identifier getBaseTextureEmissiveIdentifierOrNullForNone() {
        if (hasEmissives && canUseFeaturesForThisPlayer() && etfTextureOfFinalBaseSkin != null) {
            return etfTextureOfFinalBaseSkin.getEmissiveIdentifierOfCurrentState();
        }
        return null;
    }

    @Nullable
    public Identifier getBaseTextureEnchantIdentifierOrNullForNone() {
        if (hasEnchant && canUseFeaturesForThisPlayer() && etfTextureOfFinalBaseSkin != null) {
            switch (etfTextureOfFinalBaseSkin.currentTextureState) {
                case NORMAL, NORMAL_PATCHED:
                    return baseEnchantIdentifier;
                case BLINK, BLINK_PATCHED:
                    return baseEnchantBlinkIdentifier;
                case BLINK2, BLINK2_PATCHED:
                    return baseEnchantBlink2Identifier;
                default:
                    return null;
            }
        }
        return null;
    }

//    public void renderFeatures(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityModel<AbstractClientPlayerEntity> model) {
//        if (canUseFeaturesForThisPlayer()) {
//            if (etfCapeIdentifier != null) {
//                // VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(etfCapeIdentifier));
//                // model.renderCape(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
//
//                if (etfCapeEmissiveIdentifier != null) {
//                    VertexConsumer emissiveVert = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(etfCapeEmissiveIdentifier));
//                    model.renderCape(matrixStack, emissiveVert, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV);
//                }
//                if (etfCapeEnchantedIdentifier != null) {
//                    VertexConsumer enchantVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(etfCapeEnchantedIdentifier), false, true);
//                    model.renderCape(matrixStack, enchantVert, light, OverlayTexture.DEFAULT_UV);
//                }
//
//            }
//        }
//    }

    public boolean canUseFeaturesForThisPlayer() {
        return isTextureReady
                && hasFeatures
                && (//not on enemy team or doesn't matter
                ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                        || (player.etf$isTeammate(MinecraftClient.getInstance().player)
                        || player.etf$getScoreboardTeam() == null));
    }


    private void initiateThirdPartyCapeDownload(String capeUrl) {
        CompletableFuture.runAsync(() -> {
            HttpURLConnection httpURLConnection;
            try {
                httpURLConnection = (HttpURLConnection) (new URL(capeUrl)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() / 100 == 2) {
                    InputStream inputStream = httpURLConnection.getInputStream();

                    MinecraftClient.getInstance().execute(() -> {
                        try {
                            NativeImage one = NativeImage.read(inputStream);
                            this.receiveThirdPartyCape(one);
                        } catch (Exception e) {
                            ETFUtils2.logError("ThirdPartyCapeDownload failed for player:" + player.etf$getName().getString() + "retrying again later " + e);
                            e.printStackTrace();
                            //this.skinFailed(false);
                        }
                    });
                }
            } catch (Exception var6) {
                ETFUtils2.logError("ThirdPartyCapeDownload2 failed for player:" + player.etf$getName().getString() + "retrying again later" + var6);
                //this.skinFailed(false);
            }
        }, Util.getMainWorkerExecutor());
    }

    public void receiveThirdPartyCape(@NotNull NativeImage capeImage) {
        //optifine resizes them for space cause expensive servers I guess

        Identifier newCapeId = new Identifier(SKIN_NAMESPACE, player.etf$getUuid().toString().replaceAll("/[^a-z]/g", "") + "_cape_third_party.png");
        //boolean changed = false;
        NativeImage resizedImage;

        if (capeImage.getWidth() % capeImage.getHeight() != 0) {
            //resize optifine image
            int newWidth = 64;
            while (newWidth < capeImage.getWidth()) {
                newWidth = newWidth + newWidth;
            }
            int newHeight = newWidth / 2;
            try {
                resizedImage = ETFUtils2.emptyNativeImage(newWidth, newHeight);
                for (int x = 0; x < capeImage.getWidth(); x++) {
                    for (int y = 0; y < capeImage.getHeight(); y++) {
                        resizedImage.setColor(x, y, capeImage.getColor(x, y));
                    }
                }
                //ETFUtils2.registerNativeImageToIdentifier(resizedImage, newCapeId);
                //capeImage = resizedImage;
                //changed = true;
                checkThirdPartyCapeFeaturesAndFinalize(resizedImage, newCapeId);
            } catch (Exception e) {
                ETFUtils2.logError("optifine cape resize failed");
            }
        } else {
            checkThirdPartyCapeFeaturesAndFinalize(capeImage, newCapeId);
        }
    }

    private void checkThirdPartyCapeFeaturesAndFinalize(NativeImage capeImage, Identifier etfCapeIdentifier) {

        Identifier etfCapeEmissiveIdentifier = null;
        Identifier etfCapeEnchantedIdentifier = null;

        NativeImage checkCapeEmissive = returnMatchPixels(originalSkin, emissiveCapeBounds, capeImage);
        //UUID_PLAYER_HAS_EMISSIVE_CAPE.put(id, checkCape != null);
        if (checkCapeEmissive != null) {
            Identifier newCapeEmissive = new Identifier(SKIN_NAMESPACE, player.etf$getUuid() + "_cape_third_party_e.png");
            ETFUtils2.registerNativeImageToIdentifier(checkCapeEmissive, newCapeEmissive);
            etfCapeEmissiveIdentifier = newCapeEmissive;
        }
        NativeImage checkCapeEnchant = returnMatchPixels(originalSkin, enchantCapeBounds, capeImage);
        //UUID_PLAYER_HAS_EMISSIVE_CAPE.put(id, checkCape != null);
        if (checkCapeEnchant != null) {
            Identifier newCapeEnchanted = new Identifier(SKIN_NAMESPACE, player.etf$getUuid() + "_cape_third_party_enchant.png");
            ETFUtils2.registerNativeImageToIdentifier(checkCapeEnchant, newCapeEnchanted);
            etfCapeEnchantedIdentifier = newCapeEnchanted;
        }
        ETFUtils2.registerNativeImageToIdentifier(capeImage, etfCapeIdentifier);
//        etfCape = new ETFTexture(etfCapeIdentifier,null,null,
//                etfCapeEmissiveIdentifier,null,null,
//                etfCapeEnchantedIdentifier,null,null,
//                null,null,null);
    }

    private void skinFailed() {
        if (!(MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreenSkinTool)) {
            ETFManager.getInstance().PLAYER_TEXTURE_MAP.put(player.etf$getUuid(), new ETFPlayerTexture(normalVanillaSkinIdentifier));
        } else {
            ETFUtils2.logError("something went wrong applying skin in tool, or skin features are not added");
        }
        //this object is now unreachable
    }


    public void checkTexture(boolean skipSkinLoad) {
        if (!skipSkinLoad) {
            try {
                PlayerSkinTexture skin = (PlayerSkinTexture) ((FileCacheAccessor) ((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getSkinCache()).getTextureManager().getOrDefault(normalVanillaSkinIdentifier, null);

                FileInputStream fileInputStream = new FileInputStream(((PlayerSkinTextureAccessor) skin).getCacheFile());
                NativeImage vanilla = NativeImage.read(fileInputStream);
                //System.out.println((vanilla != null) +" skin");
                fileInputStream.close();
                originalSkin = ETFUtils2.emptyNativeImage(64, 64);
                originalSkin.copyFrom(vanilla);
                if (MinecraftClient.getInstance().player != null && player.etf$getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                    clientPlayerOriginalSkinImageForTool = originalSkin;
                }
                vanilla.close();
                //try cape
                try {
                    Identifier capeId = ((AbstractClientPlayerEntity) player).getSkinTextures().capeTexture();
                    PlayerSkinTexture cape = (PlayerSkinTexture) ((FileCacheAccessor) ((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getCapeCache()).getTextureManager().getOrDefault(capeId, null);
                    if (cape != null) {
                        FileInputStream fileInputStreamCape = new FileInputStream(((PlayerSkinTextureAccessor) cape).getCacheFile());
                        NativeImage vanillaCape = NativeImage.read(fileInputStreamCape);
                        //System.out.println((vanilla != null) +" skin");
                        fileInputStreamCape.close();
                        originalCape = ETFUtils2.emptyNativeImage(64, 32);
                        originalCape.copyFrom(vanillaCape);
                        vanillaCape.close();
                    }
                } catch (Exception e) {
                    // System.out.println("cape failed no textures loaded");
                }
            } catch (Exception e) {
                skinFailed();
                // System.out.println("skin failed no textures loaded");
                return;
            }
        }
        // System.out.println("endskin");

        UUID id = player.etf$getUuid();
        NativeImage modifiedCape;
        if (originalCape != null) {
            modifiedCape = ETFUtils2.emptyNativeImage(originalCape.getWidth(), originalCape.getHeight());
            modifiedCape.copyFrom(originalCape);
        } else {
            modifiedCape = ETFUtils2.emptyNativeImage(64, 32);
        }
        NativeImage modifiedSkin = ETFUtils2.emptyNativeImage(originalSkin.getWidth(), originalSkin.getHeight());
        modifiedSkin.copyFrom(originalSkin);

//        if (ETFConfigData.skinFeaturesPrintETFReadySkin && MinecraftClient.getInstance().player != null && id.equals(MinecraftClient.getInstance().player.getUuid())) {
//            ETFUtils2.logMessage("Skin feature layout is being applied to a copy of your skin please wait...", true);
//            printPlayerSkinCopyWithFeatureOverlay(originalSkin);
//            ETFConfigData.skinFeaturesPrintETFReadySkin = false;
//            ETFUtils2.saveConfig();
//        }
        if (originalSkin != null) {
            if (originalSkin.getColor(1, 16) == -16776961 &&
                    originalSkin.getColor(0, 16) == -16777089 &&
                    originalSkin.getColor(0, 17) == -16776961 &&
                    originalSkin.getColor(2, 16) == -16711936 &&
                    originalSkin.getColor(3, 16) == -16744704 &&
                    originalSkin.getColor(3, 17) == -16711936 &&
                    originalSkin.getColor(0, 18) == -65536 &&
                    originalSkin.getColor(0, 19) == -8454144 &&
                    originalSkin.getColor(1, 19) == -65536 &&
                    originalSkin.getColor(3, 18) == -1 &&
                    originalSkin.getColor(2, 19) == -1 &&
                    originalSkin.getColor(3, 18) == -1
            ) {


                hasFeatures = true;
                ETFUtils2.logMessage("Found Player {" + player.etf$getName().getString() + "} with ETF texture features in skin.", false);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //locate and convert choices to ints
                int[] choiceBoxChoices = {
                        getSkinPixelColourToNumber(originalSkin.getColor(52, 16)),//blink
                        getSkinPixelColourToNumber(originalSkin.getColor(52, 17)),//jacket choice
                        getSkinPixelColourToNumber(originalSkin.getColor(52, 18)),//jacket height
                        getSkinPixelColourToNumber(originalSkin.getColor(52, 19)),//blink height
                        getSkinPixelColourToNumber(originalSkin.getColor(53, 16)),//cape choice
                        getSkinPixelColourToNumber(originalSkin.getColor(53, 17)),//nose choice
                        getSkinPixelColourToNumber(originalSkin.getColor(53, 18)),//no transparency choice
//                        getSkinPixelColourToNumber(originalSkin.getColor(53, 18)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(53, 19)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(54, 16)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(54, 17)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(54, 18)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(54, 19)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(55, 16)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(55, 17)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(55, 18)),
//                        getSkinPixelColourToNumber(originalSkin.getColor(55, 19))
                };
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //deprecated old villager nose method
                boolean noseUpper = (getSkinPixelColourToNumber(originalSkin.getColor(43, 13)) == 666 && getSkinPixelColourToNumber(originalSkin.getColor(44, 13)) == 666 &&
                        getSkinPixelColourToNumber(originalSkin.getColor(43, 14)) == 666 && getSkinPixelColourToNumber(originalSkin.getColor(44, 14)) == 666 &&
                        getSkinPixelColourToNumber(originalSkin.getColor(43, 15)) == 666 && getSkinPixelColourToNumber(originalSkin.getColor(44, 15)) == 666);
                boolean noseLower = (getSkinPixelColourToNumber(originalSkin.getColor(11, 13)) == 666 && getSkinPixelColourToNumber(originalSkin.getColor(12, 13)) == 666 &&
                        getSkinPixelColourToNumber(originalSkin.getColor(11, 14)) == 666 && getSkinPixelColourToNumber(originalSkin.getColor(12, 14)) == 666 &&
                        getSkinPixelColourToNumber(originalSkin.getColor(11, 15)) == 666 && getSkinPixelColourToNumber(originalSkin.getColor(12, 15)) == 666);
                hasVillagerNose = noseLower || noseUpper;
                boolean removeNosePixels = noseUpper;
                if (noseUpper) {
                    deletePixels(modifiedSkin, 43, 13, 44, 15);
                }
                //////////////////////////////////////////
                NativeImage noseTexture = null;
                int noseChoice = choiceBoxChoices[5];
                if (noseChoice >= 1 && noseChoice <= 9) {
                    if (noseChoice == 1 || noseChoice == 7 || noseChoice == 8 || noseChoice == 9) {
                        hasVillagerNose = true;
                        noseType = noseChoice == 1 || noseChoice == 8 ? ETFConfigScreenSkinTool.NoseType.VILLAGER : ETFConfigScreenSkinTool.NoseType.VILLAGER_TEXTURED;
                        if(noseChoice > 7){
                            removeNosePixels = true;
                            deletePixels(modifiedSkin, 43, 13, 44, 15);
                        }
                    } else {
                        noseTexture = ETFUtils2.emptyNativeImage(8, 8);
                        int[] bounds;
                        switch (noseChoice) {
                            case 3:
                                noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_2;
                                bounds = getSkinPixelBounds("cape2");
                                break;
                            case 4:
                                noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_3;
                                bounds = getSkinPixelBounds("cape3");
                                break;
                            case 5:
                                noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_4;
                                bounds = getSkinPixelBounds("cape4");
                                break;
                            case 6:
                                noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_5;
                                bounds = getSkinPixelBounds("cape5");
                                break;
                            default:
                                noseType = ETFConfigScreenSkinTool.NoseType.TEXTURED_1;
                                bounds = getSkinPixelBounds("cape1");
                                break;
                        }

                        int noseY = 0;
                        for (int x = bounds[0]; x <= bounds[2]; x++) {
                            int noseX = 0;
                            for (int y = bounds[1]; y <= bounds[3]; y++) {
                                noseTexture.setColor(noseX, noseY, originalSkin.getColor(x, y));
                                noseX++;
                            }
                            noseY++;
                        }
                        //copy flip to other side
                        for (int x = 4; x < 8; x++) {
                            for (int y = 0; y < 8; y++) {
                                noseTexture.setColor(x, y, noseTexture.getColor(7 - x, y));
                            }
                        }
                        //correct vertical alignment
                        for (int x = 0; x < 8; x++) {
                            for (int y = 0; y < 4; y++) {
                                int lowerColour = noseTexture.getColor(x, y + 4);
                                noseTexture.setColor(x, y + 4, noseTexture.getColor(x, y));
                                noseTexture.setColor(x, y, lowerColour);
                            }
                        }

                        texturedNoseIdentifier = new Identifier(SKIN_NAMESPACE, id + "_nose.png");
                        ETFUtils2.registerNativeImageToIdentifier(noseTexture, texturedNoseIdentifier);
                    }
                }


                //check for coat bottom
                //pink to copy coat    light blue to remove from legs
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                NativeImage coatSkin = null;
                int controllerCoat = choiceBoxChoices[1];
                if (controllerCoat >= 1 && controllerCoat <= 8) {
                    coatStyle = controllerCoat;
                    int lengthOfCoat = choiceBoxChoices[2] - 1;
                    coatLength = lengthOfCoat + 1;
                    coatIdentifier = new Identifier(SKIN_NAMESPACE, id + "_coat.png");
                    coatSkin = getCoatTexture(originalSkin, lengthOfCoat, controllerCoat >= 5);
                    ETFUtils2.registerNativeImageToIdentifier(coatSkin, coatIdentifier);
                    //UUID_PLAYER_HAS_COAT.put(id, true);
                    if (controllerCoat == 2 || controllerCoat == 4 || controllerCoat == 6 || controllerCoat == 8) {
                        //delete original pixel from skin
                        deletePixels(modifiedSkin, 4, 32, 7, 35);
                        deletePixels(modifiedSkin, 4, 48, 7, 51);
                        deletePixels(modifiedSkin, 0, 36, 15, 36 + lengthOfCoat);
                        deletePixels(modifiedSkin, 0, 52, 15, 52 + lengthOfCoat);
                    }
                    //red or green make fat coat
                    hasFatCoat = controllerCoat == 3 || controllerCoat == 4 || controllerCoat == 7 || controllerCoat == 8;
                } else {
                    coatIdentifier = null;
                }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                wasForcedSolid = choiceBoxChoices[6] == 1;

//                if (ETFConfigData.skinFeaturesEnableTransparency) {
//                    if (isSkinNotTooTransparent(originalSkin)) {
//                        allowThisETFBaseSkin = true;
//                    } else {
//                        ETFUtils2.logMessage("Skin was too transparent or had other problems", false);
//                        allowThisETFBaseSkin = false;
//                    }
//                }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //create and register blink textures this will allow the ETFTexture to build these automatically
                NativeImage blinkSkinFile = null;
                NativeImage blinkSkinFile2 = null;
                Identifier blinkIdentifier = new Identifier(SKIN_NAMESPACE, id + "_blink.png");
                Identifier blink2Identifier = new Identifier(SKIN_NAMESPACE, id + "_blink2.png");

                int blinkChoice = choiceBoxChoices[0];
                this.blinkType = blinkChoice;
                if (blinkChoice >= 1 && blinkChoice <= 5) {

                    //check if lazy blink
                    if (blinkChoice <= 2) {
                        if(removeNosePixels){
                            deletePixels(modifiedSkin, 35, 5, 36, 7);
                        }
                        //blink 1 frame if either pink or blue optional
                        blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("face1"), 1, getSkinPixelBounds("face3"));
                        parseSkinTransparency(blinkSkinFile, wasForcedSolid);
                        ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);

                        //blink is 2 frames with blue optional
                        if (blinkChoice == 2) {
                            if(removeNosePixels){
                                deletePixels(modifiedSkin, 59, 5, 60, 7);
                            }
                            blinkSkinFile2 = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("face2"), 1, getSkinPixelBounds("face4"));
                            parseSkinTransparency(blinkSkinFile2, wasForcedSolid);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                        }
                    } else {//optimized blink
                        int eyeHeightTopDown = choiceBoxChoices[3];
                        this.blinkHeight = eyeHeightTopDown;
                        if (eyeHeightTopDown > 8 || eyeHeightTopDown < 1) {
                            eyeHeightTopDown = 1;
                        }
                        //optimized 1p high eyes
                        if (blinkChoice == 3) {
                            blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEyeSmall"), eyeHeightTopDown);
                            parseSkinTransparency(blinkSkinFile, wasForcedSolid);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);

                        } else if (blinkChoice == 4) {
                            blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                            parseSkinTransparency(blinkSkinFile, wasForcedSolid);
                            parseSkinTransparency(blinkSkinFile2, wasForcedSolid);

                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                        } else /*if( blinkChoice == 5)*/ {
                            blinkSkinFile = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(modifiedSkin, getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
                            parseSkinTransparency(blinkSkinFile, wasForcedSolid);
                            parseSkinTransparency(blinkSkinFile2, wasForcedSolid);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, blinkIdentifier);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, blink2Identifier);
                        }
                    }
                }
                if (blinkSkinFile == null) blinkIdentifier = null;
                if (blinkSkinFile2 == null) blink2Identifier = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                Identifier etfCapeIdentifier = null;
//                Identifier etfCapeEmissiveIdentifier = null;
//                Identifier etfCapeEnchantedIdentifier = null;
//
//                //check for cape recolor
//                int capeChoice1 = choiceBoxChoices[4];
//                // custom cape data experiment
//                // https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe
//                //downloadImageFromUrl(player, "https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe", "etf$CAPE",null,true);
//                if ((capeChoice1 >= 1 && capeChoice1 <= 4)) {
//                    switch (capeChoice1) {
//                        case 1 -> { //custom in skin
//                            capeType = ETFConfigScreenSkinTool.CapeType.CUSTOM;
//                            modifiedCape.copyFrom(returnCustomTexturedCape(originalSkin));
//                        }
//                        case 2 -> {
//                            capeType = ETFConfigScreenSkinTool.CapeType.MINECRAFT_CAPES_NET;
//                            modifiedCape = null;
//                            // minecraft capes mod
//                            //https://minecraftcapes.net/profile/fd22e573178c415a94fee476b328abfd/cape/
//                            initiateThirdPartyCapeDownload("https://api.minecraftcapes.net/profile/" + player.etf$getUuidAsString().replace("-", "") + "/cape/");
//
//                        }
//                        case 3 -> {
//                            capeType = ETFConfigScreenSkinTool.CapeType.OPTIFINE;
//                            modifiedCape = null;
//                            //  https://optifine.net/capes/Benjamin.png
//                            initiateThirdPartyCapeDownload("https://optifine.net/capes/" + player.etf$getName().getString() + ".png");
//
//                        }
//                        case 4 -> {
//                            capeType = ETFConfigScreenSkinTool.CapeType.ETF;
//                            NativeImage cape = ETFUtils2.getNativeImageElseNull(new Identifier(MOD_ID, "textures/capes/etf.png"));
//                            if (cape != null && !ETFUtils2.isNativeImageEmpty(modifiedCape)) {
//                                modifiedCape.copyFrom(cape);
//                            }
//                        }
//                        default -> {
//                            // cape = getNativeImageFromID(new Identifier("etf:capes/blank.png"));
//                        }
//                    }
//                }
//                if (modifiedCape != null && !ETFUtils2.isNativeImageEmpty(modifiedCape)) {
//                    //if ((capeChoice1 >= 1 && capeChoice1 <= 4) || capeChoice1 == 666) {//custom chosen
//                    etfCapeIdentifier = new Identifier(SKIN_NAMESPACE, id.toString().replaceAll("/[^a-z]/g", "") + "_cape.png");
//                    ETFUtils2.registerNativeImageToIdentifier(modifiedCape, etfCapeIdentifier);
//
//                    //UUID_PLAYER_HAS_CUSTOM_CAPE.put(id, true);
//                    // }
//                }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //check for marker choices
                //  1 = Emissives,  2 = Enchanted
                List<Integer> markerChoices = List.of(getSkinPixelColourToNumber(originalSkin.getColor(1, 17)),
                        getSkinPixelColourToNumber(originalSkin.getColor(1, 18)),
                        getSkinPixelColourToNumber(originalSkin.getColor(2, 17)),
                        getSkinPixelColourToNumber(originalSkin.getColor(2, 18)));
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //emissives
                NativeImage emissiveImage = null;
                NativeImage emissiveBlinkImage = null;
                NativeImage emissiveBlink2Image = null;
                Identifier emissiveIdentifier = null;
                Identifier blinkEmissiveIdentifier = null;//new Identifier( SKIN_NAMESPACE , id + "_blink_e.png");
                Identifier blink2EmissiveIdentifier = null;//new Identifier( SKIN_NAMESPACE , id + "_blink2_e.png");
//                NativeImage emissiveCape = null;
                hasEmissives = markerChoices.contains(1);
                if (hasEmissives) {
                    int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(1) + 1));
                    emissiveCapeBounds = boxChosenBounds;
                    emissiveImage = returnMatchPixels(modifiedSkin, boxChosenBounds);

                    if (emissiveImage != null) {
                        emissiveIdentifier = new Identifier(SKIN_NAMESPACE, id + "_e.png");
                        ETFUtils2.registerNativeImageToIdentifier(emissiveImage, emissiveIdentifier);
                        if (blinkSkinFile != null) {
                            emissiveBlinkImage = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            if (emissiveBlinkImage != null) {
                                blinkEmissiveIdentifier = new Identifier(SKIN_NAMESPACE, id + "_blink_e.png");
                                ETFUtils2.registerNativeImageToIdentifier(emissiveBlinkImage, blinkEmissiveIdentifier);
                            }
                            //registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils2::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                        if (blinkSkinFile2 != null) {
                            emissiveBlink2Image = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            if (emissiveBlink2Image != null) {
                                blink2EmissiveIdentifier = new Identifier(SKIN_NAMESPACE, id + "_blink2_e.png");
                                ETFUtils2.registerNativeImageToIdentifier(emissiveBlink2Image, blink2EmissiveIdentifier);
                            }
                            //registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils2::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(modifiedSkin, boxChosenBounds, coatSkin);

                            //UUID_PLAYER_HAS_EMISSIVE_COAT.put(id, checkCoat != null);
                            if (checkCoat != null) {
                                coatEmissiveIdentifier = new Identifier(SKIN_NAMESPACE, id + "_coat_e.png");
                                ETFUtils2.registerNativeImageToIdentifier(checkCoat, coatEmissiveIdentifier);
                            }
                        }
//                        if (modifiedCape != null) {
//
//                            emissiveCape = returnMatchPixels(modifiedSkin, boxChosenBounds, modifiedCape);
//                            //UUID_PLAYER_HAS_EMISSIVE_CAPE.put(id, checkCape != null);
//                            if (emissiveCape != null) {
//                                etfCapeEmissiveIdentifier = new Identifier(SKIN_NAMESPACE, id + "_cape_e.png");
//                                ETFUtils2.registerNativeImageToIdentifier(emissiveCape, etfCapeEmissiveIdentifier);
//                            }
//                        }
                        if (noseTexture != null) {
                            NativeImage checkNose = returnMatchPixels(modifiedSkin, boxChosenBounds, noseTexture);
                            //UUID_PLAYER_HAS_EMISSIVE_CAPE.put(id, checkCape != null);
                            if (checkNose != null) {
                                texturedNoseIdentifierEmissive = new Identifier(SKIN_NAMESPACE, id + "_nose_e.png");
                                ETFUtils2.registerNativeImageToIdentifier(checkNose, texturedNoseIdentifierEmissive);
                            }
                        }
                    } else {
                        hasEmissives = false;
                    }
                }
//                if (capeType == ETFConfigScreenSkinTool.CapeType.ETF) {
//                    etfCapeEmissiveIdentifier = new Identifier(MOD_ID, "textures/capes/etf_e.png");
//                }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //enchant
                hasEnchant = markerChoices.contains(2);
                if (hasEnchant) {
                    int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(2) + 1));
                    enchantCapeBounds = boxChosenBounds;
                    NativeImage check = returnMatchPixels(modifiedSkin, boxChosenBounds);
                    if (check != null) {
                        baseEnchantIdentifier = new Identifier(SKIN_NAMESPACE, id + "_enchant.png");
                        ETFUtils2.registerNativeImageToIdentifier(check, baseEnchantIdentifier);
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            if (checkBlink != null) {
                                baseEnchantBlinkIdentifier = new Identifier(SKIN_NAMESPACE, id + "_blink_enchant.png");
                                ETFUtils2.registerNativeImageToIdentifier(checkBlink, baseEnchantBlinkIdentifier);
                            }
                            //registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils2::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            if (checkBlink != null) {
                                baseEnchantBlink2Identifier = new Identifier(SKIN_NAMESPACE, id + "_blink2_enchant.png");
                                ETFUtils2.registerNativeImageToIdentifier(checkBlink, baseEnchantBlink2Identifier);
                            }
                            //registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils2::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(modifiedSkin, boxChosenBounds, coatSkin);

                            //UUID_PLAYER_HAS_EMISSIVE_COAT.put(id, checkCoat != null);
                            if (checkCoat != null) {
                                coatEnchantedIdentifier = new Identifier(SKIN_NAMESPACE, id + "_coat_enchant.png");
                                ETFUtils2.registerNativeImageToIdentifier(checkCoat, coatEnchantedIdentifier);
                            }
                        }
//                        if (modifiedCape != null) {
//
//                            NativeImage checkCape = returnMatchPixels(modifiedSkin, boxChosenBounds, modifiedCape);
//                            //UUID_PLAYER_HAS_EMISSIVE_CAPE.put(id, checkCape != null);
//                            if (checkCape != null) {
//
//                                etfCapeEnchantedIdentifier = new Identifier(SKIN_NAMESPACE, id + "_cape_enchant.png");
//                                ETFUtils2.registerNativeImageToIdentifier(checkCape, etfCapeEnchantedIdentifier);
//                            }
//                        }
                        if (noseTexture != null) {
                            NativeImage checkNose = returnMatchPixels(modifiedSkin, boxChosenBounds, noseTexture);
                            //UUID_PLAYER_HAS_EMISSIVE_CAPE.put(id, checkCape != null);
                            if (checkNose != null) {
                                texturedNoseIdentifierEnchanted = new Identifier(SKIN_NAMESPACE, id + "_nose_enchant.png");
                                ETFUtils2.registerNativeImageToIdentifier(checkNose, texturedNoseIdentifierEnchanted);
                            }
                        }
                    } else {
                        hasEnchant = false;
                    }
                }

                parseSkinTransparency(modifiedSkin, wasForcedSolid);


                Identifier modifiedSkinBlinkPatchedIdentifier = null;
                Identifier modifiedSkinPatchedIdentifier = null;
                Identifier modifiedSkinBlink2PatchedIdentifier = null;

//                Identifier modifiedCapePatchedIdentifier = etfCapeIdentifier;
                if (hasEmissives) {
                    if (emissiveImage != null) {
                        modifiedSkinPatchedIdentifier = new Identifier(SKIN_NAMESPACE, id + "_e_patched.png");
                        ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(modifiedSkin, emissiveImage);

                        ETFUtils2.registerNativeImageToIdentifier(modifiedSkin, modifiedSkinPatchedIdentifier);
                        if (blinkSkinFile != null) {
                            modifiedSkinBlinkPatchedIdentifier = new Identifier(SKIN_NAMESPACE, id + "_blink_e_patched.png");
                            ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(blinkSkinFile, emissiveBlinkImage);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile, modifiedSkinBlinkPatchedIdentifier);
                        }
                        if (blinkSkinFile2 != null) {
                            modifiedSkinBlink2PatchedIdentifier = new Identifier(SKIN_NAMESPACE, id + "_blink2_e_patched.png");
                            ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(blinkSkinFile2, emissiveBlink2Image);
                            ETFUtils2.registerNativeImageToIdentifier(blinkSkinFile2, modifiedSkinBlink2PatchedIdentifier);
                        }
                    }
//                    if (emissiveCape != null) {
//                        modifiedCapePatchedIdentifier = new Identifier(SKIN_NAMESPACE, id + "_cape_e_patched.png");
//                        ETFTexture.patchTextureToRemoveZFightingWithOtherTexture(modifiedCape, emissiveCape);
//                        ETFUtils2.registerNativeImageToIdentifier(modifiedCape, modifiedCapePatchedIdentifier);
//                    }
                }



                Identifier modifiedSkinIdentifier = new Identifier(SKIN_NAMESPACE, id + ".png");
                ETFUtils2.registerNativeImageToIdentifier(modifiedSkin, modifiedSkinIdentifier);

                //create etf texture with player initiator
                etfTextureOfFinalBaseSkin = new ETFTexture(modifiedSkinIdentifier,
                        blinkIdentifier,
                        blink2Identifier,
                        emissiveIdentifier,
                        blinkEmissiveIdentifier,
                        blink2EmissiveIdentifier,
                        baseEnchantIdentifier,
                        baseEnchantBlinkIdentifier,
                        baseEnchantBlink2Identifier,
                        modifiedSkinPatchedIdentifier,
                        modifiedSkinBlinkPatchedIdentifier,
                        modifiedSkinBlink2PatchedIdentifier);


                //if vanilla cape and there is no enchant or emissive
                //then just clear it from etf to defer to cape mods
//                if (capeType == ETFConfigScreenSkinTool.CapeType.NONE
//                        && modifiedCapePatchedIdentifier != null
//                        && etfCapeEnchantedIdentifier == null
//                        && etfCapeEmissiveIdentifier == null) {
//                    etfCape = null;
//                }else{
//                    etfCape = new ETFTexture(modifiedCapePatchedIdentifier,null,null,
//                            etfCapeEmissiveIdentifier,null,null,
//                            etfCapeEnchantedIdentifier,null,null,
//                            null,null,null);
//                }


//                if (modifiedCape != null) {
//                    modifiedCape.close();
//                }
//                modifiedSkin.close();
            } else {

                //check if they want to try load transparent skin anyway
                if (ETFConfigData.tryETFTransparencyForAllSkins) {
                    //parseSkinTransparency(originalSkin,wasForcedSolid);
                    Identifier skinIdentifier = new Identifier(SKIN_NAMESPACE, id + ".png");
                    ETFUtils2.registerNativeImageToIdentifier(originalSkin, skinIdentifier);
                    etfTextureOfFinalBaseSkin = new ETFTexture(skinIdentifier, null, null, null, null, null, null, null, null, null, null, null);

                } else {
                    skinFailed();
                }

                // System.out.println("asdasd");

            }
        } else {
            //System.out.println("asdasdffsdfsdsd");
            skinFailed();
        }
        isTextureReady = true;
    }


    public void changeSkinToThisForTool(NativeImage image) {
//        this.etfCape = null;
        this.baseEnchantBlinkIdentifier = null;
        this.baseEnchantIdentifier = null;
        this.coatEmissiveIdentifier = null;
        this.coatEnchantedIdentifier = null;
        this.baseEnchantBlink2Identifier = null;
        this.etfTextureOfFinalBaseSkin = null;
        this.coatIdentifier = null;
        this.hasEmissives = false;
        this.hasEnchant = false;
        this.hasFatCoat = false;
        this.hasFeatures = false;
        this.hasVillagerNose = false;
        // this.hasVanillaCape = false;
        this.isTextureReady = false;

        this.coatStyle = 0;
        this.coatLength = 1;
        this.blinkHeight = 1;
        this.blinkType = 0;
//        this.capeType = ETFConfigScreenSkinTool.CapeType.NONE;
        this.texturedNoseIdentifier = null;
        this.texturedNoseIdentifierEmissive = null;
        this.texturedNoseIdentifierEnchanted = null;
        this.noseType = ETFConfigScreenSkinTool.NoseType.NONE;

        //this.THIS_SKIN_IS_IN_EDITOR = true;
        this.originalSkin = image;
        checkTexture(true);

        etfTextureOfFinalBaseSkin.setGUIBlink();
    }

}
