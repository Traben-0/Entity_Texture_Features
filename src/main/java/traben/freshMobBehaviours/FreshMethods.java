package traben.freshMobBehaviours;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import traben.freshMobBehaviours.mixin.accessor.ACC_MobEntity;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public interface FreshMethods {

    static float slowDownToVanillaByTarget(LivingEntity self, PlayerEntity target, double baseCloseBoost, double distantSpeedBoost, boolean canDash) {
        float distance123 = self.distanceTo(target);
        //speed up for chasing players then slow down when close
        //basically you cannot outrun mobs but they do not overwhelm you at close range
        double range;
        try {
            range = self.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).getValue();
        } catch (NullPointerException e) {
            range = 24;
        }
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (canDash && self.canSee(target) && !target.getAbilities().creativeMode
                && (range * config.hostilesTargetRange) >= self.distanceTo(target)
        ) {
            if (distance123 <= 32) {
                //Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
                if (config.skeletonKeepDistance && self instanceof SkeletonEntity && distance123 < 8) {
                    //dash away from player up to dash speed
                    distantSpeedBoost = (distantSpeedBoost / 8) * (8 - distance123);
                } else if (self instanceof EndermanEntity) {
                    distantSpeedBoost = ((EndermanEntity) self).isAngry() ? distantSpeedBoost * (distance123 / 32) : 0;
                } else if (self instanceof HostileEntity) {
                    distantSpeedBoost = distantSpeedBoost * (distance123 / 32);
                    System.out.println("boi is attacking");
                } else {//all mobs
                    distantSpeedBoost = distantSpeedBoost * (distance123 / 32);
                }
            } else if (self instanceof EndermanEntity) {
                distantSpeedBoost = ((EndermanEntity) self).isAngry() ? distantSpeedBoost : 0;
            }
            return (float) (distantSpeedBoost + baseCloseBoost);
        } else {//stay vanilla speed + base boost when unseen
            return (float) baseCloseBoost;
        }
    }

    // only called if meant to happem
    static void animalSpookedGoalAdjustments(PathAwareEntity mob) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        GoalSelector goal = ((ACC_MobEntity) mob).getGoalSelector();
        //remove goals
        for (Goal g :
                goal.getGoals()) {//remove
            if (g instanceof EscapeDangerGoal) {
                goal.remove(g);
                break;//only if 1 goal to replace
            }
        }
        goal.add(5, new FleeEntityGoal<>(mob, PlayerEntity.class, 8, 1.25D, 1.75D));
        if (mob instanceof SheepEntity) {
            goal.add(5, new FleeEntityGoal<>(mob, WolfEntity.class, 8, 1.25D, 1.75D));
        } else if (config.animalsEatGrass
                && (mob instanceof CowEntity
                || mob instanceof HorseBaseEntity
                || mob instanceof GoatEntity)) {
            goal.add(5, new EatGrassGoal(mob));
        }
        goal.add(1, new EscapeDangerGoal(mob, 2D));
    }


    static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks) {
        return isBlockWithin2(pos, world, searchblocks, false, false, false);
    }

    static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks, boolean checkUp, boolean checkDown) {
        return isBlockWithin2(pos, world, searchblocks, checkUp, checkDown, false);
    }

    static boolean isBlockWithin2(BlockPos pos, WorldView world, Block[] searchblocks, boolean checkUp, boolean checkDown, boolean DO_PYRAMID_CHECK) {
        //efficiency skip
        BlockPos[] notAirPositions;
        //remove air blocks from check to increase efficiency in large searchlist
        ArrayList<BlockPos> testpositions = new ArrayList<>();
        if (!world.getBlockState(pos).isAir()) testpositions.add(pos);
        try {
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
        } catch (NullPointerException e) {
            System.out.println(e);
        }//not valid}
        notAirPositions = testpositions.toArray(new BlockPos[0]);

        for (Block block :
                searchblocks) {
            for (BlockPos checkPos :
                    notAirPositions) {
                if (world.getBlockState(checkPos).isOf(block)) {
                    if (DO_PYRAMID_CHECK) {
                        return (world.getLightLevel(checkPos) == 0
                                && world.getBlockState(checkPos.north(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.east(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.west(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.south(2)).isOf(Blocks.CHEST)
                                && world.getBlockState(checkPos.down(2)).isOf(Blocks.TNT));
                    } else {
                        return true;
                    }
                }
            }

        }
        if (checkUp) {
            if (isBlockWithin2(pos.up(), world, searchblocks, false, false)) {
                return true;
            }
        }
        if (checkDown) {
            return isBlockWithin2(pos.down(), world, searchblocks, false, false);
        }
        return false;

    }

    static void setFire(BlockPos pos, World world, int percentChance) {
        setFire(pos, world, percentChance, false);
    }

    static void setFire(BlockPos pos, World world, int percentChance, boolean ignoreFloor) {
        Random rand = new Random();
        if (percentChance <= rand.nextInt(100) + 1
                && world.getBlockState(pos).isAir()
                && (ignoreFloor || !world.getBlockState(pos.down()).isAir())) {
            world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
        }
    }

    static void extinguishFire(SnowballEntity ball) {
        BlockPos pos = ball.getBlockPos();
        BlockState blockState = ball.world.getBlockState(pos);
        if (blockState.isIn(BlockTags.FIRE)) {
            ball.world.removeBlock(pos, false);
        } else if (AbstractCandleBlock.isLitCandle(blockState)) {
            AbstractCandleBlock.extinguish(null, blockState, ball.world, pos);
        } else if (CampfireBlock.isLitCampfire(blockState)) {
            ball.world.syncWorldEvent(null, 1009, pos, 0);
            CampfireBlock.extinguish(ball.getOwner(), ball.world, pos, blockState);
            ball.world.setBlockState(pos, blockState.with(CampfireBlock.LIT, false));
        }

    }

    static void sendGlobalMessage(LivingEntity self, String text) {
        try {
            for (PlayerEntity p :
                    Objects.requireNonNull(self.world.getServer()).getPlayerManager().getPlayerList()) {
                p.sendMessage(Text.of(text), false);
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }
}
