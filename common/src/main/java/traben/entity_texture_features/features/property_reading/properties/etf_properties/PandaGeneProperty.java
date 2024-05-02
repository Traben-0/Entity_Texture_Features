package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;
import net.minecraft.world.entity.animal.Panda;

public class PandaGeneProperty extends StringArrayOrRegexProperty {


    protected PandaGeneProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "hiddenGene", "gene"));
    }

    public static PandaGeneProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new PandaGeneProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    protected boolean shouldForceLowerCaseCheck() {
        return true;
    }

    @Override
    @Nullable
    protected String getValueFromEntity(ETFEntity entityETF) {
        if (entityETF != null) {
            if (entityETF instanceof Panda panda) {
                return panda.getHiddenGene().getSerializedName();
            }
        }
        return null;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"hiddenGene", "gene"};
    }

}
