package traben.entity_texture_features.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//inspired by puzzles custom gui code
public class ETFConfigScreenPrintOutcome extends ETFConfigScreen {
    private final boolean didSucceed;

    protected ETFConfigScreenPrintOutcome(Screen parent, boolean success) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".print_skin.result"), parent);
        didSucceed = success;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.PROCEED,
                (button) -> {
                    Objects.requireNonNull(client).setScreen(parent);
                }));
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        String[] strings =
                ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".player_skin_editor.print_skin.result." + (didSucceed ? "success" : "fail")
                ).getString().split("\n");
        List<Text> lines = new ArrayList<>();

        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }
        int i = 0;
        for (Text txt :
                lines) {
            drawCenteredTextWithShadow(matrices, textRenderer, txt.asOrderedText(), (int) (width * 0.5), (int) (height * 0.3) + i, 0xFFFFFF);
            i += txt.getString().isBlank() ? 5 : 10;
        }


    }

}
