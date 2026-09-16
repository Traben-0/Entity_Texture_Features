package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;

@Mixin(Player.class)
public abstract class MixinPlayerEntity_DebugClick extends Entity {


    @SuppressWarnings("unused")
    public MixinPlayerEntity_DebugClick(EntityType<?> type, Level world) {
        super(type, world);
    }



    //will force update entity texture at any player interaction useful for debugging
    @Inject(method = "interactOn", at = @At("HEAD"))
    private void etf$injected(CallbackInfoReturnable<InteractionResult> cir, @Local(argsOnly = true) Entity entity) {
        if (level().isClientSide()) {
            if (ETF.config().getConfig().debugLoggingMode != ETFConfig.DebugLogMode.None)
                ETFManager.getInstance().markEntityForDebugPrint(entity.getUUID());
        }
    }

}


