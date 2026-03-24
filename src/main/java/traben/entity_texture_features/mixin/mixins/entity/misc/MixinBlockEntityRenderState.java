package traben.entity_texture_features.mixin.mixins.entity.misc;
//#if MC >= 12109

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(BlockEntityRenderState.class)
public class MixinBlockEntityRenderState implements HoldsETFRenderState {

    @Unique
    private ETFEntityRenderState etf$state = null;

    @Override
    public @NotNull ETFEntityRenderState etf$getState() {
        assert this.etf$state != null; // something has gone badly wrong usage wise otherwise
        return etf$state;
    }

    @Override
    public void etf$initState(@NotNull final ETFEntity entity) {
        etf$state = ETFEntityRenderState.forEntity(entity);
        etf$state.setVanillaBlockState((BlockEntityRenderState) (Object) this);
    }

    @Inject(method = "extractBase",
            at = @At(value = "HEAD"))
    private static void etf$createRenderState(final BlockEntity blockEntity, final BlockEntityRenderState blockEntityRenderState, final ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, final CallbackInfo ci) {
        var holder = (HoldsETFRenderState) blockEntityRenderState;
        holder.etf$initState((ETFEntity) blockEntity);
        ETFRenderContext.setCurrentEntity(holder.etf$getState()); // either this or the one in the dispatcher is redundant but ill put both for now
    }

    @ModifyExpressionValue(method = "extractBase",
            at = @At(value = "INVOKE",target =
                    //#if MC >= 26.1
                    //$$ "Lnet/minecraft/client/renderer/LevelRenderer;getLightCoords(Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/core/BlockPos;)I"
                    //#else
                    "Lnet/minecraft/client/renderer/LevelRenderer;getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"
                    //#endif
            ))
    private static int etf$vanillaLightOverride(final int light, @Local(argsOnly = true) BlockEntityRenderState blockEntityRenderState) {
        //if need to override vanilla brightness behaviour
        //change return with overridden light value still respecting higher block and sky lights
        return ETF.config().getConfig().getLightOverrideBE(light, ((HoldsETFRenderState) blockEntityRenderState).etf$getState());
    }
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class MixinBlockEntityRenderState {
//$$ }
//#endif
