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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ETFEntity {


    boolean isZombiePiglin();

    @Nullable Entity getEntity();

    @Nullable BlockEntity getBlockEntity();

    EntityType<?> getType();

    UUID getUuid();

    World getWorld();

    BlockPos getBlockPos();

    int getBlockY();

    NbtCompound writeNbt(NbtCompound compound);

    boolean hasCustomName();

    Text getCustomName();

    AbstractTeam getScoreboardTeam();

    //boolean isOnFire();

    Iterable<ItemStack> getItemsEquipped();

    Iterable<ItemStack> getHandItems();

    Iterable<ItemStack> getArmorItems();

    float distanceTo(Entity entity);

    Vec3d getVelocity();

    EntityPose getPose();
}
