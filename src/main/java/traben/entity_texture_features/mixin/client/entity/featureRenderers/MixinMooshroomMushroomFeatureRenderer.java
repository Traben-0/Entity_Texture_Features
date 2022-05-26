package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.utils.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.*;

@Mixin(MooshroomMushroomFeatureRenderer.class)
public abstract class MixinMooshroomMushroomFeatureRenderer {

    private static final Identifier RED_SHROOM = new Identifier("textures/entity/cow/red_mushroom.png");
    private static final Identifier BROWN_SHROOM = new Identifier("textures/entity/cow/brown_mushroom.png");
    private static final Identifier RED_SHROOM_ALT = new Identifier(MOD_ID, "textures/entity/cow/red_mushroom_alt.png");
    private static final Identifier BROWN_SHROOM_ALT = new Identifier(MOD_ID, "textures/entity/cow/brown_mushroom_alt.png");


    private static final ModelPart[] shroomAsEntityModel = getModelData();

    //rewritten as original didn't seem to work, I must have accidentally changed the vanilla mushroom texture when testing originally
    @Inject(method = "renderMushroom", at = @At(value = "HEAD"), cancellable = true)
    private void etf$injected(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean renderAsModel, BlockRenderManager blockRenderManager, BlockState mushroomState, int overlay, BakedModel mushroomModel, CallbackInfo ci) {
        Boolean shroomType = returnRedTrueBrownFalseVanillaNull(mushroomState);
        if (shroomType != null) {
            VertexConsumer texturedConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(shroomType ? RED_SHROOM_ALT : BROWN_SHROOM_ALT));
            for (ModelPart model :
                    shroomAsEntityModel) {
                model.render(matrices, texturedConsumer, light, overlay, 1, 1, 1, 1);
                ETFUtils.generalEmissiveRenderPart(matrices, vertexConsumers, shroomType ? RED_SHROOM_ALT : BROWN_SHROOM_ALT, model, false);
            }
            ci.cancel();
        }
        //else continue to vanilla code
    }

    private static ModelPart[] getModelData() {
        Dilation dilation = new Dilation(0);
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shroom1", ModelPartBuilder.create().uv(32, 16).cuboid(0, 0F, 8.0F, 16.0F, 16.0F, 0F, dilation), ModelTransform.NONE);
        modelPartData.addChild("shroom2", ModelPartBuilder.create().uv(32, 16).cuboid(8F, 0F, 0.0F, 0F, 16F, 16.0F, dilation), ModelTransform.NONE);
        ModelPart shroom1 = modelData.getRoot().getChild("shroom1").createPart(32, 16);
        ModelPart shroom2 = modelData.getRoot().getChild("shroom2").createPart(32, 16);
        return new ModelPart[]{shroom1, shroom2};
    }


    @Nullable
    private static Boolean returnRedTrueBrownFalseVanillaNull(BlockState mushroomState) {
        //enable custom mooshroom mushrooms
        if (ETFConfigData.enableCustomTextures) {
            if (mushroomState.isOf(Blocks.RED_MUSHROOM)) {
                switch (mooshroomRedCustomShroom) {
                    case 1:
                        return null;
                    case 2:
                        return true;
                    default: {
                        if (ETFUtils.isExistingNativeImageFile(RED_SHROOM)) {
                            mooshroomRedCustomShroom = 2;
                            return prepareMushroomTextures(true);
                        } else {
                            mooshroomRedCustomShroom = 1;
                        }
                    }
                }
            } else if (mushroomState.isOf(Blocks.BROWN_MUSHROOM)) {
                switch (mooshroomBrownCustomShroom) {
                    case 1:
                        return null;
                    case 2:
                        return false;
                    default: {
                        if (ETFUtils.isExistingNativeImageFile(BROWN_SHROOM)) {

                            mooshroomBrownCustomShroom = 2;
                            return prepareMushroomTextures(false);
                        } else {
                            mooshroomBrownCustomShroom = 1;
                        }
                    }
                }
            }

        }
        return null;
    }

    //return isRed if valid else return null
    private static Boolean prepareMushroomTextures(boolean isRed) {
        return prepareMushroomTextures(isRed, false);
    }

    private static Boolean prepareMushroomTextures(boolean isRed, boolean doingEmissive) {
        Identifier idOfOriginal = isRed ? RED_SHROOM : BROWN_SHROOM;
        String suffix = null;
        if (doingEmissive) {
            for (String str :
                    emissiveSuffixes) {
                Identifier test = new Identifier(idOfOriginal.toString().replace(".png", str + ".png"));
                //System.out.println("trying "+test.toString());
                if (ETFUtils.isExistingNativeImageFile(test)) {
                    suffix = str;
                    idOfOriginal = test;
                    break;
                }
            }
        }
        //System.out.println("found="+suffix);
        NativeImage originalImagePreFlip = ETFUtils.getNativeImageFromID(idOfOriginal);
        if (originalImagePreFlip != null) {
            try {
                //flip vertically
                NativeImage flippedOriginalImage = ETFUtils.emptyNativeImage(originalImagePreFlip.getWidth(), originalImagePreFlip.getHeight());
                for (int x = 0; x < flippedOriginalImage.getWidth(); x++) {
                    for (int y = 0; y < flippedOriginalImage.getHeight(); y++) {
                        flippedOriginalImage.setColor(x, y, originalImagePreFlip.getColor(x, originalImagePreFlip.getHeight() - 1 - y));
                    }
                }
                //mirror 2x wide texture for entity rendering
                NativeImage newImage = ETFUtils.emptyNativeImage(flippedOriginalImage.getWidth() * 2, flippedOriginalImage.getHeight());
                for (int x = 0; x < newImage.getWidth(); x++) {
                    for (int y = 0; y < newImage.getHeight(); y++) {
                        if (x < flippedOriginalImage.getWidth()) {
                            newImage.setColor(x, y, flippedOriginalImage.getColor(x, y));
                        } else {
                            newImage.setColor(x, y, flippedOriginalImage.getColor(flippedOriginalImage.getWidth() - 1 - (x - flippedOriginalImage.getWidth()), y));
                        }
                    }
                }
                Identifier idOfNew = isRed ? RED_SHROOM_ALT : BROWN_SHROOM_ALT;
                if (doingEmissive && suffix != null) {
                    Identifier emissive = new Identifier(idOfNew.toString().replace(".png", suffix + ".png"));
                    ETFUtils.registerNativeImageToIdentifier(newImage, emissive);
                    PATH_EMISSIVE_TEXTURE_IDENTIFIER.put(idOfNew.toString(), emissive);
                } else {
                    ETFUtils.registerNativeImageToIdentifier(newImage, idOfNew);
                }
                //System.out.println("id="+idOfNew);


                //do a pass for the emissive texture if present return ignored
                if (!doingEmissive)
                    prepareMushroomTextures(isRed, true);

                return isRed;
            } catch (Exception e) {
                ETFUtils.logError("Mooshroom custom mushroom texture could not be loaded. " + e);
            }
        }
        return null;
    }
}