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

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRender<T extends Entity> {

    private float etf$animateHeightOfName = 0;

    private UUID etf$recentID = null;

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private void etf$injected(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        etf$recentID = entity.getUuid();
    }

    @ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    public void etf$injected(Args args) {
        //just some fun for the dev's accounts, makes username display bounce
        //fd22e573-178c-415a-94fe-e476b328abfd B1
        //bc2d6979-ddde-4452-8c7d-caefa4aceb01 B2
        //cab7d2e2-519f-4b34-afbd-b65f4542b8a1 J
        if (ETFConfigData.skinFeaturesEnabled) {
            if (etf$recentID.toString().equals("fd22e573-178c-415a-94fe-e476b328abfd")
                    || etf$recentID.toString().equals("bc2d6979-ddde-4452-8c7d-caefa4aceb01")
                    || etf$recentID.toString().equals("cab7d2e2-519f-4b34-afbd-b65f4542b8a1")) {
                etf$animateHeight();
                args.set(2, (etf$animateHeightOfName >= 10 ? 20 - etf$animateHeightOfName : etf$animateHeightOfName) - 3);
            }
        }
    }


    private void etf$animateHeight() {
        etf$animateHeightOfName += 0.1;
        if (etf$animateHeightOfName >= 20) {
            etf$animateHeightOfName = 0;
        }
    }
}
