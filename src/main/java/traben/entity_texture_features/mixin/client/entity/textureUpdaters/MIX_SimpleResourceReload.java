package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.entity_texture_features_METHODS;


import java.util.Objects;

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
                    try {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities from that mod!"),false);
                    }catch(NullPointerException e){
                        System.out.println("Entity Texture Features - @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities in that mod!");
                    }
                    puzzleDetected = true;
                }
                if (mod.toString().contains("iris ")){
                    try {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: Iris shader mod detected!\n Implementing Emissive shader Z-fighting fix.\n Glowing parts will float slightly :/"),false);
                    }catch(NullPointerException e) {
                        System.out.println("Entity Texture Features - Iris shader mod detected! Implementing Emissive shader Z-fighting fix");
                    }
                    irisDetected = true;
                }
            }
        }
    }
}


