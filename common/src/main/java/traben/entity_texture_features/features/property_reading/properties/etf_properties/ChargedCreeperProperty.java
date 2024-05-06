package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.world.entity.monster.Creeper;

public class ChargedCreeperProperty extends BooleanProperty {


    protected ChargedCreeperProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "creeperCharged", "creeper_charged"));
    }

    public static ChargedCreeperProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ChargedCreeperProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity instanceof Creeper creeper)
            return creeper.isPowered();
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"creeperCharged", "creeper_charged"};
    }

}
