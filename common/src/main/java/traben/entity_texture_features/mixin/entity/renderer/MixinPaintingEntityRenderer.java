package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.entity_handlers.ETFEntityWrapper;
import traben.entity_texture_features.mixin.accessor.SpriteAtlasHolderAccessor;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFSprite;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(PaintingEntityRenderer.class)
public abstract class MixinPaintingEntityRenderer extends EntityRenderer<PaintingEntity>  {


    @Shadow protected abstract void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light);

    @Unique
    private static final Identifier etf$BACK_SPRITE_ID = new Identifier("textures/painting/back.png");
    @Unique
    private ETFSprite etf$Sprite = null;
    @Unique
    private ETFSprite etf$BackSprite = null;

    @SuppressWarnings("unused")
    protected MixinPaintingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"))
    private void etf$getSprites(PaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        try {
            Sprite paintingSprite = MinecraftClient.getInstance().getPaintingManager().getPaintingSprite(paintingEntity.getVariant().value());
            Identifier paintingId = paintingSprite.getContents().getId();
            Identifier paintingTexture = new Identifier(paintingId.getNamespace(), "textures/painting/" + paintingId.getPath() + ".png");

            ETFEntity etfEntity = new ETFEntityWrapper(paintingEntity);

            etf$Sprite = ETFManager.getInstance().getETFTexture(paintingTexture, etfEntity, ETFManager.TextureSource.ENTITY, false)
                    .getSprite(paintingSprite, ((SpriteAtlasHolderAccessor) MinecraftClient.getInstance().getPaintingManager())::callGetSprite);


            etf$BackSprite = ETFManager.getInstance().getETFTexture(etf$BACK_SPRITE_ID, etfEntity, ETFManager.TextureSource.ENTITY, false)
                    .getSprite(MinecraftClient.getInstance().getPaintingManager().getBackSprite(), ((SpriteAtlasHolderAccessor) MinecraftClient.getInstance().getPaintingManager())::callGetSprite);

        }catch (Exception e){
            //ETFUtils2.logError("painting failed at "+paintingEntity.getBlockPos().toShortString());
            etf$Sprite = null;
            etf$BackSprite = null;
        }

    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"),
            index = 5)
    private Sprite etf$returnAlteredSprite(Sprite sprite) {
        if (ETFConfigData.enableCustomTextures && etf$Sprite != null) {
            return etf$Sprite.getSpriteVariant();
        }
        return sprite;
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V"),
            index = 6)
    private Sprite etf$returnAlteredBackSprite(Sprite sprite) {
        if (ETFConfigData.enableCustomTextures && etf$BackSprite != null) {
            return etf$BackSprite.getSpriteVariant();
        }
        return sprite;
    }

    @SuppressWarnings("SuspiciousNameCombination")//mapping coincidences with yarn
    @Inject(method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PaintingEntityRenderer;renderPainting(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/decoration/painting/PaintingEntity;IILnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/texture/Sprite;)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$renderEmissive(PaintingEntity entity, float f_, float g_, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i_, CallbackInfo ci, PaintingVariant paintingVariant) {
        //UUID id = livingEntity.getUuid();
        if (ETFConfigData.enableEmissiveTextures
                && etf$Sprite != null && etf$BackSprite != null
                && (etf$Sprite.isEmissive() || etf$BackSprite.isEmissive())) {


            int height = paintingVariant.getHeight();
            int width = paintingVariant.getWidth();

            Sprite paintingSprite = etf$Sprite.getEmissive();
            Sprite backSprite = etf$BackSprite.getEmissive();

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(this.getTexture(entity)));

//////////ALTERED VANILLA RENDER CODE
            MatrixStack.Entry entry = matrices.peek();
            Matrix4f matrix4f = entry.getPositionMatrix();
            Matrix3f matrix3f = entry.getNormalMatrix();
            float f = (float)(-width) / 2.0F;
            float g = (float)(-height) / 2.0F;

            float i = backSprite.getMinU();
            float j = backSprite.getMaxU();
            float k = backSprite.getMinV();
            float l = backSprite.getMaxV();
            float m = backSprite.getMinU();
            float n = backSprite.getMaxU();
            float o = backSprite.getMinV();
            float p = backSprite.getFrameV(1.0);
            float q = backSprite.getMinU();
            float r = backSprite.getFrameU(1.0);
            float s = backSprite.getMinV();
            float t = backSprite.getMaxV();
            int u = width / 16;
            int v = height / 16;
            double d = 16.0 / (double)u;
            double e = 16.0 / (double)v;

            for(int w = 0; w < u; ++w) {
                for(int x = 0; x < v; ++x) {
                    float y = f + (float)((w + 1) * 16);
                    float z = f + (float)(w * 16);
                    float aa = g + (float)((x + 1) * 16);
                    float ab = g + (float)(x * 16);


                    /////// NON VANILLA OVERRIDE
                    int af = ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE;

                    float ag = paintingSprite.getFrameU(d * (double)(u - w));
                    float ah = paintingSprite.getFrameU(d * (double)(u - (w + 1)));
                    float ai = paintingSprite.getFrameV(e * (double)(v - x));
                    float aj = paintingSprite.getFrameV(e * (double)(v - (x + 1)));

                    ////NON VANILLA IF
                    //this section renders the painting face
                    if(etf$Sprite.isEmissive()) {
                        vertex(matrix4f, matrix3f, vertexConsumer, y, ab, ah, ai, -0.5F, 0, 0, -1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, ab, ag, ai, -0.5F, 0, 0, -1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, aa, ag, aj, -0.5F, 0, 0, -1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, aa, ah, aj, -0.5F, 0, 0, -1, af);
                    }
                    ////NON VANILLA IF
                    //this section renders the painting back and sides
                    if(etf$BackSprite.isEmissive()) {
                        vertex(matrix4f, matrix3f, vertexConsumer, y, aa, j, k, 0.5F, 0, 0, 1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, aa, i, k, 0.5F, 0, 0, 1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, ab, i, l, 0.5F, 0, 0, 1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, ab, j, l, 0.5F, 0, 0, 1, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, aa, m, o, -0.5F, 0, 1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, aa, n, o, -0.5F, 0, 1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, aa, n, p, 0.5F, 0, 1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, aa, m, p, 0.5F, 0, 1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, ab, m, o, 0.5F, 0, -1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, ab, n, o, 0.5F, 0, -1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, ab, n, p, -0.5F, 0, -1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, ab, m, p, -0.5F, 0, -1, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, aa, r, s, 0.5F, -1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, ab, r, t, 0.5F, -1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, ab, q, t, -0.5F, -1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, y, aa, q, s, -0.5F, -1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, aa, r, s, -0.5F, 1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, ab, r, t, -0.5F, 1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, ab, q, t, 0.5F, 1, 0, 0, af);
                        vertex(matrix4f, matrix3f, vertexConsumer, z, aa, q, s, 0.5F, 1, 0, 0, af);
                    }
                }
            }
        }
    }

}


