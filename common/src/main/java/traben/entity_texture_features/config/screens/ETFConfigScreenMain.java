package traben.entity_texture_features.config.screens;

import com.mojang.blaze3d.systems.RenderSystem;
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
import traben.tconfig.gui.TConfigScreenMain;
import traben.tconfig.gui.entries.TConfigEntryCategory;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ETFConfigScreenMain extends TConfigScreenMain {

    final ObjectOpenHashSet<ETFConfigWarning> warningsFound = new ObjectOpenHashSet<>();

    private final Random rand = new Random();
    private final LogoCreeperRenderer LOGO_CREEPER = new LogoCreeperRenderer();
    private final Identifier BLUE = new Identifier("entity_features", "textures/gui/entity/e.png");
    private final Identifier RED = new Identifier("entity_features", "textures/gui/entity/t.png");
    private final Identifier YELLOW = new Identifier("entity_features", "textures/gui/entity/f.png");
    boolean shownWarning = false;
    int warningCount = 0;
    private long timer = 0;
    private LivingEntity livingEntity = null;

    public ETFConfigScreenMain(Screen parent) {
        super("config.entity_features", parent, ETF.configHandlers, List.of(
                new TConfigEntryCategory("config.entity_features.textures_main"),
                new TConfigEntryCategory("config.entity_features.models_main").setEmptyTooltip("config.entity_features.empty_emf"),
                new TConfigEntryCategory("config.entity_features.sounds_main").setEmptyTooltip("config.entity_features.empty_esf"),
                new TConfigEntryCategory("config.entity_features.general_settings.title"),
                new TConfigEntryCategory("Per entity overrides")
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

    @Override
    protected void init() {
        super.init();
        if (shownWarning) {
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.entity_features.warnings_main"),
                            (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenWarnings(this, warningsFound)))
                    .dimensions((int) (this.width * 0.1), (int) (this.height * 0.1) - 15, (int) (this.width * 0.2), 20
                    ).build());
        }
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        super.render(context, mouseX, mouseY, delta);
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
        //noinspection deprecation
        RenderSystem.runAsFancy(() ->
                entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880));
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
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

        float sin1 = (float) (Math.sin(System.currentTimeMillis() / 500d) / 32);
        float sin2 = (float) (Math.sin(System.currentTimeMillis() / 500d + 1) / 32);
        float sin3 = (float) (Math.sin(System.currentTimeMillis() / 500d + 2) / 32);

        MatrixStack matrixStack = context.getMatrices();

        matrixStack.push();
            matrixStack.translate(-0.6,  -sin1,0);
            matrixStack.scale( 1+sin1,  1+sin1,  1+sin1);
            LOGO_CREEPER.renderSimple(matrixStack, context.getVertexConsumers(), YELLOW);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0,  -sin2,0);
            matrixStack.scale( 1+sin2,  1+sin2,  1+sin2);
            LOGO_CREEPER.renderSimple(matrixStack, context.getVertexConsumers(), RED);
        matrixStack.pop();
        matrixStack.push();
            matrixStack.translate(0.6,  -sin3,0);
            matrixStack.scale( 1+sin3,  1+sin3,  1+sin3);
            LOGO_CREEPER.renderSimple(matrixStack, context.getVertexConsumers(), BLUE);
        matrixStack.pop();

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


        private final ModelPart root;

        public LogoCreeperRenderer() {
            root = CreeperEntityModel.getTexturedModelData(Dilation.NONE).createModel();
            root.getChild("right_hind_leg").pitch = (float) -Math.toRadians(25);
            root.getChild("left_hind_leg").pitch = (float) Math.toRadians(25);
            root.getChild("left_hind_leg").pivotZ -= 2;
            root.getChild("right_front_leg").pitch = (float) Math.toRadians(25);
            root.getChild("left_front_leg").pitch = (float) -Math.toRadians(25);
            root.getChild("left_front_leg").pivotZ += 2;
        }


        public void renderSimple(final MatrixStack matrix, final VertexConsumerProvider vcp, Identifier texture) {
            matrix.push();
            matrix.scale(-1.0F, -1.0F, 1.0F);
            matrix.translate(0.0F, -1.501F, 0.0F);
            RenderLayer rendertype = RenderLayer.getEntitySolid(texture);
            if (rendertype != null) {
                VertexConsumer vertexconsumer = vcp.getBuffer(rendertype);
                root.render(matrix, vertexconsumer, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
            matrix.pop();
        }
    }
}
