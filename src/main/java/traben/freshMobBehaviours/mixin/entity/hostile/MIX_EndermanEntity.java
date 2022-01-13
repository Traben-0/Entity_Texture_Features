package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;

import java.util.Objects;

@Mixin(EndermanEntity.class)
public abstract class MIX_EndermanEntity {
    @Shadow abstract boolean teleportTo(Entity entity);

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void adjustSpeedByDistance(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.endermenCuriousOfPlayer) {
            try {
                EndermanEntity self = (EndermanEntity) (Object) this;
                PlayerEntity player = Objects.requireNonNull(self.world.getClosestPlayer(self, -1));
                if (!self.world.isClient
                        && !self.canSee(player)
                        && !player.getAbilities().creativeMode
                        //&& self.world.getServer().getOverworld() == self.getWorld()
                        && self.getRandom().nextInt(300) == 1
                ) {
                    teleportTo(player);
                }
            } catch (Exception e) {
//nahfam
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void randomSpawnBlock(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        EndermanEntity self = (EndermanEntity) (Object) this;
        if (config.endermenSpawnBlocks
                && !self.world.getBiome(self.getBlockPos()).getCategory().getName().equals("the_end")
                && !self.world.getBiome(self.getBlockPos()).getCategory().getName().equals("nether")) {

            int rand = self.getRandom().nextInt(15);
            if (rand == 0) {
                self.setCarriedBlock(Blocks.NETHERRACK.getDefaultState());
            } else if (rand == 1) {
                self.setCarriedBlock(Blocks.END_STONE.getDefaultState());
            }else if (rand == 2) {
                self.setCarriedBlock(Blocks.WARPED_NYLIUM.getDefaultState());
            }
        }
    }
}
