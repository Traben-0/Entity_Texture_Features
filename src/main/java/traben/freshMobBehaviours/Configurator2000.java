package traben.freshMobBehaviours;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "fresh_mob_behaviours")
public class Configurator2000 implements ConfigData {
    String For_config_descriptions = "Use ModMenu";
    //ALL
    public boolean mobsHeal = true;
    public boolean mobsBurnSpreadFireIfPlayerClose = true;
    public int mobsFireRangeFromPlayer = 16;
    public int mobsFlameChance = 5;
    public double mobsFlameFrequencySeconds = 3.0;
    public boolean mobsCollideBetter = true;
    //creeper
    public double creeperBaseSpeedModifier = 0D;
    public double creeperDashSpeedModifier = 0.6D;
    public boolean creeperExplodeOnDeath = false;
    public boolean creepersAmbush = true;
    //zombie
    public double zombieBaseSpeedModifier = 0.2D;
    public double zombieDashSpeedModifier = 0.9D;
    //Skeleton
    public double skeletonBaseSpeedModifier = 0D;
    public double skeletonDashSpeedModifier = 0.75D;
    public boolean skeletonPreventFriendlyFire = true;
    public boolean skeletonKeepDistance = true;
    //Spider
    public double spiderBaseSpeedModifier = 0.1D;
    public double spiderDashSpeedModifier = 1D;
    //Phantoms & sleep
    public boolean phantomsSpawnInEnd = true;
    public boolean sleepNeedsPhantomMembranes = true;
    public boolean sleepNeedsShelter = true;
    //Wolf
    public boolean wolfIsHostile = true;
    public boolean wolfCanRandomlyBreed = true;
    //Hostiles
    public boolean hostilesCanDash = true;
    public boolean hostileCanSenseClosePlayer = true;
    public boolean hostilesStayLongerInDay = true;
    public boolean hostilesWanderBetter = true;
    public double hostilesTargetRange = 2.5;
    //other/modded hostiles
    public double otherHostileBaseSpeedModifier = 0D;
    public double otherHostileDashSpeedModifier = 0.5D;
    public boolean otherHostileCanDash = true;
    //all hostiles
    public boolean creeperCanDash = true;
    public boolean zombieCanDash = true;
    public boolean spiderCanDash = true;
    public boolean skeletonCanDash = true;
    public boolean endermenCanDash = true;
    //Stealth
    public boolean stealthBuffSneak = true;
    public boolean stealthLeatherSneak = true;
    public boolean stealthBuffHeads = true;
    public boolean stealthBuffInvisibility = true;
    public boolean stealthChainInvisibility = true;
    //Animals
    public boolean animalsHerd = true;
    public int animalsHerdStrength = 6;
    public boolean animalsWanderBetter = true;
    public boolean animalsGetSpooked = true;
    public boolean animalsEatGrass = true;
    //all animals herd
    public boolean doHerdCow = true;
    public boolean doHerdPig = true;
    public boolean doHerdChicken = true;
    public boolean doHerdDonkey = true;
    public boolean doHerdHorse = true;
    public boolean doHerdSheep = true;
    public boolean doHerdLlama = true;
    public boolean doHerdGoat = true;
    //all animals spook
    public boolean doSpookCow = true;
    public boolean doSpookPig = true;
    public boolean doSpookChicken = true;
    public boolean doSpookHorse = true;
    public boolean doSpookSheep = true;
    //Endermen
    public double endermenBaseSpeedModifier = 0D;
    public double endermenDashSpeedModifier = 0.3D;
    public boolean endermenSpawnBlocks = true;
    public boolean endermenCuriousOfPlayer = true;
    //Projectiles
    public boolean projectilesSetFire = true;
    public boolean snowballsCauseFreeze = true;


//    @ConfigEntry.Gui.Excluded
//    InnerStuff invisibleStuff = new InnerStuff();
//
//    static class InnerStuff {
//        int a = 0;
//        int b = 1;
//    }

}
