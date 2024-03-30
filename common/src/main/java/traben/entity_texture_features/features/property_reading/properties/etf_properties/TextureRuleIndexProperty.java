package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;


public class TextureRuleIndexProperty extends SimpleIntegerArrayProperty {


    protected TextureRuleIndexProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "textureRule", "texture_rule"));
    }


    public static TextureRuleIndexProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new TextureRuleIndexProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"textureRule", "texture_rule"};
    }

    @Override
    protected int getValueFromEntity(ETFEntity entity) {
        int val = ETFManager.getInstance().LAST_RULE_INDEX_OF_ENTITY.getInt(entity.etf$getUuid());
        return Math.max(val, 0);
    }
}
