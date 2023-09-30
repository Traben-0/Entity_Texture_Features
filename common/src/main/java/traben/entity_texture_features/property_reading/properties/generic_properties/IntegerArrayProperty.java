package traben.entity_texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperty;

import java.util.List;

public abstract class IntegerArrayProperty extends RandomProperty {


    protected IntegerArrayProperty(Integer[] array) throws RandomPropertyException {

        if(array == null || array.length == 0) throw new RandomPropertyException(getPropertyId() + " property was broken");
        ARRAY = new IntOpenHashSet(List.of(array));
    }
    private final IntOpenHashSet ARRAY;



    @Override
    public boolean testEntityInternal(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions){

        int entityInteger = getValueFromEntity(entity);
        if (!isPropertyUpdatable()) {
            spawnConditions.put(getPropertyId(), ARRAY.contains(entityInteger));
        }
        return ARRAY.contains(entityInteger);
    }

    protected abstract int getValueFromEntity(ETFEntity entity);





}
