package traben.entity_texture_features.client;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.Random;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class PlayerCoatExtensionModel<T extends LivingEntity> extends AnimalModel<T> {

    public final ModelPart jacket;
    public boolean sneaking;
    public float leaningPitch;

    public PlayerCoatExtensionModel() {
        this(RenderLayer::getEntityTranslucent);
    }

    public PlayerCoatExtensionModel( Function<Identifier, RenderLayer> renderLayerFactory) {
        super(renderLayerFactory, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
        //this.jacket = root.getChild("jacket");
        //getModelData(new Dilation(0)).getRoot().getChild("jacket")

        this.jacket = getJacket();
    }
    private ModelPart getJacket(){
        return  getModelData(new Dilation(4.5f,6.5f,2.5f)).getRoot().getChild("jacket").createPart(64,64); //23   15
    }

    //public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
    //    return getModelData(dilation, slim);
    //}

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        //ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 7F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.NONE);
        return modelData;
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelPart> getBodyParts() {
        //return Iterables.concat(/*super.getBodyParts(),*/ ImmutableList.of(this.jacket));
        return ImmutableList.of(this.jacket);
    }

    public void animateModel(T livingEntity, float f, float g, float h) {
        this.leaningPitch = livingEntity.getLeaningPitch(h);
        super.animateModel(livingEntity, f, g, h);
    }

    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {

        this.jacket.yaw = 0.0F;

        if (this.sneaking) {
            this.jacket.pitch = 0.5F;
            this.jacket.pivotY = 3.2F;
        } else {
            this.jacket.pitch = 0.0F;
            this.jacket.pivotY = 0.0F;
        }

        //this.jacket.copyTransform(this.body);
    }
    public void setVisible(boolean visible) {
        this.jacket.visible = visible;
    }

    public void setAttributes(PlayerCoatExtensionModel<T> model) {
        super.copyStateTo(model);
        model.sneaking = this.sneaking;
        model.jacket.copyTransform(this.jacket);
    }

    public ModelPart getRandomPart(Random random) {
        return jacket;
    }

}
