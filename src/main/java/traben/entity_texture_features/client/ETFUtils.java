package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.client.ETFClient.*;

public class ETFUtils {

    //checks if files exists and is in the same or higher resourcepack as id 2
    public static boolean isExistingFileAndSameOrHigherResourcepackAs(Identifier id, Identifier vanillaIdToMatch, boolean id1IsNativeImage) {
        if (isExistingFileDirect(id, id1IsNativeImage)) {
            try {
                ResourceManager resource = MinecraftClient.getInstance().getResourceManager();
                String packname = resource.getResource(id).getResourcePackName();
                String packname2 = resource.getResource(vanillaIdToMatch).getResourcePackName();
                if (packname.equals(packname2)) {
                    return true;
                } else {
                    for (ResourcePack pack :
                            resource.streamResourcePacks().toList()) {
                        //loops through all resourcepacks from bottom "public static " to top
                        if (packname.equals(pack.getName())) {
                            //if first id is reached first it is lower and must be false
                            return false;
                        }
                        if (packname2.equals(pack.getName())) {
                            //if the second file is reached first it must be the lower resource, thus id 1 is higher return true
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
        return false;
    }

    public static boolean isExistingNativeImageFile(Identifier id) {
        return isExistingFileDirect(id, true);
    }

    public static boolean isExistingPropertyFile(Identifier id) {
        return isExistingFileDirect(id, false);
    }

    public static boolean isExistingPropertyFile(String id) {
        return isExistingFileDirect(new Identifier(id), false);
    }

    public static boolean isExistingFileDirect(Identifier id, boolean isNativeImage) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id);
            try {
                InputStream stream = resource.getInputStream();
                if (isNativeImage) {
                    //throw exception if it's not a native image
                    NativeImage.read(stream);
                }
                resource.close();
                return true;
            } catch (IOException e) {
                resource.close();
                return false;
            }
        } catch (IOException f) {
            return false;
        }
    }

    private static boolean checkPropertyPathExistsAndSameOrHigherResourcepackAs(String propertiesPath, String path2) {
        return isExistingFileAndSameOrHigherResourcepackAs(new Identifier(propertiesPath), new Identifier(path2), false);
    }


    public static void tryClearUneededMobData() {
        //todo implement logic here to check if saved mob data is still needed and remove all data of that UUID if no longer needed
        //check either random known UUIDs or find other selection logic
    }


    public static void resetVisuals() {
        modMessage("Reloading...", false);
        PATH_TOTAL_TRUE_RANDOM.clear();

        KNOWN_UUID_LIST.clear();

        UUID_RANDOM_TEXTURE_SUFFIX.clear();
        UUID_RANDOM_TEXTURE_SUFFIX_2.clear();
        UUID_RANDOM_TEXTURE_SUFFIX_3.clear();
        UUID_RANDOM_TEXTURE_SUFFIX_4.clear();
        UUID_HAS_UPDATABLE_RANDOM_CASES.clear();
        UUID_HAS_UPDATABLE_RANDOM_CASES_2.clear();
        UUID_HAS_UPDATABLE_RANDOM_CASES_3.clear();
        UUID_HAS_UPDATABLE_RANDOM_CASES_4.clear();

        PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.clear();
        PATH_OPTIFINE_OR_JUST_RANDOM.clear();
        PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.clear();// 0,1,2
        PATH_IGNORE_ONE_PNG.clear();
        UUID_ENTITY_ALREADY_CALCULATED.clear();//only time it clears
        UUID_ENTITY_AWAITING_DATA_CLEARING.clear();
        UUID_ENTITY_AWAITING_DATA_CLEARING_2.clear();

        UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.clear();

        UUID_PLAYER_HAS_FEATURES.clear();
        UUID_PLAYER_HAS_ENCHANT.clear();
        UUID_PLAYER_HAS_EMISSIVE.clear();
        UUID_PLAYER_TRANSPARENT_SKIN_ID.clear();
        UUID_PLAYER_HAS_SKIN_DOWNLOADED_YET.clear();
        for (HttpURLConnection h :
                URL_HTTP_TO_DISCONNECT_1.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        for (HttpURLConnection h :
                URL_HTTP_TO_DISCONNECT_2.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        UUID_PLAYER_HAS_COAT.clear();
        UUID_PLAYER_HAS_FAT_COAT.clear();
        UUID_PLAYER_HAS_VILLAGER_NOSE.clear();
        UUID_PLAYER_HAS_CAPE.clear();
        UUID_PLAYER_HAS_CUSTOM_CAPE.clear();

        UUID_PLAYER_LAST_SKIN_CHECK.clear();
        UUID_PLAYER_LAST_SKIN_CHECK_COUNT.clear();

        URL_HTTP_TO_DISCONNECT_1.clear();
        URL_HTTP_TO_DISCONNECT_2.clear();

        PATH_FAILED_PROPERTIES_TO_IGNORE.clear();

        UUID_NEXT_BLINK_TIME.clear();
        PATH_HAS_BLINK_TEXTURE.clear();
        PATH_HAS_BLINK_TEXTURE_2.clear();
        PATH_BLINK_PROPERTIES.clear();

        PATH_HAS_DEFAULT_REPLACEMENT.clear();

        UUID_TRIDENT_NAME.clear();

        PATH_EMISSIVE_TEXTURE_IDENTIFIER.clear();
        setEmissiveSuffix();

        PATH_IS_EXISTING_FEATURE.clear();

        mooshroomRedCustomShroom = 0;
        mooshroomBrownCustomShroom = 0;

        lecternHasCustomTexture = null;

        PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.clear();

        registerNativeImageToIdentifier(emptyNativeImage(1, 1), "etf:blank.png");
    }

    public static void resetSingleSuffixData(UUID id) {
        UUID_RANDOM_TEXTURE_SUFFIX.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX_2.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX_3.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX_4.remove(id);
        KNOWN_UUID_LIST.remove(id);
    }

    public static void forceResetAllDataOfUUID(UUID id) {
        KNOWN_UUID_LIST.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX_2.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX_3.remove(id);
        UUID_RANDOM_TEXTURE_SUFFIX_4.remove(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES.remove(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES_2.remove(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES_3.remove(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES_4.remove(id);
        UUID_ENTITY_ALREADY_CALCULATED.remove(id);
        UUID_ENTITY_AWAITING_DATA_CLEARING.remove(id);
        UUID_ENTITY_AWAITING_DATA_CLEARING_2.remove(id);
        UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.remove(id);
        UUID_TRIDENT_NAME.remove(id);
        UUID_NEXT_BLINK_TIME.remove(id);
    }

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


    public static Properties readProperties(String path) {
        return readProperties(path, null);
    }

    public static Properties readProperties(String path, String pathOfTextureToUseForResourcepackCheck) {
        Properties props = new Properties();
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path));
            //skip if it needs to be same resource-pack
            if (pathOfTextureToUseForResourcepackCheck != null) {
                ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
                String packName1 = resource.getResourcePackName();
                String packName2 = resourceManager.getResource(new Identifier(pathOfTextureToUseForResourcepackCheck)).getResourcePackName();
                if (!packName1.equals(packName2)) {
                    //not same pack check it is a higher pack and only continue if packName1 is higher
                    for (ResourcePack pack :
                            resourceManager.streamResourcePacks().toList()) {
                        //loops through all resourcepacks from bottom "public static " to top
                        if (packName1.equals(pack.getName())) {
                            //if first id is reached first it is lower and must not be used return null
                            return null;
                        }
                        if (packName2.equals(pack.getName())) {
                            //if the second file is reached first it must be the lower resource, thus id 1 is higher so break to continue
                            break;
                        }
                    }
                }
            }
            try {
                InputStream in = resource.getInputStream();
                props.load(in);
                resource.close();
            } catch (Exception e) {
                resource.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        // Example return
        // {skins.4=3, skins.5=1-3, skins.2=2, skins.3=3, weights.5=1 1 , biomes.2=desert, health.3=1-50%, names.4=iregex:mob name.*}
        return props;
    }

    public static NativeImage getNativeImageFromID(Identifier identifier) {
        NativeImage img;
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
            try {
                InputStream in = resource.getInputStream();
                img = NativeImage.read(in);
                resource.close();
            } catch (Exception e) {
                resource.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return img;
    }

    public static void processNewRandomTextureCandidate(String vanillaTexturePath) {
        processNewRandomTextureCandidate(vanillaTexturePath, false);
    }

    public static void processNewRandomTextureCandidate(String vanillaTexturePath, boolean skipProcessing) {
        //boolean hasProperties = false;

        //set default in case of no change
        //PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaTexturePath, 2);
        //check and apply hashmap data
        //System.out.println("checking=" + vanillaTexturePath);
        String properties = checkAndSetPathsToUseForPropertyRandoms(vanillaTexturePath.replace(".png", ".properties"));
        //System.out.println("returned=" + properties);

        if (!skipProcessing) {
            if (properties != null && !PATH_FAILED_PROPERTIES_TO_IGNORE.contains(properties)) {//optifine settings found
                processOptifineTextureCandidate(vanillaTexturePath, properties);
            } else {
                //process a true random texture begins with 2.png
                //checkAndSetPathToUseForRandoms(vanillaTexturePath.replace(".png", "2.png"), false);
                processTrueRandomCandidate(vanillaTexturePath);
            }
        }
    }

    private static String checkAndSetPathsToUseForPropertyRandoms(String texturePath) {
        //preserve checking order 3 > 0 > 1 > 2
        String check = checkPropertyPath(texturePath, 3);
        if (check != null) return check;
        check = checkPropertyPath(texturePath, 0);
        if (check != null) return check;
        check = checkPropertyPath(texturePath, 1);
        if (check != null) return check;
        check = checkPropertyPath(texturePath, 2);
        return check;
    }

    private static String checkPropertyPath(String vanillaTexturePath, int pathToCheck_0123) {
        String[] replaceStrings = switch (pathToCheck_0123) {
            case 1 -> new String[]{"textures/entity", "optifine/mob"};
            case 2 -> new String[]{"$", "$"};
            case 3 -> new String[]{"textures", "etf/random"};
            default -> new String[]{"textures", "optifine/random"};
        };
        String checkingPath = vanillaTexturePath.replace(replaceStrings[0], replaceStrings[1]);
        if (isExistingPropertyFile(checkingPath)) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaTexturePath.replace(".properties", ".png"), pathToCheck_0123);
            Properties properties = readProperties(checkingPath);
            if (properties != null) {
                for (String propertyName :
                        properties.stringPropertyNames()) {
                    if (propertyName.contains("skin") || propertyName.contains("texture")) {
                        //use this one for check
                        String[] suffixData = properties.getProperty(propertyName).trim().split("\s+");
                        //assume data may be formatted stupidly like  "   13-14   67  800-805"
                        //just want a number present in this properties file to check so just grab the first by splitting and grabbing[0]

                        String checkingPath1 = null;
                        for (String possibleSuffix :
                                suffixData) {
                            //if range use right most as less likely to be 1
                            if (possibleSuffix.contains("-")) possibleSuffix = possibleSuffix.split("-")[1];
                            possibleSuffix = possibleSuffix.replaceAll("[^0-9]", "");
                            if (!possibleSuffix.isEmpty()) {
                                String tryHere = checkingPath.replace(".properties", possibleSuffix + ".png");
                                //System.out.println("tried=" + tryHere);
                                if (isExistingNativeImageFile(new Identifier(tryHere))) {
                                    checkingPath1 = tryHere;
                                    break;
                                }
                            }
                        }
                        if (checkingPath1 != null) {
                            if (checkPropertyPathExistsAndSameOrHigherResourcepackAs(checkingPath, checkingPath1)) {
                                //this return only occurs if the properties file exists and the first texture named in the properties file is of the same or a lower pack
                                return checkingPath;
                            }
                        }
                    }
                }
            }
        }
        //failed all checks
        return null;
    }

    private static void checkAndSetPathsToUseForUncheckedTextures(String vanillaTexturePath, String testPath) {
        //preserve checking order 3 > 0 > 1 > 2
        if (hiddenCheckSpecificPath(testPath, 3)) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaTexturePath, 3);
            return;
        }
        if (hiddenCheckSpecificPath(testPath, 0)) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaTexturePath, 0);
            return;
        }
        if (hiddenCheckSpecificPath(testPath, 1)) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaTexturePath, 1);
            return;
        }
        if (hiddenCheckSpecificPath(testPath, 2)) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaTexturePath, 2);
        }
    }

    private static boolean hiddenCheckSpecificPath(String vanillaTexturePath, int pathToCheck_0123) {
        String[] replaceStrings = switch (pathToCheck_0123) {
            case 1 -> new String[]{"textures/entity", "optifine/mob"};
            case 2 -> new String[]{"$", "$"};
            case 3 -> new String[]{"textures", "etf/random"};
            default -> new String[]{"textures", "optifine/random"};
        };
        String checkingPath = vanillaTexturePath.replace(replaceStrings[0], replaceStrings[1]);
        return isExistingNativeImageFile(new Identifier(checkingPath));
    }

    private static void processOptifineTextureCandidate(String vanillaTexturePath, String propertiesPath) {
        try {
            PATH_IGNORE_ONE_PNG.put(vanillaTexturePath, !(isExistingPropertyFile(new Identifier(propertiesPath.replace(".properties", "1.png")))));

            //String twoPngPath = returnOptifineOrVanillaPath(vanillaTexturePath, 2, "");
            Properties props = readProperties(propertiesPath);

            if (props != null) {
                Set<String> propIds = props.stringPropertyNames();
                //set so only 1 of each
                Set<Integer> numbers = new HashSet<>();
                //get the numbers we are working with
                for (String str :
                        propIds) {
                    numbers.add(Integer.parseInt(str.replaceAll("[^0-9]", "")));
                }
                //sort from lowest to largest
                List<Integer> numbersList = new ArrayList<>(numbers);
                Collections.sort(numbersList);
                Set<ETFTexturePropertyCase> allCasesForTexture = new HashSet<>();
                for (Integer num :
                        numbersList) {
                    //loops through each known number in properties
                    //all case.1 ect should be processed here
                    Integer[] suffixes = {};
                    Integer[] weights = {};
                    String[] biomes = {};
                    Integer[] heights = {};
                    String[] names = {};
                    String[] professions = {};
                    String[] collarColours = {};
                    int baby = 0; // 0 1 2 - dont true false
                    int weather = 0; //0,1,2,3 - no clear rain thunder
                    String[] health = {};
                    Integer[] moon = {};
                    String[] daytime = {};
                    String[] blocks = {};
                    String[] teams = {};

                    if (props.containsKey("skins." + num) || props.containsKey("textures." + num)) {
                        String dataFromProps = props.containsKey("skins." + num) ? props.getProperty("skins." + num).trim() : props.getProperty("textures." + num).trim();
                        String[] skinData = dataFromProps.split("\s+");
                        ArrayList<Integer> suffixNumbers = new ArrayList<>();
                        for (String data :
                                skinData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    suffixNumbers.addAll(Arrays.asList(getIntRange(data)));
                                } else {
                                    suffixNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        suffixes = suffixNumbers.toArray(new Integer[0]);
                    }
                    if (props.containsKey("weights." + num)) {
                        String dataFromProps = props.getProperty("weights." + num).trim();
                        String[] weightData = dataFromProps.split("\s+");
                        ArrayList<Integer> builder = new ArrayList<>();
                        for (String s :
                                weightData) {
                            s = s.trim();
                            if (!s.replaceAll("[^0-9]", "").isEmpty()) {
                                builder.add(Integer.parseInt(s.replaceAll("[^0-9]", "")));
                            }
                        }
                        weights = builder.toArray(new Integer[0]);
                    }
                    if (props.containsKey("biomes." + num)) {
                        String dataFromProps = props.getProperty("biomes." + num).trim();
                        biomes = dataFromProps.toLowerCase().split("\s+");
                    }
                    //add legacy height support
                    if (!props.containsKey("heights." + num) && (props.containsKey("minHeight." + num) || props.containsKey("maxHeight." + num))) {
                        String min = "-64";
                        String max = "319";
                        if (props.containsKey("minHeight." + num)) {
                            min = props.getProperty("minHeight." + num).trim();
                        }
                        if (props.containsKey("maxHeight." + num)) {
                            max = props.getProperty("maxHeight." + num).trim();
                        }
                        props.put("heights." + num, min + "-" + max);
                    }
                    if (props.containsKey("heights." + num)) {
                        String dataFromProps = props.getProperty("heights." + num).trim();
                        String[] heightData = dataFromProps.split("\s+");
                        ArrayList<Integer> heightNumbers = new ArrayList<>();
                        for (String data :
                                heightData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    heightNumbers.addAll(Arrays.asList(getIntRange(data)));
                                } else {
                                    heightNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        heights = heightNumbers.toArray(new Integer[0]);
                    }

                    if (props.containsKey("names." + num) || props.containsKey("name." + num)) {
                        String dataFromProps = props.containsKey("name." + num) ? props.getProperty("name." + num).trim() : props.getProperty("names." + num).trim();
                        if (dataFromProps.contains("regex:") || dataFromProps.contains("pattern:")) {
                            names = new String[]{dataFromProps};
                        } else {
                            //names = dataFromProps.split("\s+");
                            //allow    "multiple names" among "other"
                            List<String> list = new ArrayList<>();
                            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(dataFromProps);
                            while (m.find()) {
                                list.add(m.group(1).replace("\"", "").trim());
                            }
                            names = list.toArray(new String[0]);
                        }
                    }
                    if (props.containsKey("professions." + num)) {
                        professions = props.getProperty("professions." + num).trim().split("\s+");
                    }
                    if (props.containsKey("collarColors." + num) || props.containsKey("collarColours." + num)) {
                        collarColours = props.containsKey("collarColors." + num) ? props.getProperty("collarColors." + num).trim().split("\s+") : props.getProperty("collarColours." + num).trim().split("\s+");
                    }
                    if (props.containsKey("baby." + num)) {
                        String dataFromProps = props.getProperty("baby." + num).trim();
                        switch (dataFromProps) {
                            case "true" -> baby = 1;
                            case "false" -> baby = 2;
                        }
                    }
                    if (props.containsKey("weather." + num)) {
                        String dataFromProps = props.getProperty("weather." + num).trim();
                        switch (dataFromProps) {
                            case "clear" -> weather = 1;
                            case "rain" -> weather = 2;
                            case "thunder" -> weather = 3;
                        }
                    }
                    if (props.containsKey("health." + num)) {
                        health = props.getProperty("health." + num).trim().split("\s+");
                    }
                    if (props.containsKey("moonPhase." + num)) {
                        String dataFromProps = props.getProperty("moonPhase." + num).trim();
                        String[] moonData = dataFromProps.split("\s+");
                        ArrayList<Integer> moonNumbers = new ArrayList<>();
                        for (String data :
                                moonData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    moonNumbers.addAll(Arrays.asList(getIntRange(data)));
                                } else {
                                    moonNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        moon = moonNumbers.toArray(new Integer[0]);
                    }
                    if (props.containsKey("dayTime." + num)) {
                        daytime = props.getProperty("dayTime." + num).trim().split("\s+");
                    }
                    if (props.containsKey("blocks." + num)) {
                        blocks = props.getProperty("blocks." + num).trim().split("\s+");
                    } else if (props.containsKey("block." + num)) {
                        blocks = props.getProperty("block." + num).trim().split("\s+");
                    }
                    if (props.containsKey("teams." + num)) {
                        String teamData = props.getProperty("teams." + num).trim();
                        List<String> list = new ArrayList<>();
                        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
                        while (m.find()) {
                            list.add(m.group(1).replace("\"", ""));
                        }
                        teams = list.toArray(new String[0]);
                    } else if (props.containsKey("team." + num)) {
                        String teamData = props.getProperty("team." + num).trim();
                        List<String> list = new ArrayList<>();
                        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
                        while (m.find()) {
                            list.add(m.group(1).replace("\"", ""));
                        }
                        teams = list.toArray(new String[0]);
                    }

                    if (suffixes.length != 0) {
                        allCasesForTexture.add(new ETFTexturePropertyCase(suffixes, weights, biomes, heights, names, professions, collarColours, baby, weather, health, moon, daytime, blocks, teams));
                    }
                }
                if (!allCasesForTexture.isEmpty()) {
                    PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.put(vanillaTexturePath, allCasesForTexture);
                    PATH_OPTIFINE_OR_JUST_RANDOM.put(vanillaTexturePath, true);
                } else {
                    modMessage("Ignoring properties file that failed to load any cases @ " + propertiesPath, false);
                    PATH_FAILED_PROPERTIES_TO_IGNORE.add(propertiesPath);
                }
            } else {//properties file is null
                modMessage("Ignoring properties file that was null @ " + propertiesPath, false);
                PATH_FAILED_PROPERTIES_TO_IGNORE.add(propertiesPath);
            }
        } catch (Exception e) {
            modMessage("Ignoring properties file that caused Exception @ " + propertiesPath + e, false);
            PATH_FAILED_PROPERTIES_TO_IGNORE.add(propertiesPath);
        }
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
            int[] minMax = {Integer.parseInt(split[0].replaceAll("[^0-9]", "")), Integer.parseInt(split[1].replaceAll("[^0-9]", ""))};
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
                modMessage("Optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \"" + rawRange.replace("N", "-") + "\"", false);
            }
            return builder.toArray(new Integer[0]);
        } else {//only 1 number but method ran because of "-" present
            if (split[0].contains("N")) {
                return new Integer[]{-Integer.parseInt(split[0].replaceAll("[^0-9]", ""))};
            } else {
                return new Integer[]{Integer.parseInt(split[0].replaceAll("[^0-9]", ""))};
            }

        }
    }

    public static void testCases(String vanillaPath, UUID id, Entity entity, boolean isUpdate) {
        testCases(vanillaPath, id, entity, isUpdate, UUID_RANDOM_TEXTURE_SUFFIX, UUID_HAS_UPDATABLE_RANDOM_CASES);
    }

    public static void testCases(String vanillaPath, UUID id, Entity entity, boolean isUpdate, HashMap<UUID, Integer> UUID_RandomSuffixMap, HashMap<UUID, Boolean> UUID_CaseHasUpdateablesCustom) {
        for (ETFTexturePropertyCase test :
                PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.get(vanillaPath)) {

            //skip if it is only an update and case is not updatable
            if (test.testEntity((LivingEntity) entity, UUID_ENTITY_ALREADY_CALCULATED.contains(id), UUID_CaseHasUpdateablesCustom)) {
                UUID_RandomSuffixMap.put(id, test.getWeightedSuffix(id, PATH_IGNORE_ONE_PNG.get(vanillaPath)));
                Identifier tested = returnOptifineOrVanillaIdentifier(vanillaPath, UUID_RandomSuffixMap.get(id));

                if (!isExistingNativeImageFile(tested) && !isUpdate) {
                    UUID_RandomSuffixMap.put(id, 0);
                }
                break;
            }
        }
        if (!UUID_CaseHasUpdateablesCustom.containsKey(id))
            UUID_CaseHasUpdateablesCustom.put(id, false);
    }

    public static void modMessage(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: " + message), false);
            } else {
                LogManager.getLogger().info("[Entity Texture Features]: " + message);
            }
        } else {
            LogManager.getLogger().info("[Entity Texture Features]: " + message);
        }
    }

    public static String returnOptifineOrVanillaPath(String vanillaPath, int randomId, String emissiveSuffx) {

        if (!PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(vanillaPath)) {

            if (randomId != 0) {
                //for special cases of weird textures like horse armour and markings
                checkAndSetPathsToUseForUncheckedTextures(vanillaPath, vanillaPath.replace(".png", randomId + ".png"));
            } else {
                //for brand new unknown textures that slip in
                ETFUtils.processNewRandomTextureCandidate(vanillaPath, true);
            }
        }

        String append = (randomId == 0 ? "" : randomId) + emissiveSuffx + ".png";
        if (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(vanillaPath)) {
            return switch (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.get(vanillaPath)) {
                case 0 -> vanillaPath.replace(".png", append).replace("textures", "optifine/random");
                case 1 -> vanillaPath.replace(".png", append).replace("textures/entity", "optifine/mob");
                case 3 -> vanillaPath.replace(".png", append).replace("textures", "etf/random");
                default -> vanillaPath.replace(".png", append);
            };
        } else {
            return vanillaPath;
        }
    }

    public static Identifier returnOptifineOrVanillaIdentifier(String vanillaPath, int randomId) {
        return new Identifier(returnOptifineOrVanillaPath(vanillaPath, randomId, ""));
    }

    public static Identifier returnOptifineOrVanillaIdentifier(String vanillaPath) {
        return new Identifier(returnOptifineOrVanillaPath(vanillaPath, 0, ""));
    }

    private static void processTrueRandomCandidate(String vanillaPath) {
        PATH_IGNORE_ONE_PNG.put(vanillaPath, true);
        boolean keepGoing = false;
        //first iteration longer
        int successCount = 0;
        //allTextures.add(vanillaPath);
        //can start from either texture1.png or texture2.png check both first
        //check if texture1.png is used
        String checkPath = vanillaPath.replace(".png", "1.png");
        String checkPathOldRandomFormat = vanillaPath.replace(".png", "1.png").replace("textures/entity", "optifine/mob");
        String checkPathOptifineFormat = vanillaPath.replace(".png", "1.png").replace("textures", "optifine/random");
        String checkPathETFFormat = vanillaPath.replace(".png", "1.png").replace("textures", "etf/random");
        if (isExistingNativeImageFile(new Identifier(checkPathETFFormat))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 3);
            PATH_IGNORE_ONE_PNG.put(vanillaPath, false);
            //successCount++;
        } else if (isExistingNativeImageFile(new Identifier(checkPathOptifineFormat))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 0);
            PATH_IGNORE_ONE_PNG.put(vanillaPath, false);
            //successCount++;
        } else if (isExistingNativeImageFile(new Identifier(checkPathOldRandomFormat))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 1);
            PATH_IGNORE_ONE_PNG.put(vanillaPath, false);
            //successCount++;
        } else if (isExistingNativeImageFile(new Identifier(checkPath))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 2);
            PATH_IGNORE_ONE_PNG.put(vanillaPath, false);
            //successCount++;
        }

        //check if texture 2.png is used
        checkPath = vanillaPath.replace(".png", "2.png");
        checkPathOldRandomFormat = vanillaPath.replace(".png", "2.png").replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", "2.png").replace("textures", "optifine/random");
        checkPathETFFormat = vanillaPath.replace(".png", "2.png").replace("textures", "etf/random");
        if (isExistingNativeImageFile(new Identifier(checkPathETFFormat))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 3);
            keepGoing = true;
            successCount = 2;
        } else if (isExistingNativeImageFile(new Identifier(checkPathOptifineFormat))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 0);
            keepGoing = true;
            successCount = 2;
        } else if (isExistingNativeImageFile(new Identifier(checkPathOldRandomFormat))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 1);
            keepGoing = true;
            successCount = 2;
        } else if (isExistingNativeImageFile(new Identifier(checkPath))) {
            PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.put(vanillaPath, 2);
            keepGoing = true;
            successCount = 2;
        }
        //texture3.png and further optimized iterations
        int count = 2;
        while (keepGoing) {
            count++;

            checkPath = switch (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.get(vanillaPath)) {
                case 3 -> vanillaPath.replace(".png", (count + ".png")).replace("textures", "etf/random");
                case 0 -> vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
                case 1 -> vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
                default -> vanillaPath.replace(".png", (count + ".png"));
            };

            keepGoing = isExistingNativeImageFile(new Identifier(checkPath));
            if (keepGoing) successCount++;
        }
        //true if any random textures at all

        PATH_TOTAL_TRUE_RANDOM.put(vanillaPath, successCount);
        PATH_OPTIFINE_OR_JUST_RANDOM.put(vanillaPath, false);

    }


    public static void setEmissiveSuffix() {
        try {
            Properties suffix = readProperties("optifine/emissive.properties");
            if (suffix.isEmpty()) {
                suffix = readProperties("textures/emissive.properties");
            }
            Set<String> builder = new HashSet<>();
            if (suffix.contains("entities.suffix.emissive")) {
                builder.add(suffix.getProperty("entities.suffix.emissive"));
            }
            if (suffix.contains("suffix.emissive")) {
                builder.add(suffix.getProperty("suffix.emissive"));
            }
            if (ETFConfigData.alwaysCheckVanillaEmissiveSuffix) {
                builder.add("_e");
            }
            emissiveSuffixes = builder.toArray(new String[0]);
            if (emissiveSuffixes.length == 0) {
                modMessage("default emissive suffix '_e' used", false);
                emissiveSuffixes = new String[]{"_e"};
            }
        } catch (Exception e) {
            modMessage("default emissive suffix '_e' used", false);
            emissiveSuffixes = new String[]{"_e"};
        }
    }

    public static void saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
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
            modMessage("Config could not be saved", false);
        }
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
                    registerNativeImageToIdentifier(resizeOptifineImage(image), SKIN_NAMESPACE + id + "_cape.png");
                } else {
                    registerNativeImageToIdentifier(image, SKIN_NAMESPACE + id + "_cape.png");
                }
                UUID_PLAYER_HAS_CUSTOM_CAPE.put(id, true);
            } else {
                modMessage("Player skin {" + player.getName().getString() + "} no THIRD_PARTY_CAPE Found", false);
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
        NativeImage resizedImage = emptyNativeImage(newWidth, newHeight);
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
                modMessage("Found Player {" + id + "} with texture features in skin.", false);
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
                    registerNativeImageToIdentifier(coatSkin, coatID.toString());
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
                        registerNativeImageToIdentifier(skin, transId.toString());

                    } else {
                        modMessage("Skin was too transparent or had other problems", false);
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

                        registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");

                        //blink is 2 frames with blue optional
                        if (blinkChoice == 2) {
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("face2"), 1, getSkinPixelBounds("face4"));
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", true);
                            registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
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

                            registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");

                        } else if (blinkChoice == 4) {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", true);

                            registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                            registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        } else /*if( blinkChoice == 5)*/ {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
                            PATH_HAS_BLINK_TEXTURE_2.put(SKIN_NAMESPACE + id + ".png", true);
                            registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                            registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
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
                        case 666 -> cape = getNativeImageFromID(new Identifier("etf:capes/error.png"));
                        default -> {
                            //cape = getNativeImageFromID(new Identifier("etf:capes/blank.png"));
                        }
                    }
                }
                if (cape != null) {
                    if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666) {//custom chosen
                        registerNativeImageToIdentifier(cape, SKIN_NAMESPACE + id + "_cape.png");
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
                        registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_enchant.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_enchant.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_enchant.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(coatSkin, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_coat_enchant.png");
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
                        registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_e.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(coatSkin, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, ETFUtils::emptyNativeImage), SKIN_NAMESPACE + id + "_coat_e.png");
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

    private static NativeImage emptyNativeImage() {
        return emptyNativeImage(64, 64);
    }

    private static NativeImage emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
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
        NativeImage cape = emptyNativeImage(64, 32);
        NativeImage elytra = getNativeImageFromID(new Identifier("etf:capes/public static _elytra.png"));
        if (elytra == null) {
            elytra = getNativeImageFromID(new Identifier("textures/entity/elytra.png"));
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

    private static void registerNativeImageToIdentifier(NativeImage img, String identifierPath) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        NativeImageBackedTexture bob = new NativeImageBackedTexture(img);
        textureManager.registerTexture(new Identifier(identifierPath), bob);

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

    public static Identifier returnBlinkIdOrGiven(LivingEntity entity, String givenTexturePath, UUID id) {
        return returnBlinkIdOrGiven(entity, givenTexturePath, id, false);
    }


    public static Identifier returnBlinkIdOrGiven(LivingEntity entity, String givenTexturePath, UUID id, boolean isPlayer) {
        if (ETFConfigData.enableBlinking) {
            if (!PATH_HAS_BLINK_TEXTURE.containsKey(givenTexturePath)) {
                //check for blink textures
                PATH_HAS_BLINK_TEXTURE.put(givenTexturePath, isExistingFileAndSameOrHigherResourcepackAs(new Identifier(givenTexturePath.replace(".png", "_blink.png")), new Identifier(givenTexturePath), true));
                PATH_HAS_BLINK_TEXTURE_2.put(givenTexturePath, isExistingFileAndSameOrHigherResourcepackAs(new Identifier(givenTexturePath.replace(".png", "_blink2.png")), new Identifier(givenTexturePath), true));
                PATH_BLINK_PROPERTIES.put(givenTexturePath, readProperties(givenTexturePath.replace(".png", "_blink.properties"), givenTexturePath));

            }
            PATH_BLINK_PROPERTIES.putIfAbsent(givenTexturePath, null);
            PATH_HAS_BLINK_TEXTURE.putIfAbsent(givenTexturePath, false);
            PATH_HAS_BLINK_TEXTURE_2.putIfAbsent(givenTexturePath, false);
            if (PATH_HAS_BLINK_TEXTURE.containsKey(givenTexturePath)) {
                if (PATH_HAS_BLINK_TEXTURE.get(givenTexturePath)) {
                    if (entity.getPose() == EntityPose.SLEEPING) {
                        return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                    }
                    //force eyes closed if blinded
                    else if (entity.hasStatusEffect(StatusEffects.BLINDNESS)) {
                        if (PATH_HAS_BLINK_TEXTURE_2.containsKey(givenTexturePath)) {
                            return new Identifier(givenTexturePath.replace(".png", (PATH_HAS_BLINK_TEXTURE_2.get(givenTexturePath) ? "_blink2.png" : "_blink.png")));
                        } else {
                            return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                        }
                    } else {
                        //do regular blinking
                        Properties props = PATH_BLINK_PROPERTIES.get(givenTexturePath);
                        int blinkLength;
                        int blinkFrequency;
                        if (props != null) {
                            blinkLength = props.containsKey("blinkLength") ?
                                    Integer.parseInt(props.getProperty("blinkLength").replaceAll("[^0-9]", "")) :
                                    ETFConfigData.blinkLength;
                            blinkFrequency = props.containsKey("blinkFrequency") ?
                                    Integer.parseInt(props.getProperty("blinkFrequency").replaceAll("[^0-9]", "")) :
                                    ETFConfigData.blinkFrequency;
                        } else {
                            blinkLength = ETFConfigData.blinkLength;
                            blinkFrequency = ETFConfigData.blinkFrequency;
                        }


                        // long timer = entity.world.getTime() % blinkFrequency;
                        //int blinkTimeVariedByUUID = Math.abs(id.hashCode()) % blinkFrequency;
                        //make blink timer not overlap the wrap around to 0
                        //if (blinkTimeVariedByUUID < blinkLength) blinkTimeVariedByUUID = blinkLength;
                        //if (blinkTimeVariedByUUID > blinkFrequency - blinkLength)
                        //  blinkTimeVariedByUUID = blinkFrequency - blinkLength;

                        if (!UUID_NEXT_BLINK_TIME.containsKey(id)) {
                            UUID_NEXT_BLINK_TIME.put(id, entity.world.getTime() + blinkLength + 1);
                        }
                        long nextBlink = UUID_NEXT_BLINK_TIME.get(id);
                        long currentTime = entity.world.getTime();

                        if (currentTime >= nextBlink - blinkLength && currentTime <= nextBlink + blinkLength) {
                            if (PATH_HAS_BLINK_TEXTURE_2.containsKey(givenTexturePath)) {
                                if (PATH_HAS_BLINK_TEXTURE_2.get(givenTexturePath)) {
                                    if (currentTime >= nextBlink - (blinkLength / 3) && currentTime <= nextBlink + (blinkLength / 3)) {
                                        return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                                    }
                                    return new Identifier(givenTexturePath.replace(".png", "_blink2.png"));
                                }
                            }
                            if (!(currentTime > nextBlink)) {
                                return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                            }
                        } else if (currentTime > nextBlink + blinkLength) {
                            //calculate new next blink
                            UUID_NEXT_BLINK_TIME.put(id, currentTime + entity.getRandom().nextInt(blinkFrequency) + 20);
                        }
                    }
                }
            }
        }

        if (isPlayer && ETFConfigData.skinFeaturesEnabled
                && UUID_PLAYER_TRANSPARENT_SKIN_ID.containsKey(id) && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (entity.isTeammate(MinecraftClient.getInstance().player) || entity.getScoreboardTeam() == null))) {
            Identifier ident = UUID_PLAYER_TRANSPARENT_SKIN_ID.get(id);
            if (ident != null) {
                return ident;
            }
        }
        return new Identifier(givenTexturePath);
    }


    //no update logic as that will be kept to living entity renderer to reset only once per UUID
    public static Identifier generalProcessAndReturnAlteredTexture(Identifier texture, Entity entity) {
        if (entity == null) return texture;
        UUID id = entity.getUuid();
        if (ETFConfigData.enableCustomTextures) {
            if (!PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(texture.toString())) {

                ETFUtils.processNewRandomTextureCandidate(texture.toString());
            }
            if (PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(texture.toString())) {
                //check if this needs a texture update
                if (UUID_ENTITY_AWAITING_DATA_CLEARING.containsKey(id)) {
                    if (UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                        if (!UUID_HAS_UPDATABLE_RANDOM_CASES.containsKey(id)) {
                            UUID_HAS_UPDATABLE_RANDOM_CASES.put(id, true);
                        }
                        if (UUID_HAS_UPDATABLE_RANDOM_CASES.get(id)) {
                            //skip a few ticks
                            //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                            if (UUID_ENTITY_AWAITING_DATA_CLEARING.get(id) + 100 < System.currentTimeMillis()) {
                                if (PATH_OPTIFINE_OR_JUST_RANDOM.get(texture.toString())) {
                                    //if (UUID_randomTextureSuffix.containsKey(id)) {
                                    int hold = UUID_RANDOM_TEXTURE_SUFFIX.get(id);
                                    ETFUtils.resetSingleSuffixData(id);
                                    ETFUtils.testCases(texture.toString(), id, entity, true, UUID_RANDOM_TEXTURE_SUFFIX, UUID_HAS_UPDATABLE_RANDOM_CASES);
                                    //if didn't change keep it the same
                                    if (!UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                                        UUID_RANDOM_TEXTURE_SUFFIX.put(id, hold);
                                    }
                                    //}
                                }//else here would do something for true random but no need really - may optimise this

                                UUID_ENTITY_AWAITING_DATA_CLEARING.remove(id);
                            }
                        } else {
                            UUID_ENTITY_AWAITING_DATA_CLEARING.remove(id);
                        }
                    }

                }
                if (PATH_OPTIFINE_OR_JUST_RANDOM.get(texture.toString())) {//optifine random


                    //if it doesn't have a random already assign one
                    if (!UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                        ETFUtils.testCases(texture.toString(), id, entity, false);
                        //if all failed set to vanilla
                        if (!UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                            UUID_RANDOM_TEXTURE_SUFFIX.put(id, 0);
                        }
                        UUID_ENTITY_ALREADY_CALCULATED.add(id);
                    }
                    return generalReturnAlreadySetAlteredTexture(texture, entity);

                } else {//true random assign
                    UUID_HAS_UPDATABLE_RANDOM_CASES.put(id, false);
                    if (PATH_TOTAL_TRUE_RANDOM.get(texture.toString()) > 0) {
                        if (!UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= PATH_TOTAL_TRUE_RANDOM.get(texture.toString());
                            randomReliable++;
                            if (randomReliable == 1 && PATH_IGNORE_ONE_PNG.get(texture.toString())) {
                                randomReliable = 0;
                            }
                            UUID_RANDOM_TEXTURE_SUFFIX.put(id, randomReliable);
                            UUID_ENTITY_ALREADY_CALCULATED.add(id);
                        }
                        return generalReturnAlreadySetAlteredTexture(texture, entity);
                    }//elses to vanilla
                }
            } else {
                ETFUtils.modMessage("not random", false);
            }
        }
        //return original if it was changed and should be set back to original

        return ETFUtils.returnBlinkIdOrGiven((LivingEntity) entity, texture.toString(), id);

    }

    public static Identifier generalReturnAlreadySetAlteredTexture(Identifier texture, Entity entity) {
        Identifier returned = hiddenGeneralReturnAlreadySetAlteredTexture(texture, entity);
        if (ETFConfigData.enableEmissiveTextures && isShaderOn()) {
            if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(returned.toString())) {
                if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.get(returned.toString())) {
                    return new Identifier(returned + "etf_iris_patched_file.png");
                }
            }
        }

        return returned;
    }

    //returns an already processed texture
    private static Identifier hiddenGeneralReturnAlreadySetAlteredTexture(Identifier texture, Entity entity) {
        UUID id = entity.getUuid();


        if (UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
            if (UUID_RANDOM_TEXTURE_SUFFIX.get(id) != 0) {
//                if(!PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(texture.toString())){
//                    //for special cases where we do not want to process again fully, e.g. horse armour
//                    checkAndSetPathsToUseForUncheckedTextures(texture.toString(),texture.toString().replace(".png",UUID_RANDOM_TEXTURE_SUFFIX.get(id)+".png"));
//                }
                return returnBlinkIdOrGiven((LivingEntity) entity, returnOptifineOrVanillaIdentifier(texture.toString(), UUID_RANDOM_TEXTURE_SUFFIX.get(id)).toString(), id);
            } else {
                if (!PATH_HAS_DEFAULT_REPLACEMENT.containsKey(texture.toString())) {
                    PATH_HAS_DEFAULT_REPLACEMENT.put(texture.toString(), isExistingNativeImageFile(returnOptifineOrVanillaIdentifier(texture.toString())));
                }
                if (PATH_HAS_DEFAULT_REPLACEMENT.get(texture.toString())) {
                    return returnBlinkIdOrGiven((LivingEntity) entity, returnOptifineOrVanillaIdentifier(texture.toString()).toString(), id);
                } else {
                    return returnBlinkIdOrGiven((LivingEntity) entity, texture.toString(), id);
                }
            }
        } else {
            return returnBlinkIdOrGiven((LivingEntity) entity, texture.toString(), id);
        }
    }

    public static void generalEmissiveRenderModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Identifier texture, Model model) {
        generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, texture.toString(), model);
    }

    //will set and render emissive texture for any texture and model
    public static void generalEmissiveRenderModel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, String fileString, Model model) {
        if (fileString.contains("etf_iris_patched_file.png")) {
            fileString = fileString.replace("etf_iris_patched_file.png", "");
        }
        VertexConsumer textureVert = generalEmissiveGetVertexConsumer(fileString, vertexConsumerProvider, false);
        if (textureVert != null) {
            if (isShaderOn()) {
                if (!PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(fileString)) {
                    //prevent flickering by removing pixels from the base texture
                    // the iris fix setting will now require a re-load
                    replaceTextureMinusEmissive(fileString);
                }
            }
            model.render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
        }
    }


    private static final HashMap<String, Boolean> PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION = new HashMap<>();

    private static void replaceTextureMinusEmissive(String originalTexturePath) {
        String emissiveTexturePath = null;
        for (String s :
                emissiveSuffixes) {
            String test = originalTexturePath.replace(".png", s + ".png");
            if (isExistingFileDirect(new Identifier(test), true)) {
                emissiveTexturePath = test;
                break;
            }
        }
        if (emissiveTexturePath != null) {
            NativeImage emissive = getNativeImageFromID(new Identifier(emissiveTexturePath));
            NativeImage originalCopy = getNativeImageFromID(new Identifier(originalTexturePath));

            try {
                //noinspection ConstantConditions - it's in a catch for a reason
                if (emissive.getWidth() == originalCopy.getWidth() && emissive.getHeight() == originalCopy.getHeight()) {
                    //float widthMultipleEmissive  = originalCopy.getWidth()  / (float)emissive.getWidth();
                    //float heightMultipleEmissive = originalCopy.getHeight() / (float)emissive.getHeight();

                    for (int x = 0; x < originalCopy.getWidth(); x++) {
                        for (int y = 0; y < originalCopy.getHeight(); y++) {
                            //int newX = Math.min((int)(x*widthMultipleEmissive),originalCopy.getWidth()-1);
                            //int newY = Math.min((int)(y*heightMultipleEmissive),originalCopy.getHeight()-1);
                            if (emissive.getOpacity(x, y) != 0) {
                                originalCopy.setColor(x, y, 0);
                            }
                        }
                    }
                    //no errors and fully replaced
                    PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.put(originalTexturePath, true);
                    registerNativeImageToIdentifier(originalCopy, originalTexturePath + "etf_iris_patched_file.png");
                    return;
                }
            } catch (NullPointerException ignored) {

            }
        }
        PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.put(originalTexturePath, false);
    }

    public static boolean isShaderOn() {
        if (FabricLoader.getInstance().isModLoaded("iris")) {
            return ETFIrisApi.isShaderOn();
        } else {
            return false;
        }
    }


    public static void generalEmissiveRenderPart(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Identifier texture, ModelPart model, boolean isBlockEntity) {
        generalEmissiveRenderPart(matrixStack, vertexConsumerProvider, texture.toString(), model, isBlockEntity);
    }

    //will set and render emissive texture for any texture and model
    public static void generalEmissiveRenderPart(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, String fileString, ModelPart modelPart, boolean isBlockEntity) {
        VertexConsumer textureVert = generalEmissiveGetVertexConsumer(fileString, vertexConsumerProvider, isBlockEntity);
        if (textureVert != null) {
            if (isShaderOn()) {
                if (!PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(fileString)) {
                    //prevent flickering by removing pixels from the base texture
                    // the iris fix setting will now require a re-load
                    replaceTextureMinusEmissive(fileString);
                }
            }
            modelPart.render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        }
    }

    public static VertexConsumer generalEmissiveGetVertexConsumer(String fileString, VertexConsumerProvider vertexConsumerProvider, boolean isBlockEntity) {

        if (ETFConfigData.enableEmissiveTextures) {
            //String fileString = texture.toString();
            if (!PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(fileString)) {
                //creates and sets emissive for texture if it exists
                Identifier fileName_e;
                for (String suffix1 :
                        emissiveSuffixes) {
                    fileName_e = new Identifier(fileString.replace(".png", suffix1 + ".png"));
                    if (isExistingNativeImageFile(fileName_e)) {
                        PATH_EMISSIVE_TEXTURE_IDENTIFIER.put(fileString, fileName_e);
                        break;
                    }
                }
                if (!PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(fileString)) {
                    PATH_EMISSIVE_TEXTURE_IDENTIFIER.put(fileString, null);
                }
            }
            if (PATH_EMISSIVE_TEXTURE_IDENTIFIER.containsKey(fileString)) {
                if (PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString) != null) {
                    //VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getItemEntityTranslucentCull(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString)));
                    //textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true));

                    if (isBlockEntity) {
                        if (irisDetected && ETFConfigData.fullBrightEmissives) {
                            return vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true));
                        } else {
                            return vertexConsumerProvider.getBuffer(RenderLayer.getItemEntityTranslucentCull(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString)));
                        }
                    } else {
                        if (ETFConfigData.fullBrightEmissives) {
                            return vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), !isShaderOn()));
                        } else {
                            return vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString)));
                        }
                    }
                }
            }
        }
        return null;
    }


}
