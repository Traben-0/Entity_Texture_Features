package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.ETF_METHODS;

import static traben.entity_texture_features.client.ETF_CLIENT.puzzleDetected;


@Mixin(SimpleResourceReload.class)
public abstract class MIX_SimpleResourceReload implements ETF_METHODS {


    private static boolean falseAfterFirstRun = true;

    @Inject(method = "getProgress", at = @At("RETURN"))
    private void injected(CallbackInfoReturnable<Float> cir) {
        if (cir.getReturnValue() == 1.0) {
            if (falseAfterFirstRun) {
                falseAfterFirstRun = false;
                resetVisuals();
                for (ModContainer mod :
                        FabricLoader.getInstance().getAllMods()) {
                    if (mod.toString().contains("puzzle")) {
                        modMessage("Entity Texture Features - @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities in that mod!", false);
                        puzzleDetected = true;
                    }
                }
            }
        } else {
            falseAfterFirstRun = true;
        }
    }
}


