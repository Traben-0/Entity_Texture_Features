package traben.entity_texture_features.mixin.mixins.submit;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

@Mixin(ModelFeatureRenderer.class)
public class Mixin_ModelRenderer {

    @Unique
    private static <S> void headLogic(
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
            //#endif
    ) {
        var state = modelSubmit.state();
        //TODO temp fix, will be replaced by improving EMFs implementation and moving upstream

        // Set up the current entity context for this render
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() != null) {
            ETFRenderContext.setCurrentEntity(holds.etf$getState());
        } else if (!(state instanceof EntityRenderState)) { // temp handle block entities and a few others weird instances, will be resolved by above todo
            ETFRenderContext.temp_markRenderingEntity = true;
        } else {
            ETFRenderContext.reset();
        }

        // Handle emissive lighting setup
        if (modelSubmit.lightCoords() == ETF.EMISSIVE_FEATURE_LIGHT_VALUE)
            ETFRenderContext.startSpecialRenderOverlayPhase();
    }

    @Unique
    private static void tailLogic() {
        ETFRenderContext.endSpecialRenderOverlayPhase();
        ETFRenderContext.reset();
    }

    //#if MC >= 26.2
    //$$ @Inject(method = "prepareModel", at = @At(value = "HEAD"))
    //$$ private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit) {
    //$$     headLogic(modelSubmit);
    //$$ }
    //$$
    //$$ @Inject(method = "prepareModel", at = @At(value = "TAIL"))
    //$$ private void emf$endRender(final CallbackInfo ci) {
    //$$     tailLogic();
    //$$ }
    //#else
    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit) {
        headLogic(modelSubmit);
    }

    @Inject(method = "renderTranslucents", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci) {
        tailLogic();
    }

    @Inject(method = "renderBatch", at = @At(value = "TAIL"))
    private void emf$endRender2(final CallbackInfo ci) {
        tailLogic();
    }
    //#endif

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelRenderer { }
//#endif