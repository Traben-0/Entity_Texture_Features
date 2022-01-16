package traben.freshMobBehaviours.mixin.blocks;

import com.mojang.datafixers.util.Either;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMethods;

import java.util.List;
import java.util.Objects;

@Mixin(BedBlock.class)
public abstract class MIX_BedBlock {


    @Inject(method = "onUse", at = @At(value = "RETURN"), cancellable = true)
    private void injected(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        List<PhantomEntity> loudbois = player.world.getNonSpectatingEntities(PhantomEntity.class, player.getBoundingBox().expand(128));
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (player.world.isClient()
                && config.sleepNeedsPhantomMembranes
                && !loudbois.isEmpty()){
            loudbois.get(0).world.playSound(player, player.getBlockPos().up(10), SoundEvents.ENTITY_PHANTOM_AMBIENT, SoundCategory.HOSTILE, 1.3F, 1.0F);
        }
    }

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;trySleep(Lnet/minecraft/util/math/BlockPos;)Lcom/mojang/datafixers/util/Either;"))
    private Either<PlayerEntity.SleepFailureReason, Unit>  injected(PlayerEntity player, BlockPos pos) {
        if (player.world.getDifficulty() == Difficulty.PEACEFUL) {
        return player.trySleep(pos);
    } else {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.sleepNeedsShelter && (player.world.isSkyVisible(pos) || player.world.getLightLevel(LightType.SKY, pos) > 11)) {//if see sky
            player.sendMessage(Text.of("This bed needs more shelter"), true);
            FreshMethods.sendGlobalMessage(player, "This bed needs more shelter");
            return Either.right(Unit.INSTANCE);
        } else {
            //if has membrane sleep
            if ((config.sleepNeedsPhantomMembranes && (player.getInventory().getMainHandStack().getTranslationKey().equals("item.minecraft.phantom_membrane") ||
                    player.getEquippedStack(EquipmentSlot.OFFHAND).getTranslationKey().equals("item.minecraft.phantom_membrane"))
                    || player.getAbilities().creativeMode)) {
                if (!player.getAbilities().creativeMode) {
                    if (player.getInventory().getMainHandStack().getTranslationKey().equals("item.minecraft.phantom_membrane")) {
                        player.getInventory().getMainHandStack().decrement(1);
                    } else {
                        player.getEquippedStack(EquipmentSlot.OFFHAND).decrement(1);
                    }
                    //player.sendMessage(Text.of("With your nightmares slain you rest through the night..."), true);
                    if (player.world.isClient) {
                        player.world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_PHANTOM_DEATH, SoundCategory.HOSTILE, 0.4F, 0.8F);
                    }
                }
                return player.trySleep(pos);
            } else {
                //send no membrane msg and dont sleep
                if (config.sleepNeedsPhantomMembranes) {
                    player.sendMessage(Text.of("Needs Phantom Membranes..."), true);
                    FreshMethods.sendGlobalMessage(player, "You must slay your nightmares to sleep.");
                    if (player.world.isNight()
                            && player.world.getNonSpectatingEntities(PhantomEntity.class, player.getBoundingBox().expand(128)).size() == 0) {
                        if (!player.world.isClient()) {

                            ServerWorld servWrld = Objects.requireNonNull(player.getServer()).getOverworld();
                            PhantomEntity entity = (PhantomEntity) EntityType.PHANTOM.create(servWrld);
                            if (entity != null) {
                                BlockPos checkpos = new BlockPos(
                                        player.getBlockX() + player.getRandom().nextInt(64) - 32,
                                        player.getBlockY() + player.getRandom().nextInt(64) + 16,
                                        player.getBlockZ() + player.getRandom().nextInt(64) - 32);
                                while (!(player.world.getBlockState(checkpos).isAir())) {
                                    checkpos = new BlockPos(
                                            player.getBlockX() + player.getRandom().nextInt(64) - 32,
                                            player.getBlockY() + player.getRandom().nextInt(64) + 16,
                                            player.getBlockZ() + player.getRandom().nextInt(64) - 32);
                                }
                                entity.refreshPositionAndAngles(checkpos, 0.0F, 0.0F);
                                servWrld.spawnEntityAndPassengers(entity);
                                servWrld.sendEntityStatus(player, (byte) 18);
                                //entity.world.playSound(player, player.getBlockPos(), SoundEvents.ENTITY_PHANTOM_SWOOP, SoundCategory.HOSTILE, 0.8F, 0.8F);
                                //player.sendMessage(Text.of("Needs Phantom Membranes..."), true);
                            }
                        }
                    }
                } else {
                    return player.trySleep(pos);
                }
                return Either.right(Unit.INSTANCE);
            }
        }
    }
}
}
