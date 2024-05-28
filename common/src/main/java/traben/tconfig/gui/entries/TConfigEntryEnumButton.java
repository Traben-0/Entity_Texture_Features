package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class TConfigEntryEnumButton<E extends Enum<E>> extends TConfigEntryNullSafe<E> {


    private final EnumButtonWidget<E> widget;

    private boolean appendNullValue = false;

    public TConfigEntryEnumButton(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        super(text, tooltip, getter, setter, defaultValue);
        if (defaultValue == null) appendNullValue = true;
        widget = new EnumButtonWidget<>(getText(), getter.get(), getTooltip(), enumClass);
    }

    public TConfigEntryEnumButton(@Translatable String text, @Translatable String tooltip, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, tooltip, getter, setter, defaultValue, defaultValue.getDeclaringClass());
    }

    @SuppressWarnings("unused")
    public TConfigEntryEnumButton(@Translatable String text, Supplier<E> getter, Consumer<E> setter, E defaultValue, Class<E> enumClass) {
        this(text, null, getter, setter, defaultValue, enumClass);
    }

    @SuppressWarnings("unused")
    public TConfigEntryEnumButton(@Translatable String text, Supplier<E> getter, Consumer<E> setter, @NotNull E defaultValue) {
        this(text, null, getter, setter, defaultValue, defaultValue.getDeclaringClass());
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


    public class EnumButtonWidget<T extends Enum<?>> extends Button {
        private final T[] enumValues;
        private final String title;
        private int index;

        public EnumButtonWidget(final Component text, final T initialValue, final Tooltip tooltip, Class<T> enumClass) {
            //super(0, 0, 20, 20, text, initialValue.ordinal() / (double) (initialValue.getDeclaringClass().getEnumConstants().length - 1));
            super(0, 0, 20, 20, text, (button) -> {
            }, Button.DEFAULT_NARRATION);

            this.enumValues = enumClass.getEnumConstants();
            this.title = text.getString() + ": ";
            setTooltip(tooltip);
            setValue(initialValue);
        }

        public int getIndex() {
            return index;
        }

        private int getChoiceCount() {
            return enumValues.length - (appendNullValue ? 0 : 1);
        }

        @Nullable
        private T getValue() {
            if (index >= enumValues.length) return null;
            return enumValues[index];
        }

        private void setValue(T value) {
            this.index = value == null ? enumValues.length : value.ordinal();
            updateMessage();
        }

        protected void updateMessage() {
            T value = getValue();
            setMessage(Component.nullToEmpty(title + (value != getter.get() ? CHANGED_COLOR : "") +
                    (value == null ? "---" : value)));

        }

        @Override
        public void onPress() {
            index++;
            if (index > getChoiceCount()) {
                index = 0;
            }
            updateMessage();
        }
    }
}
