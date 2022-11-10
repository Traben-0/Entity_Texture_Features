package traben.entity_texture_features.texture_handlers;


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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.mixin.accessor.MooshroomEntityAccessor;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class ETFTexturePropertyCase {
    //public final int PROPERTY_NUMBER;
    private final Integer[] SUFFIX_NUMBERS_WEIGHTED;
    private final @Nullable String[] BIOME_VALUES;
    private final @Nullable Integer[] HEIGHT_Y_VALUES;
    private final @Nullable String[] NAME_STRINGS;//add
    private final @Nullable String[] PROFESSION_VALUES;
    private final @Nullable String[] COLOR_VALUES;//add
    private final int IS_BABY; // 0 1 2 - don't true false
    private final int WEATHER_TYPE; //0,1,2,3 - no clear rain thunder
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
                                  int baby012,
                                  int weather0123,
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
//                                  @Nullable Boolean isTrapHorse,
                                 @Nullable Boolean isAngry,
                                  @Nullable PandaEntity.Gene[] hiddenGene,
//                                  @Nullable Angriness[] wardenAngriness,
//                                  @Nullable Boolean isAngryWithClient,
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
//        IS_TRAP_HORSE = isTrapHorse;
        IS_ANGRY = isAngry;
        HIDDEN_GENE = hiddenGene;
//        WARDEN_ANGRINESS = wardenAngriness;
//        IS_ANGRY_WITH_CLIENT = isAngryWithClient;
        IS_PLAYER_CREATED = isPlayerCreated;
        IS_SCREAMING_GOAT = isScreamingGoat;


        BIOME_VALUES = biomesX ;
        HEIGHT_Y_VALUES = heights ;
        NAME_STRINGS = namesX ;
        PROFESSION_VALUES = professionsX ;
        COLOR_VALUES = collarColoursX ;
        IS_BABY = baby012;
        WEATHER_TYPE = weather0123;
        HEALTH_RANGE_STRINGS = healthX  ;
        MOON_PHASE_VALUES = moonX ;
        TIME_RANGE_STRINGS = daytimeX ;
        BLOCK_VALUES = blocksX ;
        TEAM_VALUES = teamsX ;
        //PROPERTY_NUMBER = propNumber;
        SIZE_VALUES = sizeX;

        if (weightsX == null) {
            weightsX = new Integer[0];
        }


        //todo optimize selection of suffixes by weight better than this, not efficient when scaled, also only happens once per property so meh
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
                ETFUtils2.logMessage("random texture weights don't match", false);
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
                && IS_BABY == 0
                && WEATHER_TYPE == 0
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
//                && IS_TRAP_HORSE == null
                && IS_ANGRY == null
                && HIDDEN_GENE == null
//                && WARDEN_ANGRINESS == null
//                && IS_ANGRY_WITH_CLIENT == null
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

        ObjectImmutableList<String> spawnConditions ;
        if (ETFConfigData.restrictUpdateProperties) {
            if (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.containsKey(id)) {
                spawnConditions = (ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.get(id));
            } else {
                spawnConditions = readAllSpawnConditionsForCache(entity);
                ETFManager.getInstance().ENTITY_SPAWN_CONDITIONS_CACHE.put(id, spawnConditions);
            }
        }else{
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
        if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && IS_BABY != 0) {
            wasEntityTestedByAnUpdatableProperty = true;
            doesEntityMeetThisCaseTest = (IS_BABY == 1) == ((LivingEntity)entity).isBaby();
            //System.out.println("baby " + doesEntityMeetThisCaseTest);
        }
        if (doesEntityMeetThisCaseTest && WEATHER_TYPE != 0) {
            if (!ETFConfigData.restrictWeather) wasEntityTestedByAnUpdatableProperty = true;
            boolean raining;
            boolean thundering;
            if (isUpdate && ETFConfigData.restrictWeather && ETFConfigData.restrictUpdateProperties && spawnConditions != null && spawnConditions.size() >= 4) {
                String[] data = spawnConditions.get(3).split("-");
                raining = data[0].trim().equals("1");
                thundering = data[1].trim().equals("1");
            } else {
                raining = entity.world.isRaining();
                thundering = entity.world.isThundering();
            }
            boolean check = false;
            if (WEATHER_TYPE == 1 && !(raining || thundering)) {
                check = true;
            } else if (WEATHER_TYPE == 2 && raining) {
                check = true;
            } else if (WEATHER_TYPE == 3 && thundering) {
                check = true;
            }
            doesEntityMeetThisCaseTest = check;
        }
        if (doesEntityMeetThisCaseTest && entity instanceof LivingEntity && HEALTH_RANGE_STRINGS != null) {
            wasEntityTestedByAnUpdatableProperty = true;
            //float entityHealth = entity.getHealth();
            boolean check = false;
            //always check percentage
            float checkValue = ((LivingEntity)entity).getHealth() / ((LivingEntity)entity).getMaxHealth() * 100;
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
            double speed = ((LivingEntity)entity).getMovementSpeed();
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
            float checkValue = ((LivingEntity)entity).getMaxHealth();
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
            }else if (entity instanceof BlazeEntity) {
                wasEntityTestedByAnUpdatableProperty = true;
                doesEntityMeetThisCaseTest = entity.isOnFire() == IS_ANGRY;
            }else if (entity instanceof GuardianEntity) {
                wasEntityTestedByAnUpdatableProperty = true;
                doesEntityMeetThisCaseTest = (((GuardianEntity)entity).getBeamTarget() != null) == IS_ANGRY;
            }else if (entity instanceof VindicatorEntity) {
                wasEntityTestedByAnUpdatableProperty = true;
                doesEntityMeetThisCaseTest = (((VindicatorEntity)entity).isAttacking()) == IS_ANGRY;
            }else if (entity instanceof SpellcastingIllagerEntity) {
                wasEntityTestedByAnUpdatableProperty = true;
                doesEntityMeetThisCaseTest = (((SpellcastingIllagerEntity)entity).isSpellcasting()) == IS_ANGRY;
            }else{
                doesEntityMeetThisCaseTest =false;
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
                if (((LivingEntity)entity).hasStatusEffect(effect)) {
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
        if (doesEntityMeetThisCaseTest && ITEMS != null ) {
            wasEntityTestedByAnUpdatableProperty = true;
            System.out.println(Arrays.toString(ITEMS) +" - " +entity.getItemsEquipped().toString());
            if(ITEMS.length == 1
                    && ("none".equals(ITEMS[0])
                    || "any".equals(ITEMS[0])
                    || "holding".equals(ITEMS[0])
                    || "wearing".equals(ITEMS[0]))){
                if (ITEMS[0].equals( "none")){
                    Iterable<ItemStack> equipped = entity.getItemsEquipped();
                    for (ItemStack item :
                            equipped) {
                        if (item != null && !item.isEmpty()) {
                            //found a valid item break and deny
                            doesEntityMeetThisCaseTest=false;
                            break;
                        }
                    }
                }else{
                    Iterable<ItemStack> items;
                    if ("any".equals(ITEMS[0])){//any
                        items = entity.getItemsEquipped();
                    }else if ("holding".equals(ITEMS[0])){
                        items = entity.getItemsHand();
                    }else {//wearing
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
            }else {
                //specifically named item

                //both armour and hand held
                Iterable<ItemStack> equipped = entity.getItemsEquipped();
                boolean found = false;
                upper: for (String itemToFind:
                     ITEMS){
                    if(itemToFind != null) {
                        if (itemToFind.contains("minecraft:")) {
                            itemToFind = itemToFind.replace("minecraft:","");
                        }

                        for (ItemStack item :
                                equipped) {
                            if (item != null
                                    && !item.isEmpty()
                                &&item.getItem().toString().replace("minecraft:","").equals(itemToFind)) {
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
