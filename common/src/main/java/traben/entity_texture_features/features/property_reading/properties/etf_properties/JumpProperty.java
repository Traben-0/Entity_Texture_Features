package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.entity.passive.AbstractHorseEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class JumpProperty extends FloatRangeFromStringArrayProperty {


    protected JumpProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "jump", "jumpStrength", "jumpHeight"));
    }

    public static JumpProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new JumpProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Float getRangeValueFromEntity(ETFEntity entity) {
        if (entity instanceof AbstractHorseEntity horse)
            return (float) horse.getJumpStrength();
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return false;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"jump", "jumpStrength", "jumpHeight"};
    }

}
