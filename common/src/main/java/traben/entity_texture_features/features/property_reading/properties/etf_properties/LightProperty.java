package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class LightProperty extends SimpleIntegerArrayProperty {


    protected LightProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "light"));
    }


    public static LightProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new LightProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"light"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        if (entity == null || entity.etf$getWorld() == null || entity.etf$getBlockPos() == null) return -1;

        return entity.etf$getWorld().getMaxLocalRawBrightness(entity.etf$getBlockPos());
    }
}
