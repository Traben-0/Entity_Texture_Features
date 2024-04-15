package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.Nullable;

public abstract class LongRangeFromStringArrayProperty extends NumberRangeFromStringArrayProperty<Long> {


    protected LongRangeFromStringArrayProperty(String string) throws RandomPropertyException {
        super(string);
    }


    @Override
    protected @Nullable RangeTester<Long> getRangeTesterFromString(String possibleRange) {
        try {
            if (possibleRange.matches("(\\d+|-\\d+)-(\\d+|-\\d+)")) {
                String[] str = possibleRange.split("(?<!^|-)-");
                long left = Long.parseLong(str[0].replaceAll("[^0-9-]", ""));
                long right = Long.parseLong(str[1].replaceAll("[^0-9-]", ""));
                if (left == right) {
                    return (value) -> value == left;
                } else if (right > left) {
                    return (value) -> value >= left && value <= right;
                } else {
                    return (value) -> value >= right && value <= left;
                }
            } else {
                long single = Long.parseLong(possibleRange.replaceAll("[^0-9-]", ""));
                return (value) -> value == single;
            }
        } catch (Exception ignored) {
        }
        return null;
    }


}
