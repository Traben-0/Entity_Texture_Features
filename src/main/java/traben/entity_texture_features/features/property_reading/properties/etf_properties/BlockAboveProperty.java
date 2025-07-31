package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.properties.optifine_properties.BlocksProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class BlockAboveProperty extends BlocksProperty {


    protected BlockAboveProperty(final Properties properties, final int propertyNum, final String[] ids) throws RandomPropertyException {
        super(properties, propertyNum, ids);
    }

    public static BlocksProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new BlockAboveProperty(properties, propertyNum, new String[]{"blockBelow"});
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    protected @Nullable BlockState[] getTestingBlocks(final ETFEntityRenderState entity) {
        if (entity.uuid().getLeastSignificantBits() == ETFApi.ETF_SPAWNER_MARKER) {
            // entity is a mini mob spawner entity
            // return a blank mob spawner block state
            return new BlockState[]{Blocks.SPAWNER.defaultBlockState()};
        } else {
            if (entity.world() == null || entity.blockPos() == null){
                return null;
            }
            Level world = entity.world();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            mutablePos.set(entity.blockPos());

            if(world.canSeeSky(mutablePos)){
                return null;
            }

            int minBuildHeight =
            //#if MC >= 12103
                world.getMinY();
            //#else
            //$$    world.getMinBuildHeight();
            //#endif

            while (minBuildHeight <= mutablePos.getY() && world.getBlockState(mutablePos).isAir()) {
                mutablePos.move(0, 1, 0);
            }
            if (minBuildHeight > mutablePos.getY()) {
                return null;
            }
            return new BlockState[]{world.getBlockState(mutablePos)};
        }

    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"blockBelow"};
    }
}
