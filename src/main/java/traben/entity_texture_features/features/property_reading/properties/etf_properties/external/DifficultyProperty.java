package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class DifficultyProperty extends SimpleIntegerArrayProperty {


    protected DifficultyProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "difficulty"));
    }


    public static DifficultyProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new DifficultyProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"difficulty"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null) {
            return entity.world().getDifficulty().getId(); // 0 - 3 in vanilla
        }
        return 0;
    }
}
