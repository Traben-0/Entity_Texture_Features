package traben.tconfig.gui.entries;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETF;
import traben.tconfig.gui.TConfigEntryListWidget;

public abstract class TConfigEntry extends TConfigEntryListWidget.TConfigEntryForList {

    public final static String CHANGED_COLOR = "§a";
    private final Component text;
    private final Tooltip tooltip;

    public TConfigEntry(@Translatable String text, @Translatable String tooltip) {
        this.text = ETF.getTextFromTranslation(text);
        this.tooltip = tooltip == null || tooltip.isBlank() ? null : Tooltip.create(ETF.getTextFromTranslation(tooltip));
    }

    @SuppressWarnings("unused")
    public TConfigEntry(@Translatable String text) {
        this(text, null);
    }

    public Component getText() {
        return text;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }


    public TConfigEntry setEnabled(boolean enabled) {
        var widget = getWidget(0, 0, 0, 0);
        widget.active = enabled;
        return this;
    }

    abstract boolean hasChangedFromInitial();

    abstract boolean saveValuesToConfig();

    abstract void setValuesToDefault();

    abstract void resetValuesToInitial();

    public static class Empty extends TConfigEntry {

        @SuppressWarnings("unused")
        public Empty() {
            //noinspection NoTranslation
            super("", null);
        }

        @Override
        public AbstractWidget getWidget(int x, int y, int width, int height) {
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
