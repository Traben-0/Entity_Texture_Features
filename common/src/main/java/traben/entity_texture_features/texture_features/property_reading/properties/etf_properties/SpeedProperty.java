package traben.entity_texture_features.texture_features.property_reading.properties.etf_properties;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;

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
        if (entity.getEntity() instanceof LivingEntity alive)
            return alive.getMovementSpeed();
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return false;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"speed", "maxSpeed", "speeds"};
    }

}
