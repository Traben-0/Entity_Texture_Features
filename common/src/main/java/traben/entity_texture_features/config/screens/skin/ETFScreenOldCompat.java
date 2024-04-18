package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import traben.tconfig.gui.TConfigScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class ETFScreenOldCompat extends TConfigScreen {

    @SuppressWarnings("SameParameterValue")
    protected ETFScreenOldCompat(final String title, final Screen parent, @SuppressWarnings("SameParameterValue") final boolean showBackButton) {
        super(title, parent, showBackButton);
    }

    public static void renderGUITexture(Identifier texture, double x1, double y1, double x2, double y2) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(x1, y2, 0.0).texture(0, 1/*(float)x1, (float)y2*heightYValue*/).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(x2, y2, 0.0).texture(1, 1/*(float)x2*widthXValue, (float)y2*heightYValue*/).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(x2, y1, 0.0).texture(1, 0/*(float)x2*widthXValue, (float)y1*/).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(x1, y1, 0.0).texture(0, 0/*(float)x1, (float)y1*/).color(255, 255, 255, 255).next();
        tessellator.draw();
    }

    public static void renderBackgroundTexture(int vOffset, Identifier texture, int height, int width) {
        renderBackgroundTexture(vOffset, texture, height, width, 0);
    }

    public static void renderBackgroundTexture(int vOffset, Identifier texture, int height, int width, int minHeight) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, height, 0.0).texture(0.0F, (float) height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(width, height, 0.0).texture((float) width / 32.0F, (float) height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(width, minHeight, 0.0).texture((float) width / 32.0F, (float) minHeight / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0, minHeight, 0.0).texture(0.0F, (float) minHeight / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        tessellator.draw();
    }

//    @Override
//    public void renderBackgroundTexture(final int vOffset) {
//        super.renderBackgroundTexture(vOffset);
//    }

//    @Override
//    public void renderBackgroundTexture(final MatrixStack context) {
//        super.renderBackgroundTexture(context);
//    }

//        renderBackgroundTexture(0, new Identifier("textures/gui/options_background.png"), (int) (height * 0.15), width);
//        renderBackgroundTexture(0, new Identifier("textures/gui/options_background.png"), height, width, (int) (height * 0.85));
//        fillGradient(matrices, 0, (int) (height * 0.15), width, (int) (height * 0.85), -1072689136, -804253680);
//
//        drawCenteredTextWithShadow(matrices, textRenderer, title.asOrderedText(), width / 2, 15, 0xFFFFFF);

//        renderBackgroundTexture(0, new Identifier("textures/gui/options_background.png"), (int) (height * 0.15), width);
//        renderBackgroundTexture(0, new Identifier("textures/gui/options_background.png"), height, width, (int) (height * 0.85));
//        this.fillGradient(matrices, 0, (int) (height * 0.15), width, (int) (height * 0.85), -1072689136, -804253680);
//
//        drawCenteredText(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);

//    @Override
//    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        // ETFUtils2.renderBackgroundTexture(0,new Identifier("textures/block/deepslate_tiles.png"),this.height,this.width);
//        backgroundCube.render(MinecraftClient.getInstance().getLastFrameDuration() * 1.5f, 1);
////        backgroundCube.render((float) 0.5, 1);
//
//        renderBackgroundTexture(0, new Identifier("textures/gui/options_background.png"), (int) (height * 0.15), width);
//        renderBackgroundTexture(0, new Identifier("textures/gui/options_background.png"), height, width, (int) (height * 0.85));
//        fillGradient(matrices, 0, (int) (height * 0.15), width, (int) (height * 0.85), -1072689136, -804253680);
//
//        drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);
//
//        super.render(matrices, mouseX, mouseY, delta);
//    }

    public static String booleanAsOnOff(boolean bool) {
        return ScreenTexts.onOrOff(bool).getString();
    }

    public ButtonWidget getETFButton(int x, int y, int width, @SuppressWarnings("SameParameterValue") int height, Text buttonText, ButtonWidget.PressAction onPress) {
        return getETFButton(x, y, width, height, buttonText, onPress, Text.of(""));
    }

    public ButtonWidget getETFButton(int x, int y, int width, int height, Text buttonText, ButtonWidget.PressAction onPress, Text toolTipText) {
        int nudgeLeftEdge;
        if (width > 384) {
            nudgeLeftEdge = (width - 384) / 2;
            width = 384;
        } else {
            nudgeLeftEdge = 0;
        }
//        if (width > 800)
//            height=80;
//        if (width > 1600)
//            height=16;
        boolean tooltipIsEmpty = toolTipText.getString().isBlank();
        String[] strings = toolTipText.getString().split("\n");
        List<Text> lines = new ArrayList<>();
        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }

        return new ButtonWidget(x + nudgeLeftEdge, y, width, height,
                buttonText,
                onPress,
                (buttonWidget, matrices, mouseX, mouseY) -> {
                    if (buttonWidget.isHovered() && !tooltipIsEmpty) {
                        this.renderTooltip(matrices, lines, mouseX, mouseY);
                    }
                });
    }
}
