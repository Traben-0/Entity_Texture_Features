package traben.entity_texture_features.utils;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.mixin.accessor.MooshroomEntityAccessor;
import traben.entity_texture_features.texture_handlers.ETFManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public abstract class ETFTexturePropertiesUtils {

    public static void processNewOptifinePropertiesFile(Entity entity, Identifier vanillaIdentifier, Identifier properties) {
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
                if (entity instanceof ZombifiedPiglinEntity
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
                    Integer[] suffixes = getSuffixes(props, num);


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
                                getMoving(props, num)
                        ));

                    } else {
                        ETFUtils2.logWarn("property number \"" + num + ". in file \"" + vanillaIdentifier + ". failed to read.");
                    }
                }
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

    @Nullable
    private static Integer[] getSuffixes(Properties props, int num) {
        Integer[] ints = getGenericIntegerSplitWithRanges(props, num, "skins");
        return ints == null ? getGenericIntegerSplitWithRanges(props, num, "textures") : ints;
        //        if (props.containsKey("." + num) || props.containsKey("textures." + num)) {
        //            String dataFromProps = props.containsKey("skins." + num) ? props.getProperty("skins." + num).strip() : props.getProperty("textures." + num).strip();
        //            String[] skinData = dataFromProps.split("\s+");
        //            ArrayList<Integer> suffixNumbers = new ArrayList<>();
        //            for (String data :
        //                    skinData) {
        //                //check if range
        //                data = data.strip();
        //                if (!data.replaceAll("\\D", "").isEmpty()) {
        //                    if (data.contains("-")) {
        //                        suffixNumbers.addAll(Arrays.asList(ETFUtils2.getIntRange(data)));
        //                    } else {
        //                        try {
        //                            int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
        //                            suffixNumbers.add(tryNumber);
        //                        } catch (NumberFormatException e) {
        //                            ETFUtils2.logWarn("properties files number error in skins / textures category");
        //                        }
        //                    }
        //                }
        //            }
        //            return suffixNumbers.toArray(new Integer[0]);
        //        }
        //        return null;
    }

    @Nullable
    private static Integer[] getWeights(Properties props, int num) {
        if (props.containsKey("weights." + num)) {
            String dataFromProps = props.getProperty("weights." + num).trim();
            String[] weightData = dataFromProps.split("\s+");
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
    private static String[] getBiomes(Properties props, int num) {
        if (props.containsKey("biomes." + num)) {
            String dataFromProps = props.getProperty("biomes." + num).strip();
            String[] biomeList = dataFromProps.toLowerCase().split("\s+");

            //strip out old format optifine biome names
            //I could be way more in-depth and make these line up to all variants but this is legacy code
            //only here for compat, pack makers need to fix these
            if (biomeList.length > 0) {
                for (int i = 0; i < biomeList.length; i++) {
                    String biome = biomeList[i].strip();
                    switch (biome) {
                        //case "Ocean" -> biomeList[i] = "ocean";
                        //case "Plains" -> biomeList[i] = "plains";
                        case "ExtremeHills" -> biomeList[i] = "stony_peaks";
                        case "Forest", "ForestHills" -> biomeList[i] = "forest";
                        case "Taiga", "TaigaHills" -> biomeList[i] = "taiga";
                        case "Swampland" -> biomeList[i] = "swamp";
                        case "River" -> biomeList[i] = "river";
                        case "Hell" -> biomeList[i] = "nether_wastes";
                        case "Sky" -> biomeList[i] = "the_end";
                        //case "FrozenOcean" -> biomeList[i] = "frozen_ocean";
                        //case "FrozenRiver" -> biomeList[i] = "frozen_river";
                        case "IcePlains" -> biomeList[i] = "snowy_plains";
                        case "IceMountains" -> biomeList[i] = "snowy_slopes";
                        case "MushroomIsland", "MushroomIslandShore" -> biomeList[i] = "mushroom_fields";
                        //case "Beach" -> biomeList[i] = "beach";
                        case "DesertHills", "Desert" -> biomeList[i] = "desert";
                        case "ExtremeHillsEdge" -> biomeList[i] = "meadow";
                        case "Jungle", "JungleHills" -> biomeList[i] = "jungle";
                        default -> {
                            if (!biome.contains("_") && biome.matches("[A-Z]")) {
                                //has capitals and no "_" it is probably the weird old format
                                String snake_case_version = biome.replaceAll("(\\B)([A-Z])", "_$2");
                                biomeList[i] = snake_case_version.toLowerCase();
                            }
                        }
                    }
                }
                return biomeList;
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
    private static String[] getNames(Properties props, int num) {
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
            names = null;
        }
        return names == null ? null : names.toArray(new String[0]);
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
    private static String[] getTeams(Properties props, int num) {
        if (props.containsKey("teams." + num)) {
            String teamData = props.getProperty("teams." + num).trim();
            List<String> list = new ArrayList<>();
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
            while (m.find()) {
                list.add(m.group(1).replace("\"", ""));
            }
            return list.toArray(new String[0]);
        } else if (props.containsKey("team." + num)) {
            String teamData = props.getProperty("team." + num).trim();
            List<String> list = new ArrayList<>();
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
            while (m.find()) {
                list.add(m.group(1).replace("\"", ""));
            }
            return list.toArray(new String[0]);
        }
        return null;
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
            String[] input = props.getProperty("hiddenGene." + num).trim().split("\s+");
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
            String[] columnData = dataFromProps.split("\s+");
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
            return props.getProperty(propertyName + "." + num).trim().split("\s+");
        }
        return null;
    }

    @Nullable
    private static Integer[] getGenericIntegerSplitWithRanges(Properties props, int num, String propertyName) {
        if (props.containsKey(propertyName + "." + num)) {
            String dataFromProps = props.getProperty(propertyName + "." + num).strip();
            String[] skinData = dataFromProps.split("\s+");
            ArrayList<Integer> suffixNumbers = new ArrayList<>();
            for (String data :
                    skinData) {
                //check if range
                data = data.strip();
                if (!data.replaceAll("\\D", "").isEmpty()) {
                    if (data.contains("-")) {
                        suffixNumbers.addAll(Arrays.asList(ETFUtils2.getIntRange(data)));
                    } else {
                        try {
                            int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
                            suffixNumbers.add(tryNumber);
                        } catch (NumberFormatException e) {
                            ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
                        }
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

    //    @Nullable
    //    private static Integer[] get(Properties props, int num) {
    //
    //        return null;
    //    }

    public static class ETFTexturePropertyCase {
        //public final int PROPERTY_NUMBER;
        private final Integer[] SUFFIX_NUMBERS_WEIGHTED;
        private final @Nullable String[] BIOME_VALUES;
        private final @Nullable Integer[] HEIGHT_Y_VALUES;
        private final @Nullable String[] NAME_STRINGS;//add
        private final @Nullable String[] PROFESSION_VALUES;
        private final @Nullable String[] COLOR_VALUES;//add
        private final @Nullable Boolean IS_BABY; // 0 1 2 - don't true false
        private final @Nullable WeatherType WEATHER_TYPE; //0,1,2,3 - no clear rain thunder
        private final @Nullable String[] HEALTH_RANGE_STRINGS;
        private final @Nullable Integer[] MOON_PHASE_VALUES;
        private final @Nullable String[] TIME_RANGE_STRINGS;
        private final @Nullable String[] BLOCK_VALUES;
        private final @Nullable String[] TEAM_VALUES;
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

        //whether case should be ignored by updates


        public ETFTexturePropertyCase(Integer[] suffixesX,
                                      @Nullable Integer[] weightsX,
                                      @Nullable String[] biomesX,
                                      @Nullable Integer[] heights,
                                      @Nullable String[] namesX,
                                      @Nullable String[] professionsX,
                                      @Nullable String[] collarColoursX,
                                      @Nullable Boolean baby012,
                                      @Nullable WeatherType weather0123,
                                      @Nullable String[] healthX,
                                      @Nullable Integer[] moonX,
                                      @Nullable String[] daytimeX,
                                      @Nullable String[] blocksX,
                                      @Nullable String[] teamsX,
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
                                      @Nullable Boolean moving


        ) {

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


            BIOME_VALUES = biomesX;
            HEIGHT_Y_VALUES = heights;
            NAME_STRINGS = namesX;
            PROFESSION_VALUES = professionsX;
            COLOR_VALUES = collarColoursX;
            IS_BABY = baby012;
            WEATHER_TYPE = weather0123;
            HEALTH_RANGE_STRINGS = healthX;
            MOON_PHASE_VALUES = moonX;
            TIME_RANGE_STRINGS = daytimeX;
            BLOCK_VALUES = blocksX;
            TEAM_VALUES = teamsX;
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
        }

        @Override
        public String toString() {
            return "randomCase{" +
                    "weightedSuffixes=" + Arrays.toString(SUFFIX_NUMBERS_WEIGHTED) +
                    ", biomes=" + Arrays.toString(BIOME_VALUES) +
                    ", heights=" + Arrays.toString(HEIGHT_Y_VALUES) +
                    ", names=" + Arrays.toString(NAME_STRINGS) +
                    '}';
        }


        public boolean doesEntityMeetConditionsOfThisCase(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {


            //System.out.println("checking property number "+propertyNumber);


            if (BIOME_VALUES == null
                    && NAME_STRINGS == null
                    && HEIGHT_Y_VALUES == null
                    && PROFESSION_VALUES == null
                    && COLOR_VALUES == null
                    && IS_BABY == null
                    && WEATHER_TYPE == null
                    && HEALTH_RANGE_STRINGS == null
                    && MOON_PHASE_VALUES == null
                    && TIME_RANGE_STRINGS == null
                    && BLOCK_VALUES == null
                    && TEAM_VALUES == null
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
            ) {
                return true;
            }

            if (!ETFConfigData.restrictUpdateProperties) {
                isUpdate = false;
            }
            UUID id = entity.getUuid();

            ObjectImmutableList<String> spawnConditions;
            if (ETFConfigData.restrictUpdateProperties) {
                if (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.containsKey(id)) {
                    spawnConditions = (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.get(id));
                } else {
                    spawnConditions = readAllSpawnConditionsForCache(entity);
                    ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.put(id, spawnConditions);
                }
            } else {
                spawnConditions = null;
            }


            boolean wasEntityTestedByAnUpdatableProperty = false;
            boolean doesEntityMeetThisCaseTest = true;
            if (BIOME_VALUES != null) {
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
                    entityBiome = ETFVersionDifferenceHandler.getBiomeString(entity.world, entity.getBlockPos());
                }
                //            } else if (MinecraftVersion.CURRENT.getName().equals("1.18") || MinecraftVersion.CURRENT.getName().equals("1.18.1")) {
                //                entityBiome = ETF_1_18_1_versionPatch.getBiome(entity.world, entity.getBlockPos());
                //            } else {
                //                entityBiome = ETF_1_18_2_versionPatch.getBiome(entity.world, entity.getBlockPos());
                //            }

                //System.out.println("biome="+entityBiome);
                boolean check = false;

                for (String str :
                        BIOME_VALUES) {
                    //System.out.println("biometest="+str);
                    if (str != null && entityBiome.replace("minecraft:", "").equals(str.trim().toLowerCase().replace("minecraft:", ""))) {
                        check = true;
                        break;
                    }
                }

                doesEntityMeetThisCaseTest = check;
            }
            if (doesEntityMeetThisCaseTest && NAME_STRINGS != null) {
                // System.out.println("start name"+doesEntityMeetThisCaseTest);
                wasEntityTestedByAnUpdatableProperty = true;
                if (entity.hasCustomName()) {
                    String entityName = Objects.requireNonNull(entity.getCustomName()).getString();

                    boolean check = false;
                    boolean invert = false;
                    for (String str :
                            NAME_STRINGS) {
                        if (str != null) {
                            str = str.trim();
                            if (str.startsWith("!")) {
                                str = str.replaceFirst("!", "");
                                invert = true;
                                check = true;
                            }

                            if (str.contains("regex:")) {
                                if (str.contains("iregex:")) {
                                    str = str.split(":")[1];
                                    if (entityName.matches("(?i)" + str)) {
                                        check = !invert;
                                        break;
                                    }
                                } else {
                                    str = str.split(":")[1];
                                    if (entityName.matches(str)) {
                                        check = !invert;
                                        break;
                                    }
                                }

                                //I do not understand pattern in optifine and no-one has ever had a problem with this implementation
                                //is this really it, doesn't feel right???
                            } else if (str.contains("pattern:")) {
                                str = str.replace("?", ".?").replace("*", ".*");
                                if (str.contains("ipattern:")) {
                                    str = str.replace("ipattern:", "");
                                    if (entityName.matches("(?i)" + str)) {
                                        check = !invert;
                                        break;
                                    }
                                } else {
                                    str = str.replace("pattern:", "");
                                    if (entityName.matches(str)) {
                                        check = !invert;
                                        break;
                                    }
                                }
                            } else {//direct comparison
                                if (entityName.equals(str)) {
                                    check = !invert;
                                    break;
                                }
                            }

                        }
                    }
                    doesEntityMeetThisCaseTest = check;
                } else {
                    doesEntityMeetThisCaseTest = false;
                }
                //System.out.println("endname"+doesEntityMeetThisCaseTest);
            }
            if (doesEntityMeetThisCaseTest && HEIGHT_Y_VALUES != null) {
                if (!ETFConfigData.restrictHeight) wasEntityTestedByAnUpdatableProperty = true;
                int entityHeight;
                if (isUpdate && ETFConfigData.restrictHeight && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 2) {
                    entityHeight = Integer.parseInt(spawnConditions.get(1).trim());
                } else {
                    entityHeight = entity.getBlockY();
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
                        str = str.toLowerCase().replaceAll("\s*", "").replace("minecraft:", "");
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
                                            levelData.addAll(Arrays.asList(ETFUtils2.getIntRange(lvls)));
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
                    DyeColor str = TropicalFishEntity.getBaseDyeColor(fishy.getVariant());
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
                    raining = entity.world.isRaining();
                    thundering = entity.world.isThundering();
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
                    moonPhase = entity.world.getMoonPhase();
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
                    time = entity.world.getTimeOfDay();
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
                if (isUpdate && ETFConfigData.restrictBlock && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 6) {
                    entityOnBlocks = new String[]{spawnConditions.get(2).trim(), spawnConditions.get(6).trim()};
                } else {
                    String entityOnBlock1 = entity.world.getBlockState(entity.getBlockPos().down()).toString()
                            .replaceFirst("minecraft:", "")
                            .replaceFirst("Block\\{", "")
                            //will print with
                            .replaceFirst("}.*", "").toLowerCase();
                    String entityOnBlock2 = entity.world.getBlockState(entity.getBlockPos()).toString()
                            .replaceFirst("minecraft:", "")
                            .replaceFirst("Block\\{", "")
                            //will print with
                            .replaceFirst("}.*", "").toLowerCase();
                    entityOnBlocks = new String[]{entityOnBlock1, entityOnBlock2};
                }

                boolean check2 = false;
                boolean check1 = false;
                for (String block :
                        BLOCK_VALUES) {
                    if (block != null) {
                        block = block.strip();
                        if (block.startsWith("!")) {
                            block = block.replaceFirst("!", "");
                            if (!block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[0])) {
                                //can continue to check cases
                                check1 = true;
                            } else {
                                //will prevent future checking
                                doesEntityMeetThisCaseTest = false;

                            }
                        } else if (block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[0])) {
                            check1 = true;
                            break;
                        }
                    }
                }
                for (String block :
                        BLOCK_VALUES) {
                    if (block != null) {
                        block = block.strip();
                        if (block.startsWith("!")) {
                            block = block.replaceFirst("!", "");
                            if (!block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[1])) {
                                //can continue to check cases
                                check2 = true;
                            } else {
                                //will prevent future checking
                                doesEntityMeetThisCaseTest = false;

                            }
                        } else if (block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlocks[1])) {
                            check2 = true;
                            break;
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
            if (doesEntityMeetThisCaseTest && TEAM_VALUES != null) {
                wasEntityTestedByAnUpdatableProperty = true;
                if (entity.getScoreboardTeam() != null) {
                    String teamName = entity.getScoreboardTeam().getName();

                    boolean check = false;
                    boolean invert = false;
                    for (String str :
                            TEAM_VALUES) {
                        if (str != null) {
                            str = str.trim();
                            if (str.startsWith("!")) {
                                str = str.replaceFirst("!", "");
                                invert = true;
                                check = true;
                            }
                            if (teamName.equals(str)) {
                                check = !invert;
                                break;
                            }
                        }
                    }
                    doesEntityMeetThisCaseTest = check;
                } else {
                    doesEntityMeetThisCaseTest = false;
                }
            }
            if (doesEntityMeetThisCaseTest && SIZE_VALUES != null &&
                    (entity instanceof SlimeEntity || entity instanceof PhantomEntity)) {
                int size;
                if (entity instanceof SlimeEntity slime) {
                    //magma cube too
                    size = slime.getSize();
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
            if (doesEntityMeetThisCaseTest && JUMP_MIN_MAX != null && entity instanceof HorseBaseEntity) {
                double jumpHeight = ((HorseBaseEntity) entity).getJumpStrength();
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
                float checkValue = entity.distanceTo(MinecraftClient.getInstance().player);
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
                System.out.println(Arrays.toString(ITEMS) + " - " + entity.getItemsEquipped().toString());
                if (ITEMS.length == 1
                        && ("none".equals(ITEMS[0])
                        || "any".equals(ITEMS[0])
                        || "holding".equals(ITEMS[0])
                        || "wearing".equals(ITEMS[0]))) {
                    if ("none".equals(ITEMS[0])) {
                        Iterable<ItemStack> equipped = entity.getItemsEquipped();
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
                            items = entity.getItemsEquipped();
                        } else if ("holding".equals(ITEMS[0])) {
                            items = entity.getItemsHand();
                        } else {//wearing
                            items = entity.getArmorItems();
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
                    Iterable<ItemStack> equipped = entity.getItemsEquipped();
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
                        entity.getVelocity().horizontalLength() == 0.0
                ) != MOVING;
            }


            if (wasEntityTestedByAnUpdatableProperty) {
                UUID_CaseHasUpdateablesCustom.put(entity.getUuid(), true);
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
        private ObjectImmutableList<String> readAllSpawnConditionsForCache(@NotNull Entity entity) {
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
            @NotNull String biome = !ETFConfigData.restrictBiome ? "" : ETFVersionDifferenceHandler.getBiomeString(entity.world, entity.getBlockPos());
            @NotNull String height = !ETFConfigData.restrictHeight ? "" : "" + entity.getBlockY();
            @NotNull String block = !ETFConfigData.restrictBlock ? "" : entity.world.getBlockState(entity.getBlockPos().down()).toString()
                    .replaceFirst("minecraft:", "")
                    .replaceFirst("Block\\{", "")
                    .replaceFirst("}.*", "").toLowerCase();
            //check the block the mob is inside also
            // this solves issues with soul sand and mud being undetected
            @NotNull String block2 = !ETFConfigData.restrictBlock ? "" : entity.world.getBlockState(entity.getBlockPos()).toString()
                    .replaceFirst("minecraft:", "")
                    .replaceFirst("Block\\{", "")
                    .replaceFirst("}.*", "").toLowerCase();

            @NotNull String weather = !ETFConfigData.restrictWeather ? "" : (entity.world.isRaining() ? "1" : "0") + "-" + (entity.world.isThundering() ? "1" : "0");
            @NotNull String time = !ETFConfigData.restrictDayTime ? "" : "" + entity.world.getTimeOfDay();
            @NotNull String moon = !ETFConfigData.restrictMoonPhase ? "" : "" + entity.world.getMoonPhase();
            return ObjectImmutableList.of(biome, height, block, weather, time, moon, block2);
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
