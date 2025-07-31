package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class MoonPhaseProperty extends SimpleIntegerArrayProperty {


    protected MoonPhaseProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "moonPhase"));
    }


    public static MoonPhaseProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MoonPhaseProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"moonPhase"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (entity.world() == null)
            return Integer.MIN_VALUE;
        return entity.world().getMoonPhase();
    }
}
