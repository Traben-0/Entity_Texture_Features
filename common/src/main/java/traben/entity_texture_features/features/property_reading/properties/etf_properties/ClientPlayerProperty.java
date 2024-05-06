package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientPlayerProperty extends BooleanProperty {


    protected ClientPlayerProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "isClientPlayer", "clientPlayer"));
    }

    public static ClientPlayerProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ClientPlayerProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        return etfEntity instanceof Player entity
                && Minecraft.getInstance().player != null
                && entity.getUUID().equals(Minecraft.getInstance().player.getUUID());
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"isClientPlayer", "clientPlayer"};
    }

}
