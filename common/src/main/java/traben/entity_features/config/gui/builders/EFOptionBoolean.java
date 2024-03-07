package traben.entity_features.config.gui.builders;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EFOptionBoolean extends EFOptionValue<Boolean>{

    private final BooleanButtonWidget widget;

    public EFOptionBoolean(String text, @Nullable String tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue){
        super(text, tooltip, getter, setter, defaultValue);
        widget = new BooleanButtonWidget(0, 0, 20, 20, getText().getString(), getter.get(), getTooltip());
    }


    @Override
    protected Boolean getValueFromWidget() {
        return widget.value;
    }

    @Override
    public <T extends Element & Drawable & Selectable> T  getWidget(final int x, final int y, final int width, final int height) {
        widget.setDimensionsAndPosition(width, height, x, y);
        //noinspection unchecked
        return (T) widget;
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

    private static class BooleanButtonWidget extends ButtonWidget {
        private final String title;
        private boolean value;

        public BooleanButtonWidget(final int x, final int y, final int width, final int height, final String text,
                                   final boolean initialValue, final Tooltip tooltip) {
            super(x, y, width, height, Text.of(""), null,DEFAULT_NARRATION_SUPPLIER);
            this.value = initialValue;
            this.title = text + ": ";
            updateMessage();
            setTooltip(tooltip);
        }

        private void updateMessage() {
            setMessage(Text.of(title + (value ? ScreenTexts.ON.getString() : ScreenTexts.OFF.getString())));
        }

        @Override
        public void onPress() {
            value = !value;
            updateMessage();
        }
    }
}
