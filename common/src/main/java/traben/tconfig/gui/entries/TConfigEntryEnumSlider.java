package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

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


    public class EnumSliderWidget<T extends Enum<?>> extends AbstractSliderButton {
        private final T[] enumValues;
        private final String title;

        public EnumSliderWidget(final Component text, final T initialValue, final Tooltip tooltip, Class<T> enumClass) {
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
            setMessage(Component.nullToEmpty(title + (value2 != getter.get() ? CHANGED_COLOR : "") + (value2 == null ? "---" : value2)));
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
