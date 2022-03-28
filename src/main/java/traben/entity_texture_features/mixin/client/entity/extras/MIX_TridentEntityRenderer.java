package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.ETF_ConfigData;
import static traben.entity_texture_features.client.ETF_CLIENT.ETF_UUID_TridentName;

@Mixin(TridentEntityRenderer.class)
public abstract class MIX_TridentEntityRenderer implements SynchronousResourceReloader, ETF_METHODS {
    @Shadow
    @Final
    private TridentEntityModel model;

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/TridentEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void ETF_changeEmissiveTexture(TridentEntity tridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (ETF_ConfigData.enableTridents && ETF_ConfigData.enableEmissiveTextures) {
            UUID id = tridentEntity.getUuid();
            String path = TridentEntityModel.TEXTURE.toString();
            String name = ETF_UUID_TridentName.get(id) != null ? "_" + ETF_UUID_TridentName.get(id).toLowerCase().replaceAll("[^a-z0-9/_.-]", "") : "";
            String fileString = ETF_UUID_TridentName.get(id) != null ? path.replace(".png", "_" + name + ".png") : path;
            ETF_GeneralEmissiveRender(matrixStack, vertexConsumerProvider, fileString, this.model);


        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/TridentEntityRenderer;getTexture(Lnet/minecraft/entity/projectile/TridentEntity;)Lnet/minecraft/util/Identifier;"))
    private Identifier ETF_returnTexture(TridentEntityRenderer instance, TridentEntity tridentEntity) {
        if (ETF_ConfigData.enableTridents && ETF_ConfigData.enableCustomTextures) {
            UUID id = tridentEntity.getUuid();
            if (ETF_UUID_TridentName.get(id) != null) {
                String path = TridentEntityModel.TEXTURE.toString();
                String name = ETF_UUID_TridentName.get(id).toLowerCase().replaceAll("[^a-z0-9/_.-]", "");
                Identifier possibleId = new Identifier(path.replace(".png", "_" + name + ".png"));
                if (ETF_isExistingFile(possibleId)) {
                    return possibleId;
                }
            }

        }
        return instance.getTexture(tridentEntity);
    }
}


