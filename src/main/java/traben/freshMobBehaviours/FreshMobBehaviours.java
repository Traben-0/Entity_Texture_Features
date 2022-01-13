package traben.freshMobBehaviours;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        }else if (mob instanceof CowEntity
                || mob instanceof HorseBaseEntity ){
            goal.add(5,new EatGrassGoal(mob));
        }
        goal.add(1, new EscapeDangerGoal(mob, 2D));
    }



    public static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks){
        return isBlockWithin2(pos,world,searchblocks,false,false,false);
    }
    public static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks,boolean checkUp,boolean checkDown){
        return isBlockWithin2(pos,world,searchblocks,checkUp,checkDown,false);
    }
    public static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks,boolean checkUp,boolean checkDown, boolean DO_PYRAMID_CHECK){
        //efficiency skip
        BlockPos[] notAirPositions;
        //remove air blocks from check to increase efficiency in large searchlist
        ArrayList<BlockPos> testpositions = new ArrayList<>();
        if (!world.getBlockState(pos).isAir()) testpositions.add(pos);
        try{
            if (!world.getBlockState(pos.east()).isAir()) testpositions.add(pos.east());
            if (!world.getBlockState(pos.east(2)).isAir()) testpositions.add(pos.east(2));
            if (!world.getBlockState(pos.north()).isAir()) testpositions.add(pos.north());
            if (!world.getBlockState(pos.north(2)).isAir()) testpositions.add(pos.north(2));
            if (!world.getBlockState(pos.north().east()).isAir()) testpositions.add(pos.north().east());
            if (!world.getBlockState(pos.north().west()).isAir()) testpositions.add(pos.north().west());
            if (!world.getBlockState(pos.west()).isAir()) testpositions.add(pos.west());
            if (!world.getBlockState(pos.west(2)).isAir()) testpositions.add(pos.west(2));
            if (!world.getBlockState(pos.south()).isAir()) testpositions.add(pos.south());
            if (!world.getBlockState(pos.south(2)).isAir()) testpositions.add(pos.south(2));
            if (!world.getBlockState(pos.south().west()).isAir()) testpositions.add(pos.south().west());
            if (!world.getBlockState(pos.south().east()).isAir()) testpositions.add(pos.south().east());
        }catch(NullPointerException e){
            System.out.println(e.toString());
        }//not valid}
        notAirPositions =testpositions.toArray(new BlockPos[0]);
            //System.out.println("saved "+(12-notAirPositions.length)+" position checks");

        //if (notAirPositions.length==0) return false;

        for (Block block:
             searchblocks) {
            for (BlockPos checkPos:
                 notAirPositions) {
                if(world.getBlockState(checkPos).isOf(block)) {
                    if (DO_PYRAMID_CHECK){
                        return (world.getLightLevel(checkPos)==0
                                && world.getBlockState(checkPos.north(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.east(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.west(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.south(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.down(2)).isOf(Blocks.TNT));
                    }else{
                        return  true;
                    }

                }
            }

        }
        if (checkUp){
            if (isBlockWithin2(pos.up(),world,searchblocks,false,false)){
                return true;
            }
        }
        if (checkDown){
            return isBlockWithin2(pos.down(), world, searchblocks, false, false);
        }
        return false;

    }
    public static void setFire(BlockPos pos, World world, int randomChanceAtZero){
        setFire(pos,world,randomChanceAtZero,false);
    }
    public static void setFire(BlockPos pos, World world, int randomChanceAtZero, boolean ignoreFloor){
        if (randomChanceAtZero ==0
                && world.getBlockState(pos).isAir()
                && (ignoreFloor || !world.getBlockState(pos.down()).isAir())){
            world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
        }
    }
}