package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


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
        if (entity.etf$getWorld() != null) {
            if (entity.etf$getWorld().isThundering()) return "thunder";
            if (entity.etf$getWorld().isRaining()) return "rain";
            return "clear";
        }
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"weather"};
    }

}
