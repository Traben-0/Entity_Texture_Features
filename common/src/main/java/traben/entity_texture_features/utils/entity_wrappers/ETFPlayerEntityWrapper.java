package traben.entity_texture_features.utils.entity_wrappers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ETFPlayerEntityWrapper(PlayerEntity entity) implements ETFPlayerEntity {


    public boolean isZombiePiglin() {
        return false;
    }

    @Override
    public @Nullable Entity getEntity() {
        return entity;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return null;
    }

    public EntityType<?> getType() {
        return entity.getType();
    }

    public UUID getUuid() {
        return entity.getUuid();
    }

    @Override
    public World getWorld() {
        return entity.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return entity.getBlockPos();
    }

    @Override
    public int getBlockY() {
        return entity.getBlockY();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound compound) {
        return entity.writeNbt(compound);
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public Text getCustomName() {
        return getName();
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        return entity.getScoreboardTeam();
    }

    //@Override
    //public boolean isOnFire() {
    //    return entity.isOnFire();
    //}

    @Override
    public Iterable<ItemStack> getItemsEquipped() {
        return entity.getItemsEquipped();
    }

    @Override
    public Iterable<ItemStack> getHandItems() {
        return entity.getHandItems();
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return entity.getArmorItems();
    }

    @Override
    public float distanceTo(Entity entity) {
        return this.entity.distanceTo(entity);
    }

    @Override
    public Vec3d getVelocity() {
        return entity.getVelocity();
    }

    @Override
    public EntityPose getPose() {
        return entity.getPose();
    }


    @Override
    public boolean isTeammate(PlayerEntity player) {
        return this.entity.isTeammate(player);
    }

    @Override
    public PlayerInventory getInventory() {
        return entity.getInventory();
    }

    @Override
    public boolean isPartVisible(PlayerModelPart part) {
        return entity.isPartVisible(part);
    }

    @Override
    public Text getName() {
        return entity.getName();
    }

    @Override
    public String getUuidAsString() {
        return entity.getUuidAsString();
    }
}
