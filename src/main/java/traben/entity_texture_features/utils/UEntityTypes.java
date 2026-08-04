package traben.entity_texture_features.utils;


import net.minecraft.world.entity.EntityType;
//#if MC >= 26.2
//$$ import net.minecraft.world.entity.EntityTypes;
//#endif

public abstract class UEntityTypes {
    //#if MC >= 26.2
    //$$ public static EntityType<?> PARROT = EntityTypes.PARROT;
    //$$ public static EntityType<?> PLAYER = EntityTypes.PLAYER;
    //#else
    public static EntityType<?> PARROT = EntityType.PARROT;
    public static EntityType<?> PLAYER = EntityType.PLAYER;
    //#endif
}
