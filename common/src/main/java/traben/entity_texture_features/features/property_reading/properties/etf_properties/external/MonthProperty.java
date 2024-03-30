package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Calendar;
import java.util.Properties;


public class MonthProperty extends SimpleIntegerArrayProperty {


    protected MonthProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "month"));
    }


    public static MonthProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MonthProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"month"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        return Calendar.getInstance().get(Calendar.MONTH);
        //january 0
    }
}
