package traben.freshMobBehaviours.mixin.entity.hostile;

import com.sun.jna.platform.win32.WinBase;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
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
import traben.freshMobBehaviours.FreshMobBehaviours;

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
        if (config.hostilesWanderBetter) {
            int weight = world.getBlockState(pos.down()).isOf(Blocks.DIRT_PATH) ? 64 : 1;
            weight = world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK) ? 2 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.COBBLESTONE) ? 8 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.FARMLAND) ? 4 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.STONE_BRICKS) ? 16 : weight;
            weight = world.getBlockState(pos.down()).isOf(Blocks.MOSSY_COBBLESTONE) ? 32 : weight;

            if (self instanceof CreeperEntity && config.creepersAmbush) {
                Block[] doors = new Block[]{Blocks.OAK_DOOR,
                        Blocks.DARK_OAK_DOOR,
                        Blocks.ACACIA_DOOR,
                        Blocks.BIRCH_DOOR,
                        Blocks.IRON_DOOR,
                        Blocks.JUNGLE_DOOR,
                        Blocks.SPRUCE_DOOR,
                        Blocks.WARPED_DOOR,
                        Blocks.CRIMSON_DOOR,
                        Blocks.CRAFTING_TABLE,
                        Blocks.CHEST};
                for (Block b :
                        doors) {
                    if (
                            world.getBlockState(pos.east()).isOf(b) ||
                                    world.getBlockState(pos.west()).isOf(b) ||
                                    world.getBlockState(pos.north()).isOf(b) ||
                                    world.getBlockState(pos.south()).isOf(b)) {
                        weight = 256;
                        break;
                    }
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
    private int count = 40;

    //make mobs faster further from players
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void adjustSpeedByDistance(CallbackInfo ci) {
        if (self == null) {
            self = (HostileEntity) (Object) this;
        }
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
                        modifiedSpeed = FreshMobBehaviours.slowDownToVanillaByTarget(hostileBoi, closest, config.zombieBaseSpeedModifier, config.zombieDashSpeedModifier, config.hostilesCanDash && config.zombieCanDash);
                    } else if (hostileBoi instanceof SkeletonEntity) {
                        modifiedSpeed = FreshMobBehaviours.slowDownToVanillaByTarget(hostileBoi, closest, config.skeletonBaseSpeedModifier, config.skeletonDashSpeedModifier, config.hostilesCanDash && config.skeletonCanDash);
                    } else if (hostileBoi instanceof SpiderEntity) {
                        modifiedSpeed = FreshMobBehaviours.slowDownToVanillaByTarget(hostileBoi, closest, config.spiderBaseSpeedModifier, config.spiderDashSpeedModifier, config.hostilesCanDash && config.spiderCanDash);
                    } else if (hostileBoi instanceof CreeperEntity) {
                        modifiedSpeed = FreshMobBehaviours.slowDownToVanillaByTarget(hostileBoi, closest, config.creeperBaseSpeedModifier, config.creeperDashSpeedModifier, config.hostilesCanDash && config.creeperCanDash);
                    } else {
                        modifiedSpeed = FreshMobBehaviours.slowDownToVanillaByTarget(hostileBoi, closest, config.otherHostileBaseSpeedModifier, config.otherHostileDashSpeedModifier, config.hostilesCanDash && config.otherHostileCanDash);
                    }
                    modifier = new EntityAttributeModifier("BOOST", modifiedSpeed, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                    Objects.requireNonNull(hostileBoi.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addTemporaryModifier(modifier);
                }
                this.count = 0;
            }
        }
    }

}
