package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(TridentEntityRenderer.class)
public abstract class MIX_TridentEntityRenderer implements SynchronousResourceReloader, ETF_METHODS {
    @Shadow
    @Final
    private TridentEntityModel model;

    @Shadow public abstract void render(TridentEntity tridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i);

    @Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target ="Lnet/minecraft/client/render/entity/model/TridentEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void changeEmissiveTexture(TridentEntity tridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (ETFConfigData.enableTridents && ETFConfigData.enableEmissiveTextures) {
            UUID id = tridentEntity.getUuid();
                String path = TridentEntityModel.TEXTURE.getPath();
                String name = UUID_TridentName.get(id) != null ? "_" + UUID_TridentName.get(id).toLowerCase().replaceAll("[^a-z0-9/_.-]", "") : "";
                String fileString = UUID_TridentName.get(id) != null ? path.replace(".png", "_" + name + ".png") : path;
                if (!Texture_Emissive.containsKey(fileString)) {
                    for (String suffix :
                            emissiveSuffix) {
                        Identifier possibleId = new Identifier(fileString.replace(".png", suffix + ".png"));
                        if (isExistingFile(possibleId)) {
                            Texture_Emissive.put(fileString, possibleId);
                        }
                    }
                    if (!Texture_Emissive.containsKey(fileString)) {
                        Texture_Emissive.put(fileString, null);
                    }
                }
                if (Texture_Emissive.get(fileString) != null) {
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString), true));
                    this.model.render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                }

        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/TridentEntityRenderer;getTexture(Lnet/minecraft/entity/projectile/TridentEntity;)Lnet/minecraft/util/Identifier;"))
    private Identifier returnTexture(TridentEntityRenderer instance, TridentEntity tridentEntity) {
        if (ETFConfigData.enableTridents && ETFConfigData.enableRandomTextures) {
            UUID id = tridentEntity.getUuid();
                if (UUID_TridentName.get(id) != null) {
                    String path = TridentEntityModel.TEXTURE.getPath();
                    String name = UUID_TridentName.get(id).toLowerCase().replaceAll("[^a-z0-9/_.-]", "");
                    Identifier possibleId = new Identifier(path.replace(".png", "_" + name + ".png"));
                    if (isExistingFile(possibleId)) {
                        //VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(possibleId), false, tridentEntity.isEnchanted());
                        //this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        return possibleId;
                    }
                }

        }
        return instance.getTexture(tridentEntity);
    }
}


