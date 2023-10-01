package traben.entity_texture_features.property_reading.properties.generic_properties;

public abstract class FloatRangeFromStringArrayProperty extends RangeFromStringArrayProperty<Float> {


    protected FloatRangeFromStringArrayProperty(String string) throws RandomPropertyException {
        super(string);
    }

    @Override
    protected boolean isValueWithinRangeOrEqual(Float value, String rangeToParse) {
        if (rangeToParse.contains("-")) {
            String[] str = rangeToParse.split("-");
            return value >= Float.parseFloat(str[0].replaceAll("\\D", ""))
                    && value <= Float.parseFloat(str[1].replaceAll("\\D", ""));
        } else {
            return value == Float.parseFloat(rangeToParse.replaceAll("\\D", ""));
        }
    }

}
