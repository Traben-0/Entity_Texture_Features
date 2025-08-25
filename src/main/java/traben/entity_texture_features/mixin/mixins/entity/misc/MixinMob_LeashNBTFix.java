package traben.entity_texture_features.mixin.mixins.entity.misc;

import org.spongepowered.asm.mixin.Mixin;
//#if MC>= 12105
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Leashable;

@Mixin(Mob.class)
public class MixinMob_LeashNBTFix {

    //#if MC >= 12106
    @WrapOperation(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;writeLeashData(Lnet/minecraft/world/level/storage/ValueOutput;Lnet/minecraft/world/entity/Leashable$LeashData;)V"))
    private void etf$catchLeashCrash(final Mob instance, final net.minecraft.world.level.storage.ValueOutput valueOutput, final Leashable.LeashData leashData, final Operation<Void> original) {
    //#else
    //$$ @WrapOperation(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;writeLeashData(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/entity/Leashable$LeashData;)V"))
    //$$ private void etf$catchLeashCrash(final Mob instance, final net.minecraft.nbt.CompoundTag valueOutput, final Leashable.LeashData leashData, final Operation<Void> original) {
    //#endif
        try {
            original.call(instance, valueOutput, leashData);
        } catch (Exception e) {
            // ignore this crash, seems to always be the case when an entity is ever unleashed
            if (leashData.leashHolder == null && leashData.delayedLeashInfo == null) {
                return;
            }
            throw e;
        }
    }
}
//#else
//$$ @Mixin(traben.entity_texture_features.mixin.CancelTarget.class)
//$$ public class MixinMob_LeashNBTFix { }
//#endif
