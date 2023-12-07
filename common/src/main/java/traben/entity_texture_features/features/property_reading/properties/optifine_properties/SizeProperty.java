package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class SizeProperty extends SimpleIntegerArrayProperty {


    protected SizeProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "sizes", "size"));
    }


    public static SizeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new SizeProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"sizes", "size"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        if (entity instanceof SlimeEntity slime) {
            //magma cube too
            return slime.getSize() - 1;
        } else if (entity instanceof PhantomEntity phantom) {
            return phantom.getPhantomSize();
        }
        return Integer.MIN_VALUE;
    }
}
