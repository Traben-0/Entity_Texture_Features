package traben.entity_texture_features.client;


import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.MinecraftVersion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.util.DyeColor;
import traben.entity_texture_features.client.utils.ETFUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;
import static traben.entity_texture_features.client.ETFClient.UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS;

public class ETFTexturePropertyCase {
    public final int propertyNumber;
    private final Integer[] weightedSuffixes;
    private final String[] biomes;
    private final Integer[] heights;
    private final String[] names;//add
    private final String[] professions;
    private final String[] colors;//add
    private final int baby; // 0 1 2 - dont true false
    private final int weather; //0,1,2,3 - no clear rain thunder
    private final String[] health;
    private final Integer[] moon;
    private final String[] daytime;
    private final String[] blocks;
    private final String[] teams;
    private final Integer[] sizes;

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
                                  int propNumber,
                                  Integer[] sizeX
    ) {

        biomes = biomesX != null ? biomesX : new String[0];
        heights = heightsX != null ? heightsX : new Integer[0];
        names = namesX != null ? namesX : new String[0];
        professions = professionsX != null ? professionsX : new String[0];
        colors = collarColoursX != null ? collarColoursX : new String[0];
        baby = baby012;
        weather = weather0123;
        health = healthX != null ? healthX : new String[0];
        moon = moonX != null ? moonX : new Integer[0];
        daytime = daytimeX != null ? daytimeX : new String[0];
        blocks = blocksX != null ? blocksX : new String[0];
        teams = teamsX != null ? teamsX : new String[0];
        propertyNumber = propNumber;
        sizes = sizeX;

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
                weightedSuffixes = buildWeighted.toArray(new Integer[0]);

            } else {
                ETFUtils.logMessage("random texture weights don't match", false);
                weightedSuffixes = suffixesX;
            }
        } else {

            weightedSuffixes = suffixesX;
        }
    }

    @Override
    public String toString() {
        return "randomCase{" +
                "weightedSuffixes=" + Arrays.toString(weightedSuffixes) +
                ", biomes=" + Arrays.toString(biomes) +
                ", heights=" + Arrays.toString(heights) +
                ", names=" + Arrays.toString(names) +
                '}';
    }


    public boolean testEntity(LivingEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {


        //System.out.println("checking property number "+propertyNumber);


        if (biomes.length == 0
                && names.length == 0
                && heights.length == 0
                && professions.length == 0
                && colors.length == 0
                && baby == 0
                && weather == 0
                && health.length == 0
                && moon.length == 0
                && daytime.length == 0
                && blocks.length == 0
                && teams.length == 0
                && sizes.length == 0
        ) {
            return true;
        }

        if (!ETFConfigData.restrictUpdateProperties) {
            isUpdate = false;
        }

        if (!UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.containsKey(entity.getUuid())) {
            UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.put(entity.getUuid(), getNonUpdateables(entity));
        }

        boolean wasTestedByUpdateable = false;
        boolean allBoolean = true;
        if (biomes.length > 0) {
            //String entityBiome = entity.world.getBiome(entity.getBlockPos()).getCategory().getName();//has no caps// desert
            //1.18.1 old mapping String entityBiome = Objects.requireNonNull(entity.world.getRegistryManager().get(Registry.BIOME_KEY).getId(entity.world.getBiome(entity.getBlockPos()))).toString();
            //not an exact grabbing of the name, but it works for the contains check so no need for more processing
            //example "Reference{ResourceKey[minecraft:worldgen/biome / minecraft:river]=net.minecraft.class_1959@373fe79a}"
            //String entityBiome = entity.world.getBiome(entity.getBlockPos()).toString();
            //example  "Optional[minecraft:worldgen/biome / minecraft:river]"
            String entityBiome;
            if (isUpdate && ETFConfigData.restrictBiome) {
                entityBiome = UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(entity.getUuid())[0].trim();
            }else {
                entityBiome = entity.getWorld().getBiome(entity.getBlockPos()).getKey().toString().split("\s/\s")[1].replaceAll("[^0-9a-zA-Z_:-]", "");
            }

            //System.out.println("biome="+entityBiome);
            boolean check = false;

            for (String str :
                    biomes) {
                //System.out.println("biometest="+str);
                if (entityBiome.replace("minecraft:", "").equals(str.trim().toLowerCase().replace("minecraft:", ""))) {
                    check = true;
                    break;
                }
            }

            allBoolean = check;
        }
        if (allBoolean && names.length > 0) {
            wasTestedByUpdateable = true;
            if (entity.hasCustomName()) {
                String entityName = Objects.requireNonNull(entity.getCustomName()).getString();

                boolean check = false;
                boolean invert = false;
                for (String str :
                        names) {
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

                        //I do not understand pattern and no-one has ever had a problem with this implementation
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

                allBoolean = check;
            } else {
                allBoolean = false;
            }
        }
        if (allBoolean && heights.length > 0) {
            int entityHeight = entity.getBlockY();
            if (isUpdate && ETFConfigData.restrictHeight) {
                entityHeight = Integer.parseInt(UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(entity.getUuid())[1].trim());
            }
            boolean check = false;
            for (int i :
                    heights) {
                if (i == entityHeight) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && professions.length > 0 && entity instanceof VillagerEntity) {
            wasTestedByUpdateable = true;
            String entityProfession = ((VillagerEntity) entity).getVillagerData().getProfession().toString().toLowerCase().replace("minecraft:", "");
            int entityProfessionLevel = ((VillagerEntity) entity).getVillagerData().getLevel();
            boolean check = false;
            for (String str :
                    professions) {
                //str could be   librarian:1,3-4
                str = str.toLowerCase().replaceAll("\s*", "").replace("minecraft:", "");
                //could be   "minecraft:cleric:1-4
                if (str.contains(":")) {
                    //splits at seperator for profession level check only
                    String[] data = str.split(":[0-9]");
                    if (entityProfession.contains(data[0]) || data[0].contains(entityProfession)) {
                        //has profession now check level
                        if (data.length == 2) {
                            String[] levels = data[1].split(",");
                            ArrayList<Integer> levelData = new ArrayList<>();
                            for (String lvls :
                                    levels) {
                                if (lvls.contains("-")) {
                                    levelData.addAll(Arrays.asList(ETFUtils.getIntRange(lvls)));
                                } else {
                                    levelData.add(Integer.parseInt(lvls.replaceAll("[^0-9]", "")));
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
            allBoolean = check;
        }

        if (allBoolean && colors.length > 0) {

            wasTestedByUpdateable = true;
            String entityColor;
            if (entity instanceof WolfEntity wolf) {
                entityColor = wolf.getCollarColor().asString().toLowerCase();
            } else if (entity instanceof SheepEntity sheep) {
                entityColor = sheep.getColor().asString().toLowerCase();
            } else if (entity instanceof LlamaEntity llama) {
                DyeColor str = llama.getCarpetColor();
                if (str != null) {
                    entityColor = str.asString().toLowerCase();
                } else {
                    entityColor = "NOT_A_COLOR";
                }
            } else if (entity instanceof CatEntity cat) {
                entityColor = cat.getCollarColor().asString().toLowerCase();
            } else if (entity instanceof ShulkerEntity shulker) {
                DyeColor str = shulker.getColor();
                if (str != null) {
                    entityColor = str.asString().toLowerCase();
                } else {
                    entityColor = "NOT_A_COLOR";
                }
            } else if (entity instanceof TropicalFishEntity fishy) {
                DyeColor str = TropicalFishEntity.getBaseDyeColor(fishy.getVariant());
                if (str != null) {
                    entityColor = str.asString().toLowerCase();
                } else {
                    entityColor = "NOT_A_COLOR";
                }
            } else {
                entityColor = "NOT_A_COLOR";
            }

            boolean check = false;
            for (String i :
                    colors) {
                i = i.toLowerCase();
                if (i.contains(entityColor) || entityColor.contains(i)) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && baby != 0) {
            wasTestedByUpdateable = true;
            allBoolean = (baby == 1) == entity.isBaby();
            //System.out.println("baby " + allBoolean);
        }
        if (allBoolean && weather != 0) {
            boolean raining = entity.world.isRaining();
            boolean thundering = entity.world.isThundering();
            if (isUpdate && ETFConfigData.restrictWeather) {
                String[] data = UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(entity.getUuid())[3].split("-");
                raining = data[0].trim().equals("1");
                thundering = data[1].trim().equals("1");
            }
            boolean check = false;
            if (weather == 1 && !(raining || thundering)) {
                check = true;
            } else if (weather == 2 && raining) {
                check = true;
            } else if (weather == 3 && thundering) {
                check = true;
            }
            allBoolean = check;
        }
        if (allBoolean && health.length > 0) {
            wasTestedByUpdateable = true;
            //float entityHealth = entity.getHealth();
            boolean check = false;
            //always check percentage
            float checkValue = entity.getHealth() / entity.getMaxHealth() * 100;
            for (String hlth :
                    health) {
                if (hlth.contains("-")) {
                    String[] str = hlth.split("-");
                    if (checkValue >= Integer.parseInt(str[0].replaceAll("[^0-9]", ""))
                            && checkValue <= Integer.parseInt(str[1].replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }

                } else {
                    if (checkValue == Integer.parseInt(hlth.replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }
                }
            }
            allBoolean = check;
        }
        if (allBoolean && moon.length > 0) {
            int moonPhase = entity.world.getMoonPhase();
            if (isUpdate && ETFConfigData.restrictMoonPhase) {
                moonPhase = Integer.parseInt(UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(entity.getUuid())[5].trim());
            }
            boolean check = false;
            for (int i :
                    moon) {
                if (i == moonPhase) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && daytime.length > 0) {
            long time = entity.world.getTimeOfDay();
            boolean check = false;
            if (isUpdate && ETFConfigData.restrictDayTime) {
                time = Long.parseLong(UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(entity.getUuid())[4].trim());
            }
            for (String rangeOfTime :
                    daytime) {
                if (rangeOfTime.contains("-")) {
                    String[] str = rangeOfTime.split("-");
                    if (time >= Long.parseLong(str[0].replaceAll("[^0-9]", ""))
                            && time <= Long.parseLong(str[1].replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }

                } else {
                    if (time == Long.parseLong(rangeOfTime.replaceAll("[^0-9]", ""))) {
                        check = true;
                        break;
                    }
                }
            }
            allBoolean = check;
        }
        if (allBoolean && blocks.length > 0) {
            String entityOnBlock = entity.world.getBlockState(entity.getBlockPos().down()).toString()
                    .replaceFirst("minecraft:", "")
                    .replaceFirst("Block\\{", "")
                    //will print with
                    .replaceFirst("}.*", "");
            if (isUpdate && ETFConfigData.restrictBlock) {
                entityOnBlock = UUID_ORIGINAL_NON_UPDATE_PROPERTY_STRINGS.get(entity.getUuid())[2].trim();
            }

            boolean check = false;
            for (String block :
                    blocks) {
                block = block.trim();
                if (block.startsWith("!")) {
                    block = block.replaceFirst("!", "");
                    if (!block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlock)) {
                        //can continue to check cases
                        check = true;
                    } else {
                        //will prevent future checking
                        check = false;
                        break;
                    }
                } else if (block.replace("minecraft:", "").equalsIgnoreCase(entityOnBlock)) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && teams.length > 0) {
            wasTestedByUpdateable = true;
            if (entity.getScoreboardTeam() != null) {
                String teamName = entity.getScoreboardTeam().getName();

                boolean check = false;
                boolean invert = false;
                for (String str :
                        teams) {
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
                allBoolean = check;
            } else {
                allBoolean = false;
            }
        }
        if (allBoolean && sizes.length > 0 &&
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
                    sizes) {
                if (i == size) {
                    check = true;
                    break;
                }
            }
            allBoolean = check;
        }


        if (wasTestedByUpdateable) {
            UUID_CaseHasUpdateablesCustom.put(entity.getUuid(), true);
        }
        return allBoolean;
    }

    public int getWeightedSuffix(UUID id, boolean ignoreOne) {
        int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();

        randomReliable %= weightedSuffixes.length;

        randomReliable = weightedSuffixes[randomReliable];

        if (randomReliable == 1 && ignoreOne) {
            randomReliable = 0;
        }

        return randomReliable;
    }

    private String[] getNonUpdateables(LivingEntity entity) {
        //must be 6 length
        // 0 biome
        // 1 height
        // 2 block
        // 3 weather
        // 4 daytime
        // 5 moonphase
        String biome = entity.getWorld().getBiome(entity.getBlockPos()).getKey().toString().split("\s/\s")[1].replaceAll("[^0-9a-zA-Z_:-]", "");;

        String height = "" + entity.getBlockY();
        String block = entity.world.getBlockState(entity.getBlockPos().down()).toString()
                .replaceFirst("minecraft:", "")
                .replaceFirst("Block\\{", "")
                .replaceFirst("}.*", "");
        String weather = (entity.world.isRaining() ? "1" : "0") + "-" + (entity.world.isThundering() ? "1" : "0");
        String time = "" + entity.world.getTimeOfDay();
        String moon = "" + entity.world.getMoonPhase();
        return new String[]{biome, height, block, weather, time, moon};
    }
}
