package traben.emissive_entities.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.DrownedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.util.Identifier;
import traben.emissive_entities.mixin.accessor.ACC_LivingEntityRenderer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface EmissiveClient {

    public static void addEmissiveRenderer(LivingEntityRenderer renderer, EntityRendererFactory.Context context, FeatureRenderer[] add){
        for (FeatureRenderer g:
             add) {
            ((ACC_LivingEntityRenderer)renderer).getFeatures().add(g);
        }
    }
    public static void addSingleEmissiveRenderer(LivingEntityRenderer renderer, EntityRendererFactory.Context context, String str){
        ((ACC_LivingEntityRenderer)renderer).getFeatures().add( new EmissiveFeatureRenderer(renderer){
            @Override
            public RenderLayer getEyesTexture() {
                return RenderLayer.getEyes(new Identifier(str));
            }
        }
        );
    }

}
