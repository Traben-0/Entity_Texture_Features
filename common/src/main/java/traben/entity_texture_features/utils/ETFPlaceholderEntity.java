package traben.entity_texture_features.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.Arm;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;

import java.util.UUID;

// this entity exists as a placeholder for Block Entities when they are processed by ETF
public class ETFPlaceholderEntity extends Entity {

   // private BlockEntity HiddenBlockEntity;

    public ETFPlaceholderEntity( BlockEntity block, UUID uuid) {
        super(EntityType.MARKER , block.getWorld());
        //HiddenBlockEntity = block;
        setPos(block.getPos().getX(), block.getPos().getY(), block.getPos().getZ());
        setUuid(uuid);
        if (block instanceof Nameable nameable) {
            setCustomName(nameable.getCustomName());
            setCustomNameVisible(nameable.hasCustomName());
        }
    }

    @Override
    public String toString() {
        return getBlockPos().toString()+getUuidAsString();
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }


    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }



    @Override
    public void checkDespawn() {
        this.discard();
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
        this.discard();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }


}
