package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.texture_features.ETFManager;

@Mixin(BlazeEntity.class)
public abstract class MixinBlazeEntity extends HostileEntity {

    @SuppressWarnings("unused")
    protected MixinBlazeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"),
            index = 2
    )
    private double mixin(double x) {
        if (ETFClientCommon.ETFConfigData.enableCustomTextures
                && ETFManager.getInstance().ENTITY_TYPE_IGNORE_PARTICLES.contains(this.getType())) {
            return -500;
        }
        return x;
    }


}


