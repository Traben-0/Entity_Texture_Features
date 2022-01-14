package traben.freshMobBehaviours.mixin.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ThrownEntity.class)
public abstract class MIX_ThrownEntity {

    @Inject(method = "tick", at = @At("TAIL"))
    private void snowparticles(CallbackInfo ci) {
        ThrownEntity thrown = (ThrownEntity) (Object)this;
        if (thrown.world.isClient && thrown.world.random.nextInt(5)==0) {
            thrown.world.addParticle(ParticleTypes.SNOWFLAKE, thrown.getX(), thrown.getY()+0.25, thrown.getZ(), MathHelper.nextBetween(thrown.world.getRandom(), -1.0F, 1.0F) * 0.008333333F, 0.05000000074505806D, MathHelper.nextBetween(thrown.world.getRandom(), -1.0F, 1.0F) * 0.008333333F);
        }
    }
}
