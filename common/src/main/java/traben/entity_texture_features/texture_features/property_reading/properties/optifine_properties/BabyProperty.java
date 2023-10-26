package traben.entity_texture_features.texture_features.property_reading.properties.optifine_properties;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;


public class BabyProperty extends BooleanProperty {


    protected BabyProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "baby"));
    }

    public static BabyProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new BabyProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }


    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity entity) {
        if (entity.getEntity() instanceof LivingEntity alive) {
            return alive.isBaby();
        }
        return null;
    }


    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"baby"};
    }

}
