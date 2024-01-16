package traben.entity_texture_features.features.player;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import traben.entity_texture_features.utils.ETFEntity;

public interface ETFPlayerEntity extends ETFEntity {


    Entity etf$getEntity();

    boolean etf$isTeammate(PlayerEntity player);

    PlayerInventory etf$getInventory();

    boolean etf$isPartVisible(PlayerModelPart part);

    Text etf$getName();

    String etf$getUuidAsString();
}
