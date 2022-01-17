package traben.emissive_entities.mixin.client.entity.multitexture;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.emissive_entities.client.EmissiveFeatureRenderer;
import traben.emissive_entities.mixin.accessor.ACC_LivingEntityRenderer;

import static traben.emissive_entities.client.EmissiveClient.addEmissiveRenderer;

@Mixin(DrownedEntityRenderer.class)
public class MIX_DrownedEntityRenderer {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injected(EntityRendererFactory.Context context, CallbackInfo ci) {
        LivingEntityRenderer renderer = (LivingEntityRenderer) (Object)this;
        FeatureRenderer[]adding= {
               // new EmissiveFeatureRenderer(renderer,"textures/entity/zombie/drowned_e.png"),
                new EmissiveFeatureRenderer(renderer) {
                    @Override
                    public RenderLayer getEyesTexture() {
                        return RenderLayer.getEyes(new Identifier("textures/entity/zombie/drowned_e.png"));
                    }
                },
                new DrownedOverlayFeatureRenderer<>(renderer, context.getModelLoader())};

        //remove overlay for drowned
        for (Object f :
                ((ACC_LivingEntityRenderer) renderer).getFeatures()) {
            if (f instanceof DrownedOverlayFeatureRenderer) {
                ((ACC_LivingEntityRenderer) renderer).getFeatures().remove(f);
                break;
            }
        }
        addEmissiveRenderer(renderer,context,adding);
    }

   // this.features.add(new EndermanEyeFeatureRenderer(this))
}
