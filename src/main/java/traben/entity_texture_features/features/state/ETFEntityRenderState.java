package traben.entity_texture_features.features.state;


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
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;
import java.util.*;
import java.util.function.Supplier;

public interface ETFEntityRenderState {

    UUID uuid();
    boolean canRenderBright();
    boolean isBlockEntity();
    EntityType<?> entityType();
    Level world();
    BlockPos blockPos();
    int optifineId();
    int optifineVehicleId();
    int blockY();
    CompoundTag nbt();
    boolean hasCustomName();
    Component customName();
    Team scoreboardTeam();
    Iterable<ItemStack> itemsEquipped();
    Iterable<ItemStack> handItems();
    Iterable<ItemStack> armorItems();
    Vec3 velocity();

    @Deprecated()
    Pose pose();

    String entityKey();

    @Deprecated() // TODO
    ETFEntity entity();

    //#if MC>=12102
    @Nullable EntityRenderState vanillaState();
    void setVanillaState(EntityRenderState vanillaState);
    //#endif

    //#if MC>=12109
    @Nullable net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState vanillaBlockState();
    void setVanillaBlockState(net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState vanillaState);
    //#endif

    float distanceTo(Entity entity);

    CompoundTag cacheEntityNBTForState(UUID entityUUID, Supplier<CompoundTag> computeNBT);


    default void preSubmitActivate(ETFSubmitData submitData,
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit vanillaSubmit
            //#elseif MC >= 1.21.9
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit vanillaSubmit
            //#else
            //$$ Object vanillaSubmit
            //#endif
    ) {

    }

    default void activate(boolean inMount) {

    }

    default void deactivate(boolean inMount) {

    }


    interface ETFRenderStateInit {
        ETFEntityRenderState make(ETFEntity entity);
    }

    @SuppressWarnings("unused")
    static void setEtfRenderStateConstructor(String reason, ETFRenderStateInit init) {
        ETFUtils2.logMessage("Modifying ETF Render State constructor because: " + reason); // likely EMF or ESF
        ETF.etfRenderStateConstructor = init;
    }

    static ETFEntityRenderState forEntity(ETFEntity entity) {
        return ETF.etfRenderStateConstructor.make(entity);
    }

    //#if MC >= 1.21.2
    @Nullable
    static ETFEntityRenderState from(net.minecraft.client.renderer.entity.state.EntityRenderState state) {
        if (state instanceof HoldsETFRenderState holds) {
            return holds.etf$getState();
        }
        return null;
    }
    //#endif

    //region NULL
    ETFEntityRenderState NULL = new Null();
    class Null implements ETFEntityRenderState {

        @Override
        public UUID uuid() {
            return null;
        }

        @Override
        public boolean canRenderBright() {
            return false;
        }

        @Override
        public boolean isBlockEntity() {
            return false;
        }

        @Override
        public EntityType<?> entityType() {
            return null;
        }

        @Override
        public Level world() {
            return null;
        }

        @Override
        public BlockPos blockPos() {
            return null;
        }

        @Override
        public int optifineId() {
            return 0;
        }

        @Override
        public int optifineVehicleId() {
            return 0;
        }

        @Override
        public int blockY() {
            return 0;
        }

        @Override
        public CompoundTag nbt() {
            return null;
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public Component customName() {
            return null;
        }

        @Override
        public Team scoreboardTeam() {
            return null;
        }

        @Override
        public Iterable<ItemStack> itemsEquipped() {
            return null;
        }

        @Override
        public Iterable<ItemStack> handItems() {
            return null;
        }

        @Override
        public Iterable<ItemStack> armorItems() {
            return null;
        }

        @Override
        public Vec3 velocity() {
            return null;
        }

        @Override
        public Pose pose() {
            return null;
        }

        @Override
        public String entityKey() {
            return "";
        }

        @Override
        public ETFEntity entity() {
            return null;
        }

        //#if MC>=12102

        @Override
        public EntityRenderState vanillaState() {
            return null;
        }

        @Override
        public void setVanillaState(EntityRenderState vanillaState) {
        }
        //#endif

        //#if MC>=12109

        @Override
        public net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState vanillaBlockState() {
            return null;
        }

        @Override
        public void setVanillaBlockState(net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState vanillaBlockState) {
        }
        //#endif

        @Override
        public float distanceTo(Entity entity) {
            return 0;
        }

        @Override
        public CompoundTag cacheEntityNBTForState(UUID entityUUID, Supplier<CompoundTag> computeNBT) {
            return null;
        }
    }
    //endregion
}