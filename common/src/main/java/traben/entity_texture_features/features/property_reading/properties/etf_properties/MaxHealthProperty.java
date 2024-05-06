package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.world.entity.LivingEntity;

public class MaxHealthProperty extends FloatRangeFromStringArrayProperty {


    protected MaxHealthProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "maxHealth", "max_health"));
    }

    public static MaxHealthProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MaxHealthProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Float getRangeValueFromEntity(ETFEntity entity) {
        if (entity instanceof LivingEntity alive)
            return alive.getMaxHealth();
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"maxHealth", "max_health"};
    }

}
