package traben.entity_features.config.gui;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import traben.entity_features.config.EFConfig;
import traben.entity_features.config.EFConfigHandler;
import traben.entity_features.config.EFConfigWarning;
import traben.entity_features.config.EFConfigWarnings;
import traben.entity_features.config.gui.builders.EFOption;
import traben.entity_features.config.gui.builders.EFOptionCategory;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.awt.*;
import java.util.List;
import java.util.*;

public class EFMainConfigScreen extends EFScreen {

    private static Set<EFConfigHandler<?>> configHandlers = null;
    final ObjectOpenHashSet<EFConfigWarning> warningsFound = new ObjectOpenHashSet<>();
    private final EFOptionCategory mainCategories;

    private final List<Identifier> modIcons;
    private final Random rand = new Random();
    boolean shownWarning = false;
    int warningCount = 0;
    private long timer = 0;
    private LivingEntity livingEntity = null;

    public EFMainConfigScreen(Screen parent) {
        super("config.entity_features", parent, true);
        mainCategories = new EFOptionCategory.Empty().add(
                new EFOptionCategory("config.entity_features.textures_main"),
                new EFOptionCategory("config.entity_features.models_main"),
                new EFOptionCategory("config.entity_features.sounds_main"),
                new EFOptionCategory("config.entity_features.general_settings.title")
        );


        modIcons = new ArrayList<>();
        for (EFConfigHandler<?> configHandler : configHandlers) {
            EFConfig config = configHandler.getConfig();
            for (EFOption value : config.getGUIOptions().getOptions().values()) {
                mainCategories.add(value);
            }
            modIcons.add(config.getModIcon());
        }


        for (EFConfigWarning warning :
                EFConfigWarnings.getRegisteredWarnings()) {
            if (warning.isConditionMet()) {
                shownWarning = true;
                warningCount++;
                warningsFound.add(warning);
            }
        }

    }

    public static void registerConfigHandler(EFConfigHandler<?> configHandler) {
        if (configHandlers == null) configHandlers = new ObjectArraySet<>();
        configHandlers.add(configHandler);
    }

    @Override
    public void close() {
        if (mainCategories.saveValuesToConfig()) {
            for (EFConfigHandler<?> configHandler : configHandlers) {
                configHandler.saveToFile();
            }
            MinecraftClient.getInstance().reloadResources();
        }
        super.close();
    }

    @Override
    protected void init() {
        super.init();
        addColumn((int) (this.width * 0.6), (int) (this.height * 0.2), (int) (this.width * 0.3), 20, 0);

        if (shownWarning) {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.entity_features.warnings_main"),
                            (button) -> Objects.requireNonNull(client).setScreen(new EFConfigScreenWarnings(this, warningsFound)))
                    .dimensions((int) (this.width * 0.1), (int) (this.height * 0.1) - 15, (int) (this.width * 0.2), 20
                    ).build());
        }

        this.addDrawableChild(ButtonWidget.builder(
                ETFVersionDifferenceHandler.getTextFromTranslation("dataPack.validation.reset"),
                (button) -> {
                    mainCategories.setValuesToDefault();
                    clearAndInit();
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.entity_features.undo"),
                (button) -> {
                    mainCategories.resetValuesToInitial();
                    clearAndInit();
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());

    }

    private void addColumn(int x, int y, int width, int height, int startIndex) {
        EFOption[] options = mainCategories.getOptions().values().toArray(new EFOption[0]);
        for (int i = startIndex; i < options.length; i++) {
            EFOption option = options[i];
            var widget = option.getWidget(x, y + ((i - startIndex) * 24), width, height);
            if (widget != null)
                this.addDrawableChild(widget);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        //draw mod icons in the top right corner of the screen
        // from left to right
        if (!modIcons.isEmpty()) {
            int ix = this.width - (modIcons.size() * 12);
            for (Identifier modIcon : modIcons) {
                context.drawTexture(modIcon, ix, 2, 0, 0, 10, 10);
                ix += 12;
            }
        }


        if (timer + 5000 < System.currentTimeMillis() && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getWorld() != null) {
            List<Entity> entityList = MinecraftClient.getInstance().player.getWorld().getOtherEntities(null, MinecraftClient.getInstance().player.getBoundingBox().expand(128));
            Entity entity = null;
            for (int i = 0; i < Math.min(entityList.size(), 24); i++) {
                entity = entityList.get(rand.nextInt(entityList.size()));
                if (entity instanceof LivingEntity) break;
            }
            if (entity instanceof LivingEntity) {
                livingEntity = (LivingEntity) entity;
                timer = System.currentTimeMillis();
            }
        }
        if (livingEntity != null && !livingEntity.isRemoved()) {
            int y = (int) (this.height * 0.75);
            if (livingEntity.getHeight() < 0.7) y -= (int) (this.height * 0.15);
            int x = (int) (this.width * 0.33);
            //float f = (float)Math.atan((double)(-mouseX / 40.0F));
            float g = (float) Math.atan(((-mouseY + this.height / 2f) / 40.0F));
            Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F).rotateY((float) (System.currentTimeMillis() / 1000d % (2 * Math.PI)));
            Quaternionf quaternionf2 = (new Quaternionf()).rotateX(-(g * 20.0F * 0.017453292F));
            quaternionf.mul(quaternionf2);
            double scale;
            double autoScale = (this.height * 0.4) / ((Math.max(1, Math.max(livingEntity.getHeight(), livingEntity.getWidth()))));
            if (livingEntity instanceof SquidEntity) {
                y -= (int) (this.height * 0.15);
                scale = autoScale * 0.5;
            } else if (livingEntity instanceof GuardianEntity || livingEntity instanceof SnifferEntity) {
                y -= (int) (this.height * 0.1);
                scale = autoScale * 0.7;
            } else if (livingEntity instanceof EnderDragonEntity) {
                y -= (int) (this.height * 0.15);
                scale = autoScale * 1.5;
            } else {
                scale = autoScale;
            }

            double scaleModify = Math.sin((System.currentTimeMillis() - timer) / 5000d * Math.PI) * 6;
            double scaleModify2 = Math.max(Math.min(Math.abs(scaleModify), 1d), 0);//clamp
            int modelHeight = (int) Math.min(scale * scaleModify2, (this.height * 0.4));

            context.getMatrices().push();
            //context.getMatrices().translate(0, 0, 100);
            InventoryScreen.drawEntity(context, x, y, modelHeight, new Vector3f(0, 0, 10), quaternionf, quaternionf2, livingEntity);
            context.getMatrices().pop();
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Load a world and nearby entities will appear here."), this.width / 3, this.height / 2, Color.GRAY.getRGB());
        }
    }
}
