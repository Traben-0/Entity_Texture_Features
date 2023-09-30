package traben.entity_texture_features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.IntegerArrayProperty;

import java.util.Properties;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.property_reading.ETFTexturePropertiesUtils.getGenericIntegerSplitWithRanges;

public class HeightProperty extends IntegerArrayProperty {


    protected HeightProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "heights"));
    }



    public static HeightProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            if (!properties.containsKey("heights." + propertyNum)
                    && (properties.containsKey("minHeight." + propertyNum)
                    || properties.containsKey("maxHeight." + propertyNum))) {
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
        }catch(RandomPropertyException e){
            return null;
        }
    }

    @Override
    public boolean isPropertyUpdatable(){
        return !ETFConfigData.restrictHeight;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"heights"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        return entity.getBlockY();
    }
}
