package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Calendar;
import java.util.Properties;


public class SecondProperty extends SimpleIntegerArrayProperty {


    protected SecondProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "second"));
    }


    public static SecondProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SecondProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"second"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        return Calendar.getInstance().get(Calendar.SECOND);
        //up to 59
    }
}
