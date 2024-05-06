package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.client.Minecraft;

public class LanguageProperty extends StringArrayOrRegexProperty {


    protected LanguageProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "language"));
    }

    public static LanguageProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new LanguageProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @Nullable String getValueFromEntity(ETFEntity etfEntity) {
        return Minecraft.getInstance().options.languageCode;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"language"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }


}
