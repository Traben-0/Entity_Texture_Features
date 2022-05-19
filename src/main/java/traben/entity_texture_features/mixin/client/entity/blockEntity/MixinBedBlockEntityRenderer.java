package traben.entity_texture_features.mixin.client.entity.blockEntity;

import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;

@Mixin(BedBlockEntityRenderer.class)
public abstract class MixinBedBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {


    @Inject(method = "renderPart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissiveBed(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ModelPart part, Direction direction, SpriteIdentifier sprite, int light, int overlay, boolean isFoot, CallbackInfo ci, VertexConsumer vertexConsumer) {

        //hopefully works in modded scenarios, assumes the mod dev uses the actual vanilla code process and texture pathing rules
        String nameSpace = sprite.getTextureId().getNamespace();
        String texturePath = "textures/" + sprite.getTextureId().getPath() + ".png";
        //System.out.println("bed "+nameSpace +":"+texturePath);
        //Identifier textureID = new Identifier();
        ETFUtils.generalEmissiveRenderPart(matrices, vertexConsumers, nameSpace + ":" + texturePath, part, true);
        //VertexConsumer cons = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(new Identifier(nameSpace,texturePath), true));
        //part.render(matrices,cons,15728640,overlay);
    }
}


