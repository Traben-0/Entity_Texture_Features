package traben.entity_features.config.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import traben.entity_features.config.gui.builders.EFOption;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

public class EFOptionsScreen extends EFScreen{

    private final EFOption[] options;
    private final Runnable resetValuesToDefault;
    private final Runnable undoChanges;
    public EFOptionsScreen(final String title, Screen parent, EFOption[] options, Runnable resetValuesToDefault, Runnable undoChanges) {
        super(title, parent,true);
        this.options = options;
        this.parent = parent;
        this.resetValuesToDefault = resetValuesToDefault;
        this.undoChanges = undoChanges;
    }

    @Override
    protected void init() {
        super.init();
        if (options.length > 14) {
            addColumn((int) (this.width * 0.15), (int) (this.height * 0.2), (int) (this.width * 0.2),20,0);
            addColumn((int) (this.width * 0.4), (int) (this.height * 0.2), (int) (this.width * 0.2),20,7);
            addColumn((int) (this.width * 0.65), (int) (this.height * 0.2), (int) (this.width * 0.2),20,14);
        } else if (options.length > 7) {
            addColumn((int) (this.width * 0.15), (int) (this.height * 0.2), (int) (this.width * 0.3),20,0);
            addColumn((int) (this.width * 0.55), (int) (this.height * 0.2), (int) (this.width * 0.3),20,7);
        } else {
            addColumn((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6),20,0);
        }


        this.addDrawableChild(ButtonWidget.builder(
                ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                (button) -> {
                    resetValuesToDefault.run();
                    clearAndInit();
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                Text.of("Undo changes"),
                (button) -> {
                    undoChanges.run();
                    clearAndInit();
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());
    }

    private void addColumn(int x, int y, int width, int height, int startIndex) {
        for (int i = startIndex; i < options.length; i++) {
            EFOption option = options[i];
            var widget = option.getWidget(x, y + ((i-startIndex) * 24), width, height);
            if(widget != null)
                this.addDrawableChild(widget);
        }
    }
}
