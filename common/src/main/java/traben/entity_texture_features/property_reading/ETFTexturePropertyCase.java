package traben.entity_texture_features.property_reading;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.entity_handlers.ETFBlockEntityWrapper;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.entity_handlers.ETFEntityWrapper;
import traben.entity_texture_features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class ETFTexturePropertyCase {
    //public final int PROPERTY_NUMBER;
    private final Integer[] SUFFIX_NUMBERS_WEIGHTED;
    private final @Nullable StringArrayOrRegexProperty.StringPropertyMatcher BIOME_VALUE_MATCHER;
    private final @Nullable Integer[] HEIGHT_Y_VALUES;
    private final @Nullable StringArrayOrRegexProperty.StringPropertyMatcher NAME_MATCHERS;//add
    private final @Nullable String[] PROFESSION_VALUES;
    private final @Nullable String[] COLOR_VALUES;//add
    private final @Nullable Boolean IS_BABY; // 0 1 2 - don't true false
    private final @Nullable ETFTexturePropertiesUtils.WeatherType WEATHER_TYPE; //0,1,2,3 - no clear rain thunder
    private final @Nullable String[] HEALTH_RANGE_STRINGS;
    private final @Nullable Integer[] MOON_PHASE_VALUES;
    private final @Nullable String[] TIME_RANGE_STRINGS;
    private final @Nullable String[] BLOCK_VALUES;
    private final @Nullable StringArrayOrRegexProperty.StringPropertyMatcher TEAM_MATCHER;
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

//        private final @Nullable StatusEffect[] STATUS_EFFECT;

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
                                  @Nullable ETFTexturePropertiesUtils.WeatherType weather0123,
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
//                                      @Nullable StatusEffect[] statusEffect,
                                  @Nullable String[] items,
                                  @Nullable Boolean moving,
                                  @Nullable Map<String, String> nbtMap


    ) {
        NBT_MAP = nbtMap;

        MOVING = moving;
        ITEMS = items;
//            STATUS_EFFECT = statusEffect;

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
//                    && STATUS_EFFECT == null
                && ITEMS == null
                && MOVING == null
                && NBT_MAP == null
        );
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
//        if (BIOME_VALUE_MATCHER != null) {
//            if (!ETFConfigData.restrictBiome) wasEntityTestedByAnUpdatableProperty = true;
//            //String entityBiome = entity.world.getBiome(entity.getBlockPos()).getCategory().getName();//has no caps// desert
//            //1.18.1 old mapping String entityBiome = Objects.requireNonNull(entity.world.getRegistryManager().get(Registry.BIOME_KEY).getId(entity.world.getBiome(entity.getBlockPos()))).toString();
//            //not an exact grabbing of the name, but it works for the contains check so no need for more processing
//            //example "Reference{ResourceKey[minecraft:worldgen/biome / minecraft:river]=net.minecraft.class_1959@373fe79a}"
//            //String entityBiome = entity.world.getBiome(entity.getBlockPos()).toString();
//            //example  "Optional[minecraft:worldgen/biome / minecraft:river]"
//            String entityBiome;
//            if (isUpdate && ETFConfigData.restrictBiome && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 1) {
//                entityBiome = spawnConditions.get(0).trim();
//            } else {
//                entityBiome = ETFVersionDifferenceHandler.getBiomeString(etfEntity.getWorld(), etfEntity.getBlockPos()).replace("minecraft:", "");
//            }
//            //            } else if (MinecraftVersion.CURRENT.getName().equals("1.18") || MinecraftVersion.CURRENT.getName().equals("1.18.1")) {
//            //                entityBiome = ETF_1_18_1_versionPatch.getBiome(entity.world, entity.getBlockPos());
//            //            } else {
//            //                entityBiome = ETF_1_18_2_versionPatch.getBiome(entity.world, entity.getBlockPos());
//            //            }
//
//            doesEntityMeetThisCaseTest = BIOME_VALUE_MATCHER.testPropertyString(entityBiome);
//            //doesEntityMeetThisCaseTest = getStringMatcher_Regex_Pattern_List_Single(BIOME_VALUES,entityBiome);
//        }
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
                            StringArrayOrRegexProperty.StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(rawMatchString);
                            doesTestPass = matcher != null && matcher.testPropertyString(rawStringFromNBT);
                            //doesTestPass = rawMatchString.equals(rawStringFromNBT);
                        } else if (nbtTestInstruction.startsWith("raw:")) {
                            String rawStringFromNBT = finalNBTElementOrNullIfFailed.asString();
                            String rawMatchString = nbtTestInstruction.replaceFirst("raw:", "");
                            StringArrayOrRegexProperty.StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(rawMatchString);
                            doesTestPass = matcher != null && matcher.testPropertyString(rawStringFromNBT);
                            //doesTestPass = rawMatchString.equals(rawStringFromNBT);
                        } else if (nbtTestInstruction.startsWith("exists:")) {
                            doesTestPass = nbtTestInstruction.contains("exists:true");
                        } else if (nbtTestInstruction.startsWith("range:")) {
                            if (finalNBTElementOrNullIfFailed instanceof AbstractNbtNumber nbtNumber) {
                                String rawRangeString = nbtTestInstruction.replaceFirst("range:", "");
                                ETFTexturePropertiesUtils.IntRange range = ETFTexturePropertiesUtils.getIntRange(rawRangeString);
                                doesTestPass = range.isWithinRange(nbtNumber.numberValue().intValue());
                            } else {
                                ETFUtils2.logWarn("NBT range is not valid for non number nbt types: " + nbtIdentifier + "=" + nbtTestInstruction);
                            }
                            // }else  if (finalNBTElementOrNullIfFailed instanceof NbtCompound nbtCompound) {
                        } else if (finalNBTElementOrNullIfFailed instanceof AbstractNbtList<?> nbtList) {
                            if (listIndexInstructionWasWildCard) {
                                for (NbtElement element :
                                        nbtList) {
                                    StringArrayOrRegexProperty.StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
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
                            StringArrayOrRegexProperty.StringPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
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
//        if (doesEntityMeetThisCaseTest && NAME_MATCHERS != null) {
//            // System.out.println("start name"+doesEntityMeetThisCaseTest);
//            wasEntityTestedByAnUpdatableProperty = true;
//            if (etfEntity.hasCustomName()) {
//                String entityName = Objects.requireNonNull(etfEntity.getCustomName()).getString();
//                doesEntityMeetThisCaseTest = NAME_MATCHERS.testPropertyString(entityName);
//            } else {
//                doesEntityMeetThisCaseTest = false;
//            }
//        }
//        if (doesEntityMeetThisCaseTest && HEIGHT_Y_VALUES != null) {
//            if (!ETFConfigData.restrictHeight) wasEntityTestedByAnUpdatableProperty = true;
//            int entityHeight;
//            if (isUpdate && ETFConfigData.restrictHeight && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 2) {
//                entityHeight = Integer.parseInt(spawnConditions.get(1).trim());
//            } else {
//                entityHeight = etfEntity.getBlockY();
//            }
//            boolean check = false;
//            for (int i :
//                    HEIGHT_Y_VALUES) {
//                if (i == entityHeight) {
//                    check = true;
//                    break;
//                }
//            }
//            doesEntityMeetThisCaseTest = check;
//        }
//        if (doesEntityMeetThisCaseTest && PROFESSION_VALUES != null && entity instanceof VillagerEntity) {
//            wasEntityTestedByAnUpdatableProperty = true;
//            String entityProfession = ((VillagerEntity) entity).getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
//            int entityProfessionLevel = ((VillagerEntity) entity).getVillagerData().getLevel();
//            boolean check = false;
//            for (String str :
//                    PROFESSION_VALUES) {
//                if (str != null) {
//                    //str could be   librarian:1,3-4
//                    str = str.toLowerCase().replaceAll("\\s*", "").replace("minecraft:", "");
//                    //could be   "minecraft:cleric:1-4
//                    if (str.contains(":")) {
//                        //splits at seperator for profession level check only
//                        String[] data = str.split(":\\d");
//                        if (entityProfession.contains(data[0]) || data[0].contains(entityProfession)) {
//                            //has profession now check level
//                            if (data.length == 2) {
//                                String[] levels = data[1].split(",");
//                                ArrayList<Integer> levelData = new ArrayList<>();
//                                for (String lvls :
//                                        levels) {
//                                    if (lvls.contains("-")) {
//                                        levelData.addAll(Arrays.asList(ETFTexturePropertiesUtils.getIntRange(lvls).getAllWithinRangeAsList()));
//                                    } else {
//                                        levelData.add(Integer.parseInt(lvls.replaceAll("\\D", "")));
//                                    }
//                                }
//                                //now check levels
//                                for (Integer i :
//                                        levelData) {
//                                    if (i == entityProfessionLevel) {
//                                        check = true;
//                                        break;
//                                    }
//                                }
//                            } else {
//                                //no levels just send profession match confirmation
//                                check = true;
//                                break;
//                            }
//                        }
//                    } else {
//                        if (entityProfession.contains(str) || str.contains(entityProfession)) {
//                            check = true;
//                            break;
//                        }
//                    }
//                }
//            }
//            doesEntityMeetThisCaseTest = check;
//        }

//        if (doesEntityMeetThisCaseTest && COLOR_VALUES != null) {
//
//            wasEntityTestedByAnUpdatableProperty = true;
//            String entityColor;
//            if (entity instanceof WolfEntity wolf) {
//                entityColor = wolf.getCollarColor().getName().toLowerCase();
//            } else if (entity instanceof SheepEntity sheep) {
//                entityColor = sheep.getColor().getName().toLowerCase();
//            } else if (entity instanceof LlamaEntity llama) {
//                DyeColor str = llama.getCarpetColor();
//                if (str != null) {
//                    entityColor = str.getName().toLowerCase();
//                } else {
//                    entityColor = "NOT_A_COLOR";
//                }
//            } else if (entity instanceof CatEntity cat) {
//                entityColor = cat.getCollarColor().getName().toLowerCase();
//            } else if (entity instanceof ShulkerEntity shulker) {
//                DyeColor str = shulker.getColor();
//                if (str != null) {
//                    entityColor = str.getName().toLowerCase();
//                } else {
//                    entityColor = "NOT_A_COLOR";
//                }
//            } else if (entity instanceof TropicalFishEntity fishy) {
//                DyeColor str = TropicalFishEntity.getBaseDyeColor(fishy.getVariant().getId());
//                if (str != null) {
//                    entityColor = str.getName().toLowerCase();
//                } else {
//                    entityColor = "NOT_A_COLOR";
//                }
//            } else {
//                entityColor = "NOT_A_COLOR";
//            }
//
//
//            boolean check = false;
//            for (String i :
//                    COLOR_VALUES) {
//                if (i != null) {
//                    i = i.toLowerCase();
//                    if (i.equals(entityColor)) {
//                        check = true;
//                        break;
//                    }
//                }
//            }
//            doesEntityMeetThisCaseTest = check;
//        }
//        if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && IS_BABY != null) {
//            wasEntityTestedByAnUpdatableProperty = true;
//            doesEntityMeetThisCaseTest = (IS_BABY) == ((LivingEntity) entity).isBaby();
//            //System.out.println("baby " + doesEntityMeetThisCaseTest);
//        }
//        if (doesEntityMeetThisCaseTest && WEATHER_TYPE != null) {
//            if (!ETFConfigData.restrictWeather) wasEntityTestedByAnUpdatableProperty = true;
//            boolean raining;
//            boolean thundering;
//            if (isUpdate && ETFConfigData.restrictWeather && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() >= 4) {
//                String[] data = spawnConditions.get(3).split("-");
//                raining = "1".equals(data[0].trim());
//                thundering = "1".equals(data[1].trim());
//            } else {
//                raining = etfEntity.getWorld().isRaining();
//                thundering = etfEntity.getWorld().isThundering();
//            }
//            boolean check = false;
//            if (WEATHER_TYPE == ETFTexturePropertiesUtils.WeatherType.clear && !(raining || thundering)) {
//                check = true;
//            } else if (WEATHER_TYPE == ETFTexturePropertiesUtils.WeatherType.rain && raining) {
//                check = true;
//            } else if (WEATHER_TYPE == ETFTexturePropertiesUtils.WeatherType.thunder && thundering) {
//                check = true;
//            }
//            doesEntityMeetThisCaseTest = check;
//        }
//        if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && HEALTH_RANGE_STRINGS != null) {
//            wasEntityTestedByAnUpdatableProperty = true;
//            //float entityHealth = entity.getHealth();
//            boolean check = false;
//            //always check percentage
//            float checkValue = ((LivingEntity) entity).getHealth() / ((LivingEntity) entity).getMaxHealth() * 100;
//            for (String hlth :
//                    HEALTH_RANGE_STRINGS) {
//                if (hlth != null) {
//                    if (hlth.contains("-")) {
//                        String[] str = hlth.split("-");
//                        if (checkValue >= Integer.parseInt(str[0].replaceAll("\\D", ""))
//                                && checkValue <= Integer.parseInt(str[1].replaceAll("\\D", ""))) {
//                            check = true;
//                            break;
//                        }
//
//                    } else {
//                        if (checkValue == Integer.parseInt(hlth.replaceAll("\\D", ""))) {
//                            check = true;
//                            break;
//                        }
//                    }
//                }
//            }
//            doesEntityMeetThisCaseTest = check;
//        }
//        if (doesEntityMeetThisCaseTest && MOON_PHASE_VALUES != null) {
//            if (!ETFConfigData.restrictMoonPhase) wasEntityTestedByAnUpdatableProperty = true;
//            int moonPhase;
//            if (isUpdate && ETFConfigData.restrictMoonPhase && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 5) {
//                moonPhase = Integer.parseInt(spawnConditions.get(5).trim());
//            } else {
//                moonPhase = etfEntity.getWorld().getMoonPhase();
//            }
//            boolean check = false;
//            for (int i :
//                    MOON_PHASE_VALUES) {
//                if (i == moonPhase) {
//                    check = true;
//                    break;
//                }
//            }
//            doesEntityMeetThisCaseTest = check;
//        }
//        if (doesEntityMeetThisCaseTest && TIME_RANGE_STRINGS != null) {
//            if (!ETFConfigData.restrictDayTime) wasEntityTestedByAnUpdatableProperty = true;
//            long time;
//            boolean check = false;
//            if (isUpdate && ETFConfigData.restrictDayTime && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() > 4) {
//                time = Long.parseLong(spawnConditions.get(4).trim());
//            } else {
//                time = etfEntity.getWorld().getTimeOfDay();
//            }
//            for (String rangeOfTime :
//                    TIME_RANGE_STRINGS) {
//                if (rangeOfTime != null) {
//                    if (rangeOfTime.contains("-")) {
//                        String[] str = rangeOfTime.split("-");
//                        if (time >= Long.parseLong(str[0].replaceAll("\\D", ""))
//                                && time <= Long.parseLong(str[1].replaceAll("\\D", ""))) {
//                            check = true;
//                            break;
//                        }
//
//                    } else {
//                        if (time == Long.parseLong(rangeOfTime.replaceAll("\\D", ""))) {
//                            check = true;
//                            break;
//                        }
//                    }
//                }
//            }
//            doesEntityMeetThisCaseTest = check;
//        }
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
                size = slime.getSize() - 1;
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
//            if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && STATUS_EFFECT != null) {
//                wasEntityTestedByAnUpdatableProperty = true;
//                boolean found = false;
//                for (StatusEffect effect :
//                        STATUS_EFFECT) {
//                    if (((LivingEntity) entity).hasStatusEffect(effect)) {
//                        found = true;
//                        break;
//                    }
//                }
////                if (!found && entity instanceof MooshroomEntity) {
////                    //noinspection PatternVariableCanBeUsed
////                    MooshroomEntity shroom = (MooshroomEntity) entity;
////                    for (@Nullable StatusEffect effect :
////                            STATUS_EFFECT) {
////                        SuspiciousStewIngredient.
////                        if (effect != null && ((MooshroomEntityAccessor) shroom).getStewEffects().contains(effect)) {
////                            found = true;
////                            break;
////                        }
////                    }
////                }
//
//                doesEntityMeetThisCaseTest = found;
//            }
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
