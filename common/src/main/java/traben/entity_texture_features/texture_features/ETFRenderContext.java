package traben.entity_texture_features.texture_features;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;

import java.util.Optional;

public class ETFRenderContext {

//    private static Object currentTopPart = null;

    private static VertexConsumerProvider currentProvider = null;

    private static RenderLayer currentRenderLayer = null;

    public static ETFTexture getCurrentETFTexture() {
        return currentETFTexture;
    }

    public static void setCurrentETFTexture(ETFTexture currentETFTexture) {
        ETFRenderContext.currentETFTexture = currentETFTexture;
    }

    private static ETFTexture currentETFTexture = null;

//    public static void setCurrentTopPart(Object currentTopPart) {
//        ETFRenderContext.currentTopPart = currentTopPart;
//    }

    public static void setCurrentProvider(VertexConsumerProvider currentProvider) {
        ETFRenderContext.currentProvider = currentProvider;
    }

    public static void setCurrentRenderLayer(RenderLayer currentRenderLayer) {
        currentETFTexture = null;
        ETFRenderContext.currentRenderLayer = currentRenderLayer;

        //seems they aren't always this class? probably some sprite bullshit seems to happen with block entities
        if(currentRenderLayer instanceof RenderLayer.MultiPhase multiPhase){
//            RenderLayer.MultiPhaseParameters params = multiPhase.phases;
//            RenderPhase.TextureBase base = params.texture;
            Optional<Identifier> possibleId = multiPhase.phases.texture.getId();
            // ETFManager.getInstance().getETFTexture(texture,ETFRenderContext.getCurrentEntity(), ETFManager.TextureSource.ENTITY,false);
            possibleId.ifPresent(identifier -> currentETFTexture = ETFManager.getInstance().getETFDefaultTexture(identifier, false));
        }else {
            System.out.println("failed as not multiphase");
        }
    }

    public static void setCurrentEntity(ETFEntity currentEntity) {
        ETFRenderContext.currentEntity = currentEntity;
    }

//    public static Object getCurrentTopPart() {
//        return currentTopPart;
//    }

    public static VertexConsumerProvider getCurrentProvider() {
        return currentProvider;
    }

    public static RenderLayer getCurrentRenderLayer() {
        return currentRenderLayer;
    }

    public static ETFEntity getCurrentEntity() {
        return currentEntity;
    }

    private static ETFEntity currentEntity = null;


    public static boolean isRenderReady(){
        return currentRenderLayer != null && currentProvider != null && currentEntity != null && currentETFTexture != null;
    }

    public static int getCurrentModelPartDepth() {
        return currentModelPartDepth;
    }

    public static void incrementCurrentModelPartDepth() {
        currentModelPartDepth++;
    }
    public static void decrementCurrentModelPartDepth() {
        currentModelPartDepth--;
    }

    public static void resetCurrentModelPartDepth() {
        currentModelPartDepth = 0;
    }
    private static int currentModelPartDepth = 0;

    public static void reset(){
        currentETFTexture = null;
        currentProvider = null;

//        currentTopPart = null;
        currentModelPartDepth = 0;

        currentRenderLayer = null;
        currentEntity = null;
    }
}
