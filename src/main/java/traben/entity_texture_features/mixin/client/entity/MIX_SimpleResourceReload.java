package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.resource.SimpleResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

@Mixin(SimpleResourceReload.class)
public abstract class MIX_SimpleResourceReload {




    @Inject(method = "isComplete", at = @At("RETURN"))
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()){
            System.out.println("Entity Texture Features - Reloading... (this may change all random mobs)");
            UUID_isRandom.clear();// = new HashMap<UUID, Integer[]>() ;
            Texture_Emissive.clear();// = new HashMap<String, Identifier>() ;
            UUID_randomTexture.clear();
        }
    }
}


