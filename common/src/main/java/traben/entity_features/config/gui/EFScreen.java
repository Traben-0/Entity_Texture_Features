package traben.entity_features.config.gui;

import com.demonwav.mcdev.annotations.Translatable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class EFScreen extends Screen {
    private final boolean showBackButton;
    protected Screen parent;

    protected EFScreen(@Translatable final String title, Screen parent, boolean showBackButton) {
        super(Text.translatable(title));
        this.parent = parent;
        this.showBackButton = showBackButton;
    }


    @Override
    protected void init() {
        if (showBackButton) this.addDrawableChild(ButtonWidget.builder(
                        ScreenTexts.BACK,
                        (button) -> close())
                .dimensions((int) (this.width * 0.7), (int) (this.height * 0.9), (int) (this.width * 0.2), 20)
                .build());

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
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.getMatrices().push();

        context.getMatrices().translate(0, 0, -100);
        if (MinecraftClient.getInstance().world == null || !allowTransparentBackground()) {
            context.setShaderColor(0.15F, 0.15F, 0.15F, 1.0F);
            context.drawTexture(OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0.0F, 0.0F, this.width, height, 32, 32);
        } else {
            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        context.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
        context.drawTexture(OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0.0F, 0.0F, this.width, (int) (height * 0.15), 32, 32);
        context.drawTexture(OPTIONS_BACKGROUND_TEXTURE, 0, (int) (height * 0.85), 0, 0.0F, 0.0F, this.width, (int) (height * 0.15), 32, 32);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.getMatrices().pop();
    }

    @Override
    public void renderInGameBackground(final DrawContext context) {
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
