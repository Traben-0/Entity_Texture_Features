package traben.freshMobBehaviours.mixin.entity.projectile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMethods;
import traben.freshMobBehaviours.FreshMobBehaviours;

@Mixin(SnowballEntity.class)
public abstract class MIX_SnowballEntity {

    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ThrownItemEntity;onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void mixin(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity entity = entityHitResult.getEntity();
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.snowballsCauseFreeze && entity instanceof LivingEntity) {
            entity.setInPowderSnow(true);
            entity.setFrozenTicks(500);
            if (entity.isOnFire()){
                entity.extinguish();
            }
            FreshMethods.extinguishFire((SnowballEntity) (Object)this);
        }
    }

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void extinguishing(CallbackInfo ci) {
        FreshMethods.extinguishFire((SnowballEntity) (Object)this);
    }
}
