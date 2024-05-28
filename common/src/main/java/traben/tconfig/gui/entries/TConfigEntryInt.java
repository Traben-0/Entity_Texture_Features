package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TConfigEntryInt extends TConfigEntryValue<Integer> {


    private final IntSliderWidget widget;
    private boolean modifiesOffMaxToMin = true;

    public TConfigEntryInt(@Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max, boolean isMinOff, boolean isMaxOff) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new IntSliderWidget(getText(), getter.get(), getTooltip(), min, max, isMinOff, isMaxOff);
    }

    public TConfigEntryInt(@Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new IntSliderWidget(getText(), getter.get(), getTooltip(), min, max, false, false);
    }

    @SuppressWarnings("unused")
    public TConfigEntryInt(@Translatable String text, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max, boolean isMinOff, boolean isMaxOff) {
        this(text, null, getter, setter, defaultValue, min, max, isMinOff, isMaxOff);
    }

    @SuppressWarnings("unused")
    public TConfigEntryInt(@Translatable String text, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max) {
        this(text, null, getter, setter, defaultValue, min, max, false, false);
    }

    @Override
    protected Integer getValueFromWidget() {
        return widget.getValueRoundedToIntBetweenMinMax();
    }

    @Override
    public AbstractWidget getWidget(final int x, final int y, final int width, final int height) {
        #if MC > MC_20_2
        widget.setRectangle(width, height, x, y);
        #else
        widget.setX(x);
        widget.setY(y);
        widget.setWidth(width);
        widget.height = height;
        #endif
        return widget;
    }

    @Override
    void setWidgetToDefaultValue() {
        widget.setValue(defaultValue);
    }

    @Override
    void resetWidgetToInitialValue() {
        widget.setValue(getter.get());
    }

    @SuppressWarnings("unused")
    public TConfigEntryInt dontModifyOffMaxValues() {
        modifiesOffMaxToMin = false;
        return this;
    }

    public class IntSliderWidget extends AbstractSliderButton {
        private final int max;
        private final int min;
        private final String title;

        private final boolean isMinOff;
        private final boolean isMaxOff;
        private final int difference;


        public IntSliderWidget(final Component text,
                               final int initialValue, final Tooltip tooltip, int min, int max, boolean isMinOff, boolean isMaxOff) {
            super(0, 0, 20, 20, text, 0);
            this.min = min;
            this.max = max;
            this.isMinOff = isMinOff;
            this.isMaxOff = isMaxOff;
            this.difference = max - min;

            this.title = text.getString() + ": ";
            setValue(initialValue);
            setTooltip(tooltip);
        }

        private boolean isOff() {
            if (isMinOff && value == 0) {
                return true;
            }
            return isMaxOff && value == 1;
        }

        private void setValue(int intIndex) {
            this.value = (Mth.clamp(intIndex, min, max) - min) / (double) difference;
//            snapValueToNearestIndex();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            snapValueToNearestIndex();
            setMessage(Component.nullToEmpty(title + (getValueRoundedToIntBetweenMinMax() != getter.get() ? CHANGED_COLOR : "") + (isOff() ? CommonComponents.OPTION_OFF.getString() : getValueRoundedToIntBetweenMinMax())));
        }

        @Override
        protected void applyValue() {
            //ignored
        }

        private void snapValueToNearestIndex() {
            value = (int) Math.round(value * difference) / (double) difference;
        }

        //snaps the double value to the nearest index
        public int getValueRoundedToIntBetweenMinMax() {
            if (isOff() && modifiesOffMaxToMin) {
                return min;
            }
            return (int) Math.round(value * difference) + min;
        }


    }
}
