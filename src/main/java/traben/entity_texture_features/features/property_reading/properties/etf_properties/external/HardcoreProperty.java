package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class HardcoreProperty extends BooleanProperty {

    protected HardcoreProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "hardcore"));
    }

    public static HardcoreProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new HardcoreProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null) {
            return entity.world().getLevelData().isHardcore();
        }
        return null;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"hardcore"};
    }

}
