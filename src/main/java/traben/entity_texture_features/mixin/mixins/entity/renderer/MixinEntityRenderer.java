package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
//#if MC >= 12103
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//#endif
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity
    //#if MC >= 12103
        , S extends EntityRenderState> {

    @Inject(method = "extractRenderState",
    at = @At(value = "TAIL"))
    private void etf$createRenderState(final CallbackInfo ci, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true) S state) {
        ((HoldsETFRenderState) state).etf$initState((ETFEntity) entity);
    }
    //#else
    //$$    > {
    //#endif

    @ModifyReturnValue(method = "getPackedLightCoords", at = @At(value = "RETURN"))
    private int etf$vanillaLightOverrideCancel(int light, @Local(argsOnly = true) T entity, @Local(argsOnly = true) float tickDelta) {
        //if need to override vanilla brightness behaviour
        //change return with overridden light value still respecting higher block and sky lights
        return ETF.config().getConfig().getLightOverride(
                entity,
                tickDelta,
                light);
    }

    //#if MC >= 12109
    private static final String RENDER = "submit";
    //#else
    //$$ private static final String RENDER = "render";
    //#endif

    @Inject(method = RENDER, at = @At(value = "HEAD"))
    private void etf$protectPostRenderersLikeNametag(final CallbackInfo ci) {
        ETFState.pushRenderLayerModifyState(false);
    }

    @Inject(method = RENDER, at = @At(value = "TAIL"))
    private void etf$revertForRenderersThatCallSuperFirst(final CallbackInfo ci) {
        ETFState.popRenderLayerModifyState(); // see minecart rendering
    }

}