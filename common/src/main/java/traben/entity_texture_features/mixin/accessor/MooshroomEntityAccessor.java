package traben.entity_texture_features.mixin.accessor;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.MooshroomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MooshroomEntity.class)
public interface MooshroomEntityAccessor {
    @Accessor
    StatusEffect getStewEffect();
}
