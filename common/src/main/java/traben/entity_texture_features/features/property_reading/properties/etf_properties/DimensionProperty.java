package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;
import java.util.Properties;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

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
        if (etfEntity == null) return null;

        Level world = etfEntity.etf$getWorld();
        if (world == null) return null;

        Optional<ResourceKey<DimensionType>> dimKey = etfEntity.etf$getWorld().dimensionTypeRegistration().unwrapKey();
        if (dimKey.isEmpty()) return null;

        ResourceLocation key = dimKey.get().location();
        //noinspection ConstantValue
        if (key == null) return null;

        String output;
        if (key.equals(BuiltinDimensionTypes.OVERWORLD_EFFECTS) || key.getPath().equals("overworld_caves")) {
            output = "overworld";
        } else if (key.equals(BuiltinDimensionTypes.NETHER_EFFECTS)) {
            output = "the_nether";
        } else if (key.equals(BuiltinDimensionTypes.END_EFFECTS)) {
            output = "the_end";
        } else {
            //modded
            output = key.toString();
        }
        if (doPrint) ETFUtils2.logMessage("[Dimension property print]: " + output);
        return output;
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
