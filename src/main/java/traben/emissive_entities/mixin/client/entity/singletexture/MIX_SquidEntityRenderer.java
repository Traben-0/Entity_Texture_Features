package traben.emissive_entities.mixin.client.entity.singletexture;

import net.minecraft.client.render.entity.BatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static traben.emissive_entities.client.EmissiveClient.addSingleEmissiveRenderer;

@Mixin(SquidEntityRenderer.class)
public class MIX_SquidEntityRenderer {


    @Shadow
    @Final
    private static Identifier TEXTURE;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injected(EntityRendererFactory.Context ctx, SquidEntityModel model, CallbackInfo ci) {
        addSingleEmissiveRenderer((LivingEntityRenderer) (Object)this,ctx,TEXTURE.getPath().replace(".png","_e.png"));
    }
}
