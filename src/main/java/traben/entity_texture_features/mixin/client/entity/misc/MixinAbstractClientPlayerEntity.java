package traben.entity_texture_features.mixin.client.entity.misc;

import com.mojang.authlib.GameProfile;
//import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;
import static traben.entity_texture_features.client.utils.ETFPlayerSkinUtils.*;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {


    public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "getCapeTexture",
            at = @At("RETURN"),
            cancellable = true)
    private void etf$changeCape(CallbackInfoReturnable<Identifier> cir) {

        if (cir.getReturnValue() != null) {
            if (cir.getReturnValue().toString().contains("/cit/")) {
                cir.setReturnValue(cir.getReturnValue());
            }
        }

        if (//FabricLoader.getInstance().isModLoaded("fabric") &&
         (getUuid().toString().equals("fd22e573-178c-415a-94fe-e476b328abfd")
                //|| getUuid().toString().equals("bc2d6979-ddde-4452-8c7d-caefa4aceb01")
                || getUuid().toString().equals("cab7d2e2-519f-4b34-afbd-b65f4542b8a1"))) {
            if (UUID_PLAYER_HAS_CUSTOM_CAPE.containsKey(getUuid())) {
                if (!UUID_PLAYER_HAS_CUSTOM_CAPE.getBoolean(getUuid())) {
                    UUID_PLAYER_HAS_ENCHANT_CAPE.put(getUuid(), false);
                    if (getUuid().toString().equals("cab7d2e2-519f-4b34-afbd-b65f4542b8a1")) {
                        UUID_PLAYER_HAS_EMISSIVE_CAPE.put(getUuid(), false);
                        cir.setReturnValue(new Identifier("etf:textures/capes/wife.png"));
                    } else {
                        UUID_PLAYER_HAS_EMISSIVE_CAPE.put(getUuid(), true);

                        cir.setReturnValue(new Identifier("etf:textures/capes/dev.png"));
                    }
                }
            }
        }
        if (ETFConfigData.skinFeaturesEnabled && UUID_PLAYER_HAS_CUSTOM_CAPE.containsKey(getUuid())) {
            if (UUID_PLAYER_HAS_CUSTOM_CAPE.getBoolean(getUuid())) {
                cir.setReturnValue(new Identifier(SKIN_NAMESPACE + getUuid() + "_cape.png"));
            }
        }
//        if(cir.getReturnValue() == null ){
//            if(getInventory().getArmorStack(2).isOf(Items.ELYTRA)) {
//               cir.setReturnValue(new Identifier("textures/entity/elytra.png"));
//            }else{
//                cir.setReturnValue(new Identifier("etf:blank.png"));
//            }
//        }
    }

//    @Inject(method = "canRenderCapeTexture",
//            at = @At("RETURN"),
//            cancellable = true)
//    private void changeCapeReturnsBoolean(CallbackInfoReturnable<Boolean> cir) {
//        if (ETFConfigData.skinFeaturesEnabled && UUID_playerHasCustomCape.containsKey(getUuid())){
//            if (UUID_playerHasCustomCape.get(getUuid()) ){
//                cir.setReturnValue(true);
//            }
//        }
//    }
}
