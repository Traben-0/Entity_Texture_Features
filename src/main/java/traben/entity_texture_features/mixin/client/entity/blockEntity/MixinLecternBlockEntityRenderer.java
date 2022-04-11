package traben.entity_texture_features.mixin.client.entity.blockEntity;

import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

@Mixin(LecternBlockEntityRenderer.class)
public abstract class MixinLecternBlockEntityRenderer implements BlockEntityRenderer<LecternBlockEntity> {

    @Shadow
    @Final
    private BookModel book;

    private static Boolean etf$lecternHasCustomTexture = null;

    @Inject(method = "render(Lnet/minecraft/block/entity/LecternBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BookModel;renderBook(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void etf$applyEmissiveBook(LecternBlockEntity lecternBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {


        if (etf$lecternHasCustomTexture == null)
            etf$lecternHasCustomTexture = ETFUtils.isExistingFileDirect(new Identifier("minecraft:textures/entity/lectern_book.png"), true);

        String texture = (ETFConfigData.enableCustomTextures && etf$lecternHasCustomTexture) ? "minecraft:textures/entity/lectern_book.png" : EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getTextureId().toString();

        VertexConsumer etf$vertex = ETFUtils.generalEmissiveGetVertexConsumer(texture, vertexConsumerProvider);
        if (etf$vertex != null) {
            this.book.renderBook(matrixStack, etf$vertex, 15728640, j, 1, 1, 1, 1);
        }
    }
}


