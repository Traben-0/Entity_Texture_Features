package traben.entity_texture_features.features.state;
//#if MC>=12109
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
//#endif
//#if MC>=12102
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//#endif
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import traben.entity_texture_features.utils.ETFEntity;
import java.util.*;

// TODO implement caching version for 1.21.2+ as smuggling in the entity wont work forever
public class ETFEntityRenderStateViaReference implements ETFEntityRenderState {


    private final ETFEntity entity;
    public ETFEntityRenderStateViaReference(ETFEntity entity) {
            this.entity = entity;
    }

    @Deprecated
    public ETFEntity entity() {
        return entity;
    }

    //#if MC>=12102
    private EntityRenderState vanillaState = null;

    @Override
    public EntityRenderState vanillaState() {
        return vanillaState;
    }

    @Override
    public void setVanillaState(EntityRenderState vanillaState) {
        this.vanillaState = vanillaState;
    }
    //#endif

    //#if MC>=12109
    private BlockEntityRenderState vanillaBlockState = null;

    @Override
    public BlockEntityRenderState vanillaBlockState() {
        return vanillaBlockState;
    }

    @Override
    public void setVanillaBlockState(BlockEntityRenderState vanillaBlockState) {
        this.vanillaBlockState = vanillaBlockState;
    }
    //#endif

    @Override
    public UUID uuid() {
        return entity.etf$getUuid();
    }

    @Override
    public boolean canRenderBright() {
        return entity.etf$canBeBright();
    }

    @Override
    public boolean isBlockEntity() {
        return entity.etf$isBlockEntity();
    }

    @Override
    public EntityType<?> entityType() {
        return entity.etf$getType();
    }

    @Override
    public Level world() {
        return entity.etf$getWorld();
    }

    @Override
    public BlockPos blockPos() {
        return entity.etf$getBlockPos();
    }

    @Override
    public int optifineId() {
        return entity.etf$getOptifineId();
    }
    @Override
    public int optifineVehicleId() {
        return entity.etf$getOptifineVehicleId();
    }

    @Override
    public int blockY() {
        return entity.etf$getBlockY();
    }

    @Override
    public CompoundTag nbt() {
        return entity.etf$getNbt();
    }

    @Override
    public boolean hasCustomName() {
        return entity.etf$hasCustomName();
    }

    @Override
    public Component customName() {
        return entity.etf$getCustomName();
    }

    @Override
    public Team scoreboardTeam() {
        return entity.etf$getScoreboardTeam();
    }

    @Override
    public Iterable<ItemStack> itemsEquipped() {
        return entity.etf$getItemsEquipped();
    }

    @Override
    public Iterable<ItemStack> handItems() {
        return entity.etf$getHandItems();
    }

    @Override
    public Iterable<ItemStack> armorItems() {
        return entity.etf$getArmorItems();
    }

    @Override
    public Vec3 velocity() {
        return entity.etf$getVelocity();
    }

    @Deprecated
    @Override
    public Pose pose() {
        return entity.etf$getPose();
    }

    @Override
    public String entityKey() {
        return entity.etf$getEntityKey();
    }

    @Override
    public float distanceTo(Entity other) {
        return entity.etf$distanceTo(other);
    }

}