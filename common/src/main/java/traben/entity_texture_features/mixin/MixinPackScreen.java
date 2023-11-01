package traben.entity_texture_features.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

import java.nio.file.Path;
import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(PackScreen.class)
public abstract class MixinPackScreen extends Screen {


    @Shadow
    @Final
    private Path file;

    @SuppressWarnings("unused")
    protected MixinPackScreen(Text title) {
        super(title);
    }

//    @Unique
//    private static Identifier etf$focused = new Identifier(MOD_ID , "textures/gui/settings_focused.png");
//    @Unique
//    private static Identifier etf$unfocused = new Identifier(MOD_ID , "textures/gui/settings_unfocused.png");

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

            this.addDrawableChild(ButtonWidget.builder(Text.of("ETF"),
                            (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(this)))
                    .dimensions((int) (this.width * 0.9), (int) (this.height * 0.8), 24, 20)
                    .tooltip(Tooltip.of(ETFVersionDifferenceHandler.getTextFromTranslation(
                            "config.entity_texture_features.button_tooltip")))
                    .build()
            );

//broken in 1.20.2
//        (int) (this.width * 0.9), (int) (this.height * 0.8), 24, 20,
//                    new ButtonTextures(
//                            //no idea why this doesn't work in 1.20.2
//                            etf$unfocused,
//                            etf$focused
//                    ),
//                    (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(this))));
        }
    }
}


