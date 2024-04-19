package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Calendar;
import java.util.Properties;


public class MonthDayProperty extends SimpleIntegerArrayProperty {


    protected MonthDayProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "monthDay", "dayMonth"));
    }


    public static MonthDayProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MonthDayProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"monthDay", "dayMonth"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        //first 1
    }
}
