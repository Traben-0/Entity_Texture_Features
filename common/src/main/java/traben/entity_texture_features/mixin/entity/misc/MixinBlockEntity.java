package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.UUID;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements ETFEntity {

    @Unique
    private UUID etf$id = null;

    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    @Nullable
    public abstract World getWorld();

    @Shadow
    public abstract NbtCompound createNbt();

    @Override
    public EntityType<?> etf$getType() {
        return EntityType.MARKER;
    }

    @Override
    public UUID etf$getUuid() {
        if (etf$id == null) etf$id = ETFApi.getUUIDForBlockEntity((BlockEntity) ((Object) this));
        return etf$id;
    }

    @Override
    public World etf$getWorld() {
        return getWorld();
    }

    @Override
    public BlockPos etf$getBlockPos() {
        return getPos();
    }

    @Override
    public int etf$getBlockY() {
        return getPos().getY();
    }

    @Override
    public NbtCompound etf$writeNbt(NbtCompound compound) {
        return createNbt();
    }

    @Override
    public boolean etf$hasCustomName() {
        return this instanceof Nameable name && name.hasCustomName();
    }

    @Override
    public Text etf$getCustomName() {
        return this instanceof Nameable name && name.hasCustomName() ? name.getCustomName() : Text.of("null");
    }

    @Override
    public AbstractTeam etf$getScoreboardTeam() {
        return null;
    }

    @Override
    public Iterable<ItemStack> etf$getItemsEquipped() {
        return null;
    }

    @Override
    public Iterable<ItemStack> etf$getHandItems() {
        return null;
    }

    @Override
    public Iterable<ItemStack> etf$getArmorItems() {
        return null;
    }

    @Override
    public float etf$distanceTo(Entity entity) {
        BlockPos pos = getPos();
        float f = (float) (pos.getX() - entity.getX());
        float g = (float) (pos.getY() - entity.getY());
        float h = (float) (pos.getZ() - entity.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    @Override
    public Vec3d etf$getVelocity() {
        return Vec3d.ZERO;
    }

    @Override
    public EntityPose etf$getPose() {
        return EntityPose.STANDING;
    }

    @Override
    public boolean etf$canBeBright() {
        return false;
    }

    @Override
    public boolean etf$isBlockEntity() {
        return true;
    }
}
