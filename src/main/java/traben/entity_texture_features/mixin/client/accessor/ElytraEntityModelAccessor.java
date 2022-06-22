package traben.entity_texture_features.mixin.client.accessor;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ElytraEntityModel.class)
public interface ElytraEntityModelAccessor {
    @Invoker
    Iterable<ModelPart> callGetBodyParts();
}
