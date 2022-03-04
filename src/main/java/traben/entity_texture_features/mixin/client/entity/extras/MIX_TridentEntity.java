package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static traben.entity_texture_features.client.ETF_CLIENT.UUID_TridentName;
@Mixin(TridentEntity.class)
public abstract class MIX_TridentEntity extends PersistentProjectileEntity{


        protected MIX_TridentEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
                super(entityType, world);
        }

        protected MIX_TridentEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world) {
                super(type, x, y, z, world);
        }

        protected MIX_TridentEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
                super(type, owner, world);
        }

        @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
        public void injected(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
                if (stack.hasCustomName()) {
                        UUID_TridentName.put(getUuid(),stack.getName().getString());
                }else{
                        UUID_TridentName.put(getUuid(),null);
                }
        }
}
