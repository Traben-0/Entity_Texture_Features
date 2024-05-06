package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.world.entity.player.Player;

public class CreativeProperty extends BooleanProperty {


    protected CreativeProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "isCreative", "creative"));
    }

    public static CreativeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new CreativeProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity instanceof Player player) return player.isCreative();
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"isCreative", "creative"};
    }

}
