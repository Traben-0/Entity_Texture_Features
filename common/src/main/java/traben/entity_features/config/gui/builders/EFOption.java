package traben.entity_features.config.gui.builders;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

public abstract class EFOption {

    public Text getText() {
        return text;
    }

    private final Text text;

    public Tooltip getTooltip() {
        return tooltip;
    }

    public abstract <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height);

    abstract boolean saveValuesToConfig();

    abstract void setValuesToDefault();
    abstract void resetValuesToInitial();

    private final Tooltip tooltip;

    public EFOption(String text, String tooltip){
        this.text = ETFVersionDifferenceHandler.getTextFromTranslation(text);
        this.tooltip = tooltip == null || tooltip.isBlank() ? null : Tooltip.of( ETFVersionDifferenceHandler.getTextFromTranslation(tooltip));
    }

    public static class Empty extends EFOption{
        public Empty() {
            super("", null);
        }

        @Override
        public <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height) {
            return null;
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
