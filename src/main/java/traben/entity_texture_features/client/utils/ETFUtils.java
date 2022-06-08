package traben.entity_texture_features.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.client.ETFClient;
import traben.entity_texture_features.client.ETFTexturePropertyCase;
import traben.entity_texture_features.client.IrisCompat;
import traben.entity_texture_features.config.ETFConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.client.ETFClient.*;

public class ETFUtils {

    //checks if files exists and is in the same or higher resourcepack as id 2
    public static boolean isExistingFileAndSameOrHigherResourcepackAs(Identifier id, Identifier vanillaIdToMatch, boolean id1IsNativeImage) {
        if (isExistingFileDirect(id, id1IsNativeImage)) {
            try {
                ResourceManager resource = MinecraftClient.getInstance().getResourceManager();
                String packname = resource.getResource(id).get().getResourcePackName();
                String packname2 = resource.getResource(vanillaIdToMatch).get().getResourcePackName();
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

    //improvements by @maximum#8760
    public static boolean isExistingFileDirect(Identifier id, boolean isNativeImage) {
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();

        if (resourceManager.getResource(id).isPresent()) {
            if (isNativeImage) {
                try {
                    Resource resource = resourceManager.getResource(id).get();
                    InputStream resourceInputStream = resource.getInputStream();
                    NativeImage.read(resourceInputStream);
                } catch (Exception e) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
//    public static boolean isExistingFileDirect(Identifier id, boolean isNativeImage) {
//        try {
//            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id);
//            try {
//                InputStream stream = resource.getInputStream();
//                if (isNativeImage) {
//                    //throw exception if it's not a native image
//                    NativeImage.read(stream);
//                }
//                resource.close();
//                return true;
//            } catch (IOException e) {
//                resource.close();
//                return false;
//            }
//        } catch (IOException f) {
//            return false;
//        }
//    }

    private static boolean checkPropertyPathExistsAndSameOrHigherResourcepackAs(String propertiesPath, String path2) {
        return isExistingFileAndSameOrHigherResourcepackAs(new Identifier(propertiesPath), new Identifier(path2), false);
    }


    public static void tryClearUneededMobData() {
        //todo implement logic here to check if saved mob data is still needed and remove all data of that UUID if no longer needed
        //check either random known UUIDs or find other selection logic
    }


    public static void resetAllETFEntityData() {
        //logMessage("Clearing / Reloading ETF data...", false);
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

        ETFPlayerSkinUtils.clearAllPlayerETFData();

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

        //if incompatabilities are detected and are not set to be ignored by config then set conditions
        if (!ETFConfigData.ignoreConfigWarnings) {
            if (ETFConfigData.skinFeaturesEnabled && FabricLoader.getInstance().isModLoaded("figura")) {
                ETFUtils.logWarn(Text.translatable("config." + ETFClient.MOD_ID + ".figura_warn.text").getString(), false);
                ETFConfigData.skinFeaturesEnabled = false;
                ETFUtils.saveConfig();
            }
            //more compat changes when required
        }
    }

    public static void resetSingleSuffixData(UUID id) {
        UUID_RANDOM_TEXTURE_SUFFIX.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX_2.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX_3.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX_4.removeInt(id);
        KNOWN_UUID_LIST.removeInt(id);
    }

    public static void forceResetAllDataOfUUID(UUID id) {
        KNOWN_UUID_LIST.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX_2.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX_3.removeInt(id);
        UUID_RANDOM_TEXTURE_SUFFIX_4.removeInt(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES.removeBoolean(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES_2.removeBoolean(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES_3.removeBoolean(id);
        UUID_HAS_UPDATABLE_RANDOM_CASES_4.removeBoolean(id);
        UUID_ENTITY_ALREADY_CALCULATED.remove(id);
        UUID_ENTITY_AWAITING_DATA_CLEARING.removeLong(id);
        UUID_ENTITY_AWAITING_DATA_CLEARING_2.removeLong(id);
        UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.remove(id);
        UUID_TRIDENT_NAME.remove(id);
        UUID_NEXT_BLINK_TIME.removeLong(id);
    }


    public static Properties readProperties(String path) {
        return readProperties(path, null);
    }

    public static Properties readProperties(String path, String pathOfTextureToUseForResourcepackCheck) {
        Properties props = new Properties();
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path)).get();
            //skip if it needs to be same resource-pack
            if (pathOfTextureToUseForResourcepackCheck != null) {
                ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
                String packName1 = resource.getResourcePackName();
                String packName2 = resourceManager.getResource(new Identifier(pathOfTextureToUseForResourcepackCheck)).get().getResourcePackName();
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
                in.close();
            } catch (Exception e) {
                //resource.close();
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
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier).get();
            try {
                InputStream in = resource.getInputStream();
                img = NativeImage.read(in);
                in.close();
            } catch (Exception e) {
                //resource.close();
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
                List<ETFTexturePropertyCase> allCasesForTexture = new ArrayList<>();
                for (Integer num :
                        numbersList) {
                    //System.out.println("constructed as "+num);
                    //loops through each known number in properties
                    //all case.1 ect should be processed here
                    Integer[] suffixes = {};
                    Integer[] weights = {};
                    String[] biomes = {};
                    Integer[] heights = {};
                    ArrayList<String> names = new ArrayList<>();
                    String[] professions = {};
                    String[] collarColours = {};
                    int baby = 0; // 0 1 2 - dont true false
                    int weather = 0; //0,1,2,3 - no clear rain thunder
                    String[] health = {};
                    Integer[] moon = {};
                    String[] daytime = {};
                    String[] blocks = {};
                    String[] teams = {};
                    Integer[] sizes = {};

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
                            data = data.replaceAll("\\(", "").replaceAll("\\)", "");
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

                    if (props.containsKey("names." + num)) {
                        String dataFromProps = props.getProperty("names." + num).trim();
                        if (dataFromProps.contains("regex:") || dataFromProps.contains("pattern:")) {
                            names.add(dataFromProps);
                        } else {
                            //names = dataFromProps.split("\s+");
                            //allow    "multiple names" among "other"
                            //List<String> list = new ArrayList<>();
                            //add the full line as the first name option to allow for simple multiple names
                            //incase someone just writes   names.1=john smith
                            //instead of                   names.1="john smith"
                            names.add(dataFromProps);

                            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(dataFromProps);
                            while (m.find()) {
                                names.add(m.group(1).replace("\"", "").trim());
                            }
                            //names.addAll(list);
                        }
                    }
                    if (props.containsKey("name." + num)) {
                        String dataFromProps = props.getProperty("name." + num).trim();
                        names.add(dataFromProps);
                    }
                    if (props.containsKey("professions." + num)) {
                        professions = props.getProperty("professions." + num).trim().split("\s+");
                    }
                    if (props.containsKey("collarColors." + num) || props.containsKey("colors." + num)) {
                        collarColours = props.containsKey("collarColors." + num) ? props.getProperty("collarColors." + num).trim().split("\s+") : props.getProperty("colors." + num).trim().split("\s+");
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

                    if (props.containsKey("sizes." + num)) {
                        String dataFromProps = props.getProperty("sizes." + num).trim();
                        String[] sizeData = dataFromProps.split("\s+");
                        ArrayList<Integer> sizeNumbers = new ArrayList<>();
                        for (String data :
                                sizeData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    sizeNumbers.addAll(Arrays.asList(getIntRange(data)));
                                } else {
                                    sizeNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        sizes = sizeNumbers.toArray(new Integer[0]);
                    }

                    //array faster to use
                    //list easier to build
                    String[] namesArray = names.toArray(new String[0]);

                    if (suffixes.length != 0) {
                        allCasesForTexture.add(new ETFTexturePropertyCase(suffixes, weights, biomes, heights, namesArray, professions, collarColours, baby, weather, health, moon, daytime, blocks, teams, num, sizes));
                    }
                }
                //for (ETFTexturePropertyCase t:
                //     allCasesForTexture) {
                //    System.out.println("foreach as "+t.propertyNumber);
                //}
                if (!allCasesForTexture.isEmpty()) {
                    PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.put(vanillaTexturePath, allCasesForTexture);
                    PATH_OPTIFINE_OR_JUST_RANDOM.put(vanillaTexturePath, true);
                } else {
                    logMessage("Ignoring properties file that failed to load any cases @ " + propertiesPath, false);
                    PATH_FAILED_PROPERTIES_TO_IGNORE.add(propertiesPath);
                }
            } else {//properties file is null
                logMessage("Ignoring properties file that was null @ " + propertiesPath, false);
                PATH_FAILED_PROPERTIES_TO_IGNORE.add(propertiesPath);
            }
        } catch (Exception e) {
            logWarn("Ignoring properties file that caused Exception @ " + propertiesPath + e, false);
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
                logMessage("Optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \"" + rawRange.replace("N", "-") + "\"", false);
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

    public static void testCases(String vanillaPath, UUID id, Entity entity, boolean isUpdate, Object2IntOpenHashMap<UUID> UUID_RandomSuffixMap, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
        for (ETFTexturePropertyCase test :
                PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.get(vanillaPath)) {

            //skip if it is only an update and case is not updatable
            if (test.testEntity((LivingEntity) entity, UUID_ENTITY_ALREADY_CALCULATED.contains(id), UUID_CaseHasUpdateablesCustom)) {
                UUID_RandomSuffixMap.put(id, test.getWeightedSuffix(id, PATH_IGNORE_ONE_PNG.getBoolean(vanillaPath)));
                Identifier tested = returnOptifineOrVanillaIdentifier(vanillaPath, UUID_RandomSuffixMap.getInt(id));

                if (!isExistingNativeImageFile(tested) && !isUpdate) {
                    UUID_RandomSuffixMap.put(id, 0);
                }
                break;
            }
        }
        if (!UUID_CaseHasUpdateablesCustom.containsKey(id))
            UUID_CaseHasUpdateablesCustom.put(id, false);
    }

//    public static void modMessage(String message, boolean inChat) {
//        if (inChat) {
//            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
//            if (plyr != null) {
//                plyr.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: " + message), false);
//            } else {
//                LogManager.getLogger().info("[Entity Texture Features]: " + message);
//            }
//        } else {
//            LogManager.getLogger().info("[Entity Texture Features]: " + message);
//        }
//    }


    //debug printing
    public static void checkAndPrintEntityDebugIfNeeded(UUID id, String texturePath) {
        if (ETFConfigData.debugLoggingMode != ETFConfig.DebugLogMode.None && UUID_DEBUG_EXPLANATION_MARKER.contains(id)) {
            //stringbuilder as chat messages have a prefix that can be bothersome
            StringBuilder message = new StringBuilder();
            boolean inChat = ETFConfigData.debugLoggingMode == ETFConfig.DebugLogMode.Chat;
            message.append("ETF entity debug data for entity with UUID=[").append(id.toString()).append("]:\n{\n    vanillaTexture=").append(texturePath);

            if (PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(texturePath))
                message.append("\nhas an optifine properties file=").append(PATH_OPTIFINE_OR_JUST_RANDOM.getBoolean(texturePath));
            if (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(texturePath))
                message.append("\npath of custom textures=").append(switch (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.getInt(texturePath)) {
                    case 0 -> "optifine/random";
                    case 1 -> "optifine/mob";
                    case 3 -> "etf/random";
                    default -> "vanilla";
                });
            if (PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.containsKey(texturePath))
                message.append("\namount of properties=").append(PATH_OPTIFINE_RANDOM_SETTINGS_PER_TEXTURE.get(texturePath).size());
            if (PATH_TOTAL_TRUE_RANDOM.containsKey(texturePath))
                message.append("\ntotal random textures detected=").append(PATH_TOTAL_TRUE_RANDOM.getInt(texturePath));
            if (UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id))
                message.append("\nRandom texture number of this mob=")
                        .append(UUID_RANDOM_TEXTURE_SUFFIX.getInt(id))
                        .append(", probably uses {")
                        .append(texturePath.replace(".png", UUID_RANDOM_TEXTURE_SUFFIX.getInt(id) + ".png}"));
            if (UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.containsKey(id))
                message.append("\nOriginal spawn data *unsorted*=").append(Arrays.toString(UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(id)));
            message.append("\n}");

            ETFUtils.logMessage(message.toString(), inChat);
            UUID_DEBUG_EXPLANATION_MARKER.remove(id);
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
                player.sendMessage(MutableText.of(new LiteralTextContent("[INFO] [Entity Texture Features]: " + obj)).formatted(Formatting.GRAY, Formatting.ITALIC), false);
            } else {
                ETFClient.LOGGER.info(obj);
            }
        } else {
            ETFClient.LOGGER.info(obj);
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
                player.sendMessage(MutableText.of(new LiteralTextContent("[WARN] [Entity Texture Features]: " + obj)).formatted(Formatting.YELLOW), false);
            } else {
                ETFClient.LOGGER.warn(obj);
            }
        } else {
            ETFClient.LOGGER.warn(obj);
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
                player.sendMessage(MutableText.of(new LiteralTextContent("[ERROR] [Entity Texture Features]: " + obj)).formatted(Formatting.RED, Formatting.BOLD), false);
            } else {
                ETFClient.LOGGER.error(obj);
            }
        } else {
            ETFClient.LOGGER.error(obj);
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
            return switch (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.getInt(vanillaPath)) {
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

            checkPath = switch (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.getInt(vanillaPath)) {
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

    //this requires the game to be more fully load and won't always happen properly on initial boot
    private static void setEmissiveSuffix() {
        try {
            List<Properties> props = new ArrayList<>();
            String[] paths = {"optifine/emissive.properties", "textures/emissive.properties", "etf/emissive.properties"};
            for (String path :
                    paths) {
                Properties prop = readProperties(path);
                if (prop != null)
                    props.add(prop);
            }

            ObjectOpenHashSet<String> builder = new ObjectOpenHashSet<>();
            for (Properties prop :
                    props) {
                //not an optifine property that I know of but this has come up in a few packs, so I am supporting it
                if (prop.containsKey("entities.suffix.emissive")) {
                    builder.add(prop.getProperty("entities.suffix.emissive"));
                }
                if (prop.containsKey("suffix.emissive")) {
                    builder.add(prop.getProperty("suffix.emissive"));
                }
            }
            if (ETFConfigData.alwaysCheckVanillaEmissiveSuffix) {
                builder.add("_e");
            }


            emissiveSuffixes = builder.toArray(new String[0]);
            if (emissiveSuffixes.length == 0) {
                logMessage("no emissive suffixes found: default emissive suffix '_e' used");
                emissiveSuffixes = new String[]{"_e"};
            } else {
                logMessage("emissive suffixes loaded: " + Arrays.toString(emissiveSuffixes));
            }
        } catch (Exception e) {
            logError("emissive suffixes could not be read: default emissive suffix '_e' used");
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

    public static void registerNativeImageToIdentifier(NativeImage img, String identifierPath) {
        registerNativeImageToIdentifier(img, new Identifier(identifierPath));
    }

    public static void registerNativeImageToIdentifier(NativeImage img, Identifier identifier) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        NativeImageBackedTexture bob = new NativeImageBackedTexture(img);
        textureManager.registerTexture(identifier, bob);

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
                if (PATH_HAS_BLINK_TEXTURE.getBoolean(givenTexturePath)) {
                    if (entity.getPose() == EntityPose.SLEEPING) {
                        return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                    }
                    //force eyes closed if blinded
                    else if (entity.hasStatusEffect(StatusEffects.BLINDNESS)) {
                        if (PATH_HAS_BLINK_TEXTURE_2.containsKey(givenTexturePath)) {
                            return new Identifier(givenTexturePath.replace(".png", (PATH_HAS_BLINK_TEXTURE_2.getBoolean(givenTexturePath) ? "_blink2.png" : "_blink.png")));
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
                        long nextBlink = UUID_NEXT_BLINK_TIME.getLong(id);
                        long currentTime = entity.world.getTime();

                        if (currentTime >= nextBlink - blinkLength && currentTime <= nextBlink + blinkLength) {
                            if (PATH_HAS_BLINK_TEXTURE_2.containsKey(givenTexturePath)) {
                                if (PATH_HAS_BLINK_TEXTURE_2.getBoolean(givenTexturePath)) {
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
                && ETFPlayerSkinUtils.UUID_PLAYER_TRANSPARENT_SKIN_ID.containsKey(id) && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (entity.isTeammate(MinecraftClient.getInstance().player) || entity.getScoreboardTeam() == null))) {
            Identifier ident = ETFPlayerSkinUtils.UUID_PLAYER_TRANSPARENT_SKIN_ID.get(id);
            if (ident != null) {
                return ident;
            }
        }
        return new Identifier(givenTexturePath);
    }

    //this is for entity rendering features that do not need separate processing
    //e.g horse armor and markings, and glowing eye textures
    //todo reminder warden implementation needs several textures
    public static Identifier generalReturnAlteredFeatureTextureOrOriginal(Identifier originalFeatureTexture, Entity entity) {

        Identifier alteredFeatureTexture = ETFUtils.generalReturnAlreadySetAlteredTexture(originalFeatureTexture, entity);

        if (!PATH_IS_EXISTING_FEATURE.containsKey(alteredFeatureTexture.toString())) {
            PATH_IS_EXISTING_FEATURE.put(alteredFeatureTexture.toString(), isExistingNativeImageFile(alteredFeatureTexture));
        }
        if (PATH_IS_EXISTING_FEATURE.getBoolean(alteredFeatureTexture.toString())) {
            return alteredFeatureTexture;
        }

        return originalFeatureTexture;
    }


    //no update logic as that will be kept to living entity renderer to reset only once per UUID
    public static Identifier generalProcessAndReturnAlteredTexture(Identifier texture, Entity entity) {
        if (entity == null) return texture;
        if (entity.getWorld() == null) return texture;
        UUID id = entity.getUuid();
        if (ETFConfigData.enableCustomTextures) {

            checkAndPrintEntityDebugIfNeeded(id, texture.toString());

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
                        if (UUID_HAS_UPDATABLE_RANDOM_CASES.getBoolean(id)) {
                            //skip a few ticks
                            //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                            if (UUID_ENTITY_AWAITING_DATA_CLEARING.getLong(id) + 100 < System.currentTimeMillis()) {
                                if (PATH_OPTIFINE_OR_JUST_RANDOM.getBoolean(texture.toString())) {
                                    //if (UUID_randomTextureSuffix.containsKey(id)) {
                                    int hold = UUID_RANDOM_TEXTURE_SUFFIX.getInt(id);
                                    ETFUtils.resetSingleSuffixData(id);
                                    ETFUtils.testCases(texture.toString(), id, entity, true, UUID_RANDOM_TEXTURE_SUFFIX, UUID_HAS_UPDATABLE_RANDOM_CASES);
                                    //if didn't change keep it the same
                                    if (!UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                                        UUID_RANDOM_TEXTURE_SUFFIX.put(id, hold);
                                    }
                                    //}
                                }//else here would do something for true random but no need really - may optimise this

                                UUID_ENTITY_AWAITING_DATA_CLEARING.removeLong(id);
                            }
                        } else {
                            UUID_ENTITY_AWAITING_DATA_CLEARING.removeLong(id);
                        }
                    }

                }
                if (PATH_OPTIFINE_OR_JUST_RANDOM.getBoolean(texture.toString())) {//optifine random


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
                    if (PATH_TOTAL_TRUE_RANDOM.getInt(texture.toString()) > 0) {
                        if (!UUID_RANDOM_TEXTURE_SUFFIX.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= PATH_TOTAL_TRUE_RANDOM.getInt(texture.toString());
                            randomReliable++;
                            if (randomReliable == 1 && PATH_IGNORE_ONE_PNG.getBoolean(texture.toString())) {
                                randomReliable = 0;
                            }
                            UUID_RANDOM_TEXTURE_SUFFIX.put(id, randomReliable);
                            UUID_ENTITY_ALREADY_CALCULATED.add(id);
                        }
                        return generalReturnAlreadySetAlteredTexture(texture, entity);
                    }//elses to vanilla
                }
            } else {
                logMessage("not random", false);
            }
        }
        //return original if it was changed and should be set back to original

        return ETFUtils.returnBlinkIdOrGiven((LivingEntity) entity, texture.toString(), id);

    }

    public static Identifier generalReturnAlreadySetAlteredTexture(Identifier texture, Entity entity) {
        Identifier returned = hiddenGeneralReturnAlreadySetAlteredTexture(texture, entity);
        if (ETFConfigData.enableEmissiveTextures && IrisCompat.isShaderPackInUse()) {
            if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(returned.toString())) {
                if (PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.getBoolean(returned.toString())) {
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
            if (UUID_RANDOM_TEXTURE_SUFFIX.getInt(id) != 0) {
//                if(!PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(texture.toString())){
//                    //for special cases where we do not want to process again fully, e.g. horse armour
//                    checkAndSetPathsToUseForUncheckedTextures(texture.toString(),texture.toString().replace(".png",UUID_RANDOM_TEXTURE_SUFFIX.get(id)+".png"));
//                }
                return returnBlinkIdOrGiven((LivingEntity) entity, returnOptifineOrVanillaIdentifier(texture.toString(), UUID_RANDOM_TEXTURE_SUFFIX.getInt(id)).toString(), id);
            } else {
                if (!PATH_HAS_DEFAULT_REPLACEMENT.containsKey(texture.toString())) {
                    PATH_HAS_DEFAULT_REPLACEMENT.put(texture.toString(), isExistingNativeImageFile(returnOptifineOrVanillaIdentifier(texture.toString())));
                }
                if (PATH_HAS_DEFAULT_REPLACEMENT.getBoolean(texture.toString())) {
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
            if (IrisCompat.isShaderPackInUse()) {
                if (!PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(fileString)) {
                    //prevent flickering by removing pixels from the base texture
                    // the iris fix setting will now require a re-load
                    replaceTextureMinusEmissive(fileString);
                }
            }
            model.render(matrixStack, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
        }
    }


    public static void replaceTextureMinusEmissive(String originalTexturePath) {
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

//    public static boolean isShaderOn() {
//        if (FabricLoader.getInstance().isModLoaded("iris")) {
//            return IrisCompat.isShaderPackInUse();
//        } else {
//            return false;
//        }
//    }

    public static void generalEmissiveRenderPart(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Identifier texture, ModelPart modelPart, boolean isBlockEntity) {
        generalEmissiveRenderPart(matrixStack, vertexConsumerProvider, texture.toString(), modelPart, isBlockEntity);
    }

    //will set and render emissive texture for any texture and model
    public static void generalEmissiveRenderPart(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, String fileString, ModelPart modelPart, boolean isBlockEntity) {
        if (fileString.contains("etf_iris_patched_file.png")) {
            fileString = fileString.replace("etf_iris_patched_file.png", "");
        }
        VertexConsumer textureVert = generalEmissiveGetVertexConsumer(fileString, vertexConsumerProvider, isBlockEntity);
        if (textureVert != null) {
            if (IrisCompat.isShaderPackInUse()) {
                if (!PATH_HAS_EMISSIVE_OVERLAY_REMOVED_VERSION.containsKey(fileString)) {
                    //prevent flickering by removing pixels from the base texture
                    // the iris fix setting will now require a re-load
                    replaceTextureMinusEmissive(fileString);
                }
            }
            //System.out.println("rendering="+fileString);
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
                    //System.out.println("tried="+fileName_e);
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
                            return vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), !IrisCompat.isShaderPackInUse()));
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
