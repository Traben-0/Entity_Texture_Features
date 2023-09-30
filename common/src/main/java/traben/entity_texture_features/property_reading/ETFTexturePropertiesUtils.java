package traben.entity_texture_features.property_reading;

import com.google.common.base.CaseFormat;
import net.minecraft.entity.passive.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
//                        getStatusEffect(props, num),
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

//    @Nullable
//    private static StatusEffect[] getStatusEffect(Properties props, int num) {
//        if (props.containsKey("statusEffect." + num)) {
//            String dataFromProps = props.getProperty("statusEffect." + num).trim();
//            String[] columnData = dataFromProps.split("\\s+");
//            ArrayList<StatusEffect> statuses = new ArrayList<>();
//            for (String data :
//                    columnData) {
//                data = data.replaceAll("\\(", "").replaceAll("\\)", "");
//                //check if range
//                data = data.trim();
//                if (!data.replaceAll("\\D", "").isEmpty()) {
//                    try {
//                        int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
//                        StatusEffect attempt = StatusEffect.byRawId(tryNumber);
//                        if (attempt != null) {
//                            statuses.add(attempt);
//                        }
//                    } catch (NumberFormatException e) {
//                        ETFUtils2.logWarn("properties files number error in statusEffects category");
//                    }
//
//                }
//            }
//            return statuses.toArray(new StatusEffect[0]);
//        }
//        return null;
//    }

    @Nullable
    private static String[] getItems(Properties props, int num) {
        return getGenericStringSplitProperty(props, num, "items");
    }

    @Nullable
    private static Boolean getMoving(Properties props, int num) {
        return getGenericBooleanThatCanNull(props, num, "moving");
    }

    @Nullable
    public static String[] getGenericStringSplitProperty(Properties props, int num, String propertyName) {
        if (props.containsKey(propertyName + "." + num)) {
            return props.getProperty(propertyName + "." + num).trim().split("\\s+");
        }
        return null;
    }

    @Nullable
    public static Integer[] getGenericIntegerSplitWithRanges(Properties props, int num, String propertyName) {
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
    public static Boolean getGenericBooleanThatCanNull(Properties props, int num, String propertyName) {
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


    public record IntRange(int lower, int higher) {
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

    public interface StringPropertyMatcher {
        boolean testPropertyString(String currentEntityValue);
    }

    public static Pattern groupByQuotationPattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");


    enum WeatherType {
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
