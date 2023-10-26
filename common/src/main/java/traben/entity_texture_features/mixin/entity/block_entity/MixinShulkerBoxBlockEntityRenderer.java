package traben.entity_texture_features.mixin.entity.block_entity;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.mixin.accessor.SpriteContentsAccessor;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFBlockEntityWrapper;

import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public abstract class MixinShulkerBoxBlockEntityRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity> {

    @Final
    @Shadow
    private ShulkerEntityModel<?> model;

    @Unique
    private ETFBlockEntityWrapper etf$shulkerBoxWrapper = null;
    @Unique
    private VertexConsumerProvider etf$vertexConsumerProviderOfThis = null;

    @Unique
    private boolean entity_texture_features$isAnimatedTexture = false;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$injected(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, Direction direction, SpriteIdentifier spriteIdentifier) {
        try {
            entity_texture_features$isAnimatedTexture = ((SpriteContentsAccessor) spriteIdentifier.getSprite().getContents()).callGetFrameCount() != 1;
            if (!entity_texture_features$isAnimatedTexture) {
                this.entity_texture_features$thisETFTexture = null;
                if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities) {
                    etf$vertexConsumerProviderOfThis = vertexConsumerProvider;
                    try {
                        DyeColor color = shulkerBoxBlockEntity.getColor();
                        int hash;
                        if (shulkerBoxBlockEntity.hasCustomName()) {
                            hash = color == null ? 0 : color.hashCode()
                                    + Objects.requireNonNull(shulkerBoxBlockEntity.getCustomName()).getString().hashCode();
                        }else{
                            hash = color == null ? 0 : color.hashCode();
                        }
                        etf$shulkerBoxWrapper = new ETFBlockEntityWrapper(shulkerBoxBlockEntity, hash);

                        String path = "textures/" + spriteIdentifier.getTextureId().getPath() + ".png";
                        Identifier texture = new Identifier(spriteIdentifier.getTextureId().getNamespace(), path);

                        entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(texture, etf$shulkerBoxWrapper, ETFManager.TextureSource.BLOCK_ENTITY, ETFConfigData.removePixelsUnderEmissiveBlockEntity);
                    } catch (Exception e) {
                        //ETFUtils2.logError("shulker box custom rendering failed during setup, " + e);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"),
            index = 1)
    private VertexConsumer etf$alterTexture(VertexConsumer vertices) {
        if (entity_texture_features$thisETFTexture == null || entity_texture_features$isAnimatedTexture || !ETFConfigData.enableCustomTextures || !ETFConfigData.enableCustomBlockEntities || etf$shulkerBoxWrapper == null)
            return vertices;
        try {
            VertexConsumer alteredReturn = etf$vertexConsumerProviderOfThis.getBuffer(RenderLayer.getEntityCutoutNoCull(entity_texture_features$thisETFTexture.getTextureIdentifier(etf$shulkerBoxWrapper)));
            return alteredReturn == null ? vertices : alteredReturn;
        } catch (Exception e) {
            return vertices;
        }

    }

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ShulkerEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void etf$emissiveTime(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if (!entity_texture_features$isAnimatedTexture && ETFConfigData.enableEmissiveBlockEntities && entity_texture_features$thisETFTexture != null) {
            try {
                entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, this.model, ETFManager.EmissiveRenderModes.blockEntityMode());
            } catch (Exception ignored) {}
        }
    }


}


