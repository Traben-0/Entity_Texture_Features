package traben.entity_texture_features.mixin.entity.block_entity;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.mixin.accessor.SpriteContentsAccessor;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFBlockEntityWrapper;

import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class MixinShulkerBoxBlockEntityRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {

    @Final
    @Shadow
    private ShulkerEntityModel<?> model;

    private ETFBlockEntityWrapper etf$shulkerBoxStandInDummy = null;
    private VertexConsumerProvider etf$vertexConsumerProviderOfThis = null;
    private Identifier etf$textureOfThis = null;

    private boolean isAnimatedTexture = false;
    private ETFTexture thisETFTexture = null;

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$injected(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, Direction direction, SpriteIdentifier spriteIdentifier) {
        try {
            isAnimatedTexture = ((SpriteContentsAccessor) spriteIdentifier.getSprite().getContents()).callGetFrameCount() != 1;
            if (!isAnimatedTexture) {
                if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities) {
                    etf$vertexConsumerProviderOfThis = vertexConsumerProvider;
                    try {
                        //etf$shulkerBoxStandInDummy = new ArmorStandEntity(EntityType.ARMOR_STAND, shulkerBoxBlockEntity.getWorld());
                        // etf$shulkerBoxStandInDummy.setPos(shulkerBoxBlockEntity.getPos().getX(), shulkerBoxBlockEntity.getPos().getY(), shulkerBoxBlockEntity.getPos().getZ());
                        String identifier = "shulker" + shulkerBoxBlockEntity.getPos().toString() + shulkerBoxBlockEntity.getColor();

                        if (shulkerBoxBlockEntity.hasCustomName()) {
                            //noinspection ConstantConditions
                            identifier += shulkerBoxBlockEntity.getCustomName().getString();
                        }
                        //shulker boxes don't have uuid so set UUID from something repeatable this uses blockPos & container name
                        //etf$shulkerBoxStandInDummy.setUuid(UUID.nameUUIDFromBytes(identifier.getBytes()));

                        //etf$chestStandInDummy.setPos(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ());

                        //chests don't have uuid so set UUID from something repeatable this uses blockPos chestType & container name
                        World worldCheck = shulkerBoxBlockEntity.getWorld();
                        if (worldCheck == null) worldCheck = MinecraftClient.getInstance().world;
                        if (worldCheck != null) {
                            etf$shulkerBoxStandInDummy = new ETFBlockEntityWrapper(shulkerBoxBlockEntity, UUID.nameUUIDFromBytes(identifier.getBytes()));
                        }

                    } catch (Exception e) {
                        //ETFUtils2.logError("shulker box custom rendering failed during setup, " + e);
                    }
                }

                String path = "textures/" + spriteIdentifier.getTextureId().getPath() + ".png";

                etf$textureOfThis = new Identifier(spriteIdentifier.getTextureId().getNamespace(), path);

            }
        } catch (Exception ignored) {

        }
    }

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 1)
    private VertexConsumer etf$alterTexture(VertexConsumer vertices) {
        try {
            if (isAnimatedTexture || !ETFConfigData.enableCustomTextures || !ETFConfigData.enableCustomBlockEntities || etf$textureOfThis == null || etf$shulkerBoxStandInDummy == null)
                return vertices;

            thisETFTexture = ETFManager.getInstance().getETFTexture(etf$textureOfThis, etf$shulkerBoxStandInDummy, ETFManager.TextureSource.BLOCK_ENTITY, ETFConfigData.removePixelsUnderEmissiveBlockEntity);
            @SuppressWarnings("ConstantConditions") VertexConsumer alteredReturn = thisETFTexture == null ? null : etf$vertexConsumerProviderOfThis.getBuffer(RenderLayer.getEntityCutoutNoCull(thisETFTexture.getTextureIdentifier(etf$shulkerBoxStandInDummy)));
            return alteredReturn == null ? vertices : alteredReturn;
        } catch (Exception e) {
            return vertices;
        }

    }

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void etf$emissiveTime(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        try {
            if (!isAnimatedTexture && ETFConfigData.enableEmissiveBlockEntities && (thisETFTexture != null)) {
                thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, this.model, ETFManager.EmissiveRenderModes.blockEntityMode());
            }
        } catch (Exception ignored) {

        }
    }


}


