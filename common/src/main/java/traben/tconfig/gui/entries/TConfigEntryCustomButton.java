package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;

public class TConfigEntryCustomButton extends TConfigEntry {


    private final ButtonWidget.PressAction button;

    public TConfigEntryCustomButton(@Translatable final String text, @Translatable final String tooltip, ButtonWidget.PressAction button) {
        super(text, tooltip);
        this.button = button;
    }

    @SuppressWarnings("unused")
    public TConfigEntryCustomButton(@Translatable final String text, ButtonWidget.PressAction button) {
        this(text, null, button);
    }

    @Override
    public <T extends Element & Drawable & Selectable> T getWidget(final int x, final int y, final int width, final int height) {
        //noinspection unchecked
        return (T) ButtonWidget.builder(getText(), button).dimensions(x, y, width, height).tooltip(getTooltip()).build();
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
