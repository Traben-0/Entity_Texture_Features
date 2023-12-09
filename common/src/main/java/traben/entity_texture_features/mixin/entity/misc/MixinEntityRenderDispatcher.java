package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    @Inject(method = "render",
            at = @At(value = "HEAD"))
    private <E extends Entity> void etf$grabContext(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ETFRenderContext.setCurrentEntity((ETFEntity) entity);
        ETFRenderContext.setCurrentProvider(vertexConsumers);

    }

    @Inject(method = "render",
            at = @At(value = "RETURN"))
    private <E extends Entity> void etf$clearContext(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ETFRenderContext.reset();

    }


    @ModifyArg(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
            index = 4
    )
    private VertexConsumerProvider etf$injectIntoGetBuffer(VertexConsumerProvider vertexConsumers) {
        return layer -> ETFRenderContext.processVertexConsumer(vertexConsumers, layer);
    }
}
