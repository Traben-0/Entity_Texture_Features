package traben.entity_texture_features.property_reading.properties.optifine_properties;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;

import java.util.Properties;

public class HealthProperty extends FloatRangeFromStringArrayProperty {


    protected HealthProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "health"));
    }

    public static HealthProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            return new HealthProperty(properties, propertyNum);
        }catch(RandomPropertyException e){
            return null;
        }
    }


    @Nullable
    @Override
    protected Float getRangeValueFromEntity(ETFEntity entity) {
        if(entity.entity() instanceof LivingEntity alive)
            return alive.getHealth() / alive.getMaxHealth() * 100;
        return null;
    }

    @Override
    public boolean isPropertyUpdatable(){
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"health"};
    }

}