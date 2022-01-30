package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.UUID_entityAwaitingDataClearing;

@Mixin(PlayerEntity.class)
public abstract class MIX_PlayerEntity {

    //will force update entity texture at any player interaction this should cover things like nametagging and collar changing
    @Inject(method = "interact", at = @At("RETURN"))
    private void injected(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (((LivingEntity)(Object)this).world.isClient()) {
            if (!UUID_entityAwaitingDataClearing.containsKey(entity.getUuid())){ UUID_entityAwaitingDataClearing.put(entity.getUuid(),((LivingEntity) (Object) this).world.getTime());}
        }
    }
}


