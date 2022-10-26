package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.texture_handlers.ETFManager;


@Mixin(PiglinEntityRenderer.class)
public abstract class MixinPiglinEntityRenderer{




    @ModifyVariable(method = "getPiglinModel", at = @At("HEAD"), index = 2, argsOnly = true)
    private static boolean injected(boolean zombie) {
        //if it is a zombie pigling and we want to override the vanilla behaviour of hiding zombie piglin's right ear
        if(zombie && ETFClientCommon.ETFConfigData.zombiePiglinRightEarEnabled){
            return false;
        }
        return zombie;
    }
}


