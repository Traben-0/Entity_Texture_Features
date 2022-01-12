package traben.freshMobBehaviours.mixin.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RevengeGoal.class)
public class MIX_RevengeGoal {


    //if skeleton ignore revenge goal
    @ModifyVariable(method = "canStart", at = @At("STORE"), ordinal = 0)
    private LivingEntity injected(LivingEntity attacker) {
        RevengeGoal self = ((RevengeGoal) (Object) this);
        if (attacker instanceof AbstractSkeletonEntity
               // && attacker.getRandom().nextInt(5)<=3
        ){
            return null;
        }else {
            return attacker;
        }
    }
}