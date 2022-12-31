package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.entity.PiglinEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_texture_features.texture_handlers.ETFManager;


@Mixin(PiglinEntityRenderer.class)
public abstract class MixinPiglinEntityRenderer {




    @ModifyVariable(method = "getPiglinModel", at = @At("HEAD"), index = 0, argsOnly = true)
    private static boolean injected(boolean zombified) {
        //if it is a zombie pigling and we want to override the vanilla behaviour of hiding zombie piglin's right ear
        if(zombified && ETFManager.getInstance().zombiePiglinRightEarEnabled){
            return false;
        }
        return zombified;
    }
}


