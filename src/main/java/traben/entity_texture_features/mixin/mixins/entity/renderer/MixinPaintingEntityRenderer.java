package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
//#if MC >= 12109
import net.minecraft.client.renderer.state.CameraRenderState;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.SubmitNodeCollector;
//#endif

//#if MC < 12006
//$$ import org.joml.Matrix3f;
//$$ import org.joml.Matrix4f;
//#endif

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFSprite;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

//#if MC >= 12103
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
@Mixin(PaintingRenderer.class)
public abstract class MixinPaintingEntityRenderer extends EntityRenderer<Painting, PaintingRenderState> {
//#else
//$$ @Mixin(PaintingRenderer.class)
//$$ public abstract class MixinPaintingEntityRenderer extends EntityRenderer<Painting> {
//#endif


    @Unique
    private void uVertex(final PoseStack.Pose matrix, final VertexConsumer vertexConsumer, final float x, final float y, final float u, final float v, final float z, final int normalX, final int normalY, final int normalZ, final int light) {
        vertex(
                //#if MC >= 12006
                matrix
                //#else
                //$$ matrix.pose(), matrix.normal()
                //#endif
                , vertexConsumer, x, y, u, v, z, normalX, normalY, normalZ, light);
    }

    //#if MC >= 26.1
    //$$ @Shadow
    //$$ protected static void vertex(PoseStack.Pose par1, VertexConsumer par2, float par3, float par4, float par5, float par6, float par7, int par8, int par9, int par10, int par11) {
    //$$ }
    //#elseif MC >= 12006
    @Shadow
    protected abstract void vertex(final PoseStack.Pose matrix, final VertexConsumer vertexConsumer, final float x, final float y, final float u, final float v, final float z, final int normalX, final int normalY, final int normalZ, final int light);
    //#else
    //$$ @Shadow protected abstract void vertex(final Matrix4f matrix4f, final Matrix3f matrix3f, final VertexConsumer vertexConsumer, final float f, final float g, final float h, final float i, final float j, final int k, final int l, final int m, final int n);
    //#endif
    @Unique
    private static final ResourceLocation etf$BACK_SPRITE_ID = ETFUtils2.res("textures/painting/back.png");

    @SuppressWarnings("unused")
    protected MixinPaintingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }


    //#if MC >= 12109
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/PaintingRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/PaintingRenderer;renderPainting(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/RenderType;[IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private void etf$getSprites(final PaintingRenderState paintingRenderState, final PoseStack matrixStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState cameraRenderState, final CallbackInfo ci,
                                @Local(ordinal = 0) TextureAtlasSprite paintingSprite, @Local(ordinal = 1) TextureAtlasSprite backSprite
                                ) {
    //#elseif MC >= 12103
    //$$ @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/PaintingRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$     at = @At(value = "HEAD"), cancellable = true)
    //$$ private void etf$getSprites(final PaintingRenderState paintingRenderState, final PoseStack matrixStack, final MultiBufferSource vertexConsumerProvider, final int i, final CallbackInfo ci) {
    //#else
    //$$ @Inject(method = "render(Lnet/minecraft/world/entity/decoration/Painting;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$     at = @At(value = "HEAD"), cancellable = true)
    //$$ private void etf$getSprites(Painting paintingEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
    //#endif

        ETFEntityRenderState etfEntity =
                //#if MC>=12103
                ((HoldsETFRenderState) paintingRenderState).etf$getState();
                //#else
                //$$ ETFEntityRenderState.forEntity((ETFEntity) paintingEntity);
                //#endif

        //#if MC >= 12103
        if(!(etfEntity != null && etfEntity.entity() instanceof Painting paintingEntity)) return;
        float f = paintingRenderState.direction.get2DDataValue() * 90;
        //#endif

        try {
            //#if MC < 12109
            //$$ TextureAtlasSprite paintingSprite = Minecraft.getInstance().getPaintingTextures().get(paintingEntity.getVariant().value());
            //$$ TextureAtlasSprite backSprite = Minecraft.getInstance().getPaintingTextures().getBackSprite();
            //#endif
            ResourceLocation paintingId = paintingSprite.contents().name();
            String paintingFileName = paintingId.getPath();
            ResourceLocation paintingTexture = ETFUtils2.res(paintingId.getNamespace(), "textures/painting/" + paintingFileName + ".png");


            boolean aztec = "aztec".equals(paintingFileName);
            if (aztec) ETFRenderContext.allowOnlyPropertiesRandom();

            ETFTexture frontTexture = ETFManager.getInstance().getETFTextureVariant(paintingTexture, etfEntity);
            ETFSprite etf$Sprite = frontTexture.getPaintingSprite(paintingSprite, paintingTexture);

            if (aztec) ETFRenderContext.allowAllRandom();

            ETFTexture backTexture = ETFManager.getInstance().getETFTextureVariant(etf$BACK_SPRITE_ID, etfEntity);
            ETFSprite etf$BackSprite = backTexture.getPaintingSprite(backSprite, etf$BACK_SPRITE_ID);


            if (etf$Sprite.isETFAltered || etf$Sprite.isEmissive() || etf$BackSprite.isETFAltered || etf$BackSprite.isEmissive()) {
                //#if MC < 12109
                //$$ matrixStack.pushPose();
                //$$ matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - f));
                //#endif
                PaintingVariant paintingVariant = paintingEntity.getVariant().value();

                //#if MC < 12100
                //$$         matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
                //#endif

                int width =  paintingVariant.
                //#if MC < 12100
                //$$ getWidth()
                //#else
                width()
                //#endif
                ;
                int height = paintingVariant.
                //#if MC < 12100
                //$$ getHeight()
                //#else
                height()
                //#endif
                ;

                //#if MC>= 12109
                ETFRenderContext.preventRenderLayerTextureModify();
                var type =
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .entitySolid(etf$Sprite.getSpriteVariant().atlasLocation());
                var back =
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .entitySolid(etf$BackSprite.getSpriteVariant().atlasLocation());
                var typeE =
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .entityTranslucent(etf$Sprite.getEmissive().atlasLocation());
                var backE =
                        //#if MC>= 12111
                        //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                        //#else
                        RenderType
                        //#endif
                                .entityTranslucent(etf$BackSprite.getEmissive().atlasLocation());
                ETFRenderContext.allowRenderLayerTextureModify();

                submitNodeCollector.submitCustomGeometry(matrixStack, type, (pose, vertexConsumer) ->
                            etf$renderETFPaintingFront(pose, vertexConsumer, paintingEntity, width, height, etf$Sprite.getSpriteVariant(), false));

                submitNodeCollector.submitCustomGeometry(matrixStack, back, (pose, vertexConsumer) ->
                        etf$renderETFPaintingBack(pose, vertexConsumer, paintingEntity, width, height, etf$BackSprite.getSpriteVariant(), false));

                if (etf$Sprite.isEmissive()) {
                    submitNodeCollector.submitCustomGeometry(matrixStack, typeE, (pose, vertexConsumer) ->
                            etf$renderETFPaintingFront(pose, vertexConsumer, paintingEntity, width, height, etf$Sprite.getSpriteVariant(), true));
                }

                if (etf$BackSprite.isEmissive()) {
                    submitNodeCollector.submitCustomGeometry(matrixStack, backE, (pose, vertexConsumer) ->
                            etf$renderETFPaintingFront(pose, vertexConsumer, paintingEntity, width, height, etf$BackSprite.getSpriteVariant(), true));
                }

                //#else
                //$$ etf$renderETFPainting(matrixStack.last(),
                //$$         vertexConsumerProvider,
                //$$         paintingEntity,
                //$$         width,
                //$$         height,
                //$$         etf$Sprite,
                //$$         etf$BackSprite);
                //$$ matrixStack.popPose();
                    //#if MC >= 12103
                    //$$ super.render(paintingRenderState, matrixStack, vertexConsumerProvider, i);
                    //#else
                    //$$ super.render(paintingEntity, f, g, matrixStack, vertexConsumerProvider, i);
                    //#endif
                //$$ ci.cancel();
                //#endif
            }


        } catch (Exception e) {
            //ETFUtils2.logError("painting failed at "+paintingEntity.getBlockPos().toShortString());
        }

    }
    //#if MC < 12109
    //$$ @Unique
    //$$ private void etf$renderETFPainting(PoseStack.Pose entry, MultiBufferSource vertexConsumerProvider, Painting entity, int width, int height, ETFSprite ETFPaintingSprite, ETFSprite ETFBackSprite) {
    //$$     ETFRenderContext.preventRenderLayerTextureModify();
    //$$     VertexConsumer vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entitySolid(ETFPaintingSprite.getSpriteVariant().atlasLocation()));
    //$$     etf$renderETFPaintingFront(entry, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getSpriteVariant(), false);
    //$$
    //$$     VertexConsumer vertexConsumerBack = vertexConsumerProvider.getBuffer(RenderType.entitySolid(ETFBackSprite.getSpriteVariant().atlasLocation()));
    //$$     etf$renderETFPaintingBack(entry, vertexConsumerBack, entity, width, height, ETFBackSprite.getSpriteVariant(), false);
    //$$
    //$$     if (ETFPaintingSprite.isEmissive()) {
    //$$         vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(ETFPaintingSprite.getEmissive().atlasLocation()));
    //$$         etf$renderETFPaintingFront(entry, vertexConsumerFront, entity, width, height, ETFPaintingSprite.getEmissive(), true);
    //$$     }
    //$$
    //$$     if (ETFBackSprite.isEmissive()) {
    //$$         vertexConsumerFront = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(ETFBackSprite.getEmissive().atlasLocation()));
    //$$         etf$renderETFPaintingBack(entry, vertexConsumerFront, entity, width, height, ETFBackSprite.getEmissive(), true);
    //$$     }
    //$$     ETFRenderContext.allowRenderLayerTextureModify();
    //$$ }
    //#endif

    @Unique
    private void etf$renderETFPaintingFront(PoseStack.Pose entry, VertexConsumer vertexConsumerFront, Painting entity, int width, int height, TextureAtlasSprite paintingSprite, boolean emissive) {

//        PoseStack.Pose entry = matrices.last();
//        Matrix4f matrix4f = entry.getPositionMatrix();
//        Matrix3f matrix3f = entry.getNormalMatrix();

        float f = (float) (-width) / 2.0F;
        float g = (float) (-height) / 2.0F;
        //#if MC>= 12100
        int u = width;
        int v = height;
        //#else
        //$$ int u = width / 16;
        //$$ int v = height / 16;
        //#endif

        double d = 1.0 / (double) u;
        double e = 1.0 / (double) v;

        for (int w = 0; w < u; ++w) {
            for (int x = 0; x < v; ++x) {
                //#if MC < 12100
                //$$         float y = f + (float) ((w + 1) * 16);
                //$$         float z = f + (float) (w * 16);
                //$$         float aa = g + (float) ((x + 1) * 16);
                //$$         float ab = g + (float) (x * 16);
                //#else
                float y = f + (float) ((w + 1));
                float z = f + (float) (w);
                float aa = g + (float) ((x + 1));
                float ab = g + (float) (x);
                //#endif

                int light;
                if (emissive) {
                    light = ETF.EMISSIVE_FEATURE_LIGHT_VALUE;
                } else {

                    //#if MC < 12100
                    //$$ float divider = 16F;
                    //#else
                    float divider = 1F;
                    //#endif

                    int ac = entity.getBlockX();
                    int ad = Mth.floor(entity.getY() + (double) ((aa + ab) / 2.0F / divider));
                    int ae = entity.getBlockZ();
                    Direction direction = entity.getDirection();
                    if (direction == Direction.NORTH) {
                        ac = Mth.floor(entity.getX() + (double) ((y + z) / 2.0F / divider));
                    }

                    if (direction == Direction.WEST) {
                        ae = Mth.floor(entity.getZ() - (double) ((y + z) / 2.0F / divider));
                    }

                    if (direction == Direction.SOUTH) {
                        ac = Mth.floor(entity.getX() - (double) ((y + z) / 2.0F / divider));
                    }

                    if (direction == Direction.EAST) {
                        ae = Mth.floor(entity.getZ() + (double) ((y + z) / 2.0F / divider));
                    }

                    light = LevelRenderer.getLightColor(entity.level(), new BlockPos(ac, ad, ae));
                }

                float zConst =
                //#if MC < 12100
                //$$ 0.5F;
                //#else
                0.03125F;
                //#endif

                float ag = paintingSprite.getU((float) (d * (double) (u - w)));
                float ah = paintingSprite.getU((float) (d * (double) (u - (w + 1))));
                float ai = paintingSprite.getV((float) (e * (double) (v - x)));
                float aj = paintingSprite.getV((float) (e * (double) (v - (x + 1))));
                this.uVertex(entry, vertexConsumerFront, y, ab, ah, ai, -zConst, 0, 0, -1, light);
                this.uVertex(entry, vertexConsumerFront, z, ab, ag, ai, -zConst, 0, 0, -1, light);
                this.uVertex(entry, vertexConsumerFront, z, aa, ag, aj, -zConst, 0, 0, -1, light);
                this.uVertex(entry, vertexConsumerFront, y, aa, ah, aj, -zConst, 0, 0, -1, light);

            }
        }

    }

    @Unique
    private void etf$renderETFPaintingBack(PoseStack.Pose entry, VertexConsumer vertexConsumerBack, Painting entity, int width, int height, TextureAtlasSprite backSprite, boolean emissive) {

//        PoseStack.Pose entry = matrices.last();
//        Matrix4f matrix4f = entry.getPositionMatrix();
//        Matrix3f matrix3f = entry.getNormalMatrix();


        float f = (float) (-width) / 2.0F;
        float g = (float) (-height) / 2.0F;
        //float h = 0.5F;
        float i = backSprite.getU0();
        float j = backSprite.getU1();
        float k = backSprite.getV0();
        float l = backSprite.getV1();
        float m = backSprite.getU0();
        float n = backSprite.getU1();
        float o = backSprite.getV0();
        float p = backSprite.getV(0.0625F);
        float q = backSprite.getU0();
        float r = backSprite.getU(0.0625F);
        float s = backSprite.getV0();
        float t = backSprite.getV1();
        //#if MC>= 12100
        int u = width;
        int v = height;
        //#else
        //$$ int u = width / 16;
        //$$ int v = height / 16;
        //#endif

        for (int w = 0; w < u; ++w) {
            for (int x = 0; x < v; ++x) {
                //#if MC < 12100
                //$$         float y = f + (float) ((w + 1) * 16);
                //$$         float z = f + (float) (w * 16);
                //$$         float aa = g + (float) ((x + 1) * 16);
                //$$         float ab = g + (float) (x * 16);
                //#else
                float y = f + (float) ((w + 1));
                float z = f + (float) (w);
                float aa = g + (float) ((x + 1));
                float ab = g + (float) (x);
                //#endif

                int light;
                if (emissive) {
                    light = ETF.EMISSIVE_FEATURE_LIGHT_VALUE;
                } else {
                    //#if MC < 12100
                    //$$ float divider = 16F;
                    //#else
                    float divider = 1F;
                    //#endif

                    int ac = entity.getBlockX();
                    int ad = Mth.floor(entity.getY() + (double) ((aa + ab) / 2.0F / divider));
                    int ae = entity.getBlockZ();
                    Direction direction = entity.getDirection();
                    if (direction == Direction.NORTH) {
                        ac = Mth.floor(entity.getX() + (double) ((y + z) / 2.0F / divider));
                    }

                    if (direction == Direction.WEST) {
                        ae = Mth.floor(entity.getZ() - (double) ((y + z) / 2.0F / divider));
                    }

                    if (direction == Direction.SOUTH) {
                        ac = Mth.floor(entity.getX() - (double) ((y + z) / 2.0F / divider));
                    }

                    if (direction == Direction.EAST) {
                        ae = Mth.floor(entity.getZ() + (double) ((y + z) / 2.0F / divider));
                    }

                    light = LevelRenderer.getLightColor(entity.level(), new BlockPos(ac, ad, ae));
                }

                float zConst =
                        //#if MC < 12100
                        //$$ 0.5F;
                        //#else
                        0.03125F;
                //#endif

                this.uVertex(entry, vertexConsumerBack, y, aa, j, k, zConst, 0, 0, 1, light);
                this.uVertex(entry, vertexConsumerBack, z, aa, i, k, zConst, 0, 0, 1, light);
                this.uVertex(entry, vertexConsumerBack, z, ab, i, l, zConst, 0, 0, 1, light);
                this.uVertex(entry, vertexConsumerBack, y, ab, j, l, zConst, 0, 0, 1, light);
                this.uVertex(entry, vertexConsumerBack, y, aa, m, o, -zConst, 0, 1, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, aa, n, o, -zConst, 0, 1, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, aa, n, p, zConst, 0, 1, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, aa, m, p, zConst, 0, 1, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, ab, m, o, zConst, 0, -1, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, ab, n, o, zConst, 0, -1, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, ab, n, p, -zConst, 0, -1, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, ab, m, p, -zConst, 0, -1, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, aa, r, s, zConst, -1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, ab, r, t, zConst, -1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, ab, q, t, -zConst, -1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, y, aa, q, s, -zConst, -1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, aa, r, s, -zConst, 1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, ab, r, t, -zConst, 1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, ab, q, t, zConst, 1, 0, 0, light);
                this.uVertex(entry, vertexConsumerBack, z, aa, q, s, zConst, 1, 0, 0, light);
            }
        }

    }


}


