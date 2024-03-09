package traben.entity_features.config.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import traben.entity_features.config.EFConfig;
import traben.entity_features.config.EFConfigHandler;
import traben.entity_features.config.EFConfigWarning;
import traben.entity_features.config.EFConfigWarnings;
import traben.entity_features.config.gui.options.EFOption;
import traben.entity_features.config.gui.options.EFOptionCategory;
import traben.entity_texture_features.ETFVersionDifferenceHandler;

import java.util.*;

public class EFConfigScreenMain extends EFScreen {

    private static Set<EFConfigHandler<?>> configHandlers = null;
    final ObjectOpenHashSet<EFConfigWarning> warningsFound = new ObjectOpenHashSet<>();
    private final EFOptionCategory mainCategories;

    private final List<Identifier> modIcons;
    private final Random rand = new Random();
    private final LogoCreeperRenderer LOGO_CREEPER = new LogoCreeperRenderer();
    private final Identifier BLUE = new Identifier("entity_features", "textures/gui/entity/e.png");
    private final Identifier RED = new Identifier("entity_features", "textures/gui/entity/t.png");
    private final Identifier YELLOW = new Identifier("entity_features", "textures/gui/entity/f.png");
    boolean shownWarning = false;
    int warningCount = 0;
    private long timer = 0;
    private LivingEntity livingEntity = null;

    public EFConfigScreenMain(Screen parent) {
        super("config.entity_features", parent, true);
        mainCategories = new EFOptionCategory.Empty().add(
                new EFOptionCategory("config.entity_features.textures_main"),
                new EFOptionCategory("config.entity_features.models_main").setEmptyTooltip("config.entity_features.empty_emf"),
                new EFOptionCategory("config.entity_features.sounds_main").setEmptyTooltip("config.entity_features.empty_esf"),
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

    public static void drawEntity(DrawContext context, float x, float y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 150.0);
        context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling((float) size, (float) size, (float) (-size)));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }

        entityRenderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() ->
                entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880));
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
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
        setupButtons((int) (this.width * 0.6), (int) (this.height * 0.2), (int) (this.width * 0.3));

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

    private void setupButtons(int x, int y, int width) {
        EFOption[] options = mainCategories.getOptions().values().toArray(new EFOption[0]);
        for (int i = 0; i < options.length; i++) {
            EFOption option = options[i];
            var widget = option.getWidget(x, y + (i * 24), width, 20);
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
            int ix = this.width - (modIcons.size() * 34);
            for (Identifier modIcon : modIcons) {
                context.drawTexture(modIcon, ix, 2, 0, 0, 32, 32, 32, 32);
                ix += 34;
            }
        }

        //get a random entity and display for 5 seconds
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

        //draw the entity else ETF logo creepers
        if (livingEntity != null && !livingEntity.isRemoved()) {
            renderEntitySample(context, mouseY);
        } else {
            renderETFLogoCreepers(context, mouseX, mouseY);
        }
        context.getMatrices().pop();
    }

    private void renderETFLogoCreepers(final DrawContext context, final int mouseX, final int mouseY) {
        int y = (int) (this.height * 0.75);
        int x = (int) (this.width * 0.33);
        float g = (float) -Math.atan(((-mouseY + this.height / 2f) / 40.0F));
        float g2 = (float) -Math.atan(((-mouseX + this.width / 3f) / 400.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F).rotateY(g2);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(-(g * 20.0F * 0.017453292F));
        quaternionf.mul(quaternionf2);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 150.0);
        float scaling = (float) (this.height * 0.3);
        context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling(scaling, scaling, -scaling));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();

        //drawEntity(context, x, y, 1, quaternionf, quaternionf2, );
        context.getMatrices().translate(-0.6, 0, 0);
        LOGO_CREEPER.renderSimple(context.getMatrices(), context.getVertexConsumers(), YELLOW);
        context.getMatrices().translate(0.6, 0, 0);
        LOGO_CREEPER.renderSimple(context.getMatrices(), context.getVertexConsumers(), RED);
        context.getMatrices().translate(0.6, 0, 0);
        LOGO_CREEPER.renderSimple(context.getMatrices(), context.getVertexConsumers(), BLUE);

        DiffuseLighting.enableGuiDepthLighting();
    }

    private void renderEntitySample(final DrawContext context, final int mouseY) {
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
        drawEntity(context, x, y, modelHeight, quaternionf, quaternionf2, livingEntity);
    }

    public static class LogoCreeperRenderer {


        private final ModelPart leftHindLeg;
        private final ModelPart rightHindLeg;
        private final ModelPart leftFrontLeg;
        private final ModelPart rightFrontLeg;
        private final ModelPart root;

        public LogoCreeperRenderer() {
            root = CreeperEntityModel.getTexturedModelData(Dilation.NONE).createModel();
            rightHindLeg = root.getChild("right_hind_leg");
            leftHindLeg = root.getChild("left_hind_leg");
            rightFrontLeg = root.getChild("right_front_leg");
            leftFrontLeg = root.getChild("left_front_leg");
        }


        public void renderSimple(final MatrixStack matrix, final VertexConsumerProvider vcp, Identifier texture) {
            matrix.push();
            matrix.scale(-1.0F, -1.0F, 1.0F);
            matrix.translate(0.0F, -1.501F, 0.0F);
            leftHindLeg.pitch = (float) Math.toRadians(25);//MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
            rightHindLeg.pitch = (float) -Math.toRadians(25);//MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
            leftFrontLeg.pitch = (float) -Math.toRadians(25);//MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
            rightFrontLeg.pitch = (float) Math.toRadians(25);//MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
            RenderLayer rendertype = RenderLayer.getEntityCutout(texture);
            if (rendertype != null) {
                VertexConsumer vertexconsumer = vcp.getBuffer(rendertype);
                root.render(matrix, vertexconsumer, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            matrix.pop();
        }
    }
}
