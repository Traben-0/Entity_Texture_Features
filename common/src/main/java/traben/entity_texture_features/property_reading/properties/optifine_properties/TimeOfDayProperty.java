package traben.entity_texture_features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.RangeFromStringArrayProperty;

import java.util.Properties;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class TimeOfDayProperty extends RangeFromStringArrayProperty<Long> {


    protected TimeOfDayProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "dayTime"));
    }

    public static TimeOfDayProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            return new TimeOfDayProperty(properties, propertyNum);
        }catch(RandomPropertyException e){
            return null;
        }
    }


    @Nullable
    @Override
    protected Long getRangeValueFromEntity(ETFEntity entity) {
        if(entity.getWorld() != null)
            return entity.getWorld().getTimeOfDay();
        return null;
    }

    @Override
    protected boolean isValueWithinRangeOrEqual(Long value, String rangeToParse) {
        if (rangeToParse.contains("-")) {
            String[] str = rangeToParse.split("-");
            return value >= Long.parseLong(str[0].replaceAll("\\D", ""))
                    && value <= Long.parseLong(str[1].replaceAll("\\D", ""));
        } else {
            return value == Long.parseLong(rangeToParse.replaceAll("\\D", ""));
        }
    }

    @Override
    public boolean isPropertyUpdatable(){
        return !ETFConfigData.restrictDayTime;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"dayTime"};
    }

}
