package traben.entity_texture_features.utils.entity_wrappers;

import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public interface ETFPlayerEntity extends ETFEntity {


    boolean isTeammate(PlayerEntity player);

    PlayerInventory getInventory();

    boolean isPartVisible(PlayerModelPart part);

    Text getName();

    String getUuidAsString();
}
