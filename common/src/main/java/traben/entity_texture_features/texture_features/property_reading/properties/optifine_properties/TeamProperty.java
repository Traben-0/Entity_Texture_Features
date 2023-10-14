package traben.entity_texture_features.texture_features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

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
        if (etfEntity.getScoreboardTeam() != null) {
            return etfEntity.getScoreboardTeam().getName();
        }
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return true;
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
