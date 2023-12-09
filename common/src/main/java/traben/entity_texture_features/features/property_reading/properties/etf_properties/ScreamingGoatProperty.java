package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import net.minecraft.entity.passive.GoatEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class ScreamingGoatProperty extends BooleanProperty {


    protected ScreamingGoatProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "screamingGoat", "screaming_goat"));
    }

    public static ScreamingGoatProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ScreamingGoatProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity instanceof GoatEntity goat) return goat.isScreaming();
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return false;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"screamingGoat", "screaming_goat"};
    }

}
