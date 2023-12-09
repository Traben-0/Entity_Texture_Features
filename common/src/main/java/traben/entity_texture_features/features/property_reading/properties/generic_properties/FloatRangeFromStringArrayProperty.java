package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class FloatRangeFromStringArrayProperty extends NumberRangeFromStringArrayProperty<Float> {


    protected FloatRangeFromStringArrayProperty(String string) throws RandomPropertyException {
        super(string);
    }

    @Override
    protected @Nullable RangeTester<Float> getRangeTesterFromString(String possibleRange) {
        try {
            if (possibleRange.matches("([\\d.]+|-[\\d.]+)-([\\d.]+|-[\\d.]+)")) {
                String[] str = possibleRange.split("(?<!^|-)-");
                float small = Float.parseFloat(str[0].replaceAll("[^0-9.-]", ""));
                float big = Float.parseFloat(str[1].replaceAll("[^0-9.-]", ""));
                return (value) -> value >= small && value <= big;
            } else {
                float single = Float.parseFloat(possibleRange.replaceAll("[^0-9.-]", ""));
                return (value) -> value == single;
            }
        } catch (Exception ignored) {
            //System.out.println(possibleRange + "failed ");
            //ignored.printStackTrace();
            ETFUtils2.logError("number or range in [" + getPropertyId() + "] property could not be extracted from input: " + possibleRange);
        }
        return null;
    }


}
