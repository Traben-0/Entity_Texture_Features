package traben.entity_texture_features.property_reading.properties.optifine_properties;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SlimeEntity;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.IntegerArrayProperty;

import java.util.Properties;

public class SizeProperty extends IntegerArrayProperty {


    protected SizeProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "sizes","size"));
    }



    public static SizeProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            return new SizeProperty(properties, propertyNum);
        }catch(RandomPropertyException e){
            return null;
        }
    }

    @Override
    public boolean isPropertyUpdatable(){
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"sizes","size"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        if (entity.entity() instanceof SlimeEntity slime) {
            //magma cube too
            return slime.getSize() - 1;
        } else if (entity.entity() instanceof PhantomEntity phantom) {
            return phantom.getPhantomSize();
        }
        return Integer.MIN_VALUE;
    }
}
