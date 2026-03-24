package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import org.jetbrains.annotations.NotNull;
import net.minecraft.client.Minecraft;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class MoonPhaseProperty extends SimpleIntegerArrayProperty {


    protected MoonPhaseProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "moonPhase"));
    }


    public static MoonPhaseProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new MoonPhaseProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"moonPhase"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        //#if MC >= 26.1
        //$$ return Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.skyRenderState.moonPhase.index();
        //#elseif MC >= 12111
        //$$ return Minecraft.getInstance().gameRenderer.getLevelRenderState().skyRenderState.moonPhase.index();
        //#else
        if (entity.world() == null)
            return Integer.MIN_VALUE;
        return entity.world().getMoonPhase();
        //#endif
    }
}
