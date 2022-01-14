package traben.freshMobBehaviours;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.ObjectUtils;
import traben.freshMobBehaviours.mixin.accessor.ACC_MobEntity;
import traben.freshMobBehaviours.mixin.accessor.ACC_TargetPredicate;

import java.util.*;
import java.util.stream.Stream;

public class FreshMobBehaviours implements ModInitializer{

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        System.out.println("Hello Fabric world!");
        try{AutoConfig.register(Configurator2000.class, GsonConfigSerializer::new);}catch(Exception e){System.out.println(e);}


        //use below to read config
        //Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();

    }

}