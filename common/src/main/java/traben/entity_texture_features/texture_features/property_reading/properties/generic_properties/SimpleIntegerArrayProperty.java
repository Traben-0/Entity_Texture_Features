package traben.entity_texture_features.texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.RandomPropertiesFileHandler;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * A simpler implementation of {@link NumberRangeFromStringArrayProperty} utilizing an Integer set containing all valid
 * integers for property.<p>
 * I.E. a property of  "1-4 8-10" would have an internal set here of {1,2,3,4,8,9,10}
 * instead of parsing the ranges each time
 * <p>
 * also holding some static methods used by {@link RandomPropertiesFileHandler}
 */
public abstract class SimpleIntegerArrayProperty extends RandomProperty {


    private final IntOpenHashSet ARRAY;

    protected SimpleIntegerArrayProperty(Integer[] array) throws RandomPropertyException {

        if (array == null || array.length == 0)
            throw new RandomPropertyException(getPropertyId() + " property was broken");
        ARRAY = new IntOpenHashSet(List.of(array));
    }

    @Nullable
    public static Integer[] getGenericIntegerSplitWithRanges(Properties props, int num, String... propertyNames) {
        if (propertyNames.length == 0)
            throw new IllegalArgumentException("propertyNames is empty in IntegerArrayProperty");
        for (String propertyName :
                propertyNames) {
            if (propertyName != null && !propertyName.isBlank() && props.containsKey(propertyName + "." + num)) {
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
        String numberOnlyString = rawRange.trim().replaceAll("[^0-9-]", "");
        try {
            if (numberOnlyString.matches("(\\d+|-\\d+)-(\\d+|-\\d+)")) {
                String[] str = numberOnlyString.split("(?<!^|-)-");
                int small = Integer.parseInt(str[0]);
                int large = Integer.parseInt(str[1]);
                return new IntRange(small, large);
            } else {
                int single = Integer.parseInt(numberOnlyString);
                return new IntRange(single, single);
            }
        } catch (Exception e) {
            return new IntRange(0, 0);
        }
    }


    @Override
    public boolean testEntityInternal(ETFEntity entity) {
        int entityInteger = getValueFromEntity(entity);
        return ARRAY.contains(entityInteger);
    }

    protected abstract int getValueFromEntity(ETFEntity entity);

    @Override
    protected String getPrintableRuleInfo() {
        return String.valueOf(ARRAY);
    }

    public record IntRange(int lower, int higher) {
        public boolean isWithinRange(int value) {
            return value >= lower && value <= higher;
        }

        public Integer[] getAllWithinRangeAsList() {
            if (lower == higher) {
                return new Integer[]{lower};
            }

            ArrayList<Integer> builder = new ArrayList<>();
            for (int i = Math.min(lower, higher); i <= Math.max(lower, higher); i++) {
                builder.add(i);
            }
            return builder.toArray(new Integer[0]);
        }
    }
}
