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

import static traben.entity_texture_features.client.ETFClient.*;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {


    //will force update entity texture at any player interaction useful for debugging
    @Inject(method = "interact", at = @At("HEAD"))
    private void etf$injected(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (((LivingEntity) (Object) this).world.isClient()) {
            if (ETFConfigData.enableDebugLogging)
                UUID_DEBUG_EXPLANATION_MARKER.add(entity.getUuid());
            if (!UUID_ENTITY_AWAITING_DATA_CLEARING.containsKey(entity.getUuid())) {
                UUID_ENTITY_AWAITING_DATA_CLEARING.put(entity.getUuid(), System.currentTimeMillis());
            }
        }
    }
}


