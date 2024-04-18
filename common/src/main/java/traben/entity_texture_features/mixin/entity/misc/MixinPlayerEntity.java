package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.player.ETFPlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity implements ETFPlayerEntity {


    @SuppressWarnings("unused")
    public MixinPlayerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract PlayerInventory getInventory();

    @Shadow
    public abstract boolean isPartVisible(PlayerModelPart modelPart);

    @Shadow
    public abstract Text getName();

    //will force update entity texture at any player interaction useful for debugging
    @Inject(method = "interact", at = @At("HEAD"))
    private void etf$injected(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {

        //noinspection DataFlowIssue
        if (getWorld().isClient()) {
            if (ETF.config().getConfig().debugLoggingMode != ETFConfig.DebugLogMode.None)
//                UUID_DEBUG_EXPLANATION_MARKER.add(entity.getUuid());
//            if (!UUID_ENTITY_AWAITING_DATA_CLEARING.containsKey(entity.getUuid())) {
//                UUID_ENTITY_AWAITING_DATA_CLEARING.put(entity.getUuid(), System.currentTimeMillis());
                ETFManager.getInstance().markEntityForDebugPrint(entity.getUuid());
        }
    }

    @Override
    public Entity etf$getEntity() {
        return this;
    }

    @Override
    public boolean etf$isTeammate(PlayerEntity player) {
        return isTeammate(player);
    }

    @Override
    public PlayerInventory etf$getInventory() {
        return getInventory();
    }

    @Override
    public boolean etf$isPartVisible(PlayerModelPart part) {
        return isPartVisible(part);
    }

    @Override
    public Text etf$getName() {
        return getName();
    }

    @Override
    public String etf$getUuidAsString() {
        return getUuidAsString();
    }
}


