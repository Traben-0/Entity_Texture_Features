package traben.entity_texture_features.texture_features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

public class WeatherProperty extends StringArrayOrRegexProperty {
    protected WeatherProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "weather"));
        if (ARRAY.contains("rain")) ARRAY.add("thunder");
    }

    public static WeatherProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new WeatherProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    protected String getValueFromEntity(ETFEntity entity) {
        if (entity.getWorld() != null) {
            if (entity.getWorld().isThundering()) return "thunder";
            if (entity.getWorld().isRaining()) return "rain";
            return "clear";
        }
        return null;
    }


    @Override
    public boolean isPropertyUpdatable() {
        return !ETFConfigData.restrictWeather;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"weather"};
    }

}
