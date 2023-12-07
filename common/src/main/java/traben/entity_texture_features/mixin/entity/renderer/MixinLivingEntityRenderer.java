package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.compat.ETF3DSkinLayersUtil;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;


@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    @Unique
    private ETFEntity etf$heldEntity = null;

    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);

    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void etf$markFeatures(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        etf$heldEntity = ETFRenderContext.getCurrentEntity();
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.setRenderingFeatures(true);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void etf$markFeaturesLoopEnd(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        //assert main entity each loop in case of other entities within feature renderer
        ETFRenderContext.setCurrentEntity(etf$heldEntity);
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void etf$markFeaturesEnd(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        ETFRenderContext.setRenderingFeatures(false);
    }

    @ModifyArg(method = "addFeature", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), index = 0)
    private Object etf$3dSkinLayerCompat(Object featureRenderer) {
        // replace 3d skin layers mod feature renderers with ETF's child versions

        try {
            // handler class is only ever accessed if the mod is present
            // prevents NoClassDefFoundError
            if (ETFClientCommon.SKIN_LAYERS_DETECTED
                    && ETFConfigData.use3DSkinLayerPatch
                    && ETF3DSkinLayersUtil.canReplace((FeatureRenderer<?, ?>) featureRenderer)) {
                return ETF3DSkinLayersUtil.getReplacement((FeatureRenderer<?, ?>) featureRenderer, this);
            }
        } catch (Exception e) {
            ETFUtils2.logWarn("Exception with ETF's 3D skin layers mod compatibility: " + e);
        } catch (NoClassDefFoundError error) {
            // Should never be thrown
            // unless a significant change in 3d skin layers mod
            ETFUtils2.logError("Error with ETF's 3D skin layers mod compatibility: " + error);
        }

        return featureRenderer;
    }

}


