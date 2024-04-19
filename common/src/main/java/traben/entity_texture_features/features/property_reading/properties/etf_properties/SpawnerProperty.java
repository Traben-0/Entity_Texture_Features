package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class SpawnerProperty extends BooleanProperty {


    protected SpawnerProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "isSpawner", "spawner"));
    }

    public static SpawnerProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SpawnerProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity != null) {
            return etfEntity.etf$getUuid().getLeastSignificantBits() == ETFApi.ETF_SPAWNER_MARKER;
        }
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"isSpawner", "spawner"};
    }

}
