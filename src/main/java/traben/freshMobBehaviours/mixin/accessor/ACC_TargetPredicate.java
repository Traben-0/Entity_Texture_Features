package traben.freshMobBehaviours.mixin.accessor;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TargetPredicate.class)
public interface ACC_TargetPredicate {


        @Accessor("baseMaxDistance")
        double getVanillaMaxDistance();

        @Accessor("baseMaxDistance")
        void setVanillaMaxDistance(double value);

}
