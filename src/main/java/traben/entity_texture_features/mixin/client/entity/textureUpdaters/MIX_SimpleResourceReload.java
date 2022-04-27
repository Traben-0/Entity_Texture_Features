package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.minecraft.resource.SimpleResourceReload;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.ETF_METHODS;

import static traben.entity_texture_features.client.ETF_CLIENT.ETF_irisDetected;


@Mixin(SimpleResourceReload.class)
public abstract class MIX_SimpleResourceReload implements ETF_METHODS {


    private static boolean falseAfterFirstRun = true;

    @Inject(method = "getProgress", at = @At("RETURN"))
    private void ETF_injected(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValue() == 1.0) {
            if (falseAfterFirstRun) {
                falseAfterFirstRun = false;
                ETF_resetVisuals();
                ModList.get().forEachModContainer((name, mod) -> {
                    if (name.contains("iris") || name.contains("oculus")) {
                        ETF_modMessage("Entity Texture Features - Iris mod detected : message will be shown in settings", false);
                        ETF_irisDetected = true;
                    }
                });
            }
        } else {
            falseAfterFirstRun = true;
        }
    }
}


