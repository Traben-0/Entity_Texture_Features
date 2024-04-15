package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {
    @Inject(method = "runReported",
            at = @At(value = "HEAD"))
    private static void etf$grabContext(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
        ETFRenderContext.setCurrentEntity((ETFEntity) blockEntity);

    }

    @Inject(method = "runReported",
            at = @At(value = "RETURN"))
    private static void etf$clearContext(BlockEntity blockEntity, Runnable runnable, CallbackInfo ci) {
        ETFRenderContext.reset();
    }


    @ModifyArg(method = "render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"),
            index = 4)
    private static int etf$vanillaLightOverride(final int light) {
        //if need to override vanilla brightness behaviour
        //change return with overridden light value still respecting higher block and sky lights
        return ETF.config().getConfig().getLightOverrideBE(light);
    }


}
