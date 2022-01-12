package traben.freshMobBehaviours.mixin.accessor;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEntity.class)
public interface ACC_MobEntity {


        @Accessor("goalSelector")
        GoalSelector getGoalSelector();

        @Accessor("goalSelector")
        void setGoalSelector(GoalSelector value);

}
