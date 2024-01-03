package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Properties;

public class DimensionProperty extends StringArrayOrRegexProperty {

    private final boolean doPrint;
    protected DimensionProperty(String string) throws RandomPropertyException {
        super(string.replace("print:", ""));
        doPrint = string.startsWith("print:");
    }

    public static DimensionProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new DimensionProperty(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "dimension"));
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        if(etfEntity == null) return null;

        World world = etfEntity.etf$getWorld();
        if(world == null) return null;

        RegistryKey<DimensionType> dimKey = etfEntity.etf$getWorld().getDimensionKey();
        if(dimKey == null) return null;

        Identifier key = dimKey.getValue();
        if (key == null) return null;

        String output;
        if (key.equals(DimensionTypes.OVERWORLD_ID) || key.getPath().equals("overworld_caves")) {
            output = "overworld";
        } else if (key.equals(DimensionTypes.THE_NETHER_ID)) {
            output = "the_nether";
        } else if (key.equals(DimensionTypes.THE_END_ID)) {
            output = "the_end";
        } else {
            //modded
            output = key.toString();
        }
        if (doPrint) ETFUtils2.logMessage("[Dimension property print]: "+output);
        return output;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"dimension"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }


}
