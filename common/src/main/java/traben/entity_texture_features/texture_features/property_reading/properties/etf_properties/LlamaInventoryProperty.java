package traben.entity_texture_features.texture_features.property_reading.properties.etf_properties;

import net.minecraft.entity.passive.LlamaEntity;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;

public class LlamaInventoryProperty extends SimpleIntegerArrayProperty {


    protected LlamaInventoryProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "llamaInventory"));
    }

    public static LlamaInventoryProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new LlamaInventoryProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public boolean isPropertyUpdatable() {
        return false;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"llamaInventory"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        if (entity.getEntity() instanceof LlamaEntity llama)
            return llama.getInventoryColumns();
        return Integer.MIN_VALUE;
    }
}
