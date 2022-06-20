package traben.entity_texture_features.mixin.client.entity.blockEntity;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;

import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class MixinShulkerBoxBlockEntityRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {

    @Final
    @Shadow
    private ShulkerEntityModel<?> model;

    private ArmorStandEntity etf$shulkerBoxStandInDummy = null;
    private VertexConsumerProvider etf$vertexConsumerProviderOfThis = null;
    private Identifier etf$textureOfThis = null;


    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "HEAD"))
    private void etf$injected(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {

        if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities ) {
            etf$vertexConsumerProviderOfThis = vertexConsumerProvider;
            try {
                etf$shulkerBoxStandInDummy = new ArmorStandEntity(EntityType.ARMOR_STAND, shulkerBoxBlockEntity.getWorld());
                etf$shulkerBoxStandInDummy.setPos(shulkerBoxBlockEntity.getPos().getX(), shulkerBoxBlockEntity.getPos().getY(), shulkerBoxBlockEntity.getPos().getZ());
                String identifier = "shulker" + shulkerBoxBlockEntity.getPos().toString();
                etf$shulkerBoxStandInDummy.setCustomName(shulkerBoxBlockEntity.getCustomName());
                etf$shulkerBoxStandInDummy.setCustomNameVisible(shulkerBoxBlockEntity.hasCustomName());
                if (shulkerBoxBlockEntity.hasCustomName()) {
                    //noinspection ConstantConditions
                    identifier += shulkerBoxBlockEntity.getCustomName().getString();
                }
                //shulker boxes don't have uuid so set UUID from something repeatable this uses blockPos & container name
                etf$shulkerBoxStandInDummy.setUuid(UUID.nameUUIDFromBytes(identifier.getBytes()));
            } catch (Exception e) {
                ETFUtils.logError("shulker box custom rendering failed during setup, " + e);
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$injected(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, Direction direction, SpriteIdentifier spriteIdentifier) {
        //etf$shulkerBox = shulkerBoxBlockEntity;
        String path = "textures/" + spriteIdentifier.getTextureId().getPath() + ".png";
        //if(shulkerBoxBlockEntity.getColor() != null){
        //    path = path+ "_" + shulkerBoxBlockEntity.getColor().getName();
        //}
        //path = path + ".png";

        etf$textureOfThis = new Identifier(spriteIdentifier.getTextureId().getNamespace(), path);
        //System.out.println(etf$textureOfThis);

    }


    @ModifyArg(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 1)
    private VertexConsumer etf$alterTexture(VertexConsumer vertices) {
        //System.out.println("doCustom ="+(!ETFConfigData.enableCustomTextures) + ","+(etf$textureOfThis == null) +","+ (etf$shulkerBoxStandInDummy == null));
        if (!ETFConfigData.enableCustomTextures  || !ETFConfigData.enableCustomBlockEntities  || etf$textureOfThis == null || etf$shulkerBoxStandInDummy == null)
            return vertices;
        //System.out.println("pre="+etf$textureOfThis);
        etf$textureOfThis = ETFUtils.generalProcessAndReturnAlteredTexture(etf$textureOfThis, etf$shulkerBoxStandInDummy);
        //System.out.println("alterred="+etf$textureOfThis);
        VertexConsumer alteredReturn = etf$vertexConsumerProviderOfThis.getBuffer(RenderLayer.getEntityCutoutNoCull(etf$textureOfThis));
        return alteredReturn == null ? vertices : alteredReturn;

    }

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void etf$emissiveTime(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if(ETFConfigData.enableEmissiveBlockEntities) {
            ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$textureOfThis, this.model);
        }
        //etf$shulkerBoxStandInDummy = null;
        //etf$vertexConsumerProviderOfThis = null;
        //etf$textureOfThis = null;
    }


}


