package traben.emissive_entities.mixin.accessor;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public interface ACC_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {


        @Accessor("features")
        List<FeatureRenderer<T, M>> getFeatures();

        @Accessor("features")
        void setFeatures(List<FeatureRenderer<T, M>> value);


}
