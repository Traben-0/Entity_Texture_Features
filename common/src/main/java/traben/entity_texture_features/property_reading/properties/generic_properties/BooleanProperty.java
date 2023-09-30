package traben.entity_texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperty;

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
            if (!isPropertyUpdatable()) {
                spawnConditions.put(getPropertyId(), BOOLEAN == entityBoolean);
            }
            return BOOLEAN == entityBoolean;
        }
        return false;
    }

    @Nullable
    protected abstract Boolean getValueFromEntity(ETFEntity entity);


}
