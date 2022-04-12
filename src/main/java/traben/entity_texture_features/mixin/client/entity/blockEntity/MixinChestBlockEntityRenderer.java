package traben.entity_texture_features.mixin.client.entity.blockEntity;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.ETFUtils;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class MixinChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> implements BlockEntityRenderer<T> {

    @Final
    @Shadow
    private ModelPart doubleChestLeftLid;
    @Final
    @Shadow
    private ModelPart doubleChestLeftLatch;
    @Final
    @Shadow
    private ModelPart doubleChestLeftBase;

    @Final
    @Shadow
    private ModelPart doubleChestRightLid;
    @Final
    @Shadow
    private ModelPart doubleChestRightLatch;
    @Final
    @Shadow
    private ModelPart doubleChestRightBase;

    @Final
    @Shadow
    private ModelPart singleChestLid;
    @Final
    @Shadow
    private ModelPart singleChestLatch;
    @Final
    @Shadow
    private ModelPart singleChestBase;


    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/ChestBlockEntityRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissiveChest(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci, World world, boolean bl, BlockState blockState, ChestType chestType, Block block, AbstractChestBlock<?> abstractChestBlock, boolean bl2, float f, DoubleBlockProperties.PropertySource<?> propertySource, float g, int i, SpriteIdentifier spriteIdentifier, VertexConsumer vertexConsumer) {

        //hopefully works in modded scenarios, assumes the mod dev uses the actual vanilla code process and texture pathing rules
        String nameSpace = spriteIdentifier.getTextureId().getNamespace();
        String texturePath = "textures/" + spriteIdentifier.getTextureId().getPath();//+".png";

        if (bl2) {
            if (chestType == ChestType.LEFT) {
                Identifier textureID = new Identifier(nameSpace, texturePath + "_left.png");
                etf$renderThisEmissive(matrices, vertexConsumers, textureID, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase);
                //this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, i, overlay);
            } else {
                Identifier textureID = new Identifier(nameSpace, texturePath + "_right.png");
                etf$renderThisEmissive(matrices, vertexConsumers, textureID, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase);
                //  this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, i, overlay);
            }
        } else {
            Identifier textureID = new Identifier(nameSpace, texturePath + ".png");
            etf$renderThisEmissive(matrices, vertexConsumers, textureID, this.singleChestLid, this.singleChestLatch, this.singleChestBase);
            //  this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, g, i, overlay);
        }
    }

    private void etf$renderThisEmissive(MatrixStack matrices, VertexConsumerProvider vertexP, Identifier texture, ModelPart lid, ModelPart latch, ModelPart base) {
        ETFUtils.generalEmissiveRenderPart(matrices, vertexP, texture, lid, true);
        ETFUtils.generalEmissiveRenderPart(matrices, vertexP, texture, latch, true);
        ETFUtils.generalEmissiveRenderPart(matrices, vertexP, texture, base, true);
    }

}


