package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.NumberRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class TimeOfDayProperty extends NumberRangeFromStringArrayProperty<Long> {


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
            return entity.etf$getWorld().getTimeOfDay();
        return null;
    }

    @Override
    protected @Nullable RangeTester<Long> getRangeTesterFromString(String possibleRange) {
        try {
            if (possibleRange.matches("(\\d+|-\\d+)-(\\d+|-\\d+)")) {
                String[] str = possibleRange.split("(?<!^|-)-");
                long small = Long.parseLong(str[0].replaceAll("[^0-9-]", ""));
                long big = Long.parseLong(str[1].replaceAll("[^0-9-]", ""));
                return (value) -> value >= small && value <= big;
            } else {
                long single = Long.parseLong(possibleRange.replaceAll("[^0-9-]", ""));
                return (value) -> value == single;
            }
        } catch (Exception ignored) {
        }
        return null;
    }


    @Override
    public boolean isPropertyUpdatable() {
        return !ETFConfigData.restrictDayTime;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"dayTime"};
    }

}
