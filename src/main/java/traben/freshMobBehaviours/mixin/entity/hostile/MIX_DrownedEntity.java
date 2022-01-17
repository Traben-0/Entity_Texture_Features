package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import traben.freshMobBehaviours.Configurator2000;

import java.util.Random;

import static net.minecraft.entity.mob.HostileEntity.isSpawnDark;

@Mixin(DrownedEntity.class)
public abstract class MIX_DrownedEntity {


    //drown spawning algorithm will always think it is night/dark
    @Redirect(method = "canSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/DrownedEntity;isSpawnDark(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z"))
    private static boolean spawnAllDay(ServerWorldAccess serverWorldAccess, BlockPos blockPos, Random random) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        return config.drownedSpawnAnyLight || isSpawnDark(serverWorldAccess, blockPos, random);
    }
}