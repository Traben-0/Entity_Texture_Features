package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.hasEmissive;
import static traben.entity_texture_features.client.entity_texture_features_CLIENT.randomData;

@Mixin(SimpleResourceReload.class)
public abstract class MIX_SimpleResourceReload {




    @Inject(method = "isComplete", at = @At("RETURN"))
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()){
            System.out.println("Entity Texture Features - Reloading");
            randomData.clear();// = new HashMap<UUID, Integer[]>() ;
            hasEmissive.clear();// = new HashMap<String, Identifier>() ;
        }
    }
}


