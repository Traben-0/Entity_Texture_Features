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

import static traben.entity_texture_features.client.ETF_CLIENT.etf$UUID_entityAwaitingDataClearing;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    //will force update entity texture at any player interaction useful for debugging
    @Inject(method = "interact", at = @At("RETURN"))
    private void etf$injected(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (((LivingEntity) (Object) this).world.isClient()) {
            if (!etf$UUID_entityAwaitingDataClearing.containsKey(entity.getUuid())) {
                etf$UUID_entityAwaitingDataClearing.put(entity.getUuid(), System.currentTimeMillis());
            }
        }
    }
}


