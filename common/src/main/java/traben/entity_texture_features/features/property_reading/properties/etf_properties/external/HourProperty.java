package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Calendar;
import java.util.Properties;


public class HourProperty extends SimpleIntegerArrayProperty {


    protected HourProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "hour"));
    }


    public static HourProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new HourProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"hour"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //24 hour
    }
}
