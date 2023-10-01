package traben.entity_texture_features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.IntegerArrayProperty;

import java.util.Properties;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class MoonPhaseProperty extends IntegerArrayProperty {


    protected MoonPhaseProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "moonPhase"));
    }



    public static MoonPhaseProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            return new MoonPhaseProperty(properties, propertyNum);
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
        return new String[]{"moonPhase"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        if(entity.getWorld() == null)
            return Integer.MIN_VALUE;
        return entity.getWorld().getMoonPhase();
    }
}
