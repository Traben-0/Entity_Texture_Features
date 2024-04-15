package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TConfigEntryInt extends TConfigEntryValue<Integer> {


    private final IntSliderWidget widget;
    private boolean modifiesOffMaxToMin = true;

    public TConfigEntryInt(@Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max, boolean isMinOff, boolean isMaxOff) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new IntSliderWidget(getText(), getter.get(), getTooltipForElement(tooltip), min, max, isMinOff, isMaxOff);
    }

    public TConfigEntryInt(@Translatable String text, @Translatable String tooltip, Supplier<Integer> getter, Consumer<Integer> setter, int defaultValue, int min, int max) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new IntSliderWidget(getText(), getter.get(), getTooltipForElement(tooltip), min, max, false, false);
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
    public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
//        widget.setDimensionsAndPosition(width, height, x, y);
        widget.x=(x);
        widget.y=(y);
        widget.height = (height);
        widget.setWidth(width);
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

    public class IntSliderWidget extends SliderWidget {
        private final int max;
        private final int min;
        private final String title;

        private final boolean isMinOff;
        private final boolean isMaxOff;
        private final int difference;

        private final ElementTooltipSupplier tooltip;


        public IntSliderWidget(final Text text,
                               final int initialValue, final ElementTooltipSupplier tooltip, int min, int max, boolean isMinOff, boolean isMaxOff) {
            super(0, 0, 20, 20, text, 0);
            this.min = min;
            this.max = max;
            this.isMinOff = isMinOff;
            this.isMaxOff = isMaxOff;
            this.difference = max - min;

            this.title = text.getString() + ": ";
            setValue(initialValue);
            this.tooltip =(tooltip);
        }

        private boolean isOff() {
            if (isMinOff && value == 0) {
                return true;
            }
            return isMaxOff && value == 1;
        }

        @Override
        public void renderTooltip(final MatrixStack matrices, final int mouseX, final int mouseY) {
            if (tooltip != null)
                tooltip.onTooltip(this, matrices, mouseX, mouseY);
        }

        private void setValue(int intIndex) {
            this.value = (MathHelper.clamp(intIndex, min, max) - min) / (double) difference;
//            snapValueToNearestIndex();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            snapValueToNearestIndex();
            setMessage(Text.of(title + (getValueRoundedToIntBetweenMinMax() != getter.get() ? TConfigEntry.CHANGED_COLOR : "") + (isOff() ? ScreenTexts.OFF.getString() : getValueRoundedToIntBetweenMinMax())));
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
