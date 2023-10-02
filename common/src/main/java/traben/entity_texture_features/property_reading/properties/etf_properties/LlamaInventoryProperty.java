package traben.entity_texture_features.property_reading.properties.etf_properties;

import net.minecraft.entity.passive.LlamaEntity;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.generic_properties.IntegerArrayProperty;

import java.util.Properties;

public class LlamaInventoryProperty extends IntegerArrayProperty {


    protected LlamaInventoryProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "llamaInventory"));
    }

    public static LlamaInventoryProperty getPropertyOrNull(Properties properties, int propertyNum){
        try {
            return new LlamaInventoryProperty(properties, propertyNum);
        }catch(RandomPropertyException e){
            return null;
        }
    }

    @Override
    public boolean isPropertyUpdatable(){
        return false;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"llamaInventory"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        if(entity.entity() instanceof LlamaEntity llama)
            return llama.getInventoryColumns();
        return Integer.MIN_VALUE;
    }
}