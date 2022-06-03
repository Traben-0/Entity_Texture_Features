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
import traben.entity_texture_features.client.utils.ETFUtils;

import java.util.Random;
import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;
import static traben.entity_texture_features.client.ETFClient.KNOWN_UUID_LIST;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRender<T extends Entity> {


    private final Random random = new Random();

    @Inject(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private void etf$injected(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        UUID etf$recentID = entity.getUuid();
        KNOWN_UUID_LIST.put(etf$recentID, entity.getId());


        //check if some mob data can be cleared randomly
        if (random.nextInt(1000) == 1) {
            ETFUtils.tryClearUneededMobData();
        }
    }




}
