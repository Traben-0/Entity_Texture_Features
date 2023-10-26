package traben.entity_texture_features.utils.entity_wrappers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ETFEntityWrapper(Entity entity) implements ETFEntity {


    public boolean isZombiePiglin() {
        return entity instanceof ZombifiedPiglinEntity;
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
        return entity.hasCustomName() || entity.isPlayer();
    }

    @Override
    public Text getCustomName() {
        return entity.isPlayer() ? entity.getName() : entity.getDisplayName();
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


}
