package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

public class TConfigScreen extends Screen {
    private final boolean showBackButton;
    protected Screen parent;

    protected Runnable resetDefaultValuesRunnable = null;
    protected Runnable undoChangesRunnable = null;

    protected TConfigScreen(@Translatable final String title, Screen parent, boolean showBackButton) {
        super(Component.translatable(title));
        this.parent = parent;
        this.showBackButton = showBackButton;
    }

    protected Component getBackButtonText() {
        return CommonComponents.GUI_BACK;
    }


    @Override
    protected void init() {
        if (showBackButton) this.addRenderableWidget(Button.builder(
                        getBackButtonText(),
                        (button) -> onClose())
                .bounds((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20)
                .build());

        if (resetDefaultValuesRunnable != null) {
            this.addRenderableWidget(Button.builder(
                    ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                    (button) -> {
                        resetDefaultValuesRunnable.run();
                        rebuildWidgets();
                    }).bounds((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        }
        if (undoChangesRunnable != null) {
            this.addRenderableWidget(Button.builder(
                    Component.nullToEmpty("Undo changes"),
                    (button) -> {
                        undoChangesRunnable.run();
                        rebuildWidgets();
                    }).bounds((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());
        }

    }

    @Override
    public void render(final GuiGraphics context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(font, title, width / 2, 15, 0xFFFFFF);

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
