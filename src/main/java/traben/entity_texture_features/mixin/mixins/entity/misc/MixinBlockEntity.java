package traben.entity_texture_features.mixin.mixins.entity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BedPart;
//#if MC >= 12106
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
//#endif
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.UUID;

import static traben.entity_texture_features.ETFApi.getBlockEntityTypeToTranslationKey;

@Mixin(BlockEntity.class)
public abstract class MixinBlockEntity implements ETFEntity {

    @Unique
    private UUID etf$id = null;


    @Shadow
    public abstract BlockEntityType<?> getType();


    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    @Nullable
    public abstract Level getLevel();

    //#if MC>=12106
    @Shadow protected abstract void saveMetadata(final ValueOutput valueOutput);
    //#else
    //$$ @Shadow protected abstract void saveMetadata(final CompoundTag compoundTag);
    //#endif

    @Override
    public EntityType<?> etf$getType() {
        return null;
    }

    @Override
    public UUID etf$getUuid() {
        if (etf$id == null) etf$id = ETFApi.getUUIDForBlockEntity((BlockEntity) ((Object) this));
        return etf$id;
    }

    @Override
    public int etf$getOptifineId() {
        BlockPos pos = etf$getBlockPos();
        //optifine matching id
        int hash = ETFUtils2.optifineHashing(37);
        hash = ETFUtils2.optifineHashing(hash + pos.getX());
        hash = ETFUtils2.optifineHashing(hash + pos.getZ());
        return ETFUtils2.optifineHashing(hash + pos.getY());
    }

    @Override
    public int etf$getOptifineVehicleId() {
        return etf$getOptifineId();
    }

    @Override
    public Level etf$getWorld() {
        return getLevel();
    }

    @Override
    public BlockPos etf$getBlockPos() {
        var self = (BlockEntity) ((Object) this);
        if(self instanceof BedBlockEntity bed){
            var state = bed.getBlockState();
            if(state.getValue(BedBlock.PART) == BedPart.HEAD){
                return getBlockPos().relative(state.getValue(BedBlock.FACING));
            }
        }
        return getBlockPos();
    }

    @Override
    public int etf$getBlockY() {
        return getBlockPos().getY();
    }

    @Override
    public CompoundTag etf$getNbt() {
        //#if MC>=12106
        return ETFRenderContext.cacheEntityNBTForFrame(etf$getUuid(),
                ()->{
                    TagValueOutput compound = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
                    saveMetadata(compound);
                    return compound.buildResult();
                });
        //#else
        //$$ return ETFRenderContext.cacheEntityNBTForFrame(etf$getUuid(),
        //$$         ()->{
        //$$             CompoundTag compound = new CompoundTag();
        //$$             saveMetadata(compound);
        //$$             return compound;
        //$$         });
        //#endif
    }

    @Override
    public boolean etf$hasCustomName() {
        return this instanceof Nameable name && name.hasCustomName();
    }

    @Override
    public Component etf$getCustomName() {
        return this instanceof Nameable name && name.hasCustomName() ? name.getCustomName() : Component.nullToEmpty("null");
    }

    @Override
    public Team etf$getScoreboardTeam() {
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
        BlockPos pos = getBlockPos();
        float f = (float) (pos.getX() - entity.getX());
        float g = (float) (pos.getY() - entity.getY());
        float h = (float) (pos.getZ() - entity.getZ());
        return Mth.sqrt(f * f + g * g + h * h);
    }

    @Override
    public Vec3 etf$getVelocity() {
        return Vec3.ZERO;
    }

    @Override
    public Pose etf$getPose() {
        return Pose.STANDING;
    }

    @Override
    public boolean etf$canBeBright() {
        return false;
    }

    @Override
    public boolean etf$isBlockEntity() {
        return true;
    }

    @Override
    public String etf$getEntityKey() {
        return getBlockEntityTypeToTranslationKey(getType());
    }
}
