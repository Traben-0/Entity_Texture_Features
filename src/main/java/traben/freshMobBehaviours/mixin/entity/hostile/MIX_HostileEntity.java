package traben.freshMobBehaviours.mixin.entity.hostile;


import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMethods;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(HostileEntity.class)
public class MIX_HostileEntity {
    private HostileEntity self;

    @ModifyConstant(method = "updateDespawnCounter", constant = @Constant(intValue = 2))
    private int ignoreSunlightDespawnEffect(int value) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.hostilesStayLongerInDay) {
            self = ((HostileEntity) (Object) this);
            if (self instanceof CreeperEntity
                    || self instanceof SpiderEntity
                    || self instanceof EndermanEntity
                    || self instanceof DrownedEntity) {
                return 0;
            }else if (self.world.isRaining() &&
                    (self instanceof ZombieEntity
                    || self instanceof SkeletonEntity)){
                return 0;
            }
            else {
                return value;
            }
        } else {
            return 2;
        }
    }

    /**
     * @author Traben
     * @reason make em trend towards player lights and buildings needs worldview and pos
     */
    @Overwrite
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        self = ((HostileEntity) (Object) this);
        BlockPos selfpos = self.getBlockPos();
        if (config.hostilesWanderBetter) {
            //DESPAWN IF IN TEMPLE
            if (world.getBiome(selfpos).getCategory().getName().equals("desert")){
                if (FreshMethods.isBlockWithin2(selfpos,world,new Block[]{Blocks.STONE_PRESSURE_PLATE},true,false,true)){
                    System.out.println("found possible desert pyramid pressure plate, despawning mob");
                    self.discard();
                    return -40;
                }
            }
            int weight = world.getBlockState(pos.down()).isOf(Blocks.DIRT_PATH) ? 64 : 1;
            weight = world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK) ? 2 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.COBBLESTONE) ? 8 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.FARMLAND) ? 4 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.STONE_BRICKS) ? 16 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.MOSSY_COBBLESTONE) ? 32 : weight;

            if (self instanceof CreeperEntity && config.creepersAmbush) {
                Block[] doors =  new Block[]{Blocks.OAK_DOOR,
                        Blocks.DARK_OAK_DOOR,
                        Blocks.ACACIA_DOOR,
                        Blocks.BIRCH_DOOR,
                        Blocks.IRON_DOOR,
                        Blocks.JUNGLE_DOOR,
                        Blocks.SPRUCE_DOOR,
                        Blocks.WARPED_DOOR,
                        Blocks.CRIMSON_DOOR,
                        Blocks.CRAFTING_TABLE,
                        Blocks.CHEST,
                        };
                    if (FreshMethods.isBlockWithin2(pos,world, doors)){
                        weight += 1024;
                    }
            }

            int light = (world.getLightLevel(pos) - 7);
            weight *= light > 0 ? light : 1;
            return weight;
        } else {
            return 1;
        }
    }

    EntityAttributeModifier modifier;//=new EntityAttributeModifier("BOOST",0.4D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    EntityAttributeModifier zombRangemodifier;
    EntityAttributeModifier otherAndModdedRangemodifier;
    EntityAttributeModifier spiderRangemodifier;
    EntityAttributeModifier creeperRangemodifier;
    private int count = 40;

    //make mobs faster further from players
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void adjustSpeedByDistance(CallbackInfo ci) {

            self = (HostileEntity) (Object) this;

        if (!((HostileEntity) (Object) this).world.isClient) {
            this.count++;
            if (this.count >= 15) {
                HostileEntity hostileBoi = ((HostileEntity) (Object) this);
                PlayerEntity closest = hostileBoi.getWorld().getClosestPlayer(hostileBoi, -1);
                if (closest != null) {
                    if (modifier != null) {
                        Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(modifier);
                    }
                    Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
                    float modifiedSpeed;
                    if (hostileBoi instanceof ZombieEntity) {
                        //zombie has weird range coding

                        modifiedSpeed = FreshMethods.slowDownToVanillaByTarget(hostileBoi, closest, config.zombieBaseSpeedModifier, config.zombieDashSpeedModifier, config.hostilesCanDash && config.zombieCanDash);
                        float zombieRange = (float)(config.hostilesTargetRange -1);
                        if (zombRangemodifier != null) {
                            Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).removeModifier(zombRangemodifier);
                        }
                        zombRangemodifier = new EntityAttributeModifier("BOOST_RANGEZ", zombieRange, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                        Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).addTemporaryModifier(zombRangemodifier);
                    } else if (hostileBoi instanceof SkeletonEntity) {
                        modifiedSpeed = FreshMethods.slowDownToVanillaByTarget(hostileBoi, closest, config.endermenBaseSpeedModifier, config.endermenDashSpeedModifier, config.hostilesCanDash && config.endermenCanDash);
                    }else if (hostileBoi instanceof EndermanEntity) {
                            modifiedSpeed = FreshMethods.slowDownToVanillaByTarget(hostileBoi, closest, config.skeletonBaseSpeedModifier, config.skeletonDashSpeedModifier, config.hostilesCanDash && config.skeletonCanDash);
                    } else if (hostileBoi instanceof SpiderEntity) {
                        modifiedSpeed = FreshMethods.slowDownToVanillaByTarget(hostileBoi, closest, config.spiderBaseSpeedModifier, config.spiderDashSpeedModifier, config.hostilesCanDash && config.spiderCanDash);
                        float spiderRange = (float)(config.hostilesTargetRange -1);
                        if (spiderRangemodifier != null) {
                            Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).removeModifier(spiderRangemodifier);
                        }
                        spiderRangemodifier = new EntityAttributeModifier("BOOST_RANGES", spiderRange, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                        Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).addTemporaryModifier(spiderRangemodifier);
                    } else if (hostileBoi instanceof CreeperEntity) {
                        modifiedSpeed = FreshMethods.slowDownToVanillaByTarget(hostileBoi, closest, config.creeperBaseSpeedModifier, config.creeperDashSpeedModifier, config.hostilesCanDash && config.creeperCanDash);
                        float creeperrange = (float)(config.hostilesTargetRange -1);
                        if (creeperRangemodifier!= null) {
                            Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).removeModifier(creeperRangemodifier);
                        }
                        creeperRangemodifier = new EntityAttributeModifier("BOOST_RANGEC", creeperrange, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                        Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).addTemporaryModifier(creeperRangemodifier);
                    } else {
                        modifiedSpeed = FreshMethods.slowDownToVanillaByTarget(hostileBoi, closest, config.otherHostileBaseSpeedModifier, config.otherHostileDashSpeedModifier, config.hostilesCanDash && config.otherHostileCanDash);
                        float otherrange = (float)(config.hostilesTargetRange -1);
                        if (otherAndModdedRangemodifier!= null) {
                            Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).removeModifier(otherAndModdedRangemodifier);
                        }
                        otherAndModdedRangemodifier = new EntityAttributeModifier("BOOST_RANGEO", otherrange, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                        Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)).addTemporaryModifier(otherAndModdedRangemodifier);
                    }
                    modifier = new EntityAttributeModifier("BOOST", modifiedSpeed, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                    Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addTemporaryModifier(modifier);
                }
                this.count = 0;
            }
        }
    }

}
