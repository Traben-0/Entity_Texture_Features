package traben.entity_texture_features.mixin.entity.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;


@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {


    @Unique
    private final static Identifier etf$etf = new Identifier(MOD_ID, "textures/capes/etf.png");
    @Unique
    private final static Identifier etf$wife = new Identifier(MOD_ID, "textures/capes/wife.png");

    @SuppressWarnings("unused")
    public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getSkinTextures",
            at = @At("RETURN"), cancellable = true)
    private void changeCapeReturnsToNotNull(CallbackInfoReturnable<SkinTextures> cir) {
        //requires a non-null return for elytras and for enabling cape rendering which is itself overridden by etf

        Identifier cape = cir.getReturnValue().capeTexture();
        if (cape != null) {
            //catches cit elytras makes them override ETF cape returns
            if (cape.toString().contains("/cit/")) {
                return;
            }
        }
        Identifier newCape;
        ETFPlayerTexture textureData = ETFManager.getInstance().getPlayerTexture(this, cir.getReturnValue().texture());
        if (textureData != null && textureData.hasCustomCape()) {
            newCape = textureData.etfCapeIdentifier;
            //cir.setReturnValue(textureData.etfCapeIdentifier);
        } else if (getUuid().equals(ETFPlayerTexture.Dev)) {
            newCape = etf$etf;
        } else if (getUuid().equals(ETFPlayerTexture.Wife)) {
            newCape = etf$wife;
        } else {
            newCape = null;
        }
        if (newCape != null) {
            SkinTextures old = cir.getReturnValue();
            cir.setReturnValue(new SkinTextures(
                    old.texture(),
                    old.textureUrl(),
                    newCape,
                    old.elytraTexture(),
                    old.model(),
                    old.secure()
            ));
        }
    }

//    @Inject(method = "cape",
//            at = @At("RETURN"),
//            cancellable = true)
//    private void changeCapeReturnsBoolean(CallbackInfoReturnable<Boolean> cir) {
//        //returns null if skin features disabled check is inbuilt
//        ETFPlayerTexture textureData = ETFManager.getInstance().getPlayerTexture(this, getSkinTexture());
//        if ((textureData != null && textureData.hasCustomCape())
//                || getUuid().equals(ETFPlayerTexture.Dev)
//                || getUuid().equals(ETFPlayerTexture.Wife)) {
//            cir.setReturnValue(true);
//        }
//    }
}