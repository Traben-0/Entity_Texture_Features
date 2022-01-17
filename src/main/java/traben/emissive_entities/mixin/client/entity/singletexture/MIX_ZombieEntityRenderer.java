package traben.emissive_entities.mixin.client.entity.singletexture;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.emissive_entities.client.EmissiveFeatureRenderer;

import static traben.emissive_entities.client.EmissiveClient.addEmissiveRenderer;
import static traben.emissive_entities.client.EmissiveClient.addSingleEmissiveRenderer;

@Mixin(ZombieEntityRenderer.class)
public class MIX_ZombieEntityRenderer {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injected(EntityRendererFactory.Context context, CallbackInfo ci) {
        addSingleEmissiveRenderer((LivingEntityRenderer) (Object)this,context,"textures/entity/zombie/zombie_e.png");
    }

   // this.features.add(new EndermanEyeFeatureRenderer(this))
}
