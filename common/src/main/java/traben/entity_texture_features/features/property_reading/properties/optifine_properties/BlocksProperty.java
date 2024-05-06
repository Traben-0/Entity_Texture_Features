package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;


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
            return property.getName((T) value);
        }
    };
    protected final Function<BlockState, Boolean> blockStateMatcher;
    protected final boolean botherWithDeepStateCheck;

    protected BlocksProperty(Properties properties, int propertyNum, String[] ids) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, ids).replaceAll("(?<=(^| ))minecraft:", ""));
        if (usesRegex) {
            blockStateMatcher = (blockState) -> {
                if (MATCHER.testString(getFromStateBlockNameOnly(blockState))) {
                    return true;
                } else {
                    return MATCHER.testString(getFromStateBlockNameWithStateData(blockState));
                }
            };
            botherWithDeepStateCheck = false;
        } else {
            blockStateMatcher = this::testBlocks;
            boolean hasStateNeeds = false;
            for (String s : ARRAY) {
                if (s.contains(":")) {
                    hasStateNeeds = true;
                    break;
                }
            }
            botherWithDeepStateCheck = hasStateNeeds;
        }
    }

    public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new BlocksProperty(properties, propertyNum, new String[]{"blocks", "block"});
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    protected static String getFromStateBlockNameOnly(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().replaceFirst("minecraft:", "");
    }

    private static String getFromStateBlockNameWithStateData(BlockState state) {

        String block = getFromStateBlockNameOnly(state);
        if (!state.getValues().isEmpty())
            block = block + ':' + state.getValues().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(":"));

        return block;
    }

    protected boolean testBlocks(BlockState blockState) {
        //is array only non regex
        if (MATCHER.testString(getFromStateBlockNameOnly(blockState))) {
            return true;
        } else if (botherWithDeepStateCheck) {
            String fullBlockState = getFromStateBlockNameWithStateData(blockState);
            for (String string : ARRAY) {
                if (string.contains(":")) {
                    //block has state requirements
                    boolean matchesAllStateDataNeeded = true;
                    for (String split : string.split(":")) {
                        //check only the declared state data is present so foreach by the declaration
                        if (!fullBlockState.contains(split)) {
                            matchesAllStateDataNeeded = false;
                            break;
                        }
                    }
                    //if so loop can continue
                    if (matchesAllStateDataNeeded) return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    public boolean testEntityInternal(ETFEntity entity) {


        BlockState[] entityBlocks;

        if (entity.etf$getUuid().getLeastSignificantBits() == ETFApi.ETF_SPAWNER_MARKER) {
            // entity is a mini mob spawner entity
            // return a blank mob spawner block state
            entityBlocks = new BlockState[]{Blocks.SPAWNER.defaultBlockState()};
        } else if (entity instanceof BlockEntity blockEntity) {
            if (blockEntity.getLevel() == null) {
                entityBlocks = new BlockState[]{blockEntity.getBlockState()};
            } else {
                entityBlocks = new BlockState[]{blockEntity.getBlockState(), blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().below())};
            }
        } else {
            if (entity.etf$getWorld() == null || entity.etf$getBlockPos() == null) return false;
            Level world = entity.etf$getWorld();
            BlockPos pos = entity.etf$getBlockPos();
            entityBlocks = new BlockState[]{world.getBlockState(pos), world.getBlockState(pos.below())};
        }
        // if(entityBlocks.length == 0) return false;

        for (BlockState entityBlock : entityBlocks) {
            //check each block before returning false
            if (blockStateMatcher.apply(entityBlock)) return true;
        }
        return false;
    }


    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"blocks", "block"};
    }

}
