package traben.entity_texture_features.features;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFVertexConsumer;

import java.util.Optional;

public class ETFRenderContext {


    public static boolean renderingFeatures = false;

    private static boolean allowRenderLayerTextureModify = true;
    private static ETFEntity currentEntity = null;
    private static int currentModelPartDepth = 0;


    private static boolean isInSpecialRenderOverlayPhase = false;

    public static boolean isRenderingFeatures() {
        return renderingFeatures;
    }

    public static void setRenderingFeatures(boolean renderingFeatures) {
        ETFRenderContext.renderingFeatures = renderingFeatures;
    }

    public static boolean isAllowedToRenderLayerTextureModify() {
        return allowRenderLayerTextureModify && ETFConfig.getConfig().enableCustomTextures;
    }

    public static void preventRenderLayerTextureModify() {
        ETFRenderContext.allowRenderLayerTextureModify = false;
    }

    public static void allowRenderLayerTextureModify() {
        ETFRenderContext.allowRenderLayerTextureModify = true;
    }

    @Nullable
    public static ETFEntity getCurrentEntity() {
        return currentEntity;
    }

    public static boolean canRenderInBrightMode(){
        boolean setForBrightMode = ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT;
        if(setForBrightMode){
            if(currentEntity != null){
                return currentEntity.etf$canBeBright();// && !ETFRenderContext.getCurrentETFTexture().isPatched_CurrentlyOnlyArmor();
            }else{
                //establish default rule
                return true;
            }
        }
        return false;
    }

    public static boolean shouldEmissiveUseCullingLayer(){
        if(currentEntity != null){
            return currentEntity.etf$isBlockEntity();
        }else{
            //establish default rule
            return true;
        }
    }

    public static void setCurrentEntity(ETFEntity currentEntity) {
        //assert this
        allowRenderLayerTextureModify = true;
        ETFRenderContext.currentEntity = currentEntity;
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

    public static void reset() {
        currentModelPartDepth = 0;
        currentEntity = null;
        allowedToPatch = false;
        allowRenderLayerTextureModify = true;
    }

    @SuppressWarnings("unused")//used in EMF
    public static boolean isIsInSpecialRenderOverlayPhase() {
        return isInSpecialRenderOverlayPhase;
    }

    public static void startSpecialRenderOverlayPhase() {
        ETFRenderContext.isInSpecialRenderOverlayPhase = true;
    }

    public static void endSpecialRenderOverlayPhase() {
        ETFRenderContext.isInSpecialRenderOverlayPhase = false;
    }

    public static boolean isAllowedToPatch() {
        return allowedToPatch;
    }

    public static void allowTexturePatching() {
        allowedToPatch = true;
    }
    public static void preventTexturePatching() {
        allowedToPatch = false;
    }
    private static boolean allowedToPatch = false;

    public static RenderLayer modifyRenderLayerIfRequired(RenderLayer value) {

        if (isCurrentlyRenderingEntity()
                && isAllowedToRenderLayerTextureModify()
                && ETFManager.getInstance().ENTITY_TYPE_RENDER_LAYER.containsKey(currentEntity.etf$getType())
                && !value.isOutline()
                && value instanceof ETFRenderLayerWithTexture multiphase) {

            Optional<Identifier> texture = multiphase.etf$getId();
            if (texture.isPresent()) {
                preventRenderLayerTextureModify();
                RenderLayer forReturn = switch (ETFManager.getInstance().ENTITY_TYPE_RENDER_LAYER.getInt(currentEntity.etf$getType())) {
                    case 1 -> RenderLayer.getEntityTranslucent(texture.get());
                    case 2 -> RenderLayer.getEntityTranslucentCull(texture.get());
                    case 3 -> RenderLayer.getEndGateway();
                    case 4 -> RenderLayer.getOutline(texture.get());
                    default -> value;
                };
                allowRenderLayerTextureModify();
                return forReturn;
            }
        }
        return value;
    }

    public static void insertETFDataIntoVertexConsumer(VertexConsumerProvider provider, RenderLayer renderLayer, VertexConsumer vertexConsumer){
        if(isCurrentlyRenderingEntity() && vertexConsumer instanceof ETFVertexConsumer etfVertexConsumer) {
            //need to store etf texture of consumer and original render layer
            //store provider as well for future actions
            etfVertexConsumer.etf$initETFVertexConsumer(provider, renderLayer);
        }
    }

    public static boolean isCurrentlyRenderingEntity(){
        return currentEntity != null;
    }
}
