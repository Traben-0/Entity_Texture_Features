package traben.entity_texture_features.mixin.mixins.submit;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import com.llamalad7.mixinextras.sugar.Local;

//#if MC >= 26.2
//$$ @Mixin(net.minecraft.client.gui.render.pip.PictureInPictureRenderer.class)
//#else
@Mixin(GuiEntityRenderer.class)
//#endif
public class Mixin_GuiEntityRenderer {

    //#if MC >= 26.2
    //$$ @Inject(method = "prepare",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures(Lnet/minecraft/client/renderer/SubmitNodeStorage;)V"))
    //$$ private <T extends net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState> void emf$initRender2(final CallbackInfo ci, @Local(argsOnly = true) T guiEntityRenderState) {
    //$$     // things get reset by the render dispatcher, re-assert before the actual render
    //$$     if (guiEntityRenderState instanceof GuiEntityRenderState gui) {
    //$$         assertEmfState(gui);
    //$$     }
    //$$ }
    //$$
    //$$ @Inject(method = "prepare", at = @At(value = "TAIL"))
    //$$ private <T extends net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState> void emf$endRender(final CallbackInfo ci, @Local(argsOnly = true) T guiEntityRenderState) {
    //$$     if (guiEntityRenderState instanceof GuiEntityRenderState gui) {
    //$$         end(gui);
    //$$     }
    //$$ }
    //#else
    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures()V"))
    private void emf$initRender2(final CallbackInfo ci, @Local(argsOnly = true) GuiEntityRenderState guiEntityRenderState) {
        // things get reset by the render dispatcher, re-assert before the actual render
        assertEmfState(guiEntityRenderState);
    }

    @Inject(method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "TAIL"))
    private void emf$endRender(final CallbackInfo ci, @Local(argsOnly = true) GuiEntityRenderState guiEntityRenderState) {
        end(guiEntityRenderState);
    }
    //#endif

    @Unique
    private static void assertEmfState(final GuiEntityRenderState guiEntityRenderState) {
        var state = guiEntityRenderState.renderState();
        if (state instanceof HoldsETFRenderState holds && holds.etf$getState() != null) {
            var emf = holds.etf$getState();
            ETFState.mount(emf);
        } else {
            ETFState.mountNone();
        }

        var light = state.lightCoords;
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == ETF.EYES_FEATURE_LIGHT_VALUE) {
            ETFState.startSpecialRenderOverlayPhase();
        }
    }


    @Unique
    private static void end(GuiEntityRenderState guiEntityRenderState) {
        var light = guiEntityRenderState.renderState().lightCoords;
        if (light == ETF.EMISSIVE_FEATURE_LIGHT_VALUE || light == ETF.EYES_FEATURE_LIGHT_VALUE) {
            ETFState.endSpecialRenderOverlayPhase();
        }
        ETFState.unMount();
    }

}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public interface Mixin_GuiEntityRenderer { }
//#endif