package traben.entity_features.config.gui.builders;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;

public class EFOptionCustomButton extends EFOption {


    @Override
    public <T extends Element & Drawable & Selectable> T  getWidget(final int x, final int y, final int width, final int height) {
        //noinspection unchecked
        return (T) ButtonWidget.builder(getText(),button).dimensions(x,y,width,height).tooltip(getTooltip()).build();
    }

    @Override
    boolean saveValuesToConfig() {
        return false;
    }


    private final ButtonWidget.PressAction button;

    public EFOptionCustomButton(final String text, final String tooltip, ButtonWidget.PressAction button) {
        super(text, tooltip);
        this.button = button;
    }


    @Override
    void setValuesToDefault() {
    }

    @Override
    void resetValuesToInitial() {
    }
}
