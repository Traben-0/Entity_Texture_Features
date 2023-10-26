package traben.entity_texture_features.texture_features.property_reading.properties.optifine_properties;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class BlocksProperty extends StringArrayOrRegexProperty {


    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<>() {
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                Property<?> property = entry.getKey();
                String var10000 = property.getName();
                return var10000 + "=" + this.nameValue(property, entry.getValue());
            }
        }

        private <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
            //noinspection unchecked
            return property.name((T) value);
        }
    };

    protected BlocksProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "blocks", "block").replaceAll("(?<=(^| ))minecraft:", ""));
    }

    public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new BlocksProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    private static String getBlockFormattedFromState(BlockState state) {
        String block = Registries.BLOCK.getId(state.getBlock()).toString();
        if (!state.getEntries().isEmpty())
            block = block + ':' + state.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(":"));
        if (block.startsWith("minecraft:"))
            return block.replaceFirst("minecraft:", "");
        return block;
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    public boolean testEntityInternal(ETFEntity entity) {

        String[] entityBlocks;
        BlockEntity blockEntity = entity.getBlockEntity();
        if (blockEntity != null) {
            entityBlocks = new String[]{getBlockFormattedFromState(blockEntity.getCachedState())};
        } else {
            if (entity.getWorld() == null || entity.getBlockPos() == null) return false;
            World world = entity.getWorld();
            BlockPos pos = entity.getBlockPos();
            entityBlocks = new String[]{
                    getBlockFormattedFromState(world.getBlockState(pos)),
                    getBlockFormattedFromState(world.getBlockState(pos.down()))
            };
        }
        // if(entityBlocks.length == 0) return false;

        boolean foundAMatch = false;
        for (String block :
                entityBlocks) {
            if (block != null) {
                //simple contains check or regex if present
                foundAMatch = MATCHER.testString(block.toLowerCase());
                if (!foundAMatch) {
                    //if no regex or simple contains check match then try each defined property
                    boolean foundEach = true;
                    for (String definition :
                            ARRAY) {
                        for (String partsToFind :
                                definition.split(":")) {
                            if (!block.contains(partsToFind)) {
                                foundEach = false;
                                break;
                            }
                        }
                    }
                    foundAMatch = foundEach;
                }
                if (foundAMatch) break;
            }
        }

        return foundAMatch;
    }

    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return !ETFConfigData.restrictBlock;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"blocks", "block"};
    }

//todo old code, still relevant?
//
//    private static boolean doBlockEntriesMatch(List<String> propertyEntries, String blockStateEntries) {
//        if (propertyEntries.isEmpty()) return true;
//
//        String[] fixedStateEntries = blockStateEntries.replaceFirst("\\{", "").replaceFirst("}$", "").split(", ");
//
//        HashMap<String, String> stateMap = new HashMap<>();
//        for (String entry :
//                fixedStateEntries) {
//            if (entry.contains("=")) {
//                String[] set = entry.split("=");
//                stateMap.put(set[0], set[1]);
//            } else {
//                ETFUtils2.logWarn("block state failed in property check");
//                return false;
//            }
//        }
//
//        if (stateMap.isEmpty()) return false;
//
//        for (String property :
//                propertyEntries) {
//            String[] set = property.split("=");
//            String key = set[0];
//            if (stateMap.containsKey(key)) {
//                String stateValue = stateMap.get(key);
//                List<String> properties = List.of(set[1].split(","));
//                if (!properties.contains(stateValue)) return false;
//
//            } else {
//                return false;
//            }
//        }
//        return true;
//    }
}
