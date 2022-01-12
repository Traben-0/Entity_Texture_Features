package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import traben.freshMobBehaviours.Configurator2000;

import java.util.List;

@Mixin(AbstractSkeletonEntity.class)
public class MIX_AbstractSkeletonEntity {



    /**
     * @author Traben
     * @reason cancel attack if hostile mob in thew way - not precise
     */
    @Overwrite
    public void attack(LivingEntity target, float pullProgress) {
        boolean doAttack = true;
        AbstractSkeletonEntity skelly = ((AbstractSkeletonEntity) (Object) this);
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.skeletonPreventFriendlyFire){
        Vec3d vec3d = new Vec3d(skelly.getX(), skelly.getEyeY(), skelly.getZ());
        Vec3d vec3d2 = new Vec3d(target.getX(), target.getEyeY(), target.getZ());
        double xtravel = (vec3d2.getX() - vec3d.getX()) / 22;
        double ytravel = (vec3d2.getY() - vec3d.getY()) / 22;
        double ztravel = (vec3d2.getZ() - vec3d.getZ()) / 22;

        //some hastily made rough path tracing code
        for (int i = 1; i < 21; i++) {
            //if first 15th block isnt empty   i.e has a hostile in it
            List<HostileEntity> list = skelly.world.getNonSpectatingEntities(HostileEntity.class, new Box(
                    vec3d.getX() + (xtravel * (i)),
                    vec3d.getY() + (ytravel * (i)),
                    vec3d.getZ() + (ztravel * (i)),
                    vec3d.getX() + (xtravel * (i + 1)),
                    vec3d.getY() + (ytravel * (i + 1)),
                    vec3d.getZ() + (ztravel * (i + 1))).expand(0.25));
            list.remove(skelly);
            if (!list.isEmpty()) {
                //System.out.println("sight blocked by hostile entity ");
                doAttack = false;
                break;
            }
        }
        }
        if (doAttack) {
            ItemStack itemStack = skelly.getArrowType(skelly.getStackInHand(ProjectileUtil.getHandPossiblyHolding(skelly, Items.BOW)));
            PersistentProjectileEntity persistentProjectileEntity = ProjectileUtil.createArrowProjectile(skelly, itemStack, 1.0F);
            double d = target.getX() - skelly.getX();
            double e = target.getBodyY(0.3333333333333333D) - persistentProjectileEntity.getY();
            double f = target.getZ() - skelly.getZ();
            double g = Math.sqrt(d * d + f * f);
            persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, (float) (14 - skelly.world.getDifficulty().getId() * 4));
            skelly.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (skelly.getRandom().nextFloat() * 0.4F + 0.8F));
            skelly.world.spawnEntity(persistentProjectileEntity);
        }
    }
}

