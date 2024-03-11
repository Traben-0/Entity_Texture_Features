package traben.entity_features.config.gui.options;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

public abstract class EFOption {

    private final Text text;
    private final Tooltip tooltip;

    public EFOption(@Translatable String text, @Translatable String tooltip) {
        this.text = ETFVersionDifferenceHandler.getTextFromTranslation(text);
        this.tooltip = tooltip == null || tooltip.isBlank() ? null : Tooltip.of(ETFVersionDifferenceHandler.getTextFromTranslation(tooltip));
    }

    @SuppressWarnings("unused")
    public EFOption(@Translatable String text) {
        this(text, null);
    }

    public Text getText() {
        return text;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public abstract <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height);

    public EFOption setEnabled(boolean enabled) {
        var widget = getWidget(0, 0, 0, 0);
        if (widget instanceof ClickableWidget w) {
            w.active = enabled;
        }
        return this;
    }

    abstract boolean hasChangedFromInitial();

    abstract boolean saveValuesToConfig();

    abstract void setValuesToDefault();

    abstract void resetValuesToInitial();

    public static class Empty extends EFOption {
        public final static String CHANGED_COLOR = "§a";

        @SuppressWarnings("unused")
        public Empty() {
            //noinspection NoTranslation
            super("", null);
        }

        @Override
        public <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height) {
            return null;
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

}
