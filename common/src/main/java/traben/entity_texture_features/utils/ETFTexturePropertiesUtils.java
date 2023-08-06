package traben.entity_texture_features.utils;

import com.google.common.base.CaseFormat;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.entity_handlers.ETFBlockEntityWrapper;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.entity_handlers.ETFEntityWrapper;
import traben.entity_texture_features.mixin.accessor.MooshroomEntityAccessor;
import traben.entity_texture_features.texture_handlers.ETFManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public abstract class ETFTexturePropertiesUtils {


    public static void processNewOptifinePropertiesFile(ETFEntity entity, Identifier vanillaIdentifier, Identifier properties) {
        ETFManager manager = ETFManager.getInstance();
        try {
            Properties props = ETFUtils2.readAndReturnPropertiesElseNull(properties);

            if (props != null) {
                //check for etf entity properties
                if (props.containsKey("vanillaBrightnessOverride")) {
                    String value = props.getProperty("vanillaBrightnessOverride").trim();
                    int tryNumber;
                    try {
                        tryNumber = Integer.parseInt(value.replaceAll("\\D", ""));
                    } catch (NumberFormatException e) {
                        tryNumber = 0;
                    }
                    if (tryNumber >= 16) tryNumber = 15;
                    if (tryNumber < 0) tryNumber = 0;
                    manager.ENTITY_TYPE_VANILLA_BRIGHTNESS_OVERRIDE_VALUE.put(entity.getType(), tryNumber);
                }
                if (entity.isZombiePiglin()
                        && props.containsKey("showHiddenModelParts")
                        && "true".equals(props.getProperty("showHiddenModelParts"))) {
                    manager.zombiePiglinRightEarEnabled = true;
                }
                if (props.containsKey("suppressParticles")
                        && "true".equals(props.getProperty("suppressParticles"))) {
                    manager.ENTITY_TYPE_IGNORE_PARTICLES.add(entity.getType());
                }

                if (props.containsKey("entityRenderLayerOverride")) {
                    String layer = props.getProperty("entityRenderLayerOverride");
                    //noinspection EnhancedSwitchMigration
                    switch (layer) {
                        case "translucent":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 1);
                            break;
                        case "translucent_cull":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 2);
                            break;
                        case "end_portal":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 3);
                            break;
                        case "outline":
                            manager.ENTITY_TYPE_RENDER_LAYER.put(entity.getType(), 4);
                            break;
                    }
                }
                List<ETFTexturePropertyCase> allCasesForTexture = getAllValidPropertyObjects(props, "skins", vanillaIdentifier);

                if (!allCasesForTexture.isEmpty()) {
                    //it all worked now just get the first texture called and everything is set for the next time the texture is called for fast processing
                    manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, allCasesForTexture);
                } else {
                    ETFUtils2.logMessage("Ignoring properties file that failed to load any cases @ " + vanillaIdentifier, false);
                    manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
                }
            } else {//properties file is null
                ETFUtils2.logMessage("Ignoring properties file that was null @ " + vanillaIdentifier, false);
                manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Ignoring properties file that caused Exception @ " + vanillaIdentifier + "\n" + e, false);
            e.printStackTrace();
            manager.OPTIFINE_PROPERTY_CACHE.put(vanillaIdentifier, null);
        }
    }

    public static List<ETFTexturePropertyCase> getAllValidPropertyObjects(Properties props, String suffixToTest, Identifier vanillaIdentifier) {
        Set<String> propIds = props.stringPropertyNames();
        //set so only 1 of each
        Set<Integer> numbers = new HashSet<>();

        //get the numbers we are working with
        for (String str :
                propIds) {
            str = str.replaceAll("\\D", "");
            if (!str.isEmpty()) {
                try {
                    numbers.add(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    ETFUtils2.logWarn("properties file number error in start count");
                }
            }
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
            Integer[] suffixes = getSuffixes(props, num, suffixToTest);


            //list easier to build
            if (suffixes != null && suffixes.length != 0) {
                allCasesForTexture.add(new ETFTexturePropertyCase(
                        suffixes,
                        getWeights(props, num),
                        getBiomes(props, num),
                        getHeights(props, num),
                        getNames(props, num),
                        getProfessions(props, num),
                        getColors(props, num),
                        getBaby(props, num),
                        getWeather(props, num), //0,1,2,3 - no clear rain thunder
                        getHealth(props, num),
                        getMoon(props, num),
                        getDayTime(props, num),
                        getBlocks(props, num),
                        getTeams(props, num),
                        getSizes(props, num),
                        getSpeed(props, num),
                        getJump(props, num),
                        getMaxHealth(props, num),
                        getLlamaInv(props, num),
                        getAngry(props, num),
                        getHiddenGene(props, num),
                        getPlayerCreated(props, num),
                        getScreamingGoat(props, num),
                        getDistanceFromPlayer(props, num),
                        getCreeperCharge(props, num),
                        getStatusEffect(props, num),
                        getItems(props, num),
                        getMoving(props, num),
                        getNBT(props, num)
                ));

            } else {
                ETFUtils2.logWarn("property number \"" + num + ". in file \"" + vanillaIdentifier + ". failed to read.");
            }
        }
        return allCasesForTexture;
    }


    @Nullable
    private static Integer[] getSuffixes(Properties props, int num, String suffixToTest) {

        if ("skins".equals(suffixToTest)) {
            Integer[] ints = getGenericIntegerSplitWithRanges(props, num, "skins");
            return ints == null ? getGenericIntegerSplitWithRanges(props, num, "textures") : ints;
        }
        return getGenericIntegerSplitWithRanges(props, num, suffixToTest);
    }

    @Nullable
    private static Integer[] getWeights(Properties props, int num) {
        if (props.containsKey("weights." + num)) {
            String dataFromProps = props.getProperty("weights." + num).trim();
            String[] weightData = dataFromProps.split("\\s+");
            ArrayList<Integer> builder = new ArrayList<>();
            for (String s :
                    weightData) {
                s = s.trim();
                if (!s.replaceAll("\\D", "").isEmpty()) {
                    try {
                        int tryNumber = Integer.parseInt(s.replaceAll("\\D", ""));
                        builder.add(tryNumber);
                    } catch (NumberFormatException e) {
                        ETFUtils2.logWarn("properties files number error in weights category");
                    }
                }
            }
            return builder.toArray(new Integer[0]);
        }
        return null;
    }

    @Nullable
    private static String getBiomes(Properties props, int num) {
        if (props.containsKey("biomes." + num)) {
            String dataFromProps = props.getProperty("biomes." + num).strip();
            String[] biomeList = dataFromProps.split("\\s+");

            //strip out old format optifine biome names
            //I could be way more in-depth and make these line up to all variants but this is legacy code
            //only here for compat, pack makers need to fix these
            if (biomeList.length > 0) {
                for (int currentIndex = 0; currentIndex < biomeList.length; currentIndex++) {
                    String currentBiome = biomeList[currentIndex].strip();
                    switch (currentBiome) {
                        //case "Ocean" -> biomeList[i] = "ocean";
                        //case "Plains" -> biomeList[i] = "plains";
                        case "ExtremeHills" -> biomeList[currentIndex] = "stony_peaks";
                        case "Forest", "ForestHills" -> biomeList[currentIndex] = "forest";
                        case "Taiga", "TaigaHills" -> biomeList[currentIndex] = "taiga";
                        case "Swampland" -> biomeList[currentIndex] = "swamp";
//                        case "River" -> biomeList[currentIndex] = "river";
                        case "Hell" -> biomeList[currentIndex] = "nether_wastes";
                        case "Sky" -> biomeList[currentIndex] = "the_end";
                        //case "FrozenOcean" -> biomeList[i] = "frozen_ocean";
                        //case "FrozenRiver" -> biomeList[i] = "frozen_river";
                        case "IcePlains" -> biomeList[currentIndex] = "snowy_plains";
                        case "IceMountains" -> biomeList[currentIndex] = "snowy_slopes";
                        case "MushroomIsland", "MushroomIslandShore" -> biomeList[currentIndex] = "mushroom_fields";
                        //case "Beach" -> biomeList[i] = "beach";
                        case "DesertHills", "Desert" -> biomeList[currentIndex] = "desert";
                        case "ExtremeHillsEdge" -> biomeList[currentIndex] = "meadow";
                        case "Jungle", "JungleHills" -> biomeList[currentIndex] = "jungle";
                        default -> {
                            if (!currentBiome.contains("_") && !currentBiome.equals(currentBiome.toLowerCase())) {
                                //has capitals and no "_" it is probably the camel case format
                                biomeList[currentIndex] = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,currentBiome);
                                //biomeList[currentIndex] = currentBiome.replaceAll("(\\B)([A-Z])", "_$2");
                            }
                        }
                    }
                }
                StringBuilder builder = new StringBuilder();
                for (String str :
                        biomeList) {
                    builder.append(str).append(" ");
                }
                //lower case required
                return builder.toString().trim().toLowerCase();
            }

        }
        return null;
    }

    @Nullable
    private static Integer[] getHeights(Properties props, int num) {
        //add legacy height support
        if (!props.containsKey("heights." + num) && (props.containsKey("minHeight." + num) || props.containsKey("maxHeight." + num))) {
            String min = "-64";
            String max = "319";
            if (props.containsKey("minHeight." + num)) {
                min = props.getProperty("minHeight." + num).strip();
            }
            if (props.containsKey("maxHeight." + num)) {
                max = props.getProperty("maxHeight." + num).strip();
            }
            props.put("heights." + num, min + "-" + max);
        }
        return getGenericIntegerSplitWithRanges(props, num, "heights");
        //        if (props.containsKey("." + num)) {
        //            String dataFromProps = props.getProperty("heights." + num).trim();
        //            String[] heightData = dataFromProps.split("\s+");
        //            ArrayList<Integer> heightNumbers = new ArrayList<>();
        //            for (String data :
        //                    heightData) {
        //                data = data.replaceAll("\\(", "").replaceAll("\\)", "");
        //                //check if range
        //                data = data.trim();
        //                if (!data.replaceAll("\\D", "").isEmpty()) {
        //                    if (data.contains("-")) {
        //                        heightNumbers.addAll(Arrays.asList(ETFUtils2.getIntRange(data)));
        //                    } else {
        //                        try {
        //                            int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
        //                            heightNumbers.add(tryNumber);
        //                        } catch (NumberFormatException e) {
        //                            ETFUtils2.logWarn("properties files number error in height category");
        //                        }
        //                    }
        //                }
        //            }
        //            return heightNumbers.toArray(new Integer[0]);
        //        }
        //        return null;
    }

    @Nullable
    private static String getNames(Properties props, int num) {
        ArrayList<String> names = new ArrayList<>();
        if (props.containsKey("names." + num)) {
            String dataFromProps = props.getProperty("names." + num).trim();
            if (dataFromProps.contains("regex:") || dataFromProps.contains("pattern:")) {
                names.add(dataFromProps);
            } else {
                //names = dataFromProps.split("\s+");
                //allow    "multiple names" among "other"
                //List<String> list = new ArrayList<>();
                //add the full line as the first name option to allow for simple multiple names
                //in case someone just writes   names.1=john smith
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
        if (names.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String str :
                names) {
            builder.append(str).append(" ");
        }
        return builder.toString().trim();
    }

    @Nullable
    private static String[] getProfessions(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "professions");
    }

    @Nullable
    private static String[] getColors(Properties props, int num) {
        String[] str = getGenericStringSplitProperty(props, num, "collarColors");
        return str == null ? getGenericStringSplitProperty(props, num, "colors") : str;
    }

    @Nullable
    private static Boolean getBaby(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "baby");
    }

    private static WeatherType getWeather(Properties props, int num) {
        if (props.containsKey("weather." + num)) {
            return WeatherType.getType(props.getProperty("weather." + num).trim());
        }
        return null;
    }

    @Nullable
    private static String[] getHealth(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "health");
    }

    @Nullable
    private static Integer[] getMoon(Properties props, int num) {
        return getGenericIntegerSplitWithRanges(props, num, "moonPhase");
    }

    @Nullable
    private static String[] getDayTime(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "dayTime");
    }

    @Nullable
    private static String[] getBlocks(Properties props, int num) {
        String[] str = getGenericStringSplitProperty(props, num, "blocks");
        return str == null ? getGenericStringSplitProperty(props, num, "block") : str;
    }

    @Nullable
    private static String getTeams(Properties props, int num) {
        if (props.containsKey("teams." + num)) {
            return props.getProperty("teams." + num).trim();
        } else if (props.containsKey("team." + num)) {
            return props.getProperty("team." + num).trim();
        }
        return null;

//        if (props.containsKey("teams." + num)) {
//            String teamData = props.getProperty("teams." + num).trim();
//            List<String> list = new ArrayList<>();
//            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
//            while (m.find()) {
//                list.add(m.group(1).replace("\"", ""));
//            }
//            return list.toArray(new String[0]);
//        } else if (props.containsKey("team." + num)) {
//            String teamData = props.getProperty("team." + num).trim();
//            List<String> list = new ArrayList<>();
//            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
//            while (m.find()) {
//                list.add(m.group(1).replace("\"", ""));
//            }
//            return list.toArray(new String[0]);
//        }
//        return null;
    }

    @Nullable
    private static Integer[] getSizes(Properties props, int num) {
        return getGenericIntegerSplitWithRanges(props, num, "sizes");
    }

    @Nullable
    private static Double[] getSpeed(Properties props, int num) {
        return getGenericMinMaxDouble(props, num, "speed");
    }

    @Nullable
    private static Double[] getJump(Properties props, int num) {
        return getGenericMinMaxDouble(props, num, "jumpStrength");
    }

    @Nullable
    private static String[] getMaxHealth(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "maxHealth");
    }

    @Nullable
    private static Integer[] getLlamaInv(Properties props, int num) {
        return getGenericIntegerSplitWithRanges(props, num, "llamaInventory");
    }

    @Nullable
    private static Boolean getAngry(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "angry");
    }

    @Nullable
    private static PandaEntity.Gene[] getHiddenGene(Properties props, int num) {
        if (props.containsKey("hiddenGene." + num)) {
            String[] input = props.getProperty("hiddenGene." + num).trim().split("\\s+");
            ArrayList<PandaEntity.Gene> genes = new ArrayList<>();
            for (String gene :
                    input) {
                //not enhanced for back compat
                //noinspection EnhancedSwitchMigration
                switch (gene.trim()) {
                    case "normal":
                        genes.add(PandaEntity.Gene.NORMAL);
                        break;
                    case "lazy":
                        genes.add(PandaEntity.Gene.LAZY);
                        break;
                    case "worried":
                        genes.add(PandaEntity.Gene.WORRIED);
                        break;
                    case "playful":
                        genes.add(PandaEntity.Gene.PLAYFUL);
                        break;
                    case "brown":
                        genes.add(PandaEntity.Gene.BROWN);
                        break;
                    case "weak":
                        genes.add(PandaEntity.Gene.WEAK);
                        break;
                    case "aggressive":
                        genes.add(PandaEntity.Gene.AGGRESSIVE);
                        break;
                    default:
                        ETFUtils2.logWarn("properties files number error in hiddenGene category, caused by input: " + gene);
                }
            }
            return genes.toArray(new PandaEntity.Gene[0]);
        }
        return null;
    }

    @Nullable
    private static Boolean getPlayerCreated(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "playerCreated");
    }

    @Nullable
    private static Boolean getScreamingGoat(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "screamingGoat");
    }

    @Nullable
    private static String[] getDistanceFromPlayer(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "distanceFromPlayer");
    }

    @Nullable
    private static Boolean getCreeperCharge(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "creeperCharged");
    }

    @Nullable
    private static StatusEffect[] getStatusEffect(Properties props, int num) {
        if (props.containsKey("statusEffect." + num)) {
            String dataFromProps = props.getProperty("statusEffect." + num).trim();
            String[] columnData = dataFromProps.split("\\s+");
            ArrayList<StatusEffect> statuses = new ArrayList<>();
            for (String data :
                    columnData) {
                data = data.replaceAll("\\(", "").replaceAll("\\)", "");
                //check if range
                data = data.trim();
                if (!data.replaceAll("\\D", "").isEmpty()) {
                    try {
                        int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
                        StatusEffect attempt = StatusEffect.byRawId(tryNumber);
                        if (attempt != null) {
                            statuses.add(attempt);
                        }
                    } catch (NumberFormatException e) {
                        ETFUtils2.logWarn("properties files number error in statusEffects category");
                    }

                }
            }
            return statuses.toArray(new StatusEffect[0]);
        }
        return null;
    }

    @Nullable
    private static String[] getItems(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "items");
    }

    @Nullable
    private static Boolean getMoving(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "moving");
    }

    @Nullable
    private static String[] getGenericStringSplitProperty(Properties props, int num, String propertyName) {
        if (props.containsKey(propertyName + "." + num)) {
            return props.getProperty(propertyName + "." + num).trim().split("\\s+");
        }
        return null;
    }

    @Nullable
    private static Integer[] getGenericIntegerSplitWithRanges(Properties props, int num, String propertyName) {
        if (props.containsKey(propertyName + "." + num)) {
            String dataFromProps = props.getProperty(propertyName + "." + num).strip().replaceAll("[)(]","");
            String[] skinData = dataFromProps.split("\\s+");
            ArrayList<Integer> suffixNumbers = new ArrayList<>();
            for (String data :
                    skinData) {
                //check if range
                data = data.strip();
                if (!data.replaceAll("\\D", "").isEmpty()) {
                    try {
                        if (data.contains("-")) {
                            suffixNumbers.addAll(Arrays.asList(getIntRange(data).getAllWithinRangeAsList()));
                        } else {
                            int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
                            suffixNumbers.add(tryNumber);
                        }
                    } catch (NumberFormatException e) {
                        ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
                    }
                }
            }
            return suffixNumbers.toArray(new Integer[0]);
        }
        return null;
    }

    @SuppressWarnings("RegExpRedundantEscape")//required in 1.16
    @Nullable
    private static Double[] getGenericMinMaxDouble(Properties props, int num, String propertyName) {
        if (props.containsKey(propertyName + "." + num)) {
            String dataFromProps = props.getProperty(propertyName + "." + num).trim();
            String[] rangeData = dataFromProps.split("-");
            if (rangeData.length == 2) {
                try {
                    double tryMinNumber = Double.parseDouble(rangeData[0].replaceAll("[^\\.\\d]", ""));
                    double tryMaxNumber = Double.parseDouble(rangeData[1].replaceAll("[^\\.\\d]", ""));
                    return new Double[]{tryMinNumber, tryMaxNumber};
                } catch (NumberFormatException e) {
                    ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
                }
            } else {
                ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
            }
        }
        return null;
    }

    @Nullable
    private static Boolean getGenericBooleanThatCanNull(Properties props, int num, String propertyName) {
        if (props.containsKey(propertyName + "." + num)) {
            String input = props.getProperty(propertyName + "." + num).trim();
            if ("true".equals(input) || "false".equals(input)) {
                return "true".equals(input);
            } else {
                ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
            }
        }
        return null;
    }

    @Nullable
    private static Map<String, String> getNBT(Properties props, int num) {
        String keyPrefix = "nbt." + num + '.';
        Map<String, String> map = new HashMap<>();
        props.forEach((key, value) -> {
            if (key != null && ((String) key).startsWith(keyPrefix)) {
                String nbtName = ((String) key).replaceFirst(keyPrefix, "");
                map.put(nbtName, ((String) value).trim());
            }
        });
        if (!map.isEmpty()) return map;
        return null;
    }


    private record IntRange(int lower, int higher) {
        public boolean isWithinRange(int value) {
            return value >= lower && value <= higher;
        }

        public Integer[] getAllWithinRangeAsList() {
            if(lower == higher){
                return new Integer[]{lower};
            }
            ArrayList<Integer> builder = new ArrayList<>();
            for (int i = lower; i <= higher; i++) {
                builder.add(i);
            }
            return builder.toArray(new Integer[0]);
        }
    }

    public static IntRange getIntRange(String rawRange) {
        //assume rawRange =  "20-56"  but can be "-64-56", "-30--10"  or "-14"
        rawRange = rawRange.trim();
        //sort negatives before split
        if (rawRange.startsWith("-")) {
            rawRange = rawRange.replaceFirst("-", "N");
        }
        rawRange = rawRange.replaceAll("--", "-N");
        String[] split = rawRange.split("-");
        if (split.length == 2 && !split[0].isEmpty() && !split[1].isEmpty()) {//sort out range
            int[] minMax = {Integer.parseInt(split[0].replaceAll("\\D", "")), Integer.parseInt(split[1].replaceAll("\\D", ""))};
            if (split[0].contains("N")) {
                minMax[0] = -minMax[0];
            }
            if (split[1].contains("N")) {
                minMax[1] = -minMax[1];
            }
            if (minMax[0] > minMax[1]) {
                //0 must be smaller
                return new IntRange(minMax[1], minMax[0]);
            } else {
                return new IntRange(minMax[0], minMax[1]);
            }
        } else {//only 1 number but method ran because of "-" present
            int number = Integer.parseInt(rawRange.replaceAll("\\D", ""));
            if (rawRange.contains("N")) {
                number = -number;
            }
            return new IntRange(number, number);
        }
    }

    private interface StringPropertyMatcher {
        boolean testPropertyString(String currentEntityValue);
    }

    static Pattern groupByQuotationPattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public static class ETFTexturePropertyCase {
        //public final int PROPERTY_NUMBER;
        private final Integer[] SUFFIX_NUMBERS_WEIGHTED;
        private final @Nullable StringPropertyMatcher BIOME_VALUE_MATCHER;
        private final @Nullable Integer[] HEIGHT_Y_VALUES;
        private final @Nullable StringPropertyMatcher NAME_MATCHERS;//add
        private final @Nullable String[] PROFESSION_VALUES;
        private final @Nullable String[] COLOR_VALUES;//add
        private final @Nullable Boolean IS_BABY; // 0 1 2 - don't true false
        private final @Nullable WeatherType WEATHER_TYPE; //0,1,2,3 - no clear rain thunder
        private final @Nullable String[] HEALTH_RANGE_STRINGS;
        private final @Nullable Integer[] MOON_PHASE_VALUES;
        private final @Nullable String[] TIME_RANGE_STRINGS;
        private final @Nullable String[] BLOCK_VALUES;
        private final @Nullable StringPropertyMatcher TEAM_MATCHER;
        private final @Nullable Integer[] SIZE_VALUES;

        private final @Nullable Double[] SPEED_MIN_MAX;
        private final @Nullable Double[] JUMP_MIN_MAX;
        private final @Nullable String[] MAX_HEALTH_STRINGS;
        private final @Nullable Integer[] INVENTORY_COLUMNS;
        //    private final @Nullable Boolean IS_TRAP_HORSE;
        private final @Nullable Boolean IS_ANGRY;
        private final @Nullable PandaEntity.Gene[] HIDDEN_GENE;
        //    private final @Nullable Angriness[] WARDEN_ANGRINESS;
        //    private final @Nullable Boolean IS_ANGRY_WITH_CLIENT;
        private final @Nullable Boolean IS_PLAYER_CREATED;
        private final @Nullable Boolean IS_SCREAMING_GOAT;
        private final @Nullable String[] DISTANCE_TO_PLAYER;
        private final @Nullable Boolean CREEPER_CHARGED;

        private final @Nullable StatusEffect[] STATUS_EFFECT;

        private final @Nullable String[] ITEMS;

        private final @Nullable Boolean MOVING;
        private final @Nullable Map<String, String> NBT_MAP;


        private final boolean isNullPropertyCase;

        //whether case should be ignored by updates


        public ETFTexturePropertyCase(Integer[] suffixesX,
                                      @Nullable Integer[] weightsX,
                                      @Nullable String biomesX,
                                      @Nullable Integer[] heights,
                                      @Nullable String namesX,
                                      @Nullable String[] professionsX,
                                      @Nullable String[] collarColoursX,
                                      @Nullable Boolean baby012,
                                      @Nullable WeatherType weather0123,
                                      @Nullable String[] healthX,
                                      @Nullable Integer[] moonX,
                                      @Nullable String[] daytimeX,
                                      @Nullable String[] blocksX,
                                      @Nullable String teamsX,
                                      @Nullable Integer[] sizeX,
                                      @Nullable Double[] speedMinMax,
                                      @Nullable Double[] jumpMinMax,
                                      @Nullable String[] maxHealthStrings,
                                      @Nullable Integer[] inventoryColumns,
                                      @Nullable Boolean isAngry,
                                      @Nullable PandaEntity.Gene[] hiddenGene,
                                      @Nullable Boolean isPlayerCreated,
                                      @Nullable Boolean isScreamingGoat,
                                      @Nullable String[] distanceToPlayer,
                                      @Nullable Boolean creeperCharged,
                                      @Nullable StatusEffect[] statusEffect,
                                      @Nullable String[] items,
                                      @Nullable Boolean moving,
                                      @Nullable Map<String, String> nbtMap


        ) {
            NBT_MAP = nbtMap;

            MOVING = moving;
            ITEMS = items;
            STATUS_EFFECT = statusEffect;

            CREEPER_CHARGED = creeperCharged;
            DISTANCE_TO_PLAYER = distanceToPlayer;

            SPEED_MIN_MAX = speedMinMax;
            JUMP_MIN_MAX = jumpMinMax;
            MAX_HEALTH_STRINGS = maxHealthStrings;
            INVENTORY_COLUMNS = inventoryColumns;
            IS_ANGRY = isAngry;
            HIDDEN_GENE = hiddenGene;
            IS_PLAYER_CREATED = isPlayerCreated;
            IS_SCREAMING_GOAT = isScreamingGoat;


            BIOME_VALUE_MATCHER = getStringMatcher_Regex_Pattern_List_Single(biomesX);
            HEIGHT_Y_VALUES = heights;
            NAME_MATCHERS = getStringMatcher_Regex_Pattern_List_Single(namesX);
            PROFESSION_VALUES = professionsX;
            COLOR_VALUES = collarColoursX;
            IS_BABY = baby012;
            WEATHER_TYPE = weather0123;
            HEALTH_RANGE_STRINGS = healthX;
            MOON_PHASE_VALUES = moonX;
            TIME_RANGE_STRINGS = daytimeX;
            BLOCK_VALUES = blocksX;
            TEAM_MATCHER = getStringMatcher_Regex_Pattern_List_Single(teamsX);
            //PROPERTY_NUMBER = propNumber;
            SIZE_VALUES = sizeX;

            if (weightsX == null) {
                weightsX = new Integer[0];
            }


            if (weightsX.length > 0) {
                if (weightsX.length == suffixesX.length) {
                    ArrayList<Integer> buildWeighted = new ArrayList<>();
                    int index = 0;
                    for (int suffix :
                            suffixesX) {
                        Integer weightValue = weightsX[index];
                        if (weightValue != null) {
                            for (int i = 0; i < weightValue; i++) {
                                //adds the suffix as many times as it is weighted
                                buildWeighted.add(suffix);
                            }
                        }
                        index++;
                    }
                    SUFFIX_NUMBERS_WEIGHTED = buildWeighted.toArray(new Integer[0]);

                } else {
                    ETFUtils2.logWarn("random texture weights don't match for:\n suffixes: " + Arrays.toString(suffixesX) + "\n weights: " + Arrays.toString(weightsX), false);
                    SUFFIX_NUMBERS_WEIGHTED = suffixesX;
                }
            } else {

                SUFFIX_NUMBERS_WEIGHTED = suffixesX;
            }
            isNullPropertyCase = (BIOME_VALUE_MATCHER == null
                    && NAME_MATCHERS == null
                    && HEIGHT_Y_VALUES == null
                    && PROFESSION_VALUES == null
                    && COLOR_VALUES == null
                    && IS_BABY == null
                    && WEATHER_TYPE == null
                    && HEALTH_RANGE_STRINGS == null
                    && MOON_PHASE_VALUES == null
                    && TIME_RANGE_STRINGS == null
                    && BLOCK_VALUES == null
                    && TEAM_MATCHER == null
                    && SIZE_VALUES == null
                    && SPEED_MIN_MAX == null
                    && JUMP_MIN_MAX == null
                    && MAX_HEALTH_STRINGS == null
                    && INVENTORY_COLUMNS == null
                    && IS_ANGRY == null
                    && HIDDEN_GENE == null
                    && IS_PLAYER_CREATED == null
                    && IS_SCREAMING_GOAT == null
                    && DISTANCE_TO_PLAYER == null
                    && CREEPER_CHARGED == null
                    && STATUS_EFFECT == null
                    && ITEMS == null
                    && MOVING == null
                    && NBT_MAP == null
            );
        }

        @Nullable
        private static StringPropertyMatcher getStringMatcher_Regex_Pattern_List_Single(@Nullable String propertyLineToBeMatchedPossiblyRegex) {
            if (propertyLineToBeMatchedPossiblyRegex == null || propertyLineToBeMatchedPossiblyRegex.isBlank())
                return null;
            String stringToMatch = propertyLineToBeMatchedPossiblyRegex.trim();
            boolean invert;
            //boolean check = false;
            //should not happen in nbt
            if (stringToMatch.startsWith("!")) {
                stringToMatch = stringToMatch.replaceFirst("!", "");
                invert = true;
            } else {
                invert = false;
            }

            if (stringToMatch.contains("regex:")) {
                if (stringToMatch.contains("iregex:")) {
                    stringToMatch = stringToMatch.replaceFirst("iregex:", "");
                    String finalStringToMatch = stringToMatch;
                    return (string) -> invert != string.matches("(?i)" + finalStringToMatch);
                } else {
                    stringToMatch = stringToMatch.replaceFirst("regex:", "");
                    String finalStringToMatch = stringToMatch;
                    return (string) -> invert != string.matches(finalStringToMatch);
                }
            } else if (stringToMatch.contains("pattern:")) {
                stringToMatch = stringToMatch.replace("*", "\\E.+\\Q").replace("?", "\\E.*\\Q");
                if (stringToMatch.contains("ipattern:")) {
                    stringToMatch = stringToMatch.replace("ipattern:", "");
                    String finalStringToMatch = stringToMatch;
                    return (string) -> invert != string.matches("(?i)" + finalStringToMatch);
                } else {
                    stringToMatch = stringToMatch.replace("pattern:", "");
                    String finalStringToMatch = stringToMatch;
                    return (string) -> invert != string.matches(finalStringToMatch);
                }
            } else {//direct comparison
                String finalStringToMatch1 = stringToMatch;
                boolean finalDoPattern = finalStringToMatch1.contains("\"");
                String[] finalSplitMatches = stringToMatch.split("\\s+");
                return (string) -> {
                    boolean check = false;
                    if (string.equals(finalStringToMatch1)) {
                        check = true;
                    } else {
                        for (String singleValue : finalSplitMatches) {
                            if (string.equals(singleValue)) {
                                check = true;
                                break;
                            }
                        }
                        //if still needed try a quotation check cause why not
                        if (finalDoPattern && !check) {
                            Matcher m = groupByQuotationPattern.matcher(finalStringToMatch1);
                            while (m.find()) {
                                String foundInBrackets = m.group(1).replace("\"", "").trim();
                                if (string.equals(foundInBrackets)) {
                                    check = true;
                                    break;
                                }
                            }
                        }
                    }
                    return invert != check;
                };
            }
        }

        @Override
        public String toString() {
            return "randomCase{" +
                    "weightedSuffixes=" + Arrays.toString(SUFFIX_NUMBERS_WEIGHTED) +
                    ", biomes=" + BIOME_VALUE_MATCHER +
                    ", heights=" + Arrays.toString(HEIGHT_Y_VALUES) +
                    ", names=" + NAME_MATCHERS +
                    '}';
        }

        private static boolean isStringValidInt(String string) {
            try {
                Integer.parseInt(string);
                return true;
            } catch (NumberFormatException e) {
                // e.printStackTrace();
                return false;
            }
        }


        public boolean doesEntityMeetConditionsOfThisCase(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
            return doesEntityMeetConditionsOfThisCase(new ETFEntityWrapper(entity), isUpdate, UUID_CaseHasUpdateablesCustom);
        }

//        public boolean doesEntityMeetConditionsOfThisCase(BlockEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
//            return doesEntityMeetConditionsOfThisCase(new ETFBlockEntityWrapper(entity, UUID.nameUUIDFromBytes("ABCDEFG".getBytes())), isUpdate, UUID_CaseHasUpdateablesCustom);
//        }

        public boolean doesEntityMeetConditionsOfThisCase(BlockEntity entity, UUID uuid, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
            return doesEntityMeetConditionsOfThisCase(new ETFBlockEntityWrapper(entity, uuid), isUpdate, UUID_CaseHasUpdateablesCustom);
        }

        public boolean doesEntityMeetConditionsOfThisCase(ETFEntity etfEntity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {


            //System.out.println("checking property number "+propertyNumber);


            if (isNullPropertyCase) {
                return true;
            }

            if (!ETFConfigData.restrictUpdateProperties) {
                isUpdate = false;
            }
            UUID id = etfEntity.getUuid();

            //null for block entity, anything using entity instead of etfEntity is not block entity compatible and must null check
            @Nullable Entity entity = etfEntity.entity();

            ObjectImmutableList<String> spawnConditions;
            if (ETFConfigData.restrictUpdateProperties) {
                if (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.containsKey(id)) {
                    spawnConditions = (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.get(id));
                } else {
                    spawnConditions = readAllSpawnConditionsForCache(etfEntity);
                    ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.put(id, spawnConditions);
                }
            } else {
                spawnConditions = null;
            }


            boolean wasEntityTestedByAnUpdatableProperty = false;
            boolean doesEntityMeetThisCaseTest = true;
            if (BIOME_VALUE_MATCHER != null) {
                if (!ETFConfigData.restrictBiome) wasEntityTestedByAnUpdatableProperty = true;
                //String entityBiome = entity.world.getBiome(entity.getBlockPos()).getCategory().getName();//has no caps// desert
                //1.18.1 old mapping String entityBiome = Objects.requireNonNull(entity.world.getRegistryManager().get(Registry.BIOME_KEY).getId(entity.world.getBiome(entity.getBlockPos()))).toString();
                //not an exact grabbing of the name, but it works for the contains check so no need for more processing
                //example "Reference{ResourceKey[minecraft:worldgen/biome / minecraft:river]=net.minecraft.class_1959@373fe79a}"
                //String entityBiome = entity.world.getBiome(entity.getBlockPos()).toString();
                //example  "Optional[minecraft:worldgen/biome / minecraft:river]"
                String entityBiome;
                if (isUpdate && ETFConfigData.restrictBiome && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 1) {
                    entityBiome = spawnConditions.get(0).trim();
                } else {
                    entityBiome = ETFVersionDifferenceHandler.getBiomeString(etfEntity.getWorld(), etfEntity.getBlockPos()).replace("minecraft:", "");
                }
                //            } else if (MinecraftVersion.CURRENT.getName().equals("1.18") || MinecraftVersion.CURRENT.getName().equals("1.18.1")) {
                //                entityBiome = ETF_1_18_1_versionPatch.getBiome(entity.world, entity.getBlockPos());
                //            } else {
                //                entityBiome = ETF_1_18_2_versionPatch.getBiome(entity.world, entity.getBlockPos());
                //            }

                doesEntityMeetThisCaseTest = BIOME_VALUE_MATCHER.testPropertyString(entityBiome);
                //doesEntityMeetThisCaseTest = getStringMatcher_Regex_Pattern_List_Single(BIOME_VALUES,entityBiome);
            }
            if (doesEntityMeetThisCaseTest && NBT_MAP != null) {
                wasEntityTestedByAnUpdatableProperty = true;


                //NbtCompound entityNBT = etfEntity.writeNbt(new NbtCompound());

                NbtCompound entityNBT;
                Entity internal = etfEntity.entity();
                if (internal != null) {
                    entityNBT = NbtPredicate.entityToNbt(internal);
                } else {
                    entityNBT = etfEntity.writeNbt(new NbtCompound());
                }


                if (!entityNBT.isEmpty()) {
                    for (Map.Entry<String, String> nbtPropertyEntry : NBT_MAP.entrySet()) {

                        String nbtIdentifier = nbtPropertyEntry.getKey();
                        String nbtTestInstruction = nbtPropertyEntry.getValue();

                        boolean invertFinalResult = nbtTestInstruction.startsWith("!");
                        nbtTestInstruction = nbtTestInstruction.replaceFirst("!", "");

                        if (nbtTestInstruction.startsWith("print:")) {
                            ETFUtils2.logMessage("NBT entity data print: ");
                            System.out.println(entityNBT.asString());
                            nbtTestInstruction = nbtTestInstruction.replaceFirst("print:", "");
                        }

                        //first find the required nbt data
                        NbtElement finalNBTElementOrNullIfFailed = null;
                        boolean listIndexInstructionWasWildCard = false;
                        NbtElement lastIterationNBTElement = entityNBT;
                        Iterator<String> nbtPathInstructionIterator = Arrays.stream(nbtIdentifier.split("\\.")).iterator();
                        while (nbtPathInstructionIterator.hasNext()) {
                            if (lastIterationNBTElement == null) {
                                System.out.println("null nbt in ETF");
                                break;
                            }
                            String nextPathInstruction = nbtPathInstructionIterator.next();

                            //find out how to handle this instruction based on what element we have
                            if (lastIterationNBTElement instanceof NbtCompound nbtCompound) {
                                if (nbtCompound.contains(nextPathInstruction)) {
                                    lastIterationNBTElement = nbtCompound.get(nextPathInstruction);
                                } else {
                                    //not found so break
                                    break;
                                }
                            } else if (lastIterationNBTElement instanceof AbstractNbtList<?> nbtList) {
                                if ("*".equals(nextPathInstruction)) {
                                    listIndexInstructionWasWildCard = true;
                                } else if (isStringValidInt(nextPathInstruction)) {
                                    //possibly further nested elements to read from
                                    try {
                                        int index = Integer.parseInt(nextPathInstruction);
                                        lastIterationNBTElement = nbtList.get(index);
                                    } catch (IndexOutOfBoundsException e) {
                                        break;
                                    }

                                } else {
                                    ETFUtils2.logWarn("cannot parse list index of [" + nextPathInstruction + "] in nbt property: " + nbtIdentifier);
                                    break;
                                }
                            } else {
                                //here this means we have an nbt element without children yet have received an additional instruction???
                                //throw a fit if there are further instructions
                                ETFUtils2.logError("cannot parse next nbt instruction of [" + nextPathInstruction + "] in nbt property: " + nbtIdentifier + ", as this nbt is not a list or compound and cannot have further instructions");
                                break;

                            }
                            //here if there are no further instructions then send the final result
                            if (!nbtPathInstructionIterator.hasNext()) {
                                finalNBTElementOrNullIfFailed = lastIterationNBTElement;
                            }
                        }

                        boolean doesTestPass = false;

                        //test if was found
                        if (finalNBTElementOrNullIfFailed != null) {
                            if (nbtTestInstruction.startsWith("print_raw:")) {
                                String rawStringFromNBT = finalNBTElementOrNullIfFailed.asString();
                                String rawMatchString = nbtTestInstruction.replaceFirst("print_raw:", "");
                                ETFUtils2.logMessage("NBT RAW data of: " + nbtIdentifier + "=" + rawStringFromNBT);
                                StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(rawMatchString);
                                doesTestPass = matcher != null && matcher.testPropertyString(rawStringFromNBT);
                                //doesTestPass = rawMatchString.equals(rawStringFromNBT);
                            } else if (nbtTestInstruction.startsWith("raw:")) {
                                String rawStringFromNBT = finalNBTElementOrNullIfFailed.asString();
                                String rawMatchString = nbtTestInstruction.replaceFirst("raw:", "");
                                StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(rawMatchString);
                                doesTestPass = matcher != null && matcher.testPropertyString(rawStringFromNBT);
                                //doesTestPass = rawMatchString.equals(rawStringFromNBT);
                            } else if (nbtTestInstruction.startsWith("exists:")) {
                                doesTestPass = nbtTestInstruction.contains("exists:true");
                            } else if (nbtTestInstruction.startsWith("range:")) {
                                if (finalNBTElementOrNullIfFailed instanceof AbstractNbtNumber nbtNumber) {
                                    String rawRangeString = nbtTestInstruction.replaceFirst("range:", "");
                                    IntRange range = getIntRange(rawRangeString);
                                    doesTestPass = range.isWithinRange(nbtNumber.numberValue().intValue());
                                } else {
                                    ETFUtils2.logWarn("NBT range is not valid for non number nbt types: " + nbtIdentifier + "=" + nbtTestInstruction);
                                }
                                // }else  if (finalNBTElementOrNullIfFailed instanceof NbtCompound nbtCompound) {
                            } else if (finalNBTElementOrNullIfFailed instanceof AbstractNbtList<?> nbtList) {
                                if (listIndexInstructionWasWildCard) {
                                    for (NbtElement element :
                                            nbtList) {
                                        StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
                                        doesTestPass = matcher != null && matcher.testPropertyString(element.asString());
                                        if (doesTestPass) break;
                                    }
                                } else {
                                    ETFUtils2.logWarn("NBT list error with: " + nbtIdentifier + "=" + nbtTestInstruction);
                                }
//                            }else if(finalNBTElementOrNullIfFailed instanceof AbstractNbtNumber nbtNumber) {
//                                doesTestPass = doesStringMatch(nbtTestInstruction,nbtNumber.asString());
//                            }else if(finalNBTElementOrNullIfFailed instanceof NbtString nbtString) {
//                                doesTestPass = doesStringMatch(nbtTestInstruction,nbtString.asString());
                            } else {
                                StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
                                doesTestPass = matcher != null && matcher.testPropertyString(finalNBTElementOrNullIfFailed.asString());
                            }
                        } else {
                            //did not find
                            if (nbtTestInstruction.startsWith("print_raw:")) {
                                String rawStringFromNBT = "";
                                String rawMatchString = nbtTestInstruction.replaceFirst("print_raw:", "");
                                ETFUtils2.logMessage("NBT RAW data of: " + nbtIdentifier + "=" + rawStringFromNBT);
                                doesTestPass = rawMatchString.equals(rawStringFromNBT);
                            } else if (nbtTestInstruction.startsWith("raw:")) {
                                String rawStringFromNBT = "";
                                String rawMatchString = nbtTestInstruction.replaceFirst("raw:", "");
                                doesTestPass = rawMatchString.equals(rawStringFromNBT);
                            } else if (nbtTestInstruction.startsWith("exists:")) {
                                doesTestPass = nbtTestInstruction.contains("exists:false");
                            }
                        }
                        //simplified from invertFinalResult? !doesTestPass : doesTestPass;
                        doesEntityMeetThisCaseTest = invertFinalResult != doesTestPass;
                        if (!doesEntityMeetThisCaseTest) break;
                    }
                } else {
                    ETFUtils2.logError("NBT test failed, as could not read entity NBT");
                    doesEntityMeetThisCaseTest = false;
                }
            }
            if (doesEntityMeetThisCaseTest && NAME_MATCHERS != null) {
                // System.out.println("start name"+doesEntityMeetThisCaseTest);
                wasEntityTestedByAnUpdatableProperty = true;
                if (etfEntity.hasCustomName()) {
                    String entityName = Objects.requireNonNull(etfEntity.getCustomName()).getString();
                    doesEntityMeetThisCaseTest = NAME_MATCHERS.testPropertyString(entityName);
                } else {
                    doesEntityMeetThisCaseTest = false;
                }
            }
            if (doesEntityMeetThisCaseTest && HEIGHT_Y_VALUES != null) {
                if (!ETFConfigData.restrictHeight) wasEntityTestedByAnUpdatableProperty = true;
                int entityHeight;
                if (isUpdate && ETFConfigData.restrictHeight && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 2) {
                    entityHeight = Integer.parseInt(spawnConditions.get(1).trim());
                } else {
                    entityHeight = etfEntity.getBlockY();
                }
                boolean check = false;
                for (int i :
                        HEIGHT_Y_VALUES) {
                    if (i == entityHeight) {
                        check = true;
                        break;
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && PROFESSION_VALUES != null && entity instanceof VillagerEntity) {
                wasEntityTestedByAnUpdatableProperty = true;
                String entityProfession = ((VillagerEntity) entity).getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
                int entityProfessionLevel = ((VillagerEntity) entity).getVillagerData().getLevel();
                boolean check = false;
                for (String str :
                        PROFESSION_VALUES) {
                    if (str != null) {
                        //str could be   librarian:1,3-4
                        str = str.toLowerCase().replaceAll("\\s*", "").replace("minecraft:", "");
                        //could be   "minecraft:cleric:1-4
                        if (str.contains(":")) {
                            //splits at seperator for profession level check only
                            String[] data = str.split(":\\d");
                            if (entityProfession.contains(data[0]) || data[0].contains(entityProfession)) {
                                //has profession now check level
                                if (data.length == 2) {
                                    String[] levels = data[1].split(",");
                                    ArrayList<Integer> levelData = new ArrayList<>();
                                    for (String lvls :
                                            levels) {
                                        if (lvls.contains("-")) {
                                            levelData.addAll(Arrays.asList(getIntRange(lvls).getAllWithinRangeAsList()));
                                        } else {
                                            levelData.add(Integer.parseInt(lvls.replaceAll("\\D", "")));
                                        }
                                    }
                                    //now check levels
                                    for (Integer i :
                                            levelData) {
                                        if (i == entityProfessionLevel) {
                                            check = true;
                                            break;
                                        }
                                    }
                                } else {
                                    //no levels just send profession match confirmation
                                    check = true;
                                    break;
                                }
                            }
                        } else {
                            if (entityProfession.contains(str) || str.contains(entityProfession)) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }

            if (doesEntityMeetThisCaseTest && COLOR_VALUES != null) {

                wasEntityTestedByAnUpdatableProperty = true;
                String entityColor;
                if (entity instanceof WolfEntity wolf) {
                    entityColor = wolf.getCollarColor().getName().toLowerCase();
                } else if (entity instanceof SheepEntity sheep) {
                    entityColor = sheep.getColor().getName().toLowerCase();
                } else if (entity instanceof LlamaEntity llama) {
                    DyeColor str = llama.getCarpetColor();
                    if (str != null) {
                        entityColor = str.getName().toLowerCase();
                    } else {
                        entityColor = "NOT_A_COLOR";
                    }
                } else if (entity instanceof CatEntity cat) {
                    entityColor = cat.getCollarColor().getName().toLowerCase();
                } else if (entity instanceof ShulkerEntity shulker) {
                    DyeColor str = shulker.getColor();
                    if (str != null) {
                        entityColor = str.getName().toLowerCase();
                    } else {
                        entityColor = "NOT_A_COLOR";
                    }
                } else if (entity instanceof TropicalFishEntity fishy) {
                    DyeColor str = TropicalFishEntity.getBaseDyeColor(fishy.getVariant().getId());
                    if (str != null) {
                        entityColor = str.getName().toLowerCase();
                    } else {
                        entityColor = "NOT_A_COLOR";
                    }
                } else {
                    entityColor = "NOT_A_COLOR";
                }


                boolean check = false;
                for (String i :
                        COLOR_VALUES) {
                    if (i != null) {
                        i = i.toLowerCase();
                        if (i.equals(entityColor)) {
                            check = true;
                            break;
                        }
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && IS_BABY != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                doesEntityMeetThisCaseTest = (IS_BABY) == ((LivingEntity) entity).isBaby();
                //System.out.println("baby " + doesEntityMeetThisCaseTest);
            }
            if (doesEntityMeetThisCaseTest && WEATHER_TYPE != null) {
                if (!ETFConfigData.restrictWeather) wasEntityTestedByAnUpdatableProperty = true;
                boolean raining;
                boolean thundering;
                if (isUpdate && ETFConfigData.restrictWeather && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() >= 4) {
                    String[] data = spawnConditions.get(3).split("-");
                    raining = "1".equals(data[0].trim());
                    thundering = "1".equals(data[1].trim());
                } else {
                    raining = etfEntity.getWorld().isRaining();
                    thundering = etfEntity.getWorld().isThundering();
                }
                boolean check = false;
                if (WEATHER_TYPE == WeatherType.clear && !(raining || thundering)) {
                    check = true;
                } else if (WEATHER_TYPE == WeatherType.rain && raining) {
                    check = true;
                } else if (WEATHER_TYPE == WeatherType.thunder && thundering) {
                    check = true;
                }
                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && HEALTH_RANGE_STRINGS != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                //float entityHealth = entity.getHealth();
                boolean check = false;
                //always check percentage
                float checkValue = ((LivingEntity) entity).getHealth() / ((LivingEntity) entity).getMaxHealth() * 100;
                for (String hlth :
                        HEALTH_RANGE_STRINGS) {
                    if (hlth != null) {
                        if (hlth.contains("-")) {
                            String[] str = hlth.split("-");
                            if (checkValue >= Integer.parseInt(str[0].replaceAll("\\D", ""))
                                    && checkValue <= Integer.parseInt(str[1].replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }

                        } else {
                            if (checkValue == Integer.parseInt(hlth.replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && MOON_PHASE_VALUES != null) {
                if (!ETFConfigData.restrictMoonPhase) wasEntityTestedByAnUpdatableProperty = true;
                int moonPhase;
                if (isUpdate && ETFConfigData.restrictMoonPhase && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 5) {
                    moonPhase = Integer.parseInt(spawnConditions.get(5).trim());
                } else {
                    moonPhase = etfEntity.getWorld().getMoonPhase();
                }
                boolean check = false;
                for (int i :
                        MOON_PHASE_VALUES) {
                    if (i == moonPhase) {
                        check = true;
                        break;
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && TIME_RANGE_STRINGS != null) {
                if (!ETFConfigData.restrictDayTime) wasEntityTestedByAnUpdatableProperty = true;
                long time;
                boolean check = false;
                if (isUpdate && ETFConfigData.restrictDayTime && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 4) {
                    time = Long.parseLong(spawnConditions.get(4).trim());
                } else {
                    time = etfEntity.getWorld().getTimeOfDay();
                }
                for (String rangeOfTime :
                        TIME_RANGE_STRINGS) {
                    if (rangeOfTime != null) {
                        if (rangeOfTime.contains("-")) {
                            String[] str = rangeOfTime.split("-");
                            if (time >= Long.parseLong(str[0].replaceAll("\\D", ""))
                                    && time <= Long.parseLong(str[1].replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }

                        } else {
                            if (time == Long.parseLong(rangeOfTime.replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && BLOCK_VALUES != null) {
                if (!ETFConfigData.restrictBlock) wasEntityTestedByAnUpdatableProperty = true;
                //check block

                String[] entityOnBlocks;
                String[] entityOnBlockStates;
                if (isUpdate && ETFConfigData.restrictBlock && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 6) {
                    entityOnBlocks = new String[]{spawnConditions.get(2).trim(), spawnConditions.get(6).trim()};
                    entityOnBlockStates = new String[]{spawnConditions.get(7).trim(), spawnConditions.get(8).trim()};
                } else {
                    BlockState blockState1 = etfEntity.getWorld().getBlockState(etfEntity.getBlockPos().down());
                    String entityOnBlock1 = Registries.BLOCK.getId(blockState1.getBlock()).toString().replaceFirst("minecraft:", "");

                    BlockState blockState2 = etfEntity.getWorld().getBlockState(etfEntity.getBlockPos());
                    String entityOnBlock2 = Registries.BLOCK.getId(blockState2.getBlock()).toString().replaceFirst("minecraft:", "");
//                    String entityOnBlock2 = blockState2.toString()
//                            .replaceFirst("minecraft:", "")
//                            .replaceFirst("Block\\{", "")
//                            //will print with
//                            .replaceFirst("}.*", "").toLowerCase();
                    entityOnBlocks = new String[]{entityOnBlock1, entityOnBlock2};
                    entityOnBlockStates = new String[]{blockState1.getEntries().toString(), blockState2.getEntries().toString()};
                }
                Pattern patterOfEntries = Pattern.compile("(?<=:)([^:]+=[^:| ]+)(?=(:|$| ))");
                boolean check2 = false;
                boolean check1 = false;
                for (String block :
                        BLOCK_VALUES) {
                    if (block != null) {
                        List<String> entries = new ArrayList<>();
                        //strip out entries if existing
                        if (block.contains("=")) {
                            Matcher m = patterOfEntries.matcher(block);
                            while (m.find()) {
                                entries.add(m.group(1).replace("\"", "").trim());
                            }
                            block = block.replaceAll("(?<=:)([^:]+=[^:| ]+)(?=(:|$| ))", "").replaceAll(":+$", "");
                        }

                        block = block.strip();
                        if (block.startsWith("!")) {
                            block = block.replaceFirst("!", "");
                            if (!block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[0])) {
                                //can continue to check cases
                                check1 = doBlockEntriesMatch(entries, entityOnBlockStates[0]);
                            } else {
                                //will prevent future checking
                                doesEntityMeetThisCaseTest = false;

                            }
                        } else if (block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[0])) {
                            check1 = doBlockEntriesMatch(entries, entityOnBlockStates[0]);
                            if (check1) break;
                        }
                    }
                }
                if (!check1) {
                    for (String block :
                            BLOCK_VALUES) {
                        if (block != null) {
                            List<String> entries = new ArrayList<>();
                            //strip out entries if existing
                            if (block.contains("=")) {
                                Matcher m = patterOfEntries.matcher(block);
                                while (m.find()) {
                                    entries.add(m.group(1).replace("\"", "").trim());
                                }
                                block = block.replaceAll("(?<=:)([^:]+=[^:| ]+)(?=(:|$| ))", "").replaceAll(":+$", "");
                            }

                            block = block.strip();
                            if (block.startsWith("!")) {
                                block = block.replaceFirst("!", "");
                                if (!block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[1])) {
                                    //can continue to check cases
                                    check2 = doBlockEntriesMatch(entries, entityOnBlockStates[1]);
                                } else {
                                    //will prevent future checking
                                    doesEntityMeetThisCaseTest = false;

                                }
                            } else if (block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[1])) {
                                check2 = doBlockEntriesMatch(entries, entityOnBlockStates[1]);
                                if (check2) break;
                            }
                        }
                    }
                }
                //will just leave if a negative check matched
                if (doesEntityMeetThisCaseTest) {
                    //true if on or inside required block
                    //allows water to be used as well as fixing soul sand and mud checks
                    doesEntityMeetThisCaseTest = check1 || check2;
                }
            }
            if (doesEntityMeetThisCaseTest && TEAM_MATCHER != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                if (etfEntity.getScoreboardTeam() != null) {
                    String teamName = etfEntity.getScoreboardTeam().getName();
                    doesEntityMeetThisCaseTest = TEAM_MATCHER.testPropertyString(teamName);
                } else {
                    doesEntityMeetThisCaseTest = false;
                }
            }
            if (doesEntityMeetThisCaseTest && SIZE_VALUES != null &&
                    (entity instanceof SlimeEntity || entity instanceof PhantomEntity)) {
                int size;
                if (entity instanceof SlimeEntity slime) {
                    //magma cube too
                    size = slime.getSize()-1;
                } else {
                    size = ((PhantomEntity) entity).getPhantomSize();
                }

                boolean check = false;
                for (int i :
                        SIZE_VALUES) {
                    if (i == size) {
                        check = true;
                        break;
                    }
                }
                doesEntityMeetThisCaseTest = check;
            }


            if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && SPEED_MIN_MAX != null) {
                double speed = ((LivingEntity) entity).getMovementSpeed();
                Double min = SPEED_MIN_MAX[0];
                Double max = SPEED_MIN_MAX[1];
                if (min != null && max != null) {
                    doesEntityMeetThisCaseTest = (speed >= min && speed <= max);
                }
            }
            if (doesEntityMeetThisCaseTest && JUMP_MIN_MAX != null && entity instanceof AbstractHorseEntity) {
                double jumpHeight = ((AbstractHorseEntity) entity).getJumpStrength();
                Double min = JUMP_MIN_MAX[0];
                Double max = JUMP_MIN_MAX[1];
                if (min != null && max != null) {
                    doesEntityMeetThisCaseTest = (jumpHeight >= min && jumpHeight <= max);
                }
            }
            if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && MAX_HEALTH_STRINGS != null) {
                boolean check = false;
                //always check percentage
                float checkValue = ((LivingEntity) entity).getMaxHealth();
                for (String hlth :
                        MAX_HEALTH_STRINGS) {
                    if (hlth != null) {
                        if (hlth.contains("-")) {
                            String[] str = hlth.split("-");
                            if (checkValue >= Integer.parseInt(str[0].replaceAll("\\D", ""))
                                    && checkValue <= Integer.parseInt(str[1].replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }

                        } else {
                            if (checkValue == Integer.parseInt(hlth.replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                doesEntityMeetThisCaseTest = check;

            }
            if (doesEntityMeetThisCaseTest && INVENTORY_COLUMNS != null && entity instanceof LlamaEntity) {
                boolean found = false;
                for (int columns :
                        INVENTORY_COLUMNS) {
                    if (((LlamaEntity) entity).getInventoryColumns() == columns) {
                        found = true;
                        break;
                    }
                }
                doesEntityMeetThisCaseTest = found;
            }
            //        if (doesEntityMeetThisCaseTest && IS_TRAP_HORSE != null && entity instanceof SkeletonHorseEntity) {
            //            wasEntityTestedByAnUpdatableProperty = true;
            //            doesEntityMeetThisCaseTest = ((SkeletonHorseEntity) entity).isTrapped() == IS_TRAP_HORSE;
            //        }
            if (doesEntityMeetThisCaseTest && IS_ANGRY != null) {
                if (entity instanceof EndermanEntity) {
                    wasEntityTestedByAnUpdatableProperty = true;
                    doesEntityMeetThisCaseTest = ((EndermanEntity) entity).isAngry() == IS_ANGRY;
                } else if (entity instanceof BlazeEntity) {
                    wasEntityTestedByAnUpdatableProperty = true;
                    doesEntityMeetThisCaseTest = entity.isOnFire() == IS_ANGRY;
                } else if (entity instanceof GuardianEntity) {
                    wasEntityTestedByAnUpdatableProperty = true;
                    doesEntityMeetThisCaseTest = (((GuardianEntity) entity).getBeamTarget() != null) == IS_ANGRY;
                } else if (entity instanceof VindicatorEntity) {
                    wasEntityTestedByAnUpdatableProperty = true;
                    doesEntityMeetThisCaseTest = (((VindicatorEntity) entity).isAttacking()) == IS_ANGRY;
                } else if (entity instanceof SpellcastingIllagerEntity) {
                    wasEntityTestedByAnUpdatableProperty = true;
                    doesEntityMeetThisCaseTest = (((SpellcastingIllagerEntity) entity).isSpellcasting()) == IS_ANGRY;
                } else {
                    doesEntityMeetThisCaseTest = false;
                }

            }
            //        if (doesEntityMeetThisCaseTest && IS_ANGRY_WITH_CLIENT != null && MinecraftClient.getInstance().player != null) {
            //            wasEntityTestedByAnUpdatableProperty = true;
            //            if (entity instanceof HostileEntity) {
            //                doesEntityMeetThisCaseTest = MinecraftClient.getInstance().player.equals(((HostileEntity) entity).getTarget()) == IS_ANGRY_WITH_CLIENT;
            //            } else if (entity instanceof Angerable) {
            //                doesEntityMeetThisCaseTest = (MinecraftClient.getInstance().player.getUuid().equals(((Angerable) entity).getAngryAt())) == IS_ANGRY_WITH_CLIENT;
            //            }else{
            //                doesEntityMeetThisCaseTest = false;
            //            }
            //        }
            if (doesEntityMeetThisCaseTest && HIDDEN_GENE != null && entity instanceof PandaEntity) {
                boolean found = false;
                for (PandaEntity.Gene gene :
                        HIDDEN_GENE) {
                    if (((PandaEntity) entity).getHiddenGene() == gene) {
                        found = true;
                        break;
                    }
                }
                doesEntityMeetThisCaseTest = found;
            }
            //        if (doesEntityMeetThisCaseTest && WARDEN_ANGRINESS != null && entity instanceof WardenEntity) {
            //            wasEntityTestedByAnUpdatableProperty = true;
            //            boolean found = false;
            //            for (Angriness angry :
            //                    WARDEN_ANGRINESS) {
            //                if (((WardenEntity) entity).getAngriness() == angry) {
            //                    found = true;
            //                    break;
            //                }
            //            }
            //            doesEntityMeetThisCaseTest = found;
            //        }
            if (doesEntityMeetThisCaseTest && IS_PLAYER_CREATED != null && entity instanceof IronGolemEntity) {
                doesEntityMeetThisCaseTest = ((IronGolemEntity) entity).isPlayerCreated() == IS_PLAYER_CREATED;
            }
            if (doesEntityMeetThisCaseTest && IS_SCREAMING_GOAT != null && entity instanceof GoatEntity) {
                doesEntityMeetThisCaseTest = ((GoatEntity) entity).isScreaming() == IS_SCREAMING_GOAT;
            }
            if (doesEntityMeetThisCaseTest && DISTANCE_TO_PLAYER != null && MinecraftClient.getInstance().player != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                boolean check = false;
                //always check percentage
                float checkValue = etfEntity.distanceTo(MinecraftClient.getInstance().player);
                for (String distances :
                        DISTANCE_TO_PLAYER) {
                    if (distances != null) {
                        if (distances.contains("-")) {
                            String[] str = distances.split("-");
                            if (checkValue >= Integer.parseInt(str[0].replaceAll("\\D", ""))
                                    && checkValue <= Integer.parseInt(str[1].replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }

                        } else {
                            if (((int) checkValue) == Integer.parseInt(distances.replaceAll("\\D", ""))) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                doesEntityMeetThisCaseTest = check;

            }
            if (doesEntityMeetThisCaseTest && CREEPER_CHARGED != null && entity instanceof CreeperEntity) {
                wasEntityTestedByAnUpdatableProperty = true;
                doesEntityMeetThisCaseTest = ((CreeperEntity) entity).shouldRenderOverlay() == CREEPER_CHARGED;
            }
            if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && STATUS_EFFECT != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                boolean found = false;
                for (StatusEffect effect :
                        STATUS_EFFECT) {
                    if (((LivingEntity) entity).hasStatusEffect(effect)) {
                        found = true;
                        break;
                    }
                }
                if (!found && entity instanceof MooshroomEntity) {
                    //noinspection PatternVariableCanBeUsed
                    MooshroomEntity shroom = (MooshroomEntity) entity;
                    for (StatusEffect effect :
                            STATUS_EFFECT) {
                        if (effect != null && effect.equals(((MooshroomEntityAccessor) shroom).getStewEffect())) {
                            found = true;
                            break;
                        }
                    }
                }

                doesEntityMeetThisCaseTest = found;
            }
            //System.out.println(Arrays.toString(ITEMS) +" - " +entity.getItemsEquipped().toString());
            if (doesEntityMeetThisCaseTest && ITEMS != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                System.out.println(Arrays.toString(ITEMS) + " - " + etfEntity.getItemsEquipped().toString());
                if (ITEMS.length == 1
                        && ("none".equals(ITEMS[0])
                        || "any".equals(ITEMS[0])
                        || "holding".equals(ITEMS[0])
                        || "wearing".equals(ITEMS[0]))) {
                    if ("none".equals(ITEMS[0])) {
                        Iterable<ItemStack> equipped = etfEntity.getItemsEquipped();
                        for (ItemStack item :
                                equipped) {
                            if (item != null && !item.isEmpty()) {
                                //found a valid item break and deny
                                doesEntityMeetThisCaseTest = false;
                                break;
                            }
                        }
                    } else {
                        Iterable<ItemStack> items;
                        if ("any".equals(ITEMS[0])) {//any
                            items = etfEntity.getItemsEquipped();
                        } else if ("holding".equals(ITEMS[0])) {
                            items = etfEntity.getHandItems();
                        } else {//wearing
                            items = etfEntity.getArmorItems();
                        }
                        boolean found = false;
                        for (ItemStack item :
                                items) {
                            if (item != null && !item.isEmpty()) {
                                //found a valid item break and resolve
                                found = true;
                                break;
                            }
                        }
                        doesEntityMeetThisCaseTest = found;
                    }
                } else {
                    //specifically named item

                    //both armour and hand held
                    Iterable<ItemStack> equipped = etfEntity.getItemsEquipped();
                    boolean found = false;
                    upper:
                    for (String itemToFind :
                            ITEMS) {
                        if (itemToFind != null) {
                            if (itemToFind.contains("minecraft:")) {
                                itemToFind = itemToFind.replace("minecraft:", "");
                            }

                            for (ItemStack item :
                                    equipped) {
                                if (item != null
                                        && !item.isEmpty()
                                        && item.getItem().toString().replace("minecraft:", "").equals(itemToFind)) {
                                    found = true;
                                    break upper;
                                }
                            }
                        }

                    }
                    doesEntityMeetThisCaseTest = found;
                }
            }
            if (doesEntityMeetThisCaseTest && MOVING != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                //System.out.println("movement: "+entity.getVelocity().horizontalLength());

                doesEntityMeetThisCaseTest = (
                        //must be horizontal as vertical velocity has a bleed in from presumably gravity physics
                        //99% of mob motion is horizontal anyway
                        etfEntity.getVelocity().horizontalLength() == 0.0
                ) != MOVING;
            }


            if (wasEntityTestedByAnUpdatableProperty && UUID_CaseHasUpdateablesCustom != null) {
                UUID_CaseHasUpdateablesCustom.put(etfEntity.getUuid(), true);
            }
            //System.out.println("passed "+ doesEntityMeetThisCaseTest+", "+ Arrays.toString(NAME_STRINGS));
            return doesEntityMeetThisCaseTest;
        }

        public int getAnEntityVariantSuffixFromThisCase(UUID id) {
            int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();


            randomReliable %= SUFFIX_NUMBERS_WEIGHTED.length;

            randomReliable = SUFFIX_NUMBERS_WEIGHTED[randomReliable];


            return randomReliable;
        }

        @NotNull
        private ObjectImmutableList<String> readAllSpawnConditionsForCache(@NotNull ETFEntity entity) {
            //check to speed up processing time

            //must be 6 length
            // 0 biome
            // 1 height
            // 2 block
            // 3 weather
            // 4 daytime
            // 5 moon-phase
            // 6 block2
            //checks to speed up runtime as values potentially won't be used but can't be null
            @NotNull String biome = !ETFConfigData.restrictBiome ? "" : ETFVersionDifferenceHandler.getBiomeString(entity.getWorld(), entity.getBlockPos())
                    .replace("minecraft:", "");
            @NotNull String height = !ETFConfigData.restrictHeight ? "" : "" + entity.getBlockY();
            @NotNull String block = !ETFConfigData.restrictBlock ? "" : Registries.BLOCK.getId(entity.getWorld().getBlockState(entity.getBlockPos().down()).getBlock()).toString().replaceFirst("minecraft:", "");

            //check the block the mob is inside also
            // this solves issues with soul sand and mud being undetected
            @NotNull String block2 = !ETFConfigData.restrictBlock ? "" : Registries.BLOCK.getId(entity.getWorld().getBlockState(entity.getBlockPos()).getBlock()).toString().replaceFirst("minecraft:", "");

            @NotNull String weather = !ETFConfigData.restrictWeather ? "" : (entity.getWorld().isRaining() ? "1" : "0") + "-" + (entity.getWorld().isThundering() ? "1" : "0");
            @NotNull String time = !ETFConfigData.restrictDayTime ? "" : "" + entity.getWorld().getTimeOfDay();
            @NotNull String moon = !ETFConfigData.restrictMoonPhase ? "" : "" + entity.getWorld().getMoonPhase();
            @NotNull String blockState1 = !ETFConfigData.restrictBlock ? "" : entity.getWorld().getBlockState(entity.getBlockPos().down()).getEntries().toString();
            @NotNull String blockState2 = !ETFConfigData.restrictBlock ? "" : entity.getWorld().getBlockState(entity.getBlockPos()).getEntries().toString();
            return ObjectImmutableList.of(biome, height, block, weather, time, moon, block2, blockState1, blockState2);
        }

        private static boolean doBlockEntriesMatch(List<String> propertyEntries, String blockStateEntries) {
            if (propertyEntries.isEmpty()) return true;

            String[] fixedStateEntries = blockStateEntries.replaceFirst("\\{", "").replaceFirst("}$", "").split(", ");

            HashMap<String, String> stateMap = new HashMap<>();
            for (String entry :
                    fixedStateEntries) {
                if (entry.contains("=")) {
                    String[] set = entry.split("=");
                    stateMap.put(set[0], set[1]);
                } else {
                    ETFUtils2.logWarn("block state failed in property check");
                    return false;
                }
            }

            if (stateMap.isEmpty()) return false;

            for (String property :
                    propertyEntries) {
                String[] set = property.split("=");
                String key = set[0];
                if (stateMap.containsKey(key)) {
                    String stateValue = stateMap.get(key);
                    List<String> properties = List.of(set[1].split(","));
                    if (!properties.contains(stateValue)) return false;

                } else {
                    return false;
                }
            }
            return true;
        }

    }


    private enum WeatherType {
        clear,
        rain,
        thunder;


        @Nullable
        public static WeatherType getType(String type) {
            if (type == null) return null;
            //noinspection EnhancedSwitchMigration
            switch (type) {
                case "clear":
                    return clear;
                case "rain":
                    return rain;
                case "thunder":
                    return thunder;
                default:
                    return null;
            }
        }
    }
}
