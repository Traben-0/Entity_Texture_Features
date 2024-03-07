package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import traben.entity_features.config.gui.EFScreen;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.mixin.accessor.TooltipAccessor;

import java.util.ArrayList;
import java.util.List;

public abstract class ETFScreenOldCompat extends EFScreen {

    protected ETFScreenOldCompat(final String title, final Screen parent, final boolean showBackButton) {
        super(title, parent, showBackButton);
    }

    public ButtonWidget getETFButton(int x, int y, int width, @SuppressWarnings("SameParameterValue") int height, Text buttonText, ButtonWidget.PressAction onPress) {
        return getETFButton(x, y, width, height, buttonText, onPress, Text.of(""));
    }

    public static void renderGUITexture(Identifier texture, double x1, double y1, double x2, double y2) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(x1, y2, 0.0).texture(0, 1/*(float)x1, (float)y2*heightYValue*/).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(x2, y2, 0.0).texture(1, 1/*(float)x2*widthXValue, (float)y2*heightYValue*/).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(x2, y1, 0.0).texture(1, 0/*(float)x2*widthXValue, (float)y1*/).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(x1, y1, 0.0).texture(0, 0/*(float)x1, (float)y1*/).color(255, 255, 255, 255).next();
        tessellator.draw();
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
//        String[] strings = toolTipText.getString().split("\n");
//        List<Text> lines = new ArrayList<>();
//        for (String str :
//                strings) {
//            lines.add(Text.of(str.strip()));
//        }

        if (tooltipIsEmpty) {
            //button with no tooltip
            return ButtonWidget.builder(buttonText, onPress).dimensions(x + nudgeLeftEdge, y, width, height).build();
        } else {
            //return ButtonWidget.builder(buttonText,onPress).dimensions(x+nudgeLeftEdge, y, width, height).tooltip(Tooltip.of(toolTipText)).build();
            //1.19.3 required only
            ///////////////////////////////////////

            Tooltip bob = Tooltip.of(toolTipText);
            if (!ETFVersionDifferenceHandler.isThisModLoaded("adaptive-tooltips")) {
                //split tooltip by our rules
                String[] strings = toolTipText.getString().split("\n");
                List<OrderedText> texts = new ArrayList<>();
                for (String str :
                        strings) {
                    texts.add(Text.of(str).asOrderedText());
                }

                //apply to tooltip object

                ((TooltipAccessor) bob).setLines(texts);
            }
            ////////////////////////////////////////
            //create button
            return ButtonWidget.builder(buttonText, onPress).dimensions(x + nudgeLeftEdge, y, width, height).tooltip(bob).build();


        }
    }

    public static String booleanAsOnOff(boolean bool) {
        return ScreenTexts.onOrOff(bool).getString();
    }
}
