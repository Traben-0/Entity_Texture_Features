package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFSprite;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntityWrapper;

@Mixin(PaintingEntityRenderer.class)
public abstract class MixinPaintingEntityRenderer extends EntityRenderer<PaintingEntity> {


    @Unique
    private static final Identifier etf$BACK_SPRITE_ID = new Identifier("textures/painting/back.png");


    @SuppressWarnings("unused")
    protected MixinPaintingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow
    protected abstract void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light);

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/painting/PaintingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$getSprites(PaintingEntity paintingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        try {
            Sprite paintingSprite = MinecraftClient.getInstance().getPaintingManager().getPaintingSprite(paintingEntity.getVariant().value());
            Identifier paintingId = paintingSprite.getContents().getId();
            Identifier paintingTexture = new Identifier(paintingId.getNamespace(), "textures/painting/" + paintingId.getPath() + ".png");

            ETFEntity etfEntity = new ETFEntityWrapper(paintingEntity);

            ETFTexture frontTexture = ETFManager.getInstance().getETFTexture(paintingTexture, etfEntity, ETFManager.TextureSource.ENTITY, false);
            ETFSprite etf$Sprite = frontTexture.getSprite(paintingSprite);


            ETFTexture backTexture = ETFManager.getInstance().getETFTexture(etf$BACK_SPRITE_ID, etfEntity, ETFManager.TextureSource.ENTITY, false);
            ETFSprite etf$BackSprite = backTexture.getSprite(MinecraftClient.getInstance().getPaintingManager().getBackSprite());

            if (etf$Sprite.isETFAltered || etf$Sprite.isEmissive() || etf$BackSprite.isETFAltered || etf$BackSprite.isEmissive()) {
                matrixStack.push();
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - f));
                PaintingVariant paintingVariant = paintingEntity.getVariant().value();
                matrixStack.scale(0.0625F, 0.0625F, 0.0625F);

                etf$renderETFPainting(matrixStack,
                        vertexConsumerProvider,
                        paintingEntity,
                        paintingVariant.getWidth(),
                        paintingVariant.getHeight(),
                        etf$Sprite,
                        etf$BackSprite);
                matrixStack.pop();
                super.render(paintingEntity, f, g, matrixStack, vertexConsumerProvider, i);
                ci.cancel();
            }


        } catch (Exception e) {
            //ETFUtils2.logError("painting failed at "+paintingEntity.getBlockPos().toShortString());
        }

    }

    @Unique
    private void etf$renderETFPainting(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, PaintingEntity entity, int width, int height, ETFSprite ETFPaintingSprite, ETFSprite ETFBackSprite) {

        VertexConsumer vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(ETFPaintingSprite.getSpriteVariant().getAtlasId()));
        etf$renderETFPaintingFront(matrices, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getSpriteVariant(), false);

        VertexConsumer vertexConsumerBack = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(ETFBackSprite.getSpriteVariant().getAtlasId()));
        etf$renderETFPaintingBack(matrices, vertexConsumerBack, entity, width, height, ETFBackSprite.getSpriteVariant(), false);

        if (ETFPaintingSprite.isEmissive()) {
            vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(ETFPaintingSprite.getEmissive().getAtlasId()));
            etf$renderETFPaintingFront(matrices, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getEmissive(), true);
        }

        if (ETFBackSprite.isEmissive()) {
            vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentCull(ETFBackSprite.getEmissive().getAtlasId()));
            etf$renderETFPaintingBack(matrices, vertexConsumerFront, entity, width, height, ETFBackSprite.getEmissive(), true);
        }

    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Unique
    private void etf$renderETFPaintingFront(MatrixStack matrices, VertexConsumer vertexConsumerFront, PaintingEntity entity, int width, int height, Sprite paintingSprite, boolean emissive) {

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();

        float f = (float) (-width) / 2.0F;
        float g = (float) (-height) / 2.0F;
        int u = width / 16;
        int v = height / 16;
        double d = 1.0 / (double) u;
        double e = 1.0 / (double) v;

        for (int w = 0; w < u; ++w) {
            for (int x = 0; x < v; ++x) {
                float y = f + (float) ((w + 1) * 16);
                float z = f + (float) (w * 16);
                float aa = g + (float) ((x + 1) * 16);
                float ab = g + (float) (x * 16);

                int light;
                if (emissive) {
                    light = ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE;
                } else {
                    int ac = entity.getBlockX();
                    int ad = MathHelper.floor(entity.getY() + (double) ((aa + ab) / 2.0F / 16.0F));
                    int ae = entity.getBlockZ();
                    Direction direction = entity.getHorizontalFacing();
                    if (direction == Direction.NORTH) {
                        ac = MathHelper.floor(entity.getX() + (double) ((y + z) / 2.0F / 16.0F));
                    }

                    if (direction == Direction.WEST) {
                        ae = MathHelper.floor(entity.getZ() - (double) ((y + z) / 2.0F / 16.0F));
                    }

                    if (direction == Direction.SOUTH) {
                        ac = MathHelper.floor(entity.getX() - (double) ((y + z) / 2.0F / 16.0F));
                    }

                    if (direction == Direction.EAST) {
                        ae = MathHelper.floor(entity.getZ() + (double) ((y + z) / 2.0F / 16.0F));
                    }

                    light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), new BlockPos(ac, ad, ae));
                }

                float ag = paintingSprite.getFrameU((float) (d * (double) (u - w)));
                float ah = paintingSprite.getFrameU((float) (d * (double) (u - (w + 1))));
                float ai = paintingSprite.getFrameV((float) (e * (double) (v - x)));
                float aj = paintingSprite.getFrameV((float) (e * (double) (v - (x + 1))));
                this.vertex(matrix4f, matrix3f, vertexConsumerFront, y, ab, ah, ai, -0.5F, 0, 0, -1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerFront, z, ab, ag, ai, -0.5F, 0, 0, -1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerFront, z, aa, ag, aj, -0.5F, 0, 0, -1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerFront, y, aa, ah, aj, -0.5F, 0, 0, -1, light);

            }
        }

    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Unique
    private void etf$renderETFPaintingBack(MatrixStack matrices, VertexConsumer vertexConsumerBack, PaintingEntity entity, int width, int height, Sprite backSprite, boolean emissive) {

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();


        float f = (float) (-width) / 2.0F;
        float g = (float) (-height) / 2.0F;
        //float h = 0.5F;
        float i = backSprite.getMinU();
        float j = backSprite.getMaxU();
        float k = backSprite.getMinV();
        float l = backSprite.getMaxV();
        float m = backSprite.getMinU();
        float n = backSprite.getMaxU();
        float o = backSprite.getMinV();
        float p = backSprite.getFrameV(0.0625F);
        float q = backSprite.getMinU();
        float r = backSprite.getFrameU(0.0625F);
        float s = backSprite.getMinV();
        float t = backSprite.getMaxV();
        int u = width / 16;
        int v = height / 16;

        for (int w = 0; w < u; ++w) {
            for (int x = 0; x < v; ++x) {
                float y = f + (float) ((w + 1) * 16);
                float z = f + (float) (w * 16);
                float aa = g + (float) ((x + 1) * 16);
                float ab = g + (float) (x * 16);

                int light;
                if (emissive) {
                    light = ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE;
                } else {
                    int ac = entity.getBlockX();
                    int ad = MathHelper.floor(entity.getY() + (double) ((aa + ab) / 2.0F / 16.0F));
                    int ae = entity.getBlockZ();
                    Direction direction = entity.getHorizontalFacing();
                    if (direction == Direction.NORTH) {
                        ac = MathHelper.floor(entity.getX() + (double) ((y + z) / 2.0F / 16.0F));
                    }

                    if (direction == Direction.WEST) {
                        ae = MathHelper.floor(entity.getZ() - (double) ((y + z) / 2.0F / 16.0F));
                    }

                    if (direction == Direction.SOUTH) {
                        ac = MathHelper.floor(entity.getX() - (double) ((y + z) / 2.0F / 16.0F));
                    }

                    if (direction == Direction.EAST) {
                        ae = MathHelper.floor(entity.getZ() + (double) ((y + z) / 2.0F / 16.0F));
                    }

                    light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), new BlockPos(ac, ad, ae));
                }

                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, aa, j, k, 0.5F, 0, 0, 1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, aa, i, k, 0.5F, 0, 0, 1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, ab, i, l, 0.5F, 0, 0, 1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, ab, j, l, 0.5F, 0, 0, 1, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, aa, m, o, -0.5F, 0, 1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, aa, n, o, -0.5F, 0, 1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, aa, n, p, 0.5F, 0, 1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, aa, m, p, 0.5F, 0, 1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, ab, m, o, 0.5F, 0, -1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, ab, n, o, 0.5F, 0, -1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, ab, n, p, -0.5F, 0, -1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, ab, m, p, -0.5F, 0, -1, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, aa, r, s, 0.5F, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, ab, r, t, 0.5F, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, ab, q, t, -0.5F, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, y, aa, q, s, -0.5F, -1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, aa, r, s, -0.5F, 1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, ab, r, t, -0.5F, 1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, ab, q, t, 0.5F, 1, 0, 0, light);
                this.vertex(matrix4f, matrix3f, vertexConsumerBack, z, aa, q, s, 0.5F, 1, 0, 0, light);

            }
        }

    }


}


