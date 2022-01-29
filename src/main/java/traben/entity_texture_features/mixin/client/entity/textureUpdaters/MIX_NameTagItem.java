package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.UUID_entityAwaitingDataClearing;

@Mixin(NameTagItem.class)
public abstract class MIX_NameTagItem implements entity_texture_features_METHODS {

    @Inject(method = "useOnEntity", at = @At("RETURN"))
    private void injected(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            if (!UUID_entityAwaitingDataClearing.containsKey(entity.getUuid())){ UUID_entityAwaitingDataClearing.put(entity.getUuid(),(entity.world.getTime()));}

    }


}


