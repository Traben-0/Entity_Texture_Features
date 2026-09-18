package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

import java.util.Properties;

public class ResourcePackLoadedProperty extends StringArrayOrRegexProperty {



    protected ResourcePackLoadedProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(RandomProperty.readPropertiesOrThrow(properties, propertyNum, "resourcepack", "resourcepackLoaded"));

    }

    public static ResourcePackLoadedProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ResourcePackLoadedProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @Nullable String getValueFromEntity(ETFEntityRenderState etfEntity) {
        return null;
    }

    @Override
    public boolean testEntity(ETFEntityRenderState entity, boolean isUpdate) {
        var packs = Minecraft.getInstance().getResourcePackRepository().getSelectedIds();
        return packs.stream().anyMatch(MATCHER::testString);
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"resourcepack", "resourcepackLoaded"};
    }

    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return false;
    }


}
