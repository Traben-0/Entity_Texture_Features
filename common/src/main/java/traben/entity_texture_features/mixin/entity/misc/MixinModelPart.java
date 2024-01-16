package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.config.ETFConfig;

import static traben.entity_texture_features.ETFClientCommon.ELYTRA_MODELPART_TO_SKIP;

@Mixin(ModelPart.class)
public abstract class MixinModelPart {

    @Inject(method = "renderCuboids", at = @At(value = "HEAD"), cancellable = true)
    private void etf$injected(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (ETFConfig.getInstance().enableCustomTextures) {
            if(ELYTRA_MODELPART_TO_SKIP.contains((ModelPart) (Object)this)){
                ci.cancel();
            }
        }
    }
}
