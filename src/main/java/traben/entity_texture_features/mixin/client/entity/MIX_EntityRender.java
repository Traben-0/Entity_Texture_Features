package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.UUID;

@Mixin(EntityRenderer.class)
public abstract class MIX_EntityRender<T extends Entity> {

    private float ETF_animateHeightOfName = 0;

    private UUID ETF_recentID = null;

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private void ETF_injected(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ETF_recentID = entity.getUuid();
    }

    @ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    public void ETF_injected(Args args) {
        //just some fun for the dev's accounts, makes username display bounce
        //fd22e573-178c-415a-94fe-e476b328abfd B1
        //bc2d6979-ddde-4452-8c7d-caefa4aceb01 B2
        //cab7d2e2-519f-4b34-afbd-b65f4542b8a1 J

        if (ETF_recentID.toString().equals("fd22e573-178c-415a-94fe-e476b328abfd")
                || ETF_recentID.toString().equals("bc2d6979-ddde-4452-8c7d-caefa4aceb01")
                || ETF_recentID.toString().equals("cab7d2e2-519f-4b34-afbd-b65f4542b8a1")) {
            ETF_animateHeight();
            args.set(2, (ETF_animateHeightOfName >= 10 ? 20 - ETF_animateHeightOfName : ETF_animateHeightOfName) - 3);
        }
    }


    private void ETF_animateHeight() {
        ETF_animateHeightOfName += 0.1;
        if (ETF_animateHeightOfName >= 20) {
            ETF_animateHeightOfName = 0;
        }
    }
}
