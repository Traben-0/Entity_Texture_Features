package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.irisDetected;
import static traben.entity_texture_features.client.entity_texture_features_CLIENT.puzzleDetected;

@Mixin(ClientPlayerEntity.class)
public abstract class MIX_ClientPlayerEntity {

    private static boolean messageOnce = true;

//just send message to player on load
    @Inject(method = "tick", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {
        if (messageOnce) {
            if (irisDetected) {
//                try {
//                    MinecraftClient.getInstance().player.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: Iris shader mod detected!\n Implementing Emissive shader Z-fighting fix.\n Glowing parts will float slightly :/"),false);
//                } catch (NullPointerException e) {
                    System.out.println("Entity Texture Features - Iris shader mod detected! Implementing Emissive shader Z-fighting fix");
//                }
            }
            if (puzzleDetected){
//                try {
//                    MinecraftClient.getInstance().player.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities from that mod!"),false);
//                }catch(NullPointerException e){
                    System.out.println("Entity Texture Features - @Motschen's Mod 'Puzzle' was detected: please ensure you disable emissive entities in that mod!");
//                }
            }
            messageOnce = false;
        }
    }

}


