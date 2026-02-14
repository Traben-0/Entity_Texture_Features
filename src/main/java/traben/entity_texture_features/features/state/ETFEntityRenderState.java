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




    interface ETFRenderStateInit {
        ETFEntityRenderState make(ETFEntity entity);
    }

    static void setEtfRenderStateConstructor(String reason, ETFRenderStateInit init) {
        ETFUtils2.logMessage("Modifying ETF Render State constructor because: " + reason); // likely EMF or ESF
        ETF.etfRenderStateConstructor = init;
    }

    static ETFEntityRenderState forEntity(ETFEntity entity) {
        return ETF.etfRenderStateConstructor.make(entity);
    }

}