package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreen;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

//inspired by puzzles custom gui code
public class ETFConfigScreenSkinToolPixelSelection extends ETFConfigScreen {

    private final SelectionMode MODE;

    private final ETFConfigScreenSkinTool etfParent;
    Set<Integer> selectedPixels;
    Identifier currentSkinToRender = new Identifier(MOD_ID + ":textures/gui/icon.png");

    protected ETFConfigScreenSkinToolPixelSelection(ETFConfigScreenSkinTool parent, SelectionMode mode) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + (mode == SelectionMode.EMISSIVE ? ".emissive_select" : ".enchanted_select") + ".title"), parent);
        this.MODE = mode;
        etfParent = parent;


    }

    @Override
    protected void init() {
        super.init();

        Identifier randomID = new Identifier(MOD_ID + "_ignore", "gui_skin_" + System.currentTimeMillis() + ".png");
        if (ETFUtils2.registerNativeImageToIdentifier(etfParent.currentEditorSkin, randomID)) {
            currentSkinToRender = randomID;
        }

        selectedPixels = new HashSet<>();
        for (int x = MODE.startX; x < MODE.startX + 8; x++) {
            for (int y = MODE.startY; y < MODE.startY + 8; y++) {
                int color = etfParent.currentEditorSkin.getColor(x, y);
                if (color != 0) {
                    selectedPixels.add(color);
                }
            }
        }

        this.addDrawableChild(getETFButton((int) (this.width * 0.024), (int) (this.height * 0.2), 20, 20,
                Text.of("⟳"),
                (button) -> etfParent.flipView = !etfParent.flipView));


        this.addDrawableChild(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> Objects.requireNonNull(client).setScreen(parent)));

        int pixelSize = (int) (this.height * 0.7 / 64);

        //simple method to create 4096 buttons instead of extrapolating mouse position
        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int finalX = x;
                int finalY = y;

                ButtonWidget butt = new ButtonWidget((int) ((this.width * 0.35) + (x * pixelSize)), (int) ((this.height * 0.2) + (y * pixelSize)), pixelSize, pixelSize,
                        Text.of(""),
                        (button) -> {
                            int colorAtPixel = etfParent.currentEditorSkin.getColor(finalX, finalY);
                            if (selectedPixels.contains(colorAtPixel)) {
                                selectedPixels.remove(colorAtPixel);
                            } else {
                                selectedPixels.add(colorAtPixel);
                            }

                            applyCurrentSelectedPixels();
                            etfParent.thisETFPlayerTexture.changeSkinToThisForTool(etfParent.currentEditorSkin);
                            Identifier randomID2 = new Identifier(MOD_ID + "_ignore", "gui_skin_" + System.currentTimeMillis() + ".png");
                            if (ETFUtils2.registerNativeImageToIdentifier(etfParent.currentEditorSkin, randomID2)) {
                                currentSkinToRender = randomID2;
                            }
                        }, Supplier::get) {
                    @Override
                    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
                        //invisible lol
                    }
                };

                this.addDrawableChild(butt);
            }
        }

    }

    private void applyCurrentSelectedPixels() {
        ArrayList<Integer> integerSet = new ArrayList<>(selectedPixels);

        for (int x = MODE.startX; x < MODE.startX + 8; x++) {
            for (int y = MODE.startY; y < MODE.startY + 8; y++) {
                if (integerSet.isEmpty()) {
                    etfParent.currentEditorSkin.setColor(x, y, 0);
                } else {
                    etfParent.currentEditorSkin.setColor(x, y, integerSet.get(0));
                    integerSet.remove(0);
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int pixelSize = (int) (this.height * 0.7 / 64);
        renderGUITexture(currentSkinToRender, (int) ((this.width * 0.35)), (int) ((this.height * 0.2)), (int) ((this.width * 0.35) + (64 * pixelSize)), (int) ((this.height * 0.2) + (64 * pixelSize)));
        context.drawTextWithShadow(textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".skin_select" + (selectedPixels.size() > 64 ? ".warn" : ".hint")), width / 7, (int) (this.height * 0.8), selectedPixels.size() > 64 ? 0xff1515 : 0xFFFFFF);

        if (MinecraftClient.getInstance() != null) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {

                int blinkModifierBySystemTimeInTicks = (int) ((System.currentTimeMillis() / 50) % (30 + (ETFConfigData.blinkLength * 2)));
                ETFManager.getInstance().ENTITY_BLINK_TIME.put(player.getUuid(), player.getWorld().getTime() + blinkModifierBySystemTimeInTicks - (15 + ETFConfigData.blinkLength));


                int height = (int) (this.height * 0.75);
                int playerX = (int) (this.width * 0.14);
                drawEntity(playerX, height, (int) (this.height * 0.3), (float) (-mouseX + playerX), (float) (-mouseY + (this.height * 0.3)), player);
            } else {
                context.drawTextWithShadow(textRenderer, Text.of("Player model only visible while in game!"), width / 7, (int) (this.height * 0.4), 0xFFFFFF);
                context.drawTextWithShadow(textRenderer, Text.of("load a single-player world and then open this menu."), width / 7, (int) (this.height * 0.45), 0xFFFFFF);
            }
        }

//        if(MODE == SelectionMode.EMISSIVE && ETFVersionDifferenceHandler.isThisModLoaded("iris"))
//            drawTextWithShadow(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.iris_message"), width / 8, (int) (this.height * 0.15), 0xFF5555);

    }

    public void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        //InventoryScreen
        float f = (float) Math.atan((mouseX / 40.0F));
        float g = (float) Math.atan((mouseY / 40.0F));
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float) size, (float) size, (float) size);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        matrixStack2.multiply(quaternionf);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = (etfParent.flipView ? 0 : 180.0F) + f * 20.0F;
        entity.setYaw((etfParent.flipView ? 0 : 180.0F) + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternionf2.conjugate();
        entityRenderDispatcher.setRotation(quaternionf2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        //noinspection deprecation
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 0x800080 /*15728880*/));
        immediate.draw();

        //second render required for iris
        VertexConsumerProvider.Immediate immediate2 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        //noinspection deprecation
        RenderSystem.runAsFancy(() -> {
            //entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate2, 15728880);
            if (etfParent.thisETFPlayerTexture != null && etfParent.thisETFPlayerTexture.etfTextureOfFinalBaseSkin != null && entity instanceof AbstractClientPlayerEntity) {
                Identifier emissive = etfParent.thisETFPlayerTexture.etfTextureOfFinalBaseSkin.getEmissiveIdentifierOfCurrentState();
                if (emissive != null) {
                    RenderLayer layer = RenderLayer.getEntityTranslucent(emissive);

                    VertexConsumer vertexC = immediate.getBuffer(layer);
                    if (vertexC != null) {
                        EntityRenderer<?> bob = entityRenderDispatcher.getRenderer(entity);
                        if (bob instanceof LivingEntityRenderer<?, ?>) {
                            // System.out.println("rendered");
                            //((LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>>) bob).render((PlayerEntity) entity, 0, 1, matrixStack2, immediate, 0xE000E0);
                            ((LivingEntityRenderer<?, ?>) bob).getModel().render(matrixStack, vertexC, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                        }
                    }
                }
            }
        });
        immediate2.draw();
        entityRenderDispatcher.setRenderShadows(true);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public enum SelectionMode {
        EMISSIVE(56, 16),
        ENCHANTED(56, 24);

        final int startX;
        final int startY;

        SelectionMode(@SuppressWarnings("SameParameterValue") int start_x, int start_y) {
            startX = start_x;
            startY = start_y;
        }
    }

}
