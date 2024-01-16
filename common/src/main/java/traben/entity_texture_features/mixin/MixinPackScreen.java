package traben.entity_texture_features.mixin;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

import java.io.File;
import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@Mixin(PackScreen.class)
public abstract class MixinPackScreen extends Screen {


    @Shadow
    @Final
    private File file;

    @Shadow
    private ButtonWidget doneButton;

    @SuppressWarnings("unused")
    protected MixinPackScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void etf$etfButton(CallbackInfo ci) {
        if (!ETFConfig.getInstance().hideConfigButton
                && this.client != null
                //ensure this is the resource-pack screen and not the data-pack screen
                && this.file.equals(this.client.getResourcePackDir())
                //fabric api required for mod asset texture loading
                && (ETFVersionDifferenceHandler.isFabric() == ETFVersionDifferenceHandler.isThisModLoaded("fabric"))) {
            int x = doneButton.x + doneButton.getWidth() + 8;
            int y = doneButton.y;
            this.addDrawableChild(new TexturedButtonWidget(x, y, 24, 20,
                    0, 0, 20, new Identifier(MOD_ID + ":textures/gui/settings.png"), 24, 40,
                    (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(this))));
        }
    }
}


