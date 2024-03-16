package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TConfigEntryEnumSlider<E extends Enum<E>> extends TConfigEntryNullSafe<E> {


    private final EnumSliderWidget<E> widget;

    private boolean appendNullValue = false;

    public TConfigEntryEnumSlider(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        super(text, tooltip, getter, setter, defaultValue);
        if (defaultValue == null) appendNullValue = true;
        widget = new EnumSliderWidget<>(getText(), getter.get(), getTooltip(), enumClass);
    }

    public TConfigEntryEnumSlider(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, tooltip, getter, setter, defaultValue, defaultValue.getDeclaringClass());
    }

    @SuppressWarnings("unused")
    public TConfigEntryEnumSlider(@Translatable String text, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, null, getter, setter, defaultValue, defaultValue.getDeclaringClass());
    }

    @SuppressWarnings("unused")
    public TConfigEntryEnumSlider(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        this(text, null, getter, setter, defaultValue, enumClass);
    }

    @Override
    public TConfigEntryNullSafe<E> allowNullValue() {
        appendNullValue = true;
        return this;
    }

    @Override
    protected E getValueFromWidget() {
        return widget.getValue();
    }

    @Override
    public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
        widget.setDimensionsAndPosition(width, height, x, y);
        //noinspection unchecked
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


    public class EnumSliderWidget<T extends Enum<?>> extends SliderWidget {
        private final T[] enumValues;
        private final String title;

        public EnumSliderWidget(final Text text, final T initialValue, final Tooltip tooltip, Class<T> enumClass) {
            super(0, 0, 20, 20, text,
                    1);

            this.enumValues = enumClass.getEnumConstants();
            this.title = text.getString() + ": ";
            setTooltip(tooltip);
            setValue(initialValue);
        }

        @Nullable
        private T getValue() {
            if (getIndex() >= enumValues.length) return null;
            return enumValues[getIndex()];
        }

        private void setValue(T value) {
            this.value = value == null ? 1 : (double) value.ordinal() / getChoiceCount();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            value = getIndex() / (double) getChoiceCount();

            T value2 = getValue();
            setMessage(Text.of(title + (value2 != getter.get() ? Empty.CHANGED_COLOR : "") + (value2 == null ? "---" : value2)));
        }

        @Override
        protected void applyValue() {

        }

        private int getChoiceCount() {
            return enumValues.length - (appendNullValue ? 0 : 1);
        }

        private int getIndex() {
            return (int) Math.round(value * getChoiceCount());
        }

    }
}
