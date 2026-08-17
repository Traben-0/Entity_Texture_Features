package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;
//#if MC >= 12109
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
//#endif

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

    private static final String RENDER_METHOD =
            //#if MC >= 12109
            "submit";
            //#elseif MC >= 12104
            //$$ "setupAndRender";
            //#else
            //$$ "tryRender";
            //#endif

    @Inject(method = RENDER_METHOD, at = @At(value = "HEAD"))
    //#if MC >= 12109
    private static <S extends BlockEntityRenderState> void etf$grabContext(final CallbackInfo ci, @Local(argsOnly = true) S state, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
    //#elseif MC >= 12104
    //$$ private static <T extends BlockEntity> void etf$grabContext(final CallbackInfo ci, @Local(argsOnly = true) T blockEntity, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
    //#else
    //$$ private static void etf$grabContext(final CallbackInfo ci, @Local(argsOnly = true) BlockEntity blockEntity, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
    //#endif

        //#if MC >= 12109
        var etf = ((HoldsETFRenderState) state).etf$getState();
        //#else
        //$$ var etf = ETFEntityRenderState.forEntity((ETFEntity) blockEntity);
        //#endif
        if (etf != null) {
            ETFState.mount(etf);
        }
        stateRef.set(etf);
    }

    @Inject(method = RENDER_METHOD, at = @At(value = "RETURN"))
    private static void etf$clearContext(CallbackInfo ci, @Share("state_etf") LocalRef<ETFEntityRenderState> stateRef) {
        if (stateRef.get() != null) {
            ETFState.stackVerify(stateRef.get());
            ETFState.unMount();
        }

    }

    //#if MC < 12109
    //$$ @ModifyArg(method = "setupAndRender",
    //$$         at = @At(value = "INVOKE",
                    //#if MC >= 12105
                    //$$ target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/phys/Vec3;)V"),
                    //#else
                    //$$ target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"),
                    //#endif
    //$$         index = 4)
    //$$ private static int etf$vanillaLightOverride(final int light) {
    //$$     //if need to override vanilla brightness behaviour
    //$$     //change return with overridden light value still respecting higher block and sky lights
    //$$     return ETF.config().getConfig().getLightOverrideBE(light);
    //$$ }
    //#endif

}
