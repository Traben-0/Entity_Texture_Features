package traben.entity_texture_features.texture_features;


import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.Function;


public class ETFCustomPlayerFeatureModel<T extends PlayerEntity> extends EntityModel<T> {
    public final ModelPart villagerNose;
    public final ModelPart textureNose;
    public final ModelPart jacket;
    public final ModelPart fatJacket;
    //public boolean sneaking;


    public ETFCustomPlayerFeatureModel() {
        this(RenderLayer::getEntityTranslucent);
    }

    public ETFCustomPlayerFeatureModel(Function<Identifier, RenderLayer> renderLayerFactory) {
        super(renderLayerFactory);

        ModelPartData data = getModelData(new Dilation(0)).getRoot();
        this.jacket = data.getChild("jacket").createPart(64, 64);
        this.fatJacket = data.getChild("fatJacket").createPart(64, 64);
        this.villagerNose = data.getChild("nose").createPart(64, 64); //23   15
        this.textureNose = data.getChild("textureNose").createPart(8, 8); //23   15
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 12.5F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F)), ModelTransform.NONE);
        modelPartData.addChild("fatJacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 12.5F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(0.25F).add(0.5F)), ModelTransform.NONE);
        modelPartData.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
        modelPartData.addChild("textureNose", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -8.0F, -8.0F, 0.0F, 8.0F, 4.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
        return modelData;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //this.jacket.render(matrices,  vertices,  light,  overlay,  red,  green,  blue,  alpha);
    }

    public void animateModel(T livingEntity, float f, float g, float h) {
        //this.leaningPitch = livingEntity.getLeaningPitch(h);
        super.animateModel(livingEntity, f, g, h);
    }

    public void setAngles(T livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.villagerNose.yaw = headYaw * 0.017453292F;
        this.villagerNose.pitch = headPitch * 0.017453292F;
        this.villagerNose.roll = 0.0F;
        this.textureNose.yaw = headYaw * 0.017453292F;
        this.textureNose.pitch = headPitch * 0.017453292F;
        this.textureNose.roll = 0.0F;

        this.jacket.yaw = 0.0F;
        if (livingEntity.isSneaking()) {
            this.jacket.pitch = 0.5F;
            this.jacket.pivotY = 3.2F;
        } else {
            this.jacket.pitch = 0.0F;
            this.jacket.pivotY = 0.0F;
        }

        this.fatJacket.copyTransform(this.jacket);
    }


}
