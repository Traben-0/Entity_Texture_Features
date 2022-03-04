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

        private float animateHeightOfName = 0;

        private UUID recentID = null;
        @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
        private void injected(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
                recentID = entity.getUuid();
        }

        @ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
        public void injected(Args args) {
                //just some fun for the dev's accounts, makes username display bounce
                //fd22e573-178c-415a-94fe-e476b328abfd
                //bc2d6979-ddde-4452-8c7d-caefa4aceb01
                String name = ((Text)args.get(0)).getString();

                if (recentID.toString().equals("fd22e573-178c-415a-94fe-e476b328abfd")
                        || recentID.toString().equals("bc2d6979-ddde-4452-8c7d-caefa4aceb01")) {
                        animateHeight();
                        args.set(2,(animateHeightOfName >= 10 ? 20-animateHeightOfName : animateHeightOfName)-3);
                }
        }


        private void animateHeight(){
                animateHeightOfName += 0.1;
                if (animateHeightOfName >= 20){
                        animateHeightOfName = 0;
                }
        }
}
