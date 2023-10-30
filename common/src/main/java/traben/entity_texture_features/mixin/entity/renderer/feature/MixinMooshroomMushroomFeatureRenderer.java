package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(MooshroomMushroomFeatureRenderer.class)
public abstract class MixinMooshroomMushroomFeatureRenderer {

    @Unique
    private static final Identifier RED_SHROOM = new Identifier("textures/entity/cow/red_mushroom.png");
    @Unique
    private static final Identifier BROWN_SHROOM = new Identifier("textures/entity/cow/brown_mushroom.png");
    @Unique
    private static final ModelPart[] entity_texture_features$shroomAsEntityModel = entity_texture_features$getModelData();
    @Unique
    private static Identifier entity_texture_features$redEmissive = null;
    @Unique
    private static Identifier entity_texture_features$brownEmissive = null;

    @Unique
    private static ModelPart[] entity_texture_features$getModelData() {
        Dilation dilation = new Dilation(0);
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shroom1", ModelPartBuilder.create().uv(32, 16).cuboid(0, 0F, 8.0F, 16.0F, 16.0F, 0F, dilation), ModelTransform.NONE);
        modelPartData.addChild("shroom2", ModelPartBuilder.create().uv(32, 16).cuboid(8F, 0F, 0.0F, 0F, 16F, 16.0F, dilation), ModelTransform.NONE);
        ModelPart shroom1 = modelData.getRoot().getChild("shroom1").createPart(32, 16);
        ModelPart shroom2 = modelData.getRoot().getChild("shroom2").createPart(32, 16);
        return new ModelPart[]{shroom1, shroom2};
    }

    @Unique
    @Nullable
    private static Boolean entity_texture_features$returnRedTrueBrownFalseVanillaNull(BlockState mushroomState) {
        //enable custom mooshroom mushrooms
        if (ETFConfigData.enableCustomTextures) {
            if (mushroomState.isOf(Blocks.RED_MUSHROOM)) {
                switch (ETFManager.getInstance().mooshroomRedCustomShroom) {
                    case 1 -> {
                        return null;
                    }
                    case 2 -> {
                        return true;
                    }
                    default -> {
                        if (MinecraftClient.getInstance().getResourceManager().getResource(RED_SHROOM).isPresent()) {
                            ETFManager.getInstance().mooshroomRedCustomShroom = 2;
                            return entity_texture_features$prepareMushroomTextures(true);
                        } else {
                            ETFManager.getInstance().mooshroomRedCustomShroom = 1;
                        }
                    }
                }
            } else if (mushroomState.isOf(Blocks.BROWN_MUSHROOM)) {
                switch (ETFManager.getInstance().mooshroomBrownCustomShroom) {
                    case 1 -> {
                        return null;
                    }
                    case 2 -> {
                        return false;
                    }
                    default -> {
                        if (MinecraftClient.getInstance().getResourceManager().getResource(BROWN_SHROOM).isPresent()) {

                            ETFManager.getInstance().mooshroomBrownCustomShroom = 2;
                            return entity_texture_features$prepareMushroomTextures(false);
                        } else {
                            ETFManager.getInstance().mooshroomBrownCustomShroom = 1;
                        }
                    }
                }
            }

        }
        return null;
    }

    //return isRed if valid else return null
    @Unique
    private static Boolean entity_texture_features$prepareMushroomTextures(boolean isRed) {
        return entity_texture_features$prepareMushroomTextures(isRed, false);
    }

    @Unique
    private static Boolean entity_texture_features$prepareMushroomTextures(boolean isRed, boolean doingEmissive) {
        Identifier idOfOriginal = isRed ? RED_SHROOM : BROWN_SHROOM;
        String suffix = null;
        if (doingEmissive) {
            boolean found = false;
            for (String str :
                    ETFManager.getInstance().EMISSIVE_SUFFIX_LIST) {
                Identifier test = new Identifier(idOfOriginal.toString().replace(".png", str + ".png"));
                //System.out.println("trying "+test.toString());
                if (MinecraftClient.getInstance().getResourceManager().getResource(test).isPresent()) {
                    suffix = str;
                    idOfOriginal = test;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }
        //System.out.println("found="+suffix);
        NativeImage originalImagePreFlip = ETFUtils2.getNativeImageElseNull(idOfOriginal);

        if (originalImagePreFlip != null) {
            try {
                //flip vertically
                NativeImage newImage;
                try (NativeImage flippedOriginalImage = ETFUtils2.emptyNativeImage(originalImagePreFlip.getWidth(), originalImagePreFlip.getHeight())) {
                    for (int x = 0; x < flippedOriginalImage.getWidth(); x++) {
                        for (int y = 0; y < flippedOriginalImage.getHeight(); y++) {
                            flippedOriginalImage.setColor(x, y, originalImagePreFlip.getColor(x, originalImagePreFlip.getHeight() - 1 - y));
                        }
                    }
                    //mirror 2x wide texture for entity rendering
                    newImage = ETFUtils2.emptyNativeImage(flippedOriginalImage.getWidth() * 2, flippedOriginalImage.getHeight());
                    for (int x = 0; x < newImage.getWidth(); x++) {
                        for (int y = 0; y < newImage.getHeight(); y++) {
                            if (x < flippedOriginalImage.getWidth()) {
                                newImage.setColor(x, y, flippedOriginalImage.getColor(x, y));
                            } else {
                                newImage.setColor(x, y, flippedOriginalImage.getColor(flippedOriginalImage.getWidth() - 1 - (x - flippedOriginalImage.getWidth()), y));
                            }
                        }
                    }
                }
                Identifier idOfNew = isRed ? new Identifier("etf", "red_shroom_alt.png") : new Identifier("etf", "brown_shroom_alt.png");
                if (doingEmissive && suffix != null) {
                    Identifier emissive = new Identifier(idOfNew.toString().replace(".png", suffix + ".png"));
                    ETFUtils2.registerNativeImageToIdentifier(newImage, emissive);
                    if (isRed) {
                        entity_texture_features$redEmissive = emissive;
                    } else {
                        entity_texture_features$brownEmissive = emissive;
                    }
                } else {
                    ETFUtils2.registerNativeImageToIdentifier(newImage, idOfNew);
                }
                //System.out.println("id="+idOfNew);


                //do a pass for the emissive texture if present return ignored
                if (!doingEmissive) {
                    entity_texture_features$prepareMushroomTextures(isRed, true);
                    if (isRed) {
                        ETFManager.getInstance().redMooshroomAlt = new ETFTexture(idOfNew, entity_texture_features$redEmissive);
                    } else {
                        ETFManager.getInstance().brownMooshroomAlt = new ETFTexture(idOfNew, entity_texture_features$brownEmissive);
                    }
                }
                return isRed;
            } catch (Exception e) {
                ETFUtils2.logError("Mooshroom custom mushroom texture could not be loaded. " + e);
            }
        }
        return null;
    }


    //rewritten as original didn't seem to work, I must have accidentally changed the vanilla mushroom texture when testing originally
    @Inject(method = "renderMushroom", at = @At(value = "HEAD"), cancellable = true)
    private void etf$injected(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean renderAsModel, BlockState mushroomState, int overlay, BakedModel mushroomModel, CallbackInfo ci) {

        Boolean shroomType = entity_texture_features$returnRedTrueBrownFalseVanillaNull(mushroomState);
        if (shroomType != null) {
            ETFTexture thisTexture = shroomType ? ETFManager.getInstance().redMooshroomAlt : ETFManager.getInstance().brownMooshroomAlt;
            if (thisTexture != null) {
                for (ModelPart model :
                        entity_texture_features$shroomAsEntityModel) {
                    VertexConsumer texturedConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(thisTexture.thisIdentifier));
                    model.render(matrices, texturedConsumer, light, overlay, 1, 1, 1, 1);

                    thisTexture.renderEmissive(matrices, vertexConsumers, model);
                    //ETFUtils2.generalEmissiveRenderPart(matrices, vertexConsumers, shroomType ? RED_SHROOM_ALT : BROWN_SHROOM_ALT, model, false);

                }
                ci.cancel();
            }
        }
        //else continue to vanilla code
    }
}