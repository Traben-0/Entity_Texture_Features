package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

@Mixin(SimpleResourceReload.class)
public abstract class MIX_SimpleResourceReload implements entity_texture_features_METHODS {




    @Inject(method = "isComplete", at = @At("RETURN"))
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()){
            resetVisuals();
            for (ModContainer mod:
            FabricLoader.getInstance().getAllMods()) {
                if (mod.toString().contains("puzzle")){
//                    try {
//                        MinecraftClient.getInstance().player.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities from that mod!"),false);
//                    }catch(NullPointerException e){
                    modMessage("Entity Texture Features - @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities in that mod!",false);
//                    }
                    puzzleDetected = true;
                }
            }
        }
    }
}


