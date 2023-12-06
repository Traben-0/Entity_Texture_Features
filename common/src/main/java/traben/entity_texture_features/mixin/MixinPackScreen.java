package traben.entity_texture_features.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

import java.nio.file.Path;
import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@Mixin(PackScreen.class)
public abstract class MixinPackScreen extends Screen {


    @Unique
    private static final Identifier etf$FOCUSED = new Identifier(MOD_ID, "textures/gui/settings_focused.png");
    @Unique
    private static final Identifier etf$UNFOCUSED = new Identifier(MOD_ID, "textures/gui/settings_unfocused.png");
    @Shadow
    @Final
    private Path file;
    @Shadow
    private ButtonWidget doneButton;

    @SuppressWarnings("unused")
    protected MixinPackScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void etf$etfButton(CallbackInfo ci) {
        if (!ETFConfigData.hideConfigButton
                && this.client != null
                //ensure this is the resource-pack screen and not the data-pack screen
                && this.file.equals(this.client.getResourcePackDir())
                //fabric api required for mod asset texture loading
                && (ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric"))
            //check for 1.20.2
//                && MinecraftClient.getInstance().getResourceManager().getResource(etf$focused).isPresent()
        ) {

            int x = doneButton.getX() + doneButton.getWidth() + 8;
            int y = doneButton.getY();

// simple text button
//            this.addDrawableChild(ButtonWidget.builder(Text.of("ETF"),
//                            (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(this)))
//                    .dimensions(x, y, 24, 20)
//                    .tooltip(Tooltip.of(ETFVersionDifferenceHandler.getTextFromTranslation(
//                            "config.entity_texture_features.button_tooltip")))
//                    .build()
//            );

            //1.20.2 onwards textured button requires these overrides
            this.addDrawableChild(new TexturedButtonWidget(
                    x, y, 24, 20,
                    new ButtonTextures(etf$UNFOCUSED, etf$FOCUSED),
                    (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(this)),
                    Text.translatable(MOD_ID + ".open_tooltip")) {
                {
                    setTooltip(Tooltip.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config.entity_texture_features.button_tooltip")));
                }

                //override required because textured button widget just doesnt work
                @Override
                public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    Identifier identifier = this.isSelected() ? etf$FOCUSED : etf$UNFOCUSED;
                    context.drawTexture(identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
                }

            });

        }
    }
}


