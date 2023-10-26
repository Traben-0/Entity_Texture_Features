package traben.entity_texture_features.texture_features.property_reading.properties.etf_properties;

import net.minecraft.entity.mob.CreeperEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Properties;

public class ChargedCreeperProperty extends BooleanProperty {


    protected ChargedCreeperProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "creeperCharged", "creeper_charged"));
    }

    public static ChargedCreeperProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ChargedCreeperProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntity etfEntity) {
        if (etfEntity.getEntity() instanceof CreeperEntity creeper)
            return creeper.shouldRenderOverlay();
        return null;
    }

    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"creeperCharged", "creeper_charged"};
    }

}
