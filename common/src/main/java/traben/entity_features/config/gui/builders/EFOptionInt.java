package traben.entity_features.config.gui.builders;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EFOptionInt extends EFOptionValue<Integer> {


    private final IntSliderWidget widget;

    public EFOptionInt(@Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max, boolean isMinOff, boolean isMaxOff) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new IntSliderWidget(getText(), getter.get(), getTooltip(), min, max, isMinOff, isMaxOff);
    }

    public EFOptionInt(@Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new IntSliderWidget(getText(), getter.get(), getTooltip(), min, max, false, false);
    }

    public EFOptionInt(@Translatable String text, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max, boolean isMinOff, boolean isMaxOff) {
        this(text, null, getter, setter, defaultValue, min, max, isMinOff, isMaxOff);
    }

    public EFOptionInt(@Translatable String text, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max) {
        this(text, null, getter, setter, defaultValue, min, max, false, false);
    }

    @Override
    protected Integer getValueFromWidget() {
        return widget.getValueRoundedToIntBetweenMinMax();
    }

    @Override
    public <T extends Element & Drawable & Selectable> T getWidget(final int x, final int y, final int width, final int height) {
        widget.setDimensionsAndPosition(width, height, x, y);
        //noinspection unchecked
        return (T) widget;
    }

    @Override
    void setWidgetToDefaultValue() {
        widget.setValue(defaultValue);
    }

    @Override
    void resetWidgetToInitialValue() {
        widget.setValue(getter.get());
    }

    public static class IntSliderWidget extends SliderWidget {
        private final int max;
        private final int min;
        private final String title;

        private final boolean isMinOff;
        private final boolean isMaxOff;

        public IntSliderWidget(final Text text,
                               final int initialValue, final Tooltip tooltip, int min, int max, boolean isMinOff, boolean isMaxOff) {
            super(0, 0, 20, 20, text, 0);
            this.min = min;
            this.max = max;
            this.isMinOff = isMinOff;
            this.isMaxOff = isMaxOff;

            this.title = text.getString() + ": ";
            setValue(initialValue);
            updateMessage();
            setTooltip(tooltip);
        }

        private boolean isOff() {
            if (isMinOff && value == 0) {
                return true;
            }
            return isMaxOff && value == 1;
        }

        private void setValue(int intIndex) {
            this.value = (MathHelper.clamp(intIndex, min, max) - min) / (double) (max - min);
            //snapValueToNearestIndex();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            snapValueToNearestIndex();
            setMessage(Text.of(title + (isOff() ? ScreenTexts.OFF.getString() : getValueRoundedToIntBetweenMinMax())));
        }

        @Override
        protected void applyValue() {
            //ignored
        }

        private void snapValueToNearestIndex() {
            value = (int) Math.round(value * (max - min)) / (double) (max - min);
        }

        //snaps the double value to the nearest index
        public int getValueRoundedToIntBetweenMinMax() {
            if (isOff()) {
                return 0;
            }
            return (int) Math.round(value * (max - min)) + min;
        }


    }
}
