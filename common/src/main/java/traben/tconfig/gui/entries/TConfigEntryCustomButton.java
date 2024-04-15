package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;

public class TConfigEntryCustomButton extends TConfigEntry {


    private final ButtonWidget button;

    public TConfigEntryCustomButton(@Translatable final String text, @Translatable final String tooltip, ButtonWidget.PressAction action) {
        super(text, tooltip);
        this.button = ButtonWidget.builder(getText(), action).dimensions(0, 0, 0, 0).tooltip(getTooltip()).build();
    }

    @SuppressWarnings("unused")
    public TConfigEntryCustomButton(@Translatable final String text, ButtonWidget.PressAction button) {
        this(text, null, button);
    }

    @Override
    public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
        //button.setDimensionsAndPosition(width, height, x, y);
        button.setX(x);
        button.setY(y);
        button.height = (height);

        button.setWidth(width);
        return button;
    }

    @Override
    boolean saveValuesToConfig() {
        return false;
    }

    @Override
    void setValuesToDefault() {
    }

    @Override
    void resetValuesToInitial() {
    }

    @Override
    boolean hasChangedFromInitial() {
        return false;
    }
}
