package traben.entity_texture_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreenWarnings;
import traben.entity_texture_features.texture_handlers.ETFManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import static traben.entity_texture_features.ETFClientCommon.CONFIG_DIR;
import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public abstract class ETFUtils2 {
    public static ETFLruCache<Identifier, NativeImage> KNOWN_NATIVE_IMAGES = new ETFLruCache<>();

    @Nullable
    public static Identifier replaceIdentifier(Identifier id, String regex, String replace) {
        if (id == null) return null;
        return new Identifier(id.getNamespace(), id.getPath().replaceFirst(regex, replace));
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseMultiple(String[] packNameList) {
        ArrayList<String> packNames = new ArrayList<>(Arrays.asList(packNameList));
        //loop through and remove the one from the lowest pack of the first 2 entries
        //this iterates over the whole array
        while (packNames.size() >= 2) {
            if (ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(packNames.get(0)) >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(packNames.get(1))) {
                packNames.remove(1);
            } else {
                packNames.remove(0);
            }
        }
        //here the array is down to 1 entry which should be the one in the highest pack
        return packNames.get(0);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") //makes more logical sense
    public static boolean isNativeImageEmpty(@NotNull NativeImage image) {
        boolean foundNonEmptyPixel = false;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getColor(x, y) != 0) {
                    foundNonEmptyPixel = true;
                    break;
                }
            }
            if (foundNonEmptyPixel) break;
        }
        return !foundNonEmptyPixel;
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseTwo(String[] packNameList) {
        //simpler faster 2 length array logic
        if (packNameList.length != 2) {
            logError("highest pack check failed");
            return null;
        }
        if (packNameList[0].equals(packNameList[1])) {
            return packNameList[0];
        }
        if (ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(packNameList[0]) >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(packNameList[1])) {
            return packNameList[0];
        } else {
            return packNameList[1];
        }


//        for (
//
//            if (packNameList.contains(packName)) {
//                //simply loops through packs and removes them from the list to check
//                if (packNameList.size() <= 1) {
//                    //if there is only 1 left we have our winner in the highest resource-pack
//                    return (String) packNameList.toArray()[0];
//                } else {
//                    packNameList.remove(packName);
//                }
//            }
//        }

        //return null;
    }

    @Nullable
    public static Properties readAndReturnPropertiesElseNull(Identifier path) {
        Properties props = new Properties();
        try {

            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(path);
            try {
                InputStream in = resource.getInputStream();
                props.load(in);
                in.close();
                return props;
            } catch (Exception e) {
                //resource.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public static NativeImage getNativeImageElseNull(@Nullable Identifier identifier) {
        if (identifier != null) {
            if (KNOWN_NATIVE_IMAGES.get(identifier) != null) {
                return KNOWN_NATIVE_IMAGES.get(identifier);
            }
        }
        NativeImage img;
        try {
            //try catch is intended
            //noinspection OptionalGetWithoutIsPresent
            InputStream in = MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream();
            try {
                img = NativeImage.read(in);
                in.close();
                KNOWN_NATIVE_IMAGES.put(identifier, img);
                return img;
            } catch (Exception e) {
                //resource.close();
                in.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    //improvements to logging by @Maximum#8760
    public static void logMessage(String obj) {
        logMessage(obj, false);
    }

    public static void logMessage(String obj, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.of("[INFO] [Entity Texture Features]: " + obj), false);
            } else {
                ETFClientCommon.LOGGER.info(obj);
            }
        } else {
            ETFClientCommon.LOGGER.info(obj);
        }
    }

    //improvements to logging by @Maximum#8760
    public static void logWarn(String obj) {
        logWarn(obj, false);
    }

    public static void logWarn(String obj, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.of("[WARN] [Entity Texture Features]: " + obj), false);
            } else {
                ETFClientCommon.LOGGER.warn(obj);
            }
        } else {
            ETFClientCommon.LOGGER.warn(obj);
        }
    }

    //improvements to logging by @Maximum#8760
    public static void logError(String obj) {
        logError(obj, false);
    }

    public static void logError(String obj, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.of("[ERROR] [Entity Texture Features]: " + obj), false);
            } else {
                ETFClientCommon.LOGGER.error(obj);
            }
        } else {
            ETFClientCommon.LOGGER.error(obj);
        }
    }

    public static boolean isExistingResource(Identifier identifier){
        if(ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.containsKey(identifier)){
            return ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.getBoolean(identifier);
        }
        try{
            MinecraftClient.getInstance().getResourceManager().getResource(identifier);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void saveConfig() {
        File config = new File(CONFIG_DIR, "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(ETFConfigData));
            fileWriter.close();
        } catch (IOException e) {
            logError("Config file could not be saved", false);
        }
    }

    public static NativeImage emptyNativeImage() {
        return emptyNativeImage(64, 64);
    }

    public static NativeImage emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
    }

    public static Integer[] getIntRange(String rawRange) {
        //assume rawRange =  "20-56"  but can be "-64-56"  or "-14"
        rawRange = rawRange.trim();
        //sort negatives before split
        if (rawRange.startsWith("-")) {
            rawRange = rawRange.replaceFirst("-", "N");
        }
        rawRange = rawRange.replaceAll("--", "-N");
        String[] split = rawRange.split("-");
        if (split.length > 1) {//sort out range
            int[] minMax = {Integer.parseInt(split[0].replaceAll("\\D", "")), Integer.parseInt(split[1].replaceAll("\\D", ""))};
            if (split[0].contains("N")) {
                minMax[0] = -minMax[0];
            }
            if (split[1].contains("N")) {
                minMax[1] = -minMax[1];
            }
            ArrayList<Integer> builder = new ArrayList<>();
            if (minMax[0] > minMax[1]) {
                //0 must be smaller
                minMax = new int[]{minMax[1], minMax[0]};
            }
            if (minMax[0] < minMax[1]) {
                for (int i = minMax[0]; i <= minMax[1]; i++) {
                    builder.add(i);
                }
            } else {
                logMessage("Optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \"" + rawRange.replace("N", "-") + "\"", false);
            }
            return builder.toArray(new Integer[0]);
        } else {//only 1 number but method ran because of "-" present
            if (split[0].contains("N")) {
                return new Integer[]{-Integer.parseInt(split[0].replaceAll("\\D", ""))};
            } else {
                return new Integer[]{Integer.parseInt(split[0].replaceAll("\\D", ""))};
            }

        }
    }

    public static boolean registerNativeImageToIdentifier(NativeImage img, Identifier identifier) {
        if (img != null && identifier != null) {
            NativeImageBackedTexture bob = new NativeImageBackedTexture(img);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, bob);
            //MinecraftClient.getInstance().getResourceManager().
            KNOWN_NATIVE_IMAGES.put(identifier, img);
            return true;
        } else {
            logError("registering native image failed: " + img + ", " + identifier);
            return false;
        }
    }


    public static void checkModCompatibility() {
//        if (ETFVersionDifferenceHandler.isThisModLoaded("quark") && !ETFConfigData.ignoredConfigs.contains(ETFConfigScreenWarnings.ConfigWarning.QUARK)) {
//            ETFConfigData.enableCustomBlockEntities = false;
//            ETFConfigData.enableEmissiveBlockEntities = false;
//            ETFUtils2.saveConfig();
//        }
        if (ETFVersionDifferenceHandler.isThisModLoaded("figura") && !ETFConfigData.ignoredConfigs.contains(ETFConfigScreenWarnings.ConfigWarning.FIGURA)) {
            ETFConfigData.skinFeaturesEnabled = false;
            ETFUtils2.saveConfig();
        }
        if (ETFVersionDifferenceHandler.isThisModLoaded("impersonate") && !ETFConfigData.ignoredConfigs.contains(ETFConfigScreenWarnings.ConfigWarning.IMPERSONATE)) {
            ETFConfigData.skinFeaturesEnabled = false;
            ETFUtils2.saveConfig();
        }
    }

}
