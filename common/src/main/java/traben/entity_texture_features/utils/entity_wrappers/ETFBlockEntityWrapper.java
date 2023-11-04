package traben.entity_texture_features.utils.entity_wrappers;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ETFBlockEntityWrapper implements ETFEntity {

    private final BlockEntity blockEntity;
    private final @Nullable Integer hashToAddToUUID;
    private UUID id = null;

    public ETFBlockEntityWrapper(BlockEntity entity, @Nullable Integer hashToAddToUUID) {
        this.blockEntity = entity;
        this.hashToAddToUUID = hashToAddToUUID;
    }

    public ETFBlockEntityWrapper(BlockEntity entity, @NotNull UUID uuid) {
        this.blockEntity = entity;
        this.id = uuid;
        this.hashToAddToUUID = 0;
    }

    public static UUID getUUIDForBlockEntity(BlockEntity blockEntity, @Nullable Integer hashToAddToUUID) {
        //Random random = new Random(blockEntity.getClass().hashCode());
        long most = /*random.nextLong() +*/ blockEntity.getType().hashCode() + (hashToAddToUUID == null ? 0 : hashToAddToUUID);
        long least = /*random.nextLong() +*/ blockEntity.getPos().hashCode() + blockEntity.getCachedState().hashCode();
        return new UUID(most, least);
    }

    @Override
    public Entity getEntity() {
        return null;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean isZombiePiglin() {
        return false;
    }

    @Override
    public EntityType<?> getType() {
        return EntityType.MARKER;
    }

    @Override
    public UUID getUuid() {
        if (id == null) id = getUUIDForBlockEntity(blockEntity, hashToAddToUUID);
        return id;
    }

    @Override
    public World getWorld() {
        return blockEntity.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return blockEntity.getPos();
    }

    @Override
    public int getBlockY() {
        return blockEntity.getPos().getY();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound compound) {
        return blockEntity.createNbt();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public Text getCustomName() {
        return Text.of("null");
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        return null;
    }

    //@Override
    //public boolean isOnFire() {
    //    return false;
    //}

    @Override
    public Iterable<ItemStack> getItemsEquipped() {
        return null;
    }

    @Override
    public Iterable<ItemStack> getHandItems() {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public float distanceTo(Entity entity) {
        return 0;
    }

    @Override
    public Vec3d getVelocity() {
        return Vec3d.ZERO;
    }

    @Override
    public EntityPose getPose() {
        return EntityPose.STANDING;
    }
}
