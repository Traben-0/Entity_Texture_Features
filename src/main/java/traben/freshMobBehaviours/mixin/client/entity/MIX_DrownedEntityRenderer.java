package traben.freshMobBehaviours.mixin.client.entity;

import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.client.DrownedGlowFeatureRenderer;
import traben.freshMobBehaviours.mixin.accessor.ACC_LivingEntityRenderer;

@Mixin(DrownedEntityRenderer.class)
public class MIX_DrownedEntityRenderer {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injected(EntityRendererFactory.Context context, CallbackInfo ci) {
        DrownedEntityRenderer drownDrenderer = (DrownedEntityRenderer) (Object)this;
        DrownedOverlayFeatureRenderer over = new DrownedOverlayFeatureRenderer<>(drownDrenderer, context.getModelLoader());

        ((ACC_LivingEntityRenderer)drownDrenderer).getFeatures().add(new DrownedGlowFeatureRenderer(drownDrenderer));
        for (Object f:
        ((ACC_LivingEntityRenderer)drownDrenderer).getFeatures()) {
            if (f instanceof DrownedOverlayFeatureRenderer<?>) {
                ((ACC_LivingEntityRenderer) drownDrenderer).getFeatures().remove(f);
                break;
            }
        }
        ((ACC_LivingEntityRenderer)drownDrenderer).getFeatures().add(over);
    }

   // this.features.add(new EndermanEyeFeatureRenderer(this))
}
