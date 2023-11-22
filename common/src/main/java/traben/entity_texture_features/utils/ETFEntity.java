package traben.entity_texture_features.utils;

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

import java.util.UUID;

public interface ETFEntity {


    boolean etf$canBeBright();

    boolean etf$isBlockEntity();

    EntityType<?> etf$getType();

    UUID etf$getUuid();


    World etf$getWorld();

    BlockPos etf$getBlockPos();

    int etf$getBlockY();

    NbtCompound etf$writeNbt(NbtCompound compound);

    boolean etf$hasCustomName();

    Text etf$getCustomName();

    AbstractTeam etf$getScoreboardTeam();

    //boolean isOnFire();

    Iterable<ItemStack> etf$getItemsEquipped();

    Iterable<ItemStack> etf$getHandItems();

    Iterable<ItemStack> etf$getArmorItems();

    float etf$distanceTo(Entity entity);

    Vec3d etf$getVelocity();

    EntityPose etf$getPose();
}
