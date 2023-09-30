package traben.entity_texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperty;

import java.util.Arrays;

public abstract class RangeFromStringArrayProperty<N extends Number> extends RandomProperty {


//    protected GenericRangeFromStringArrayProperty(String[] array) throws RandomPropertyException {
//
//        if(array == null || array.length == 0) throw new RandomPropertyException(getPropertyId() + " property was broken");
//        ARRAY = new ObjectOpenHashSet<String>();
//        ARRAY.addAll(Arrays.asList(array));
//    }
    protected RangeFromStringArrayProperty(String string) throws RandomPropertyException {
        if(string == null)
            throw new RandomPropertyException(getPropertyId() + " property was broken");

        String[] array = string.trim().split("\\s+");

        if(array.length == 0)
            throw new RandomPropertyException(getPropertyId() + " property was broken");

        ARRAY = new ObjectOpenHashSet<String>();
        ARRAY.addAll(Arrays.asList(array));
    }
    protected final ObjectOpenHashSet<String> ARRAY;


    @Override
    public boolean testEntityInternal(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions) {

            boolean check = false;
            //always check percentage
            N checkValue = getRangeValueFromEntity(entity);
            if(checkValue != null) {
                for (String range :
                        ARRAY) {
                    if (range != null) {
                        if (isValueWithinRangeOrEqual(checkValue, range)) {
                            check = true;
                            break;
                        }
                    }
                }
                if (!isPropertyUpdatable()) {
                    spawnConditions.put(getPropertyId(), check);
                }
                return check;
            }
            return false;
    }


    @Nullable
    protected abstract N getRangeValueFromEntity(ETFEntity entity);

    protected abstract boolean isValueWithinRangeOrEqual(N value, String rangeToParse);

    @Override
    public boolean isPropertyUpdatable(){
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"health"};
    }

}
