package traben.tconfig.gui.entries;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;

public class TConfigEntryText extends TConfigEntry {

    private final TextWidget widget;

    public TConfigEntryText(final String text) {
        super(text, null);
        widget = new TextWidget(getText(), MinecraftClient.getInstance().textRenderer);
    }

    @Override
    public ClickableWidget getWidget(final int x, final int y, final int width, final int height) {
        widget.setDimensionsAndPosition(width, height, x, y);
        return widget;
    }

    @Override
    boolean hasChangedFromInitial() {
        return false;
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
}
