package traben.freshMobBehaviours;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import traben.freshMobBehaviours.mixin.accessor.ACC_MobEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FreshMobBehaviours implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        System.out.println("Hello Fabric world!");
        try{AutoConfig.register(Configurator2000.class, GsonConfigSerializer::new);}catch(Exception e){System.out.println(e);}


        //use below to read config
        //Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();

    }

    public static float slowDownToVanillaByTarget(LivingEntity self, PlayerEntity target,double baseCloseBoost, double distantSpeedBoost, boolean canDash){
        float distance123 = self.distanceTo(target);
        //speed up for chasing players then slow down when close
        //basically you cannot outrun mobs but they do not overwhelm you at close range
        if (canDash && self.canSee(target) && !target.getAbilities().creativeMode){
            if (distance123 <= 32){
                Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
                if (config.skeletonKeepDistance && self instanceof SkeletonEntity && distance123< 8){
                    //dash away from player up to dash speed
                    distantSpeedBoost = (distantSpeedBoost/8) * (8-distance123);
                }else {//all mobs
                    distantSpeedBoost = distantSpeedBoost * (distance123 / 32);
                }
            }
            return (float)(distantSpeedBoost+baseCloseBoost);
        }else{//stay vanilla speed + base boost when unseen
            return (float) baseCloseBoost;
        }
    }
// only called if meant to happem
    public static void animalSpookedGoalAdjustments(PathAwareEntity mob){
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        GoalSelector goal = ((ACC_MobEntity) mob).getGoalSelector();
        //remove goals
        for (Goal g:
                goal.getGoals()) {//remove
            if (g instanceof EscapeDangerGoal) {
                goal.remove(g);
                break;//only if 1 goal to replace
            }
        }
        goal.add(5, new FleeEntityGoal<>(mob, PlayerEntity.class, 6, 1.25D, 1.75D));
        goal.add(1, new EscapeDangerGoal(mob, 1.95D));
    }

}