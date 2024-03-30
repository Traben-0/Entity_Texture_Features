package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class TeammateProperty extends BooleanProperty {


    protected TeammateProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "isTeammate", "teammate"));
    }

    public static TeammateProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TeammateProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity instanceof Entity entity && MinecraftClient.getInstance().player != null)
            return entity.isTeammate(MinecraftClient.getInstance().player);
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"isTeammate", "teammate"};
    }

}
