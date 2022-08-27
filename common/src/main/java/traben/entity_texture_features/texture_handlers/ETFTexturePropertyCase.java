package traben.entity_texture_features.texture_handlers;


import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.utils.ETFCacheKey;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.texture_handlers.ETFManager.ENTITY_SPAWN_CONDITIONS_CACHE;

public class ETFTexturePropertyCase {
    //public final int PROPERTY_NUMBER;
    private final Integer[] SUFFIX_NUMBERS_WEIGHTED;
    private final String[] BIOME_VALUES;
    private final Integer[] HEIGHT_Y_VALUES;
    private final String[] NAME_STRINGS;//add
    private final String[] PROFESSION_VALUES;
    private final String[] COLOR_VALUES;//add
    private final int IS_BABY; // 0 1 2 - don't true false
    private final int WEATHER_TYPE; //0,1,2,3 - no clear rain thunder
    private final String[] HEALTH_RANGE_STRINGS;
    private final Integer[] MOON_PHASE_VALUES;
    private final String[] TIME_RANGE_STRINGS;
    private final String[] BLOCK_VALUES;
    private final String[] TEAM_VALUES;
    private final Integer[] SIZE_VALUES;

    private final Double[] SPEED_MIN_MAX;
    private final Double[] JUMP_MIN_MAX;
    private final String[] MAX_HEALTH_STRINGS;
    private final Integer[] INVENTORY_COLUMNS;
    private final Boolean IS_TRAP_HORSE;
    private final Boolean IS_ANGRY;
    private final PandaEntity.Gene[] HIDDEN_GENE;
    private final Angriness[] WARDEN_ANGRINESS;
    private final Boolean IS_ANGRY_WITH_CLIENT;
    private final Boolean IS_PLAYER_CREATED;
    private final Boolean IS_SCREAMING_GOAT;


    //whether case should be ignored by updates


    public ETFTexturePropertyCase(Integer[] suffixesX,
                                  Integer[] weightsX,
                                  String[] biomesX,
                                  Integer[] heightsX,
                                  String[] namesX,
                                  String[] professionsX,
                                  String[] collarColoursX,
                                  int baby012,
                                  int weather0123,
                                  String[] healthX,
                                  Integer[] moonX,
                                  String[] daytimeX,
                                  String[] blocksX,
                                  String[] teamsX,
                                  Integer[] sizeX,
                                  @Nullable Double[] speedMinMax,
                                  @Nullable Double[] jumpMinMax,
                                  @Nullable String[] maxHealthStrings,
                                  @Nullable Integer[] inventoryColumns,
                                  @Nullable Boolean isTrapHorse,
                                  @Nullable Boolean isAngry,
                                  @Nullable PandaEntity.Gene[] hiddenGene,
                                  @Nullable Angriness[] wardenAngriness,
                                  @Nullable Boolean isAngryWithClient,
                                  @Nullable Boolean isPlayerCreated,
                                  @Nullable Boolean isScreamingGoat


    ) {


        SPEED_MIN_MAX = speedMinMax;
        JUMP_MIN_MAX = jumpMinMax;
        MAX_HEALTH_STRINGS = maxHealthStrings;
        INVENTORY_COLUMNS = inventoryColumns;
        IS_TRAP_HORSE = isTrapHorse;
        IS_ANGRY = isAngry;
        HIDDEN_GENE = hiddenGene;
        WARDEN_ANGRINESS = wardenAngriness;
        IS_ANGRY_WITH_CLIENT = isAngryWithClient;
        IS_PLAYER_CREATED = isPlayerCreated;
        IS_SCREAMING_GOAT = isScreamingGoat;


        BIOME_VALUES = biomesX != null ? biomesX : new String[0];
        HEIGHT_Y_VALUES = heightsX != null ? heightsX : new Integer[0];
        NAME_STRINGS = namesX != null ? namesX : new String[0];
        PROFESSION_VALUES = professionsX != null ? professionsX : new String[0];
        COLOR_VALUES = collarColoursX != null ? collarColoursX : new String[0];
        IS_BABY = baby012;
        WEATHER_TYPE = weather0123;
        HEALTH_RANGE_STRINGS = healthX != null ? healthX : new String[0];
        MOON_PHASE_VALUES = moonX != null ? moonX : new Integer[0];
        TIME_RANGE_STRINGS = daytimeX != null ? daytimeX : new String[0];
        BLOCK_VALUES = blocksX != null ? blocksX : new String[0];
        TEAM_VALUES = teamsX != null ? teamsX : new String[0];
        //PROPERTY_NUMBER = propNumber;
        SIZE_VALUES = sizeX;

        if (weightsX == null) {
            weightsX = new Integer[0];
        }


        //todo optimize selection of suffixes by weight better than this, not efficient when scaled
        if (weightsX.length > 0) {
            if (weightsX.length == suffixesX.length) {
                ArrayList<Integer> buildWeighted = new ArrayList<>();
                int index = 0;
                for (int suffix :
                        suffixesX) {
                    for (int i = 0; i < weightsX[index]; i++) {
                        //adds the suffix as many times as it is weighted
                        buildWeighted.add(suffix);
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


    public boolean doesEntityMeetConditionsOfThisCase(LivingEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {


        //System.out.println("checking property number "+propertyNumber);


        if (BIOME_VALUES.length == 0
                && NAME_STRINGS.length == 0
                && HEIGHT_Y_VALUES.length == 0
                && PROFESSION_VALUES.length == 0
                && COLOR_VALUES.length == 0
                && IS_BABY == 0
                && WEATHER_TYPE == 0
                && HEALTH_RANGE_STRINGS.length == 0
                && MOON_PHASE_VALUES.length == 0
                && TIME_RANGE_STRINGS.length == 0
                && BLOCK_VALUES.length == 0
                && TEAM_VALUES.length == 0
                && SIZE_VALUES.length == 0
                && SPEED_MIN_MAX == null
                && JUMP_MIN_MAX == null
                && MAX_HEALTH_STRINGS == null
                && INVENTORY_COLUMNS == null
                && IS_TRAP_HORSE == null
                && IS_ANGRY == null
                && HIDDEN_GENE == null
                && WARDEN_ANGRINESS == null
                && IS_ANGRY_WITH_CLIENT == null
                && IS_PLAYER_CREATED == null
                && IS_SCREAMING_GOAT == null
        ) {
            return true;
        }

        if (!ETFConfigData.restrictUpdateProperties) {
            isUpdate = false;
        }
        ETFCacheKey CacheId = new ETFCacheKey(entity.getUuid(), null);

        ObjectImmutableList<String> spawnConditions = null;
        if (ETFConfigData.restrictUpdateProperties) {
            if (ENTITY_SPAWN_CONDITIONS_CACHE.containsKey(CacheId)) {
                spawnConditions = (ENTITY_SPAWN_CONDITIONS_CACHE.get(CacheId));
            } else {
                spawnConditions = readAllSpawnConditionsForCache(entity);
                ENTITY_SPAWN_CONDITIONS_CACHE.put(CacheId, spawnConditions);
            }
        }


        boolean wasEntityTestedByAnUpdatableProperty = false;
        boolean doesEntityMeetThisCaseTest = true;
        if (BIOME_VALUES.length > 0) {
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
                if (entityBiome.replace("minecraft:", "").equals(str.trim().toLowerCase().replace("minecraft:", ""))) {
                    check = true;
                    break;
                }
            }

            doesEntityMeetThisCaseTest = check;
        }
        if (doesEntityMeetThisCaseTest && NAME_STRINGS.length > 0) {
            wasEntityTestedByAnUpdatableProperty = true;
            if (entity.hasCustomName()) {
                String entityName = Objects.requireNonNull(entity.getCustomName()).getString();

                boolean check = false;
                boolean invert = false;
                for (String str :
                        NAME_STRINGS) {
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

                doesEntityMeetThisCaseTest = check;
            } else {
                doesEntityMeetThisCaseTest = false;
            }
        }
        if (doesEntityMeetThisCaseTest && HEIGHT_Y_VALUES.length > 0) {
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
        if (doesEntityMeetThisCaseTest && PROFESSION_VALUES.length > 0 && entity instanceof VillagerEntity) {
            wasEntityTestedByAnUpdatableProperty = true;
            String entityProfession = ((VillagerEntity) entity).getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
            int entityProfessionLevel = ((VillagerEntity) entity).getVillagerData().getLevel();
            boolean check = false;
            for (String str :
                    PROFESSION_VALUES) {
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
            doesEntityMeetThisCaseTest = check;
        }

        if (doesEntityMeetThisCaseTest && COLOR_VALUES.length > 0) {

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
                i = i.toLowerCase();
                if (i.equals(entityColor)) {
                    check = true;
                    break;
                }
            }
            doesEntityMeetThisCaseTest = check;
        }
        if (doesEntityMeetThisCaseTest && IS_BABY != 0) {
            wasEntityTestedByAnUpdatableProperty = true;
            doesEntityMeetThisCaseTest = (IS_BABY == 1) == entity.isBaby();
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
        if (doesEntityMeetThisCaseTest && HEALTH_RANGE_STRINGS.length > 0) {
            wasEntityTestedByAnUpdatableProperty = true;
            //float entityHealth = entity.getHealth();
            boolean check = false;
            //always check percentage
            float checkValue = entity.getHealth() / entity.getMaxHealth() * 100;
            for (String hlth :
                    HEALTH_RANGE_STRINGS) {
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
            doesEntityMeetThisCaseTest = check;
        }
        if (doesEntityMeetThisCaseTest && MOON_PHASE_VALUES.length > 0) {
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
        if (doesEntityMeetThisCaseTest && TIME_RANGE_STRINGS.length > 0) {
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
            doesEntityMeetThisCaseTest = check;
        }
        if (doesEntityMeetThisCaseTest && BLOCK_VALUES.length > 0) {
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
            for (String block :
                    BLOCK_VALUES) {
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
            //will just leave if a negative check matched
            if (doesEntityMeetThisCaseTest) {
                //true if on or inside required block
                //allows water to be used as well as fixing soul sand and mud checks
                doesEntityMeetThisCaseTest = check1 || check2;
            }
        }
        if (doesEntityMeetThisCaseTest && TEAM_VALUES.length > 0) {
            wasEntityTestedByAnUpdatableProperty = true;
            if (entity.getScoreboardTeam() != null) {
                String teamName = entity.getScoreboardTeam().getName();

                boolean check = false;
                boolean invert = false;
                for (String str :
                        TEAM_VALUES) {
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
                doesEntityMeetThisCaseTest = check;
            } else {
                doesEntityMeetThisCaseTest = false;
            }
        }
        if (doesEntityMeetThisCaseTest && SIZE_VALUES.length > 0 &&
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


        if (doesEntityMeetThisCaseTest && SPEED_MIN_MAX != null) {
            double speed = entity.getMovementSpeed();
            doesEntityMeetThisCaseTest = (speed >= SPEED_MIN_MAX[0] && speed <= SPEED_MIN_MAX[1]);
        }
        if (doesEntityMeetThisCaseTest && JUMP_MIN_MAX != null && entity instanceof AbstractHorseEntity) {
            double jumpHeight = ((AbstractHorseEntity) entity).getJumpStrength();
            doesEntityMeetThisCaseTest = (jumpHeight >= JUMP_MIN_MAX[0] && jumpHeight <= JUMP_MIN_MAX[1]);
        }
        if (doesEntityMeetThisCaseTest && MAX_HEALTH_STRINGS != null) {
            boolean check = false;
            //always check percentage
            float checkValue = entity.getMaxHealth();
            for (String hlth :
                    MAX_HEALTH_STRINGS) {
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
        if (doesEntityMeetThisCaseTest && IS_TRAP_HORSE != null && entity instanceof SkeletonHorseEntity) {
            wasEntityTestedByAnUpdatableProperty = true;
            doesEntityMeetThisCaseTest = ((SkeletonHorseEntity) entity).isTrapped() == IS_TRAP_HORSE;
        }
        if (doesEntityMeetThisCaseTest && IS_ANGRY != null && entity instanceof Angerable) {
            wasEntityTestedByAnUpdatableProperty = true;
            doesEntityMeetThisCaseTest = (((Angerable) entity).getAngryAt() != null) == IS_ANGRY;
        }
        if (doesEntityMeetThisCaseTest && IS_ANGRY_WITH_CLIENT != null && MinecraftClient.getInstance().player != null) {
            wasEntityTestedByAnUpdatableProperty = true;
            if (entity instanceof Angerable) {
                doesEntityMeetThisCaseTest = (MinecraftClient.getInstance().player.getUuid().equals(((Angerable) entity).getAngryAt())) == IS_ANGRY_WITH_CLIENT;
            } else if (entity instanceof WardenEntity) {
                doesEntityMeetThisCaseTest = ((WardenEntity) entity).isAngryAt(MinecraftClient.getInstance().player) == IS_ANGRY_WITH_CLIENT;
            } else if (entity instanceof WitherEntity) {
                doesEntityMeetThisCaseTest = ((WitherEntity) entity).isAngryAt(MinecraftClient.getInstance().player) == IS_ANGRY_WITH_CLIENT;
            }
        }
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
        if (doesEntityMeetThisCaseTest && WARDEN_ANGRINESS != null && entity instanceof WardenEntity) {
            wasEntityTestedByAnUpdatableProperty = true;
            boolean found = false;
            for (Angriness angry :
                    WARDEN_ANGRINESS) {
                if (((WardenEntity) entity).getAngriness() == angry) {
                    found = true;
                    break;
                }
            }
            doesEntityMeetThisCaseTest = found;
        }
        if (doesEntityMeetThisCaseTest && IS_PLAYER_CREATED != null && entity instanceof IronGolemEntity) {
            doesEntityMeetThisCaseTest = ((IronGolemEntity) entity).isPlayerCreated() == IS_PLAYER_CREATED;
        }
        if (doesEntityMeetThisCaseTest && IS_SCREAMING_GOAT != null && entity instanceof GoatEntity) {
            doesEntityMeetThisCaseTest = ((GoatEntity) entity).isScreaming() == IS_SCREAMING_GOAT;
        }


        if (wasEntityTestedByAnUpdatableProperty) {
            UUID_CaseHasUpdateablesCustom.put(entity.getUuid(), true);
        }
        return doesEntityMeetThisCaseTest;
    }

    public int getAnEntityVariantSuffixFromThisCase(UUID id) {
        int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();


        randomReliable %= SUFFIX_NUMBERS_WEIGHTED.length;

        randomReliable = SUFFIX_NUMBERS_WEIGHTED[randomReliable];


        return randomReliable;
    }

    @NotNull
    private ObjectImmutableList<String> readAllSpawnConditionsForCache(@NotNull LivingEntity entity) {
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
