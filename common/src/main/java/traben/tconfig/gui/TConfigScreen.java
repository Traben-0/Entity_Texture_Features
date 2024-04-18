package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

public class TConfigScreen extends Screen {
    private final boolean showBackButton;
    protected Screen parent;

    protected Runnable resetDefaultValuesRunnable = null;
    protected Runnable undoChangesRunnable = null;

    protected TConfigScreen(@Translatable final String title, Screen parent, boolean showBackButton) {
        super(Text.translatable(title));
        this.parent = parent;
        this.showBackButton = showBackButton;
    }

    protected Text getBackButtonText() {
        return ScreenTexts.BACK;
    }


    @Override
    protected void init() {
        if (showBackButton) this.addDrawableChild(ButtonWidget.builder(
                        getBackButtonText(),
                        (button) -> close())
                .dimensions((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20)
                .build());

        if (resetDefaultValuesRunnable != null) {
            this.addDrawableChild(ButtonWidget.builder(
                    ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                    (button) -> {
                        resetDefaultValuesRunnable.run();
                        clearAndInit();
                    }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        }
        if (undoChangesRunnable != null) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.of("Undo changes"),
                    (button) -> {
                        undoChangesRunnable.run();
                        clearAndInit();
                    }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());
        }

    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 15, 0xFFFFFF);

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
