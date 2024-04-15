package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class HeightProperty extends SimpleIntegerArrayProperty {


    protected HeightProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "heights", "height"));
    }


    public static HeightProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            if (!(properties.containsKey("heights." + propertyNum) || properties.containsKey("height." + propertyNum))
                    && (properties.containsKey("minHeight." + propertyNum) || properties.containsKey("maxHeight." + propertyNum))) {
                String min = "-64";
                String max = "319";
                if (properties.containsKey("minHeight." + propertyNum)) {
                    min = properties.getProperty("minHeight." + propertyNum).strip();
                }
                if (properties.containsKey("maxHeight." + propertyNum)) {
                    max = properties.getProperty("maxHeight." + propertyNum).strip();
                }
                properties.put("heights." + propertyNum, min + "-" + max);
            }
            return new HeightProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"heights", "height"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        return entity.etf$getBlockY();
    }
}
