package traben.entity_texture_features.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;

import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;
import static traben.entity_texture_features.ETFClientCommon.MOD_ID;

@Mixin(PackScreen.class)
public abstract class MixinPackScreen extends Screen {


    protected MixinPackScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void etf$illegalPathOverride(CallbackInfo ci) {
        if (!ETFConfigData.hideConfigButton) {
            this.addDrawableChild(new TexturedButtonWidget((this.width - 48), (this.height - 40), 32, 32,
                    0, 0, 32, new Identifier(MOD_ID + ":textures/gui/settings.png"), 32, 64,
                    (button) -> Objects.requireNonNull(client).setScreen(new ETFConfigScreenMain(this))));
        }
    }
}

