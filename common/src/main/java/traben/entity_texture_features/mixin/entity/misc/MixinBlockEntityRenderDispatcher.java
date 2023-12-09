package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @ModifyVariable(
            method = "method_23080",
            at = @At(value = "HEAD"),
            index = 3,
            argsOnly = true)
    private static VertexConsumerProvider etf$injectIntoGetBuffer(VertexConsumerProvider vertexConsumers) {
        ETFRenderContext.setCurrentProvider(vertexConsumers);
        return layer -> ETFRenderContext.processVertexConsumer(vertexConsumers, layer);
    }

    @ModifyVariable(
            method = "method_23081",
            at = @At(value = "HEAD"),
            index = 4,
            argsOnly = true)
    private static VertexConsumerProvider etf$injectIntoGetBuffer2(VertexConsumerProvider vertexConsumers) {
        ETFRenderContext.setCurrentProvider(vertexConsumers);
        return layer -> ETFRenderContext.processVertexConsumer(vertexConsumers, layer);
    }
}
