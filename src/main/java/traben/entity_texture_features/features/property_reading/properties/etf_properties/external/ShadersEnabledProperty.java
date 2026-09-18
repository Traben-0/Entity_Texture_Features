package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.compat.IrisHandler;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.BooleanProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;

import java.util.Properties;


public class ShadersEnabledProperty extends BooleanProperty {


    protected ShadersEnabledProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericBooleanThatCanNull(properties, propertyNum, "usingShaders"));
    }

    public static ShadersEnabledProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ShadersEnabledProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    @Nullable
    protected Boolean getValueFromEntity(ETFEntityRenderState entity) {
        return IrisHandler.getInstance().isShaderActive();
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"usingShaders"};
    }

}
