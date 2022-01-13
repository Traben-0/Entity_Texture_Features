package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.mixin.accessor.ACC_MobEntity;


@Mixin(GiantEntity.class)
public abstract class MIX_GiantEntity {

    @ModifyConstant(method = "createGiantAttributes", constant = @Constant(doubleValue = 100.0D))
    private static double maxHP(double value) {
        return 500.0D;
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void randomSpawnBlock(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        GiantEntity giant = (GiantEntity) (Object)this;
        GoalSelector goals = ((ACC_MobEntity) giant).getGoalSelector();
        GoalSelector targets = ((ACC_MobEntity) giant).getTargetSelector();


        //goals.add(2, new AttackGoal(giant));
        goals.add(4, new WanderAroundPointOfInterestGoal(giant, 0.6D, false));
        goals.add(5, new WanderAroundFarGoal(giant, 1.0D));
        goals.add(6, new LookAtEntityGoal(giant, PlayerEntity.class, (float)(35*config.hostilesTargetRange)));
        goals.add(6, new LookAroundGoal(giant));
        targets.add(1, (new RevengeGoal(giant)).setGroupRevenge(ZombifiedPiglinEntity.class));
        targets.add(2, new ActiveTargetGoal<>(giant, PlayerEntity.class, true));
        targets.add(3, new ActiveTargetGoal<>(giant, MerchantEntity.class, false));
        targets.add(3, new ActiveTargetGoal<>(giant, IronGolemEntity.class, true));

    }
}
