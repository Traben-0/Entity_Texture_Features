package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.UUID_entityAwaitingDataClearing;

@Mixin(PassiveEntity.class)
public abstract class MIX_PassiveEntity implements entity_texture_features_METHODS {

    @Inject(method = "onGrowUp", at = @At("TAIL"))
    private void injected(CallbackInfo ci) {

            UUID id = ((LivingEntity) (Object) this).getUuid();
            if (!UUID_entityAwaitingDataClearing.containsKey(id)){ UUID_entityAwaitingDataClearing.put(id,((LivingEntity) (Object) this).world.getTime());}

    }


}


