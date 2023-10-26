package traben.entity_texture_features.texture_features.property_reading.properties.generic_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.texture_features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.ArrayList;

public abstract class RangeFromStringArrayProperty<N extends Number> extends RandomProperty {

    protected final String ORIGINAL_INPUT;
    protected final ArrayList<RangeTester<N>> ARRAY = new ArrayList<>();

    protected RangeFromStringArrayProperty(String string) throws RandomPropertyException {
        ORIGINAL_INPUT = string;
        if (string == null)
            throw new RandomPropertyException(getPropertyId() + " property was broken");

        String[] array = string.trim().split("\\s+");

        if (array.length == 0)
            throw new RandomPropertyException(getPropertyId() + " property was broken");

        for (String str :
                array) {
            RangeTester<N> tester = getRangeTesterFromString(str);
            if (tester != null) ARRAY.add(tester);
        }
    }

    @Override
    public boolean testEntityInternal(ETFEntity entity) {

        boolean check = false;
        //always check percentage
        N checkValue = getRangeValueFromEntity(entity);
        if (checkValue != null) {
            for (RangeTester<N> range :
                    ARRAY) {
                if (range != null) {
                    if (range.isValueWithinRangeOrEqual(checkValue)) {
                        check = true;
                        break;
                    }
                }
            }
            return check;
        }
        return false;
    }


    @Nullable
    protected abstract N getRangeValueFromEntity(ETFEntity entity);

    @Nullable
    protected abstract RangeTester<N> getRangeTesterFromString(String possibleRange);

    @Override
    public boolean isPropertyUpdatable() {
        return true;
    }

    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"health"};
    }

    @Override
    protected String getPrintableRuleInfo() {
        return ORIGINAL_INPUT;
    }

    public interface RangeTester<N> {
        boolean isValueWithinRangeOrEqual(N value);
    }
}
