package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class ModLoadedProperty extends StringArrayOrRegexProperty {


    private final boolean matched;

    protected ModLoadedProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "modLoaded", "modsLoaded"));

        boolean matches = false;
        assert ETF.modsLoaded() != null;
        for (String modId : ETF.modsLoaded()) {
            if (MATCHER.testString(modId)) {
                matches = true;
                break;
            }
        }
        matched = matches;
    }

    public static ModLoadedProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ModLoadedProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        return null;
    }

    @Override
    public boolean testEntity(final ETFEntity entity, final boolean isUpdate) {
        return matched;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"modLoaded", "modsLoaded"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }


}
