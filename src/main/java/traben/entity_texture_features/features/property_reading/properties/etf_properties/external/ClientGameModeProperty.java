package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class ClientGameModeProperty extends SimpleIntegerArrayProperty {


    protected ClientGameModeProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "clientGameMode"));
    }


    public static ClientGameModeProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ClientGameModeProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"clientGameMode"};
    }

    @Override
    protected int getValueFromEntity(ETFEntityRenderState entity) {
        if (Minecraft.getInstance().player != null) {
            //#if MC>=12105
            var mode = Minecraft.getInstance().player.gameMode();
            if (mode != null) return mode.getId();
            //#else
            //$$ var info  = Minecraft.getInstance().player.getPlayerInfo();
            //$$ if (info != null) return -1;
            //$$ var mode = info.getGameMode();
            //$$ if (mode != null) return -1;
            //$$ return mode.getId();
            //#endif
        }
        return -1;
    }
}
