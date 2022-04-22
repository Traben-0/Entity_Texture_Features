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
import traben.entity_texture_features.client.ETFUtils;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;
import static traben.entity_texture_features.client.ETFClient.UUID_TRIDENT_NAME;

@Mixin(TridentEntityRenderer.class)
public abstract class MixinTridentEntityRenderer implements SynchronousResourceReloader {
    @Shadow
    @Final
    private TridentEntityModel model;

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/TridentEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void etf$changeEmissiveTexture(TridentEntity tridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (ETFConfigData.enableTridents && ETFConfigData.enableEmissiveTextures) {
            UUID id = tridentEntity.getUuid();
            String path = TridentEntityModel.TEXTURE.toString();
            String name = UUID_TRIDENT_NAME.get(id) != null ? "_" + UUID_TRIDENT_NAME.get(id).toLowerCase().replaceAll("[^a-z0-9/_.-]", "") : "";
            Identifier normalTextureId = new Identifier(UUID_TRIDENT_NAME.get(id) != null ? path.substring(0, path.lastIndexOf(".png")) + "_" + name + ".png" : path);
            ETFUtils.renderEmissiveModel(matrixStack, vertexConsumerProvider, normalTextureId, this.model, false);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/TridentEntityRenderer;getTexture(Lnet/minecraft/entity/projectile/TridentEntity;)Lnet/minecraft/util/Identifier;"))
    private Identifier etf$returnTexture(TridentEntityRenderer instance, TridentEntity tridentEntity) {
        if (ETFConfigData.enableTridents && ETFConfigData.enableCustomTextures) {
            UUID id = tridentEntity.getUuid();
            if (UUID_TRIDENT_NAME.get(id) != null) {
                String path = TridentEntityModel.TEXTURE.toString();
                String name = UUID_TRIDENT_NAME.get(id).toLowerCase().replaceAll("[^a-z0-9/_.-]", "");
                Identifier possibleId = new Identifier(path.substring(0, path.lastIndexOf(".png")) + "_" + name + ".png");
                if (ETFUtils.isExistingNativeImageFile(possibleId)) {
                    return possibleId;
                }
            }

        }
        return instance.getTexture(tridentEntity);
    }
}


