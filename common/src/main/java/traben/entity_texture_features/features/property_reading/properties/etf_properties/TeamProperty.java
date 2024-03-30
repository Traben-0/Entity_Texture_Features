package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class TeamProperty extends StringArrayOrRegexProperty {


    protected TeamProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "teams", "team"));
    }

    public static TeamProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TeamProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity.etf$getScoreboardTeam() != null) {
            return etfEntity.etf$getScoreboardTeam().getName();
        }
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"teams", "team"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }


}
