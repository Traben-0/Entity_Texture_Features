package traben.entity_texture_features.texture_features.property_reading.properties.optifine_properties;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;

public class HealthProperty extends FloatRangeFromStringArrayProperty {

    private final boolean isPercentage;

    protected HealthProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "health"));
        isPercentage = originalInput.contains("%");
    }

    public static HealthProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new HealthProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Nullable
    @Override
    protected Float getRangeValueFromEntity(ETFEntity entity) {
        if (entity.getEntity() instanceof LivingEntity alive) {
            float health = alive.getHealth();
            //integer required by optifine when it is percentage
            return isPercentage ? MathHelper.ceil(health / alive.getMaxHealth() * 100) : health;
        }
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"health"};
    }

}
