package traben.entity_texture_features.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class PlayerCoatEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
    private final List<ModelPart> parts;
    public final ModelPart jacket;


    public PlayerCoatEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntityTranslucent);

        this.jacket = root.getChild("jacket");

        this.parts = List.of(this.jacket);
    }

    public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
        //return getModelData(dilation, slim);
    //}

    //public static ModelData getModelData(Dilation dilation, boolean slim) {
        ModelData modelData = new ModelData();
        //ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 8.0F, -2.0F, 16.0F, 24.0F, 8.0F, dilation.add(0.25F)), ModelTransform.NONE);
        return modelData;
    }

    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(/*super.getBodyParts(),*/ ImmutableList.of(this.jacket));
    }
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        super.setAngles(livingEntity, f, g, h, i, j);
        this.jacket.copyTransform(this.body);
    }
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.jacket.visible = visible;
    }

    public ModelPart getRandomPart(Random random) {
        return jacket;
    }
}
