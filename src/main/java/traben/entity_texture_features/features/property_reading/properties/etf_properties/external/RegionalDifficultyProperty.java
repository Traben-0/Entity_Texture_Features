package traben.entity_texture_features.features.property_reading.properties.etf_properties.external;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class RegionalDifficultyProperty extends FloatRangeFromStringArrayProperty {


    protected RegionalDifficultyProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(readPropertiesOrThrow(properties, propertyNum, "regionalDifficulty", "regional_difficulty"));
    }


    public static RegionalDifficultyProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new RegionalDifficultyProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"regionalDifficulty", "regional_difficulty"};
    }

    @Override
    protected Float getRangeValueFromEntity(ETFEntityRenderState entity) {
        if (entity != null && entity.world() != null) {
            // The raw regional difficulty is always 0.0 on Peaceful and ranges from 0.75 to 1.5 on Easy, 1.5 to 4.0 on Normal, and 2.25 to 6.75 on Hard.
            // https://minecraft.wiki/w/Difficulty#Regional_difficulty
            // i.e. 0.0 to 1.0 for clamped regional difficulty and 0.0 to 6.75 for regional difficulty
            return
                    //#if MC >= 1.21.11
                    //$$ (float) entity.world().getDifficulty().getId();
                    //#else
                    entity.world().getCurrentDifficultyAt(entity.blockPos()).getEffectiveDifficulty();
                    //#endif
        }
        return 0f;
    }
}
