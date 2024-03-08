package traben.entity_features.config.gui.builders;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EFOptionEnum<E extends Enum<E>> extends EFOptionValue<E> {


    private final EnumSliderWidget<E> widget;

    public EFOptionEnum(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue) {
        super(text, tooltip, getter, setter, defaultValue);
        widget = new EnumSliderWidget<>(getText(), getter.get(), getTooltip());
    }

    public EFOptionEnum(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue) {
        this(text, null, getter, setter, defaultValue);
    }

    @Override
    protected E getValueFromWidget() {
        return widget.enumValues[widget.getIndex()];
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

    public static class EnumSliderWidget<T extends Enum<?>> extends SliderWidget {
        private final T[] enumValues;
        private final String title;

        public EnumSliderWidget(final Text text, final T initialValue, final Tooltip tooltip) {
            super(0, 0, 20, 20, text, initialValue.ordinal() / (double) (initialValue.getDeclaringClass().getEnumConstants().length - 1));

            //noinspection unchecked
            this.enumValues = (T[]) initialValue.getDeclaringClass().getEnumConstants();
            this.title = text.getString() + ": ";
            updateMessage();
            setTooltip(tooltip);
        }

        private void setValue(T value) {
            this.value = value.ordinal() / (double) (enumValues.length - 1);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int index = getIndex();
            value = index / (double) (enumValues.length - 1);
            setMessage(Text.of(title + enumValues[getIndex()].toString()));
        }

        @Override
        protected void applyValue() {

        }

        private int getIndex() {
            return (int) Math.round(value * (enumValues.length - 1));
        }


    }
}
