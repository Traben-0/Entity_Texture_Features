package traben.entity_texture_features.mixin.mixins.submit;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.ETFSubmitData;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

@Mixin(ModelFeatureRenderer.class)
public class Mixin_ModelRenderer {

    @Unique
    private <S> void headLogic(
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
            //#endif
    ) {
        var state = modelSubmit.state();
        ETFSubmitData data = ETFSubmitData.from(modelSubmit);

        // Set up the current entity context for this render
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() != null) {
            var etf = holds.etf$getState();
            etf.preSubmitActivate(data, modelSubmit);
            ETFState.mount(etf);
        } else if (data != null && data.backupState != null) { // block entity backup
            var etf = data.backupState;
            etf.preSubmitActivate(data, modelSubmit);
            ETFState.mount(etf);
        } else {
            ETFState.mountNone();
        }

        // Handle emissive/eyes lighting setup
        var light = modelSubmit.lightCoords();
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == ETF.EYES_FEATURE_LIGHT_VALUE) {
            ETFState.startSpecialRenderOverlayPhase();
        }

        if (data != null) {
            ETFSubmitData.DATA_OUT.forEach(entry -> entry.accept(data, modelSubmit));
        }
    }

    @Unique
    private static <S> void tailLogic(
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit
            //#endif
    ) {
        var light = modelSubmit.lightCoords();
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == ETF.EYES_FEATURE_LIGHT_VALUE) {
            ETFState.endSpecialRenderOverlayPhase();
        }

        ETFState.unMount();
    }

    //#if MC >= 26.2
    //$$ @Inject(method = "prepareModel", at = @At(value = "HEAD"))
    //$$ private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit) {
    //$$     headLogic(modelSubmit);
    //$$ }
    //$$
    //$$ @Inject(method = "prepareModel", at = @At(value = "TAIL"))
    //$$ private <S> void emf$endRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<S> modelSubmit) {
    //$$     tailLogic(modelSubmit);
    //$$ }
    //#else
    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private <S> void emf$initRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit) {
        headLogic(modelSubmit);
    }

    @Inject(method = "renderModel", at = @At(value = "TAIL"))
    private <S> void emf$endRender(final CallbackInfo ci, @Local(argsOnly = true) net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<S> modelSubmit) {
        tailLogic(modelSubmit);
    }
    //#endif

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_ModelRenderer { }
//#endif