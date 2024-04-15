package traben.tconfig.gui;

import com.demonwav.mcdev.annotations.Translatable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
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
        if (showBackButton) this.addDrawableChild(new ButtonWidget(
                (int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                        getBackButtonText(),
                        (button) -> close())
                );

        if (resetDefaultValuesRunnable != null) {
            this.addDrawableChild(new ButtonWidget(
                    (int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                    ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                    (button) -> {
                        resetDefaultValuesRunnable.run();
                        clearAndInit();
                    }));
        }
        if (undoChangesRunnable != null) {
            this.addDrawableChild(new ButtonWidget(
                    (int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                    Text.of("Undo changes"),
                    (button) -> {
                        undoChangesRunnable.run();
                        clearAndInit();
                    }));
        }

    }

    @Override
    public void render(final MatrixStack context, final int mouseX, final int mouseY, final float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawCenteredTextWithShadow(context,textRenderer, title.asOrderedText(), width / 2, 15, 0xFFFFFF);

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

//    @Override
//    public void renderBackground(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
//        if (this.client.world != null) {
////            this.renderInGameBackground(context);
//            renderBackgroundTexture(context);
//        } else {
//            super.renderBackgroundTexture(context);
//            this.renderInGameBackground(context);
//            renderBackgroundTexture(context);
//        }
//    }

    protected boolean allowTransparentBackground() {
        return true;
    }

    @Override
    public void renderBackground(MatrixStack context) {
        super.renderBackground(context);
        context.push();

        int topy = (int) (height * 0.15);
        int bottomy = (int) (height * 0.85);

        context.translate(0, 0, -100);
        if (MinecraftClient.getInstance().world == null || !allowTransparentBackground()) {

            RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor(0.15F, 0.15F, 0.15F, 1.0F);
            drawTexture(context, 0, 0, 0, 0.0F, 0.0F, this.width, height, 32, 32);
        } else {
            fillGradient(context,0, 0, this.width, this.height, -1072689136, -804253680);
        }

        fillGradient(context, 0, topy, this.width, topy + 4, -16777216, 0, 0);
        fillGradient(context, 0, bottomy - 4, this.width, bottomy, 0, -16777216, 0);

        RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
        drawTexture(context, 0, 0, 0, 0.0F, 0.0F, this.width, topy, 32, 32);
        drawTexture(context, 0, bottomy, 0, 0.0F, 0.0F, this.width, topy, 32, 32);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.pop();
    }

//    @Override
//    public void renderInGameBackground(final DrawContext context) {
//    }


    @Override
    public void renderBackgroundTexture(final int vOffset) {
//        super.renderBackgroundTexture(vOffset);
    }

//    @Override
//    public void renderBackgroundTexture(MatrixStack context) {
//    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
