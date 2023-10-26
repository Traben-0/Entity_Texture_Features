package traben.entity_texture_features.texture_features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.Nullable;

public abstract class FloatRangeFromStringArrayProperty extends RangeFromStringArrayProperty<Float> {


    protected FloatRangeFromStringArrayProperty(String string) throws RandomPropertyException {
        super(string);
    }

    @Override
    protected @Nullable RangeTester<Float> getRangeTesterFromString(String possibleRange) {
        try {
            if (possibleRange.matches("\\d-(\\d|-\\d)")) {
                String[] str = possibleRange.split("(?<!^|-)-");
                float small = Float.parseFloat(str[0].replaceAll("[^0-9.-]", ""));
                float big = Float.parseFloat(str[1].replaceAll("[^0-9.-]", ""));
                return (value) -> value >= small && value <= big;
            } else {
                float single = Float.parseFloat(possibleRange.replaceAll("[^0-9.-]", ""));
                return (value) -> value == single;
            }
        } catch (Exception ignored) {
        }
        return null;
    }


}
