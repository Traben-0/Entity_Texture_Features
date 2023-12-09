package traben.entity_texture_features.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.features.ETFRenderContext;

import java.util.function.Function;

@Mixin(SpriteIdentifier.class)
public class MixinSpriteIdentifier {
    @Inject(method = "getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$modifyIfRequired(VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory, CallbackInfoReturnable<VertexConsumer> cir) {
        cir.setReturnValue(ETFRenderContext.processSpriteVertexConsumer(layerFactory, cir.getReturnValue()));
    }

}
