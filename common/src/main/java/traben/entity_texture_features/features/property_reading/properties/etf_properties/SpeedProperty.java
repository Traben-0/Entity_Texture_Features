package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

import net.minecraft.world.entity.LivingEntity;

public class SpeedProperty extends FloatRangeFromStringArrayProperty {


    protected SpeedProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "speed", "maxSpeed", "speeds"));
    }

    public static SpeedProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SpeedProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Float getRangeValueFromEntity(ETFEntity entity) {
        if (entity instanceof LivingEntity alive)
            return alive.getSpeed();
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"speed", "maxSpeed", "speeds"};
    }

}
