package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

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


    public class EnumButtonWidget<T extends Enum<?>> extends ButtonWidget {
        private final T[] enumValues;
        private final String title;
        private int index;

        public EnumButtonWidget(final Text text, final T initialValue, final Tooltip tooltip, Class<T> enumClass) {
            //super(0, 0, 20, 20, text, initialValue.ordinal() / (double) (initialValue.getDeclaringClass().getEnumConstants().length - 1));
            super(0, 0, 20, 20, text, (button) -> {
            }, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);

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
            setMessage(Text.of(title + (value != getter.get() ? Empty.CHANGED_COLOR : "") +
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
