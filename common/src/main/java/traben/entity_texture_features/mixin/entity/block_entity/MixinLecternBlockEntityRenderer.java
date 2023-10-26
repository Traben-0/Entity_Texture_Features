package traben.entity_texture_features.mixin.entity.block_entity;

import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.texture_features.ETFManager;

import java.util.function.Function;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(LecternBlockEntityRenderer.class)
public abstract class MixinLecternBlockEntityRenderer implements BlockEntityRenderer<LecternBlockEntity> {

    @Unique
    private static final String LECTERN_BOOK_PATH = "minecraft:textures/entity/lectern_book.png";

    @Shadow
    @Final
    private BookModel book;
    @Unique
    private VertexConsumerProvider entity_texture_features$recentVert = null;

    @Inject(method = "render(Lnet/minecraft/block/entity/LecternBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BookModel;renderBook(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void etf$applyEmissiveBook(LecternBlockEntity lecternBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {

        ETFManager etfManager = ETFManager.getInstance();
        if (etfManager.lecternHasCustomTexture == null)
            etfManager.lecternHasCustomTexture = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(LECTERN_BOOK_PATH)).isPresent();

        String texture = (ETFConfigData.enableCustomTextures && etfManager.lecternHasCustomTexture) ? LECTERN_BOOK_PATH : "minecraft:texture_handlers/entity/enchanting_table_book.png";//EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getTextureId().toString();

        VertexConsumer etf$vertex = etfManager.getETFDefaultTexture(new Identifier(texture), ETFClientCommon.ETFConfigData.removePixelsUnderEmissiveBlockEntity).getEmissiveVertexConsumer(vertexConsumerProvider, null, ETFManager.EmissiveRenderModes.blockEntityMode());
        if (etf$vertex != null) {
            etf$redirectingEmissiveRender(matrixStack, etf$vertex, j);
        }
    }

    //so is not caught by other injects
    @Unique
    private void etf$redirectingEmissiveRender(MatrixStack matrixStack, VertexConsumer vertexConsumer, int overlay) {
        this.book.renderBook(matrixStack, vertexConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, 1, 1, 1, 1);
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/LecternBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "HEAD"))
    private void etf$grabVertConsumer(LecternBlockEntity lecternBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        entity_texture_features$recentVert = vertexConsumerProvider;
    }

    @Redirect(method = "render(Lnet/minecraft/block/entity/LecternBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer mixin(SpriteIdentifier instance, VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory) {
        if (entity_texture_features$recentVert != null) {
            ETFManager etfManager = ETFManager.getInstance();
            if (etfManager.lecternHasCustomTexture == null) {
                etfManager.lecternHasCustomTexture = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(LECTERN_BOOK_PATH)).isPresent();
            }
            if (etfManager.lecternHasCustomTexture) {
                VertexConsumer vertCustom = entity_texture_features$recentVert.getBuffer(RenderLayer.getEntitySolid(new Identifier(LECTERN_BOOK_PATH)));
                if (vertCustom != null) {
                    return vertCustom;
                }
            }
        }
        return instance.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
    }

}


