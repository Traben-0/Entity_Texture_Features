package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_texture_features.features.player.ETFPlayerEntity;

@Mixin(SkullBlockEntity.class)
public abstract class MixinSkullBlockEntity extends BlockEntity implements ETFPlayerEntity {


    public MixinSkullBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Entity etf$getEntity() {
        return null;
    }

    @Override
    public boolean etf$isTeammate(PlayerEntity player) {
        return false;
    }

    @Override
    public PlayerInventory etf$getInventory() {
        return null;
    }

    @Override
    public boolean etf$isPartVisible(PlayerModelPart part) {
        return false;
    }

    @Override
    public Text etf$getName() {
        return Text.of("player_skull # " + etf$getUuidAsString());
    }

    @Override
    public String etf$getUuidAsString() {
        return etf$getUuid().toString();
    }
}


