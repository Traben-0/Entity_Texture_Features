package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TConfigEntryBoolean extends TConfigEntryValue<Boolean> {

    private final BooleanButtonWidget widget;


    public TConfigEntryBoolean(@Translatable String translationKey, @Translatable @Nullable String tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue) {
        super(translationKey, tooltip, getter, setter, defaultValue);
        widget = new BooleanButtonWidget(0, 0, 20, 20, getText().getString(), getter.get(), getTooltip());
    }

    @SuppressWarnings("unused")
    public TConfigEntryBoolean(@Translatable String translationKey, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue) {
        this(translationKey, null, getter, setter, defaultValue);
    }

    @SuppressWarnings("unused")
    public TConfigEntryBoolean setType(Type type) {
        widget.type = type;
        return this;
    }


    @Override
    protected Boolean getValueFromWidget() {
        return widget.value;
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
        widget.value = defaultValue;
        widget.updateMessage();
    }

    @Override
    void resetWidgetToInitialValue() {
        widget.value = getter.get();
        widget.updateMessage();
    }

    public enum Type {
        ON_OFF(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF),
        @SuppressWarnings("unused") YES_NO(CommonComponents.GUI_YES, CommonComponents.GUI_NO);

        private final String t;
        private final String f;

        Type(Component t, Component f) {
            this.t = t.getString();
            this.f = f.getString();
        }

        public String get(boolean value) {
            return value ? t : f;
        }
    }

    private class BooleanButtonWidget extends Button {
        private final String title;
        private boolean value;


        private Type type = Type.ON_OFF;

        public BooleanButtonWidget(final int x, final int y, final int width, final int height, final String text,
                                   final boolean initialValue, final Tooltip tooltip) {
            super(x, y, width, height, Component.nullToEmpty(""), null, DEFAULT_NARRATION);
            this.value = initialValue;
            this.title = text + ": ";
            updateMessage();
            setTooltip(tooltip);
        }

        private void updateMessage() {
            setMessage(Component.nullToEmpty(title + (value != getter.get() ? CHANGED_COLOR : "") + type.get(value)));
        }

        @Override
        public void onPress() {
            value = !value;
            updateMessage();
        }
    }
}
