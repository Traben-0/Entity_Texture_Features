package traben.entity_texture_features.mixin.client.entity.blockEntity;

import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;
import traben.entity_texture_features.mixin.client.accessor.SpriteAccessor;

import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(BedBlockEntityRenderer.class)
public abstract class MixinBedBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {

    private boolean isAnimatedTexture = false;
    @ModifyArg(method = "renderPart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"),
            index = 1)
    private VertexConsumer etf$alterTexture(VertexConsumer vertices) {
        if (isAnimatedTexture || !ETFConfigData.enableCustomTextures || !ETFConfigData.enableCustomBlockEntities )
            return vertices;

        etf$textureOfThis = ETFUtils.generalProcessAndReturnAlteredTexture(etf$textureOfThis, etf$bedStandInDummy);

        VertexConsumer alteredReturn = etf$vertexConsumerProviderOfThis.getBuffer(RenderLayer.getEntityCutout(etf$textureOfThis));
        return alteredReturn == null ? vertices : alteredReturn;
    }


    @Inject(method = "render(Lnet/minecraft/block/entity/BedBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BedBlockEntity;getWorld()Lnet/minecraft/world/World;",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$getChestTexture(BedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, SpriteIdentifier spriteIdentifier) {
        isAnimatedTexture = ((SpriteAccessor)spriteIdentifier.getSprite()).callGetFrameCount() != 1;
        if (!isAnimatedTexture) {
            //hopefully works in modded scenarios, assumes the mod dev uses the actual vanilla code process and texture pathing rules
            String nameSpace = spriteIdentifier.getTextureId().getNamespace();
            String texturePath = "textures/" + spriteIdentifier.getTextureId().getPath() + ".png";
            etf$textureOfThis = new Identifier(nameSpace, texturePath);
            etf$vertexConsumerProviderOfThis = vertexConsumerProvider;
            if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities) {
                etf$bedStandInDummy = new ArmorStandEntity(EntityType.ARMOR_STAND, MinecraftClient.getInstance().world);
                //System.out.println(MinecraftClient.getInstance().world.getBlockState(bedBlockEntity.getPos().down()).toString());
                etf$bedStandInDummy.setPos(bedBlockEntity.getPos().getX(), bedBlockEntity.getPos().getY(), bedBlockEntity.getPos().getZ());
                //chests don't have uuid so set UUID from something repeatable I chose from block pos
                etf$bedStandInDummy.setUuid(UUID.nameUUIDFromBytes(bedBlockEntity.getPos().toString().getBytes()));
            }
        }
    }

    private ArmorStandEntity etf$bedStandInDummy = null;
    private Identifier etf$textureOfThis = null;
    private VertexConsumerProvider etf$vertexConsumerProviderOfThis = null;

    @Inject(method = "renderPart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissiveBed(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ModelPart part, Direction direction, SpriteIdentifier sprite, int light, int overlay, boolean isFoot, CallbackInfo ci, VertexConsumer vertexConsumer) {

        //hopefully works in modded scenarios, assumes the mod dev uses the actual vanilla code process and texture pathing rules
        //String nameSpace = sprite.getTextureId().getNamespace();
        //String texturePath = "textures/" + sprite.getTextureId().getPath() + ".png";

        //System.out.println("bed "+nameSpace +":"+texturePath);
        //Identifier textureID = new Identifier();
        if(!isAnimatedTexture && ETFConfigData.enableEmissiveBlockEntities) {
            ETFUtils.generalEmissiveRenderPart(matrices, vertexConsumers, etf$textureOfThis, part, true);
        }
        //VertexConsumer cons = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(new Identifier(nameSpace,texturePath), true));
        //part.render(matrices,cons,15728640,overlay);
    }
}


