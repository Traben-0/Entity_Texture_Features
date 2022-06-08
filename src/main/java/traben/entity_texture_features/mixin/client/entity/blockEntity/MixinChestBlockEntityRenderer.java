package traben.entity_texture_features.mixin.client.entity.blockEntity;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;

import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class MixinChestBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/ChestBlockEntityRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V"),
            index = 1)
    private VertexConsumer etf$alterTexture(VertexConsumer vertices) {
        if (!ETFConfigData.enableCustomTextures)
            return vertices;

        etf$textureOfThis = ETFUtils.generalProcessAndReturnAlteredTexture(etf$textureOfThis, etf$chestStandInDummy);

        VertexConsumer alteredReturn = etf$vertexConsumerProviderOfThis.getBuffer(RenderLayer.getEntityCutout(etf$textureOfThis));
        return alteredReturn == null ? vertices : alteredReturn;
    }


    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$getChestTexture(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci, World world, boolean bl, BlockState blockState, ChestType chestType, Block block, AbstractChestBlock<?> abstractChestBlock, boolean bl2, float f, DoubleBlockProperties.PropertySource<?> propertySource, float g, int i, SpriteIdentifier spriteIdentifier) {

        //hopefully works in modded scenarios, assumes the mod dev uses the actual vanilla code process and texture pathing rules
        String nameSpace = spriteIdentifier.getTextureId().getNamespace();
        String texturePath = "textures/" + spriteIdentifier.getTextureId().getPath() + ".png";
        etf$textureOfThis = new Identifier(nameSpace, texturePath);
        etf$vertexConsumerProviderOfThis = vertexConsumers;
        if (ETFConfigData.enableCustomTextures) {
            etf$chestStandInDummy = new ArmorStandEntity(EntityType.ARMOR_STAND, MinecraftClient.getInstance().world);
            etf$chestStandInDummy.setPos(entity.getPos().getX(), entity.getPos().getY(), entity.getPos().getZ());
            String identifier = "chest" + entity.getPos().toString() + chestType.asString();
            if (entity instanceof Nameable nameable) {
                etf$chestStandInDummy.setCustomName(nameable.getCustomName());
                etf$chestStandInDummy.setCustomNameVisible(nameable.hasCustomName());
                if(nameable.hasCustomName()) {
                    //noinspection ConstantConditions
                    identifier += nameable.getCustomName().getString();
                }
            }
            //chests don't have uuid so set UUID from something repeatable this uses blockPos chestType & container name
            etf$chestStandInDummy.setUuid(UUID.nameUUIDFromBytes(identifier.getBytes()));
        }
    }

    private ArmorStandEntity etf$chestStandInDummy = null;
    private Identifier etf$textureOfThis = null;
    private VertexConsumerProvider etf$vertexConsumerProviderOfThis = null;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V",
            at = @At(value = "TAIL"))
    private void etf$renderEmissiveChest(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay, CallbackInfo ci) {
        ETFUtils.generalEmissiveRenderPart(matrices, etf$vertexConsumerProviderOfThis, etf$textureOfThis, lid, true);
        ETFUtils.generalEmissiveRenderPart(matrices, etf$vertexConsumerProviderOfThis, etf$textureOfThis, latch, true);
        ETFUtils.generalEmissiveRenderPart(matrices, etf$vertexConsumerProviderOfThis, etf$textureOfThis, base, true);

        etf$textureOfThis = null;
        etf$vertexConsumerProviderOfThis = null;
    }


}


