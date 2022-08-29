package traben.entity_texture_features.config.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.utils.ETFUtils2;

import java.nio.file.Path;
import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.*;
import static traben.entity_texture_features.texture_handlers.ETFManager.ENTITY_BLINK_TIME;
import static traben.entity_texture_features.texture_handlers.ETFManager.PLAYER_TEXTURE_MAP;

//inspired by puzzles custom gui code
public class ETFConfigScreenPlayerSkinTool extends ETFConfigScreen {
    public  Boolean originalEnableBlinking;
    public  Integer originalBlinkLength;
    public ETFPlayerTexture thisETFPlayerTexture = null;
    public NativeImage currentEditorSkin = null;
    ButtonWidget printSkinFileButton = null;
    ButtonWidget villagerNoseButton = null;
    ButtonWidget coatButton = null;
    ButtonWidget coatLengthButton = null;
    ButtonWidget blinkButton = null;
    ButtonWidget blinkHeightButton = null;
    ButtonWidget emissiveButton = null;
    ButtonWidget emissiveSelectButton = null;
    ButtonWidget enchantButton = null;
    ButtonWidget enchantSelectButton = null;
    ButtonWidget capeButton = null;

    public boolean flipView= false;
    protected ETFConfigScreenPlayerSkinTool(Screen parent) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_features.title"), parent);

    }

    public static int getPixelColour(int choice) {
        //no enhanced switch for back compat
        switch (choice) {
            case 1:
                return -65281;
            case 2:
                return -256;
            case 3:
                return -16776961;
            case 4:
                return -16711936;
            case 5:
                return -16760705;
            case 6:
                return -65536;
            case 7:
                return -16744449;
            case 8:
                return -14483457;
            default:
                return 0;
        }
    }

    private void onExit() {
        ETFConfigData.enableBlinking = originalEnableBlinking;
        ETFConfigData.blinkLength = originalBlinkLength;
        if (MinecraftClient.getInstance().player != null) {
            PLAYER_TEXTURE_MAP.removeEntryOnly(MinecraftClient.getInstance().player.getUuid());
            ENTITY_BLINK_TIME.put(MinecraftClient.getInstance().player.getUuid(), 0L);
        }
        thisETFPlayerTexture = null;
        currentEditorSkin = null;
    }
    @Override
    public void close() {
        onExit();
        super.close();
    }

    @Override
    protected void init() {
        super.init();

        //make blinking faster in skin tool
        if(originalEnableBlinking ==null && originalBlinkLength == null) {
            originalEnableBlinking = ETFConfigData.enableBlinking;
            originalBlinkLength = ETFConfigData.blinkLength;
            ETFConfigData.blinkLength = 10;
            ETFConfigData.enableBlinking = true;
        }


        if (MinecraftClient.getInstance().player != null && thisETFPlayerTexture == null) {
            thisETFPlayerTexture = PLAYER_TEXTURE_MAP.get(MinecraftClient.getInstance().player.getUuid());
            if (thisETFPlayerTexture == null) {
                ETFPlayerTexture etfPlayerTexture = new ETFPlayerTexture();
                PLAYER_TEXTURE_MAP.put(MinecraftClient.getInstance().player.getUuid(), etfPlayerTexture);
                thisETFPlayerTexture = etfPlayerTexture;
            }
        }
        if(currentEditorSkin == null) {
            currentEditorSkin = ETFUtils2.emptyNativeImage(64, 64);
            currentEditorSkin.copyFrom(ETFPlayerTexture.clientPlayerOriginalSkinImageForTool);
        }

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 210, (int) (this.height * 0.9), 200, 20, ScreenTexts.CANCEL, (button) -> {
            onExit();
            Objects.requireNonNull(client).setScreen(parent);
        }));

        this.addDrawableChild(new ButtonWidget((int) (this.width * 0.024), (int) (this.height * 0.2), 20, 20,
                Text.of("⟳"),
                (button) -> {
                    flipView = !flipView;
        }));

        printSkinFileButton = new ButtonWidget(this.width / 2 + 10, (int) (this.height * 0.9), 200, 20,
                ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.print_skin"),
                (button) -> {
                    boolean result = false;
                    if (MinecraftClient.getInstance().player != null) {
                        result = printPlayerSkinCopy();
                    }
                    onExit();
                    Objects.requireNonNull(client).setScreen(new ETFConfigScreenPrintOutcome(parent, result));
                });
        this.addDrawableChild(printSkinFileButton);


        if (MinecraftClient.getInstance().player != null) {

            //skin feature buttons

            this.addDrawableChild(new ButtonWidget((int) (this.width * 0.55), (int) (this.height * 0.2), (int) (this.width * 0.4), 20,
                    thisETFPlayerTexture.hasFeatures ?
                            ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.remove_features") :
                            ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.add_features"),
                    (button) -> {
                        if (thisETFPlayerTexture.hasFeatures) {
                            applyExistingOverlayToSkin(new Identifier(MOD_ID + ":textures/skin_feature_remove.png"));
                        } else {
                            applyExistingOverlayToSkin(new Identifier(MOD_ID + ":textures/skin_feature_printout.png"));
                        }
                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);
                        button.setMessage(thisETFPlayerTexture.hasFeatures ?
                                ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.remove_features") :
                                ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.add_features"));
                        updateButtons();
                    }));

            //todo will need to be properly enum data driven when more nose options added
            villagerNoseButton = new ButtonWidget((int) (this.width * 0.25), (int) (this.height * 0.7), (int) (this.width * 0.3), 20,
                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.nose.button").getString()+
                            (thisETFPlayerTexture.hasVillagerNose ? NoseType.VILLAGER.buttonText
                             :NoseType.NONE.buttonText).getString()),
                    (button) -> {
                        int colour = thisETFPlayerTexture.hasVillagerNose ? 0 : -12362096;

                        currentEditorSkin.setColor(43, 13, colour);
                        currentEditorSkin.setColor(44, 13, colour);
                        currentEditorSkin.setColor(43, 14, colour);
                        currentEditorSkin.setColor(44, 14, colour);
                        currentEditorSkin.setColor(43, 15, colour);
                        currentEditorSkin.setColor(44, 15, colour);

                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);

                        button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.nose.button").getString()+
                                (thisETFPlayerTexture.hasVillagerNose ? NoseType.VILLAGER.buttonText
                                        :NoseType.NONE.buttonText).getString()));
                    });

            capeButton = getETFButton((int) (this.width * 0.6), (int) (this.height * 0.7), (int) (this.width * 0.3), 20,
                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.button").getString()+
                    thisETFPlayerTexture.capeType.getButtonText().getString()),
                    (button) -> {
                        CapeType cape =thisETFPlayerTexture.capeType.next();

                        button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.button").getString()+
                                cape.getButtonText().getString()));

                        currentEditorSkin.setColor(53, 16, cape.getCapePixelColour());

                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);
                        updateButtons();
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.tooltip")
            );

            coatButton = getETFButton((int) (this.width * 0.25), (int) (this.height * 0.3), (int) (this.width * 0.42), 20,
                    CoatStyle.get(thisETFPlayerTexture.coatStyle).getTitle(),
                    (button) -> {
                        CoatStyle coat = CoatStyle.get(thisETFPlayerTexture.coatStyle).next();

                        button.setMessage(coat.getTitle());

                        currentEditorSkin.setColor(52, 17, coat.getCoatPixelColour());

                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);
                        updateButtons();
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.tooltip")
            );

            coatLengthButton = getETFButton((int) (this.width * 0.695), (int) (this.height * 0.3), (int) (this.width * 0.275), 20,
                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_length.title").getString()
                            + thisETFPlayerTexture.coatLength),
                    (button) -> {
                        int lengthChoice;
                        if (thisETFPlayerTexture.coatLength == 8) {
                            lengthChoice = 1;
                        } else {
                            lengthChoice = thisETFPlayerTexture.coatLength + 1;
                        }
                        currentEditorSkin.setColor(52, 18, getPixelColour(lengthChoice));
                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);

                        button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_length.title").getString()
                                + thisETFPlayerTexture.coatLength));
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_length.tooltip")
            );

            blinkButton = getETFButton((int) (this.width * 0.25), (int) (this.height * 0.4), (int) (this.width * 0.42), 20,
                    BlinkType.get(thisETFPlayerTexture.blinkType).getTitle(),
                    (button) -> {
                        BlinkType blink = BlinkType.get(thisETFPlayerTexture.blinkType).next();

                        button.setMessage(blink.getTitle());

                        if (blink != BlinkType.NONE
                                && blink != BlinkType.WHOLE_FACE_TWO
                                && blink != BlinkType.WHOLE_FACE) {
                            //set height choice to 0
                            currentEditorSkin.setColor(52, 19, getPixelColour(0));
                        } else if (currentEditorSkin.getColor(52, 19) > blink.getMaxEyePixelHeight()) {
                            //set height choice to the highest possible if too big for new type choice
                            currentEditorSkin.setColor(52, 19, getPixelColour(blink.getMaxEyePixelHeight()));
                        }

                        currentEditorSkin.setColor(52, 16, blink.getBlinkPixelColour());

                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);
                        updateButtons();
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.tooltip")
            );

            blinkHeightButton = getETFButton((int) (this.width * 0.695), (int) (this.height * 0.4), (int) (this.width * 0.275), 20,
                    Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_height.title").getString()
                            + thisETFPlayerTexture.blinkHeight),
                    (button) -> {
                        int heightChoice;
                        if (thisETFPlayerTexture.blinkHeight == BlinkType.get(thisETFPlayerTexture.blinkType).getMaxEyePixelHeight()) {
                            heightChoice = 1;
                        } else {
                            heightChoice = thisETFPlayerTexture.blinkHeight + 1;
                        }
                        currentEditorSkin.setColor(52, 19, getPixelColour(heightChoice));
                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);

                        button.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_height.title").getString()
                                + thisETFPlayerTexture.blinkHeight));
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_height.tooltip")
            );

            emissiveButton = getETFButton((int) (this.width * 0.25), (int) (this.height * 0.5), (int) (this.width * 0.42), 20,
                    Text.of( ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.emissive_enable.button").getString()
                            + (thisETFPlayerTexture.hasEmissives ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                    (button) -> {


                        if (thisETFPlayerTexture.hasEmissives) {
                            currentEditorSkin.setColor(1, 17, 0);
                        } else  {
                            currentEditorSkin.setColor(1, 17, getPixelColour(1));
                        }

                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);
                        button.setMessage(Text.of( ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.emissive_enable.button").getString()
                                + (thisETFPlayerTexture.hasEmissives ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                        updateButtons();
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.emissive_enable.tooltip")
            );

            emissiveSelectButton = getETFButton((int) (this.width * 0.695), (int) (this.height * 0.5), (int) (this.width * 0.275), 20,
                    ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.emissive_select.button"),
                    (button) -> {
                        Objects.requireNonNull(client).setScreen(new ETFConfigScreenPlayerSkinToolPixelSelect(this, ETFConfigScreenPlayerSkinToolPixelSelect.SelectionMode.EMISSIVE));
                    }
            );

            enchantButton = getETFButton((int) (this.width * 0.25), (int) (this.height * 0.6), (int) (this.width * 0.42), 20,
                    Text.of( ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.enchant_enable.button").getString()
                            + (thisETFPlayerTexture.hasEnchant ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                    (button) -> {


                        if (thisETFPlayerTexture.hasEnchant) {
                            currentEditorSkin.setColor(1, 18, 0);
                        } else  {
                            currentEditorSkin.setColor(1, 18, getPixelColour(2));
                        }

                        thisETFPlayerTexture.changeSkinToThisForTool(currentEditorSkin);
                        button.setMessage(Text.of( ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.enchant_enable.button").getString()
                                + (thisETFPlayerTexture.hasEnchant ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                        updateButtons();
                    }, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.enchant_enable.tooltip")
            );

            enchantSelectButton = getETFButton((int) (this.width * 0.695), (int) (this.height * 0.6), (int) (this.width * 0.275), 20,
                    ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.enchant_select.button"),
                    (button) -> {
                        Objects.requireNonNull(client).setScreen(new ETFConfigScreenPlayerSkinToolPixelSelect(this, ETFConfigScreenPlayerSkinToolPixelSelect.SelectionMode.ENCHANTED));
                    }
            );

            updateButtons();
            this.addDrawableChild(villagerNoseButton);
            this.addDrawableChild(coatButton);
            this.addDrawableChild(coatLengthButton);
            this.addDrawableChild(blinkButton);
            this.addDrawableChild(blinkHeightButton);
            this.addDrawableChild(emissiveButton);
            this.addDrawableChild(emissiveSelectButton);
            this.addDrawableChild(enchantButton);
            this.addDrawableChild(enchantSelectButton);
            this.addDrawableChild(capeButton);


        }

    }

    private void updateButtons() {
        boolean activeFeatures = thisETFPlayerTexture.hasFeatures;
        if (villagerNoseButton != null) {
            villagerNoseButton.active = activeFeatures;
            villagerNoseButton.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.nose.button").getString()+
                    (thisETFPlayerTexture.hasVillagerNose ? NoseType.VILLAGER.buttonText
                            :NoseType.NONE.buttonText).getString()));
        }
        if (coatButton != null) {
            coatButton.active = activeFeatures;
            coatButton.setMessage(CoatStyle.get(thisETFPlayerTexture.coatStyle).getTitle());
        }
        if (coatLengthButton != null) {
            coatLengthButton.active = activeFeatures && CoatStyle.get(thisETFPlayerTexture.coatStyle) != CoatStyle.NONE;
            coatLengthButton.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_length.title").getString()
                    + thisETFPlayerTexture.coatLength));
        }
        if (blinkButton != null) {
            blinkButton.active = activeFeatures;
            blinkButton.setMessage(BlinkType.get(thisETFPlayerTexture.blinkType).getTitle());
        }
        if (blinkHeightButton != null) {
            blinkHeightButton.active = activeFeatures
                    && BlinkType.get(thisETFPlayerTexture.blinkType) != BlinkType.NONE
                    && BlinkType.get(thisETFPlayerTexture.blinkType) != BlinkType.WHOLE_FACE_TWO
                    && BlinkType.get(thisETFPlayerTexture.blinkType) != BlinkType.WHOLE_FACE;
            blinkHeightButton.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_height.title").getString()
                    + thisETFPlayerTexture.blinkHeight));
        }
        if (emissiveButton != null) {
            emissiveButton.active = activeFeatures;
            emissiveButton.setMessage(Text.of( ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.emissive_enable.button").getString()
                    + (thisETFPlayerTexture.hasEmissives ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
        }
        if (emissiveSelectButton != null) {
            emissiveSelectButton.active = activeFeatures
                    && thisETFPlayerTexture.hasEmissives;
        }
        if (enchantButton != null) {
            enchantButton.active = activeFeatures;
            enchantButton.setMessage(Text.of( ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.enchant_enable.button").getString()
                    + (thisETFPlayerTexture.hasEnchant ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
        }
        if (enchantSelectButton != null) {
            enchantSelectButton.active = activeFeatures
                    && thisETFPlayerTexture.hasEnchant;
        }
        if (capeButton != null) {
            capeButton.active = activeFeatures;
            capeButton.setMessage(Text.of(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.button").getString()+
                    thisETFPlayerTexture.capeType.getButtonText().getString()));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {


        super.render(matrices, mouseX, mouseY, delta);


        if (MinecraftClient.getInstance() != null) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {

                int blinkModifierBySystemTimeInTicks = (int) ((System.currentTimeMillis() / 50) % (30 + (ETFConfigData.blinkLength * 2)));
                ENTITY_BLINK_TIME.put(player.getUuid(), player.world.getTime() + blinkModifierBySystemTimeInTicks - (15 + ETFConfigData.blinkLength));


                int height = (int) (this.height * 0.75);
                int playerX = (int) (this.width * 0.14);
                drawEntity(playerX, height, (int) (this.height * 0.3), (float) (-mouseX + playerX), (float) (-mouseY + (this.height * 0.3)), player);
            } else {
                drawTextWithShadow(matrices, textRenderer, Text.of("Player model only visible while in game!"), width / 7, (int) (this.height * 0.4), 0xFFFFFF);
                drawTextWithShadow(matrices, textRenderer, Text.of("load a single-player world and then open this menu."), width / 7, (int) (this.height * 0.45), 0xFFFFFF);
            }
        }

        drawTextWithShadow(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.crouch_message"), width / 40, (int) (this.height * 0.8), 0x555555);
        drawTextWithShadow(matrices, textRenderer, ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_message"), width / 40, (int) (this.height * 0.1), 0x555555);

    }

    public void applyExistingOverlayToSkin(Identifier overlayTexture) {
        if ((ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric"))) {
            NativeImage skinFeatureImage = ETFUtils2.getNativeImageElseNull(overlayTexture);
            try {
                for (int x = 0; x < currentEditorSkin.getWidth(); x++) {
                    for (int y = 0; y < currentEditorSkin.getHeight(); y++) {
                        //noinspection ConstantConditions
                        if (skinFeatureImage.getColor(x, y) != 0) {
                            currentEditorSkin.setColor(x, y, skinFeatureImage.getColor(x, y));
                        }
                    }
                }

                //ETFUtils2.logMessage("Skin feature layout successfully applied to a copy of your skin.", true);
            } catch (Exception e) {
                ETFUtils2.logMessage("Skin feature layout could not be applied to a copy of your skin. Error written to log.", false);
                ETFUtils2.logError(e.toString(), false);
            }

        } else {
            //requires fab api to read from mod resources
            ETFUtils2.logError("Fabric API required for skin processing, cancelling.", false);
        }
        //return skinImage;
    }

    public boolean printPlayerSkinCopy() {
        if ((ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric")) && CONFIG_DIR != null) {
            Path outputDirectory = Path.of(CONFIG_DIR.getParent(), "\\ETF_player_skin_printout.png");
            try {
                currentEditorSkin.writeTo(outputDirectory);
                ETFUtils2.logMessage(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.print_skin.result.success").getString(), true);

                return true;
            } catch (Exception e) {
                ETFUtils2.logMessage("Skin feature layout could not be applied to a copy of your skin and has not been saved. Error written to log.", true);
                ETFUtils2.logError(e.toString(), false);
            }

        } else {
            //requires fab api to read from mod resources
            ETFUtils2.logError("Fabric API required for skin printout, cancelling.", true);
        }
        return false;
    }

    public enum CapeType{
        OPTIFINE(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.optifine")),
        MINECRAFT_CAPES_NET(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.minecraftcapes")),
        ETF(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.etf")),
        CUSTOM(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.custom")),
        NONE(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.cape.none"));

        public Text getButtonText() {
            return buttonText;
        }


        public CapeType next(){
            switch (this){
                case NONE: return OPTIFINE;
                case OPTIFINE: return MINECRAFT_CAPES_NET;
                case MINECRAFT_CAPES_NET: return ETF;
                case ETF: return CUSTOM;
                default : return NONE;

            }
        }

        public int getCapePixelColour(){
            switch (this){
                case CUSTOM: return getPixelColour(1);
                case OPTIFINE: return getPixelColour(3);
                case MINECRAFT_CAPES_NET: return getPixelColour(2);
                case ETF: return getPixelColour(4);
                default : return 0;

            }
        }


        private final Text buttonText;
        CapeType(Text buttonText){
            this.buttonText=buttonText;
        }
    }

    public enum NoseType{
        VILLAGER(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.nose.villager")),
        //TEXTURED,
        NONE(ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.nose.none"));

        public Text getButtonText() {
            return buttonText;
        }


        public NoseType next(){
            switch (this){
                case NONE: return VILLAGER;
                default : return NONE;

            }
        }

        private final Text buttonText;
        NoseType(Text buttonText){
            this.buttonText=buttonText;
        }
    }

    public enum CoatStyle {
        COPIED_THIN_TOP,
        MOVED_THIN_TOP,
        COPIED_FAT_TOP,
        MOVED_FAT_TOP,
        COPIED_THIN,
        MOVED_THIN,
        COPIED_FAT,
        MOVED_FAT,
        NONE;


        public static CoatStyle get(int id) {
            //no enhanced switch for back compat
            switch (id) {
                case 1:
                    return COPIED_THIN_TOP;
                case 2:
                    return MOVED_THIN_TOP;
                case 3:
                    return COPIED_FAT_TOP;
                case 4:
                    return MOVED_FAT_TOP;
                case 5:
                    return COPIED_THIN;
                case 6:
                    return MOVED_THIN;
                case 7:
                    return COPIED_FAT;
                case 8:
                    return MOVED_FAT;
                default:
                    return NONE;
            }
        }

        public Text getTitle() {
            //no enhanced switch for back compat
            switch (this) {
                case COPIED_THIN_TOP:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.1");
                case MOVED_THIN_TOP:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.2");
                case COPIED_FAT_TOP:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.3");
                case MOVED_FAT_TOP:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.4");
                case COPIED_THIN:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.5");
                case MOVED_THIN:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.6");
                case COPIED_FAT:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.7");
                case MOVED_FAT:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.8");
                default:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.coat_style.none");
            }
        }

        public int getCoatPixelColour() {
            //no enhanced switch for back compat
            switch (this) {
                case COPIED_THIN_TOP:
                    return -65281;
                case MOVED_THIN_TOP:
                    return -256;
                case COPIED_FAT_TOP:
                    return -16776961;
                case MOVED_FAT_TOP:
                    return -16711936;
                case COPIED_THIN:
                    return -16760705;
                case MOVED_THIN:
                    return -65536;
                case COPIED_FAT:
                    return -16744449;
                case MOVED_FAT:
                    return -14483457;
                default:
                    return 0;
            }
        }

        public CoatStyle next() {
            //no enhanced switch for back compat
            switch (this) {
                case NONE:
                    return COPIED_THIN_TOP;
                case COPIED_THIN_TOP:
                    return MOVED_THIN_TOP;
                case MOVED_THIN_TOP:
                    return COPIED_FAT_TOP;
                case COPIED_FAT_TOP:
                    return MOVED_FAT_TOP;
                case MOVED_FAT_TOP:
                    return COPIED_THIN;
                case COPIED_THIN:
                    return MOVED_THIN;
                case MOVED_THIN:
                    return COPIED_FAT;
                case COPIED_FAT:
                    return MOVED_FAT;
                default:
                    return NONE;
            }
        }
    }

    public enum BlinkType {
        ONE_PIXEL,
        TWO_PIXEL,
        FOUR_PIXEL,
        WHOLE_FACE,
        WHOLE_FACE_TWO,
        NONE;


        public static BlinkType get(int id) {
            //no enhanced switch for back compat
            switch (id) {
                case 3:
                    return ONE_PIXEL;
                case 4:
                    return TWO_PIXEL;
                case 5:
                    return FOUR_PIXEL;
                case 1:
                    return WHOLE_FACE;
                case 2:
                    return WHOLE_FACE_TWO;
                default:
                    return NONE;
            }
        }

        public Text getTitle() {
            //no enhanced switch for back compat
            switch (this) {
                case ONE_PIXEL:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.1");
                case TWO_PIXEL:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.2");
                case FOUR_PIXEL:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.4");
                case WHOLE_FACE:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.whole.1");
                case WHOLE_FACE_TWO:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.whole.2");
                default:
                    return ETFVersionDifferenceHandler.getTextFromTranslation("config." + MOD_ID + ".player_skin_editor.blink_type.none");
            }
        }

        public int getBlinkPixelColour() {
            //no enhanced switch for back compat
            switch (this) {
                case ONE_PIXEL:
                    return getPixelColour(3);
                case TWO_PIXEL:
                    return getPixelColour(4);
                case FOUR_PIXEL:
                    return getPixelColour(5);
                case WHOLE_FACE:
                    return getPixelColour(1);
                case WHOLE_FACE_TWO:
                    return getPixelColour(2);
                default:
                    return 0;
            }
        }

        public int getMaxEyePixelHeight() {
            //no enhanced switch for back compat
            switch (this) {
                case ONE_PIXEL:
                    return 8;
                case TWO_PIXEL:
                    return 7;
                case FOUR_PIXEL:
                    return 5;
                default:
                    return 1;
            }
        }

        public BlinkType next() {
            //no enhanced switch for back compat
            switch (this) {
                case NONE:
                    return ONE_PIXEL;
                case ONE_PIXEL:
                    return TWO_PIXEL;
                case TWO_PIXEL:
                    return FOUR_PIXEL;
                case FOUR_PIXEL:
                    return WHOLE_FACE;
                case WHOLE_FACE:
                    return WHOLE_FACE_TWO;
                default:
                    return NONE;
            }
        }
    }

    public void drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate((double)x, (double)y, 1050.0);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = (flipView ? 0 : 180.0F) + f * 20.0F;
        entity.setYaw((flipView ? 0 : 180.0F) + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrixStack2, immediate, 15728880);
        });
        immediate.draw();
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

}
