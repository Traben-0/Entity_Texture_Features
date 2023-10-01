package traben.entity_texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class IntegerArrayProperty extends RandomProperty {


    protected IntegerArrayProperty(Integer[] array) throws RandomPropertyException {

        if(array == null || array.length == 0) throw new RandomPropertyException(getPropertyId() + " property was broken");
        ARRAY = new IntOpenHashSet(List.of(array));
    }
    private final IntOpenHashSet ARRAY;

    @Nullable
    public static Integer[] getGenericIntegerSplitWithRanges(Properties props, int num, String... propertyNames) {
        if(propertyNames.length==0) throw new IllegalArgumentException("propertyNames is empty in IntegerArrayProperty");
        for (String propertyName:
             propertyNames) {
            if (props.containsKey(propertyName + "." + num)) {
                String dataFromProps = props.getProperty(propertyName + "." + num).strip().replaceAll("[)(]", "");
                String[] skinData = dataFromProps.split("\\s+");
                ArrayList<Integer> suffixNumbers = new ArrayList<>();
                for (String data :
                        skinData) {
                    //check if range
                    data = data.strip();
                    if (!data.replaceAll("\\D", "").isEmpty()) {
                        try {
                            if (data.contains("-")) {
                                suffixNumbers.addAll(Arrays.asList(getIntRange(data).getAllWithinRangeAsList()));
                            } else {
                                int tryNumber = Integer.parseInt(data.replaceAll("\\D", ""));
                                suffixNumbers.add(tryNumber);
                            }
                        } catch (NumberFormatException e) {
                            ETFUtils2.logWarn("properties files number error in " + propertyName + " category");
                        }
                    }
                }
                return suffixNumbers.toArray(new Integer[0]);
            }
        }
        return null;
    }

    public static IntRange getIntRange(String rawRange) {
        //assume rawRange =  "20-56"  but can be "-64-56", "-30--10"  or "-14"
        rawRange = rawRange.trim();
        //sort negatives before split
        if (rawRange.startsWith("-")) {
            rawRange = rawRange.replaceFirst("-", "N");
        }
        rawRange = rawRange.replaceAll("--", "-N");
        String[] split = rawRange.split("-");
        if (split.length == 2 && !split[0].isEmpty() && !split[1].isEmpty()) {//sort out range
            int[] minMax = {Integer.parseInt(split[0].replaceAll("\\D", "")), Integer.parseInt(split[1].replaceAll("\\D", ""))};
            if (split[0].contains("N")) {
                minMax[0] = -minMax[0];
            }
            if (split[1].contains("N")) {
                minMax[1] = -minMax[1];
            }
            if (minMax[0] > minMax[1]) {
                //0 must be smaller
                return new IntRange(minMax[1], minMax[0]);
            } else {
                return new IntRange(minMax[0], minMax[1]);
            }
        } else {//only 1 number but method ran because of "-" present
            int number = Integer.parseInt(rawRange.replaceAll("\\D", ""));
            if (rawRange.contains("N")) {
                number = -number;
            }
            return new IntRange(number, number);
        }
    }


    @Override
    public boolean testEntityInternal(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions){

        int entityInteger = getValueFromEntity(entity);

        return ARRAY.contains(entityInteger);
    }

    protected abstract int getValueFromEntity(ETFEntity entity);


    public record IntRange(int lower, int higher) {
        public boolean isWithinRange(int value) {
            return value >= lower && value <= higher;
        }

        public Integer[] getAllWithinRangeAsList() {
            if(lower == higher){
                return new Integer[]{lower};
            }
            ArrayList<Integer> builder = new ArrayList<>();
            for (int i = lower; i <= higher; i++) {
                builder.add(i);
            }
            return builder.toArray(new Integer[0]);
        }
    }
}
