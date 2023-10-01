package traben.entity_texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Properties;

public abstract class BooleanProperty extends RandomProperty {


    protected BooleanProperty(Boolean bool) throws RandomPropertyException {
        if(bool == null) throw new RandomPropertyException(getPropertyId() + " property was broken");
        BOOLEAN = bool;
    }
    private final boolean BOOLEAN;



    @Override
    public boolean testEntityInternal(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions){

        Boolean entityBoolean = getValueFromEntity(entity);
        if(entityBoolean != null) {

            return BOOLEAN == entityBoolean;
        }
        return false;
    }

    @Nullable
    protected abstract Boolean getValueFromEntity(ETFEntity entity);

    @Nullable
    public static Boolean getGenericBooleanThatCanNull(Properties props, int num, String... propertyNames) {
        if(propertyNames.length==0) throw new IllegalArgumentException("BooleanProperty, empty property names given");
        for (String propertyName:
             propertyNames) {
            if (props.containsKey(propertyName + "." + num)) {
                String input = props.getProperty(propertyName + "." + num).trim();
                if ("true".equals(input) || "false".equals(input)) {
                    return "true".equals(input);
                } else {
                    ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
                }
            }
        }

        return null;
    }
}
