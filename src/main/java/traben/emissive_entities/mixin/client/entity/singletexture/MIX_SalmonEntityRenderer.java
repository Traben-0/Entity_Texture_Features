package traben.emissive_entities.mixin.client.entity.singletexture;

import net.minecraft.client.render.entity.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static traben.emissive_entities.client.EmissiveClient.addSingleEmissiveRenderer;

@Mixin(SalmonEntityRenderer.class)
public class MIX_SalmonEntityRenderer {


    @Shadow
    @Final
    private static Identifier TEXTURE;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injected(EntityRendererFactory.Context context, CallbackInfo ci) {
        addSingleEmissiveRenderer((LivingEntityRenderer) (Object)this,context,TEXTURE.getPath().replace(".png","_e.png"));
    }
}
