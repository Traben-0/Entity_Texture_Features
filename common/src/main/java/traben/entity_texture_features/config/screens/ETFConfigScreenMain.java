package traben.entity_texture_features.config.screens;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.tconfig.gui.TConfigScreenMain;
import traben.tconfig.gui.entries.TConfigEntryCategory;

import java.util.List;
import java.util.Objects;

import static traben.entity_texture_features.ETF.MOD_ID;
import static traben.entity_texture_features.config.screens.skin.ETFScreenOldCompat.renderGUITexture;

public class ETFConfigScreenMain extends TConfigScreenMain {

    final ObjectOpenHashSet<ETFConfigWarning> warningsFound = new ObjectOpenHashSet<>();

//     ETFConfigScreenWarnings warningsScreen;
//    final ETFConfigScreenSkinSettings playerSkinSettingsScreen = new ETFConfigScreenSkinSettings(this);
//    final ETFConfigScreenRandomSettings randomSettingsScreen = new ETFConfigScreenRandomSettings(this);
//    final ETFConfigScreenEmissiveSettings emissiveSettingsScreen = new ETFConfigScreenEmissiveSettings(this);
//    final ETFConfigScreenBlinkSettings blinkSettingsScreen = new ETFConfigScreenBlinkSettings(this);
//    final ETFConfigScreenDebugSettings debugSettingsScreen = new ETFConfigScreenDebugSettings(this);
//    final ETFConfigScreenGeneralSettings generalSettingsScreen = new ETFConfigScreenGeneralSettings(this);
    boolean shownWarning = false;
    int warningCount = 0;
//    private long timer = 0;
//    private LivingEntity livingEntity = null;

    public ETFConfigScreenMain(Screen parent) {
        super("config.entity_features", parent, ETF.configHandlers, List.of(
                new TConfigEntryCategory("config.entity_features.textures_main"),
                new TConfigEntryCategory("config.entity_features.models_main").setEmptyTooltip("config.entity_features.empty_emf"),
                new TConfigEntryCategory("config.entity_features.sounds_main").setEmptyTooltip("config.entity_features.empty_esf"),
                new TConfigEntryCategory("config.entity_features.general_settings.title"),
                new TConfigEntryCategory("config.entity_features.per_entity_settings")
        ));
        for (ETFConfigWarning warning :
                ETFConfigWarnings.getRegisteredWarnings()) {
            if (warning.isConditionMet()) {
                shownWarning = true;
                warningCount++;
                warningsFound.add(warning);
            }
        }
    }




//    public static void drawEntity(MatrixStack context, float x, float y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
//        context.push();
//        context.translate(x, y, 150.0);
//        context.multiplyPositionMatrix((new Matrix4f()).scaling((float) size, (float) size, (float) (-size)));
//        context.multiply(quaternionf);
//        DiffuseLighting.method_34742();
//        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
//        if (quaternionf2 != null) {
//            quaternionf2.conjugate();
//            entityRenderDispatcher.setRotation(quaternionf2);
//        }
//
//        entityRenderDispatcher.setRenderShadows(false);
//        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
//        //noinspection deprecation
//        RenderSystem.runAsFancy(() ->
//                entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context, immediate, 15728880));
//        immediate.draw();
//        entityRenderDispatcher.setRenderShadows(true);
//        context.pop();
//        DiffuseLighting.enableGuiDepthLighting();
//    }

    @Override
    protected void init() {
        super.init();
        if (shownWarning) {
            this.addDrawableChild(new ButtonWidget((int) (this.width * 0.1), (int) (this.height * 0.1) - 15, (int) (this.width * 0.2), 20,
                    ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_features.warnings_main"),
                    (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenWarnings(this, warningsFound))));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        renderGUITexture(new Identifier(MOD_ID + ":textures/gui/icon.png"), (this.width * 0.3) - 64, (this.height * 0.5) - 64, (this.width * 0.3) + 64, (this.height * 0.5) + 64);
//        if (shownWarning) {
//            drawCenteredTextWithShadow(matrices, textRenderer,
//                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".warnings_main").getString() + warningCount).asOrderedText(),
//                    (int) (width * 0.2), (int) (height * 0.1) - 9, 11546150);
//        }

    }


}
