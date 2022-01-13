package traben.freshMobBehaviours;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.ObjectUtils;
import traben.freshMobBehaviours.mixin.accessor.ACC_MobEntity;
import traben.freshMobBehaviours.mixin.accessor.ACC_TargetPredicate;

import java.util.*;
import java.util.stream.Stream;

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
        double range;
        try {
            range = self.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).getValue();
        }catch(NullPointerException e){
            range = 24;
        }
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (canDash && self.canSee(target) && !target.getAbilities().creativeMode
               && (range * config.hostilesTargetRange)>= self.distanceTo(target)
        ){
            if (distance123 <= 32) {
                //Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
                if (config.skeletonKeepDistance && self instanceof SkeletonEntity && distance123 < 8) {
                    //dash away from player up to dash speed
                    distantSpeedBoost = (distantSpeedBoost / 8) * (8 - distance123);
                } else if(self instanceof EndermanEntity){
                    distantSpeedBoost = ((EndermanEntity)self).isAngry()? distantSpeedBoost * (distance123 / 32) : 0;
                }else if(self instanceof HostileEntity){
                    distantSpeedBoost = distantSpeedBoost * (distance123 / 32);
                    System.out.println("boi is attacking");
                }else{//all mobs
                    distantSpeedBoost = distantSpeedBoost * (distance123 / 32);
                }
            }else if(self instanceof EndermanEntity) {
                distantSpeedBoost = ((EndermanEntity) self).isAngry() ? distantSpeedBoost : 0;
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
        goal.add(5, new FleeEntityGoal<>(mob, PlayerEntity.class, 8, 1.25D, 1.75D));
        if (mob instanceof SheepEntity){
            goal.add(5, new FleeEntityGoal<>(mob, WolfEntity.class, 8, 1.25D, 1.75D));
        }
        goal.add(1, new EscapeDangerGoal(mob, 2D));
    }

    public static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks){
        //efficiency skip
        BlockPos[] notAirPositions;
        //remove air blocks from check to increase efficiency in large searchlist
        ArrayList<BlockPos> testpositions = new ArrayList<>();
        if (!world.getBlockState(pos).isAir()) testpositions.add(pos);
        try{if (!world.getBlockState(pos.east()).isAir()) testpositions.add(pos.east());}catch(NullPointerException e){/**/}
        try{if (!world.getBlockState(pos.east().east()).isAir()) testpositions.add(pos.east().east());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.north()).isAir()) testpositions.add(pos.north());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.north().north()).isAir()) testpositions.add(pos.north().north());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.north().east()).isAir()) testpositions.add(pos.north().east());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.north().west()).isAir()) testpositions.add(pos.north().west());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.west()).isAir()) testpositions.add(pos.west());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.west().west()).isAir()) testpositions.add(pos.west().west());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.south()).isAir()) testpositions.add(pos.south());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.south().south()).isAir()) testpositions.add(pos.south().south());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.south().west()).isAir()) testpositions.add(pos.south().west());}catch(NullPointerException e){/**/}//not valid}
        try{if (!world.getBlockState(pos.south().east()).isAir()) testpositions.add(pos.south().east());}catch(NullPointerException e){/**/}//not valid}
        notAirPositions =testpositions.toArray(new BlockPos[0]);
            //System.out.println("saved "+(12-notAirPositions.length)+" position checks");

        if (notAirPositions.length==0) return false;

        for (Block block:
             searchblocks) {
            for (BlockPos checkPos:
                 notAirPositions) {
                if(world.getBlockState(checkPos).isOf(block)) {
                    return  true;
                }
            }

        }
        return false;

    }
}