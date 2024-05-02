package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.LongRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class TimeOfDayProperty extends LongRangeFromStringArrayProperty {


    protected TimeOfDayProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "dayTime"));
    }

    public static TimeOfDayProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TimeOfDayProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Nullable
    @Override
    protected Long getRangeValueFromEntity(ETFEntity entity) {
        if (entity.etf$getWorld() != null)
            return entity.etf$getWorld().getDayTime();
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"dayTime"};
    }

}
