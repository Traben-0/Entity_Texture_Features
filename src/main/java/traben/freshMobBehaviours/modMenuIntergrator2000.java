package traben.freshMobBehaviours;

import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Map;

public class modMenuIntergrator2000 implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        return parent -> {
            // Return the screen here with the one you created from Cloth Config Builder
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("Fresh Mob Behaviours by Traben"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory Hostiles = builder.getOrCreateCategory(Text.of("Hostile Mobs"));
            Hostiles.addEntry(entryBuilder.startBooleanToggle(Text.of("Hostile mobs can Dash at players"), config.hostilesCanDash)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Hostile mobs will dash towards players
                            They will slow back down to Base speed when closer to player
                            This makes it so mobs can keep up in a chase but not overwhelm you with speed in close range
                            This is honestly the greatest impact this Mod has in my personal opinion""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.hostilesCanDash = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config

            SubCategoryBuilder dashMobs= entryBuilder.startSubCategory(Text.of("Additional dash settings"));
            dashMobs.add(0, entryBuilder.startBooleanToggle(Text.of("Creepers can dash"), config.creeperCanDash)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.creeperCanDash = newValue).build());
            dashMobs.add(1, entryBuilder.startBooleanToggle(Text.of("Skeletons can dash"), config.skeletonCanDash)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.skeletonCanDash = newValue).build());
            dashMobs.add(2, entryBuilder.startBooleanToggle(Text.of("Spiders can dash"), config.spiderCanDash)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.spiderCanDash = newValue).build());
            dashMobs.add(3, entryBuilder.startBooleanToggle(Text.of("Zombies can dash"), config.zombieCanDash)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.zombieCanDash = newValue).build());
            Hostiles.addEntry(dashMobs.build());

            Hostiles.addEntry(entryBuilder.startBooleanToggle(Text.of("Hostile mobs can 'sense' close players"), config.hostileCanSenseClosePlayer)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Hostile mobs can Hear/Smell very close players\n" +
                            "They will continue to track you through walls for a short distance")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.hostileCanSenseClosePlayer = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Hostiles.addEntry(entryBuilder.startBooleanToggle(Text.of("Hostile mobs last longer during day"), config.hostilesStayLongerInDay)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Hostile mobs that do not burn in the sunlight will despawn later in the day than Vanilla
                            Example. Creepers, Spiders, Endermen, Drowned in water
                            Makes the danger of night time linger...""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.hostilesStayLongerInDay = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Hostiles.addEntry(entryBuilder.startBooleanToggle(Text.of("Wandering improvements"), config.hostilesWanderBetter)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Hostile mobs that are randomly wandering the world will
                            trend towards points of interest they can reach.
                            e.g Paths, Lights, Player only blocks, Villages, Strongholds, Dungeons
                            Also makes mobs not accidentally trigger pyramid explosions""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.hostilesWanderBetter = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Hostiles.addEntry(entryBuilder.startDoubleField(Text.of("Hostile aggro range multiplier"), config.hostilesTargetRange)
                    .setDefaultValue(5) // Recommended: Used when user click "Reset"
                    .setMin(1)
                    .setMax(10)
                    .setTooltip(new TranslatableText("""
                            Multiplies the vanilla aggro distance for mobs by this value [1-10]
                            VANILLA = 1
                            This Mod defaults to 5
                            Also scales stealth settings in a reasonable way
                            This setting really helps encourage players to not just ignore mobs""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.hostilesTargetRange = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            SubCategoryBuilder moddedHostiles= entryBuilder.startSubCategory(Text.of("Other & Mod added hostile mobs"));
            moddedHostiles.add(0, entryBuilder.startDoubleField(Text.of("Other & Mod added hostile mobs base speed modifier"), config.otherHostileBaseSpeedModifier)
                    .setMin(0).setDefaultValue(0D).setSaveConsumer(newValue -> config.otherHostileBaseSpeedModifier = newValue).build());
            moddedHostiles.add(1, entryBuilder.startDoubleField(Text.of("Other & Mod added hostile mobs dash speed modifier"), config.otherHostileDashSpeedModifier)
                    .setMin(0).setDefaultValue(1D).setSaveConsumer(newValue -> config.otherHostileDashSpeedModifier = newValue).build());
            moddedHostiles.add(2, entryBuilder.startBooleanToggle(Text.of("Other & Mod added hostile mobs can dash"), config.otherHostileCanDash)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.otherHostileCanDash = newValue).build());
            Hostiles.addEntry(moddedHostiles.build());

            ConfigCategory Animals = builder.getOrCreateCategory(Text.of("Animals"));
            //Animals.setBackground( new Identifier("minecraft:block/grass_block_top"));
            Animals.addEntry(entryBuilder.startBooleanToggle(Text.of("Animals Herd"), config.animalsHerd)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Animals will try and stay close to
                            other animals of the same type""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.animalsHerd = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config

            SubCategoryBuilder herdAnimals= entryBuilder.startSubCategory(Text.of("Additional herd settings"));
            herdAnimals.add(0, entryBuilder.startBooleanToggle(Text.of("Cows herd"), config.doHerdCow)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdCow = newValue).build());
            herdAnimals.add(1, entryBuilder.startBooleanToggle(Text.of("Pigs herd"), config.doHerdPig)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdPig = newValue).build());
            herdAnimals.add(2, entryBuilder.startBooleanToggle(Text.of("Chickens herd"), config.doHerdChicken)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdChicken = newValue).build());
            herdAnimals.add(3, entryBuilder.startBooleanToggle(Text.of("Donkeys herd"), config.doHerdDonkey)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdDonkey = newValue).build());
            herdAnimals.add(4, entryBuilder.startBooleanToggle(Text.of("Goats herd"), config.doHerdGoat)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdGoat = newValue).build());
            herdAnimals.add(5, entryBuilder.startBooleanToggle(Text.of("Horses herd"), config.doHerdHorse)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdHorse = newValue).build());
            herdAnimals.add(6, entryBuilder.startBooleanToggle(Text.of("Sheep herd"), config.doHerdSheep)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdSheep = newValue).build());
            herdAnimals.add(7, entryBuilder.startBooleanToggle(Text.of("Llama herd"), config.doHerdLlama)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doHerdLlama = newValue).build());
            Animals.addEntry(herdAnimals.build());

            Animals.addEntry(entryBuilder.startIntField(Text.of("Herding strength"), config.animalsHerdStrength)
                    .setDefaultValue(6) // Recommended: Used when user click "Reset"
                    .setMin(2)
                    .setTooltip(new TranslatableText("""
                            This controls how strongly animal will group together
                            Minimum = 2, Default = 10
                            the number provided is how many times more likely an
                            animal will move to a space near a another similar animal""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.animalsHerdStrength = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Animals.addEntry(entryBuilder.startBooleanToggle(Text.of("Animal Wandering Improvements"), config.animalsWanderBetter)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Animals wander around the world in a more interesting way
                            Farm animals will wander towards crops of their food type
                            animals will prefer bright areas with natural blocks
                            animals will seek shelter from rain or the night""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.animalsWanderBetter = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Animals.addEntry(entryBuilder.startBooleanToggle(Text.of("Animals get spooked"), config.animalsGetSpooked)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Animals will flee from players\njust like real life\n*Hint* build traps ;) can be fun\n*Hint* Sneaking way get you closer\nDisabled in peaceful")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.animalsGetSpooked = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config

            SubCategoryBuilder spookAnimals= entryBuilder.startSubCategory(Text.of("Additional spook settings"));
            spookAnimals.add(0, entryBuilder.startBooleanToggle(Text.of("Cows get spooked"), config.doSpookCow)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doSpookCow = newValue).build());
            spookAnimals.add(1, entryBuilder.startBooleanToggle(Text.of("Pigs get spooked"), config.doSpookPig)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doSpookPig = newValue).build());
            spookAnimals.add(2, entryBuilder.startBooleanToggle(Text.of("Chickens get spooked"), config.doSpookChicken)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doSpookChicken = newValue).build());
            spookAnimals.add(3, entryBuilder.startBooleanToggle(Text.of("Horses get spooked"), config.doSpookHorse)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doSpookHorse = newValue).build());
            spookAnimals.add(4, entryBuilder.startBooleanToggle(Text.of("Sheep get spooked"), config.doSpookSheep)
                    .setDefaultValue(true).setSaveConsumer(newValue -> config.doSpookSheep = newValue).build());
            Animals.addEntry(spookAnimals.build());

            ConfigCategory Creeper = builder.getOrCreateCategory(Text.of("Creeper"));
            Creeper.addEntry(entryBuilder.startDoubleField(Text.of("Creeper speed modifier"), config.creeperBaseSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the base speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.creeperBaseSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Creeper.addEntry(entryBuilder.startDoubleField(Text.of("Creeper Dash speed modifier"), config.creeperDashSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the dash speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.creeperDashSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Creeper.addEntry(entryBuilder.startBooleanToggle(Text.of("!!Creepers explode on death!!"), config.creeperExplodeOnDeath)
                    .setDefaultValue(false) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            NOT FOR THE FEINT OF HEART!!
                            Creepers will always explode when they die by ANY means
                            Creepers are to be feared and avoided not killed
                            Creepers that explode involuntarily will drop gunpowder like normal""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.creeperExplodeOnDeath = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Creeper.addEntry(entryBuilder.startBooleanToggle(Text.of("Creepers set ambushes"), config.creepersAmbush)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Creepers will hang around doorways and other
                            places of interest to try and ambush players
                            Wandering improvements MUST be enabled in [Hostile mobs]
                            """)) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.creepersAmbush = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            ConfigCategory Zombie = builder.getOrCreateCategory(Text.of("Zombie"));
            Zombie.addEntry(entryBuilder.startDoubleField(Text.of("Zombie speed modifier"), config.zombieBaseSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the base speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.zombieBaseSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Zombie.addEntry(entryBuilder.startDoubleField(Text.of("Zombie Dash speed modifier"), config.zombieDashSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the dash speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.zombieDashSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            ConfigCategory Skeleton = builder.getOrCreateCategory(Text.of("Skeleton"));
            Skeleton.addEntry(entryBuilder.startDoubleField(Text.of("Skeleton speed modifier"), config.skeletonBaseSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the base speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.skeletonBaseSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Skeleton.addEntry(entryBuilder.startDoubleField(Text.of("Skeleton Dash speed modifier"), config.skeletonDashSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the dash speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.skeletonDashSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Skeleton.addEntry(entryBuilder.startBooleanToggle(Text.of("Skeleton's keep distance"), config.skeletonKeepDistance)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            Skeletons will use their Dash to keep further away from players
                            They wont always run from players but when they do and are close they Dash
                            Hostile mob dashing must be enabled for this""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.skeletonKeepDistance = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Skeleton.addEntry(entryBuilder.startBooleanToggle(Text.of("Prevent Skeleton friendly fire"), config.skeletonPreventFriendlyFire)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Skeletons will rarely shoot other hostile mobs \n" +
                            "Hostile mobs will also not retaliate against them")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.skeletonPreventFriendlyFire = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            ConfigCategory Spider = builder.getOrCreateCategory(Text.of("Spider"));
            Spider.addEntry(entryBuilder.startDoubleField(Text.of("Spider speed modifier"), config.spiderBaseSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the base speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.spiderBaseSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Spider.addEntry(entryBuilder.startDoubleField(Text.of("Spider Dash speed modifier"), config.spiderDashSpeedModifier)
                    .setDefaultValue(0D) // Recommended: Used when user click "Reset"
                    .setMin(0)
                    .setTooltip(new TranslatableText("Modify the dash speed of the Mob")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.spiderDashSpeedModifier = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            ConfigCategory PhantomSleep = builder.getOrCreateCategory(Text.of("Phantoms & Sleep"));
            PhantomSleep.addEntry(entryBuilder.startBooleanToggle(Text.of("Phantoms spawn in The End"), config.phantomsSpawnInEnd)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Allows Phantoms to spawn always in The End")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.phantomsSpawnInEnd = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            PhantomSleep.addEntry(entryBuilder.startBooleanToggle(Text.of("Sleep requires Phantom Membranes"), config.sleepNeedsPhantomMembranes)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            makes sleep require Phantom Membranes
                            I recommend you try it for a while before turning this off
                            It really changes the dynamic of sleeping and how you treat the night
                            Also phantoms are just once again useless without it :(""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.sleepNeedsPhantomMembranes = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            PhantomSleep.addEntry(entryBuilder.startBooleanToggle(Text.of("Sleep needs shelter"), config.sleepNeedsShelter)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("makes it so you cannot sleep in the open where phantoms could reach you\n" +
                            "(disable this if your skylight prevents sleeping)")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.sleepNeedsShelter = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            ConfigCategory Wolf = builder.getOrCreateCategory(Text.of("Wolves"));
            Wolf.addEntry(entryBuilder.startBooleanToggle(Text.of("Wolves are Hostile"), config.wolfIsHostile)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Makes wolves hostile mobs, even in day!. \n" +
                            "Babies can still be tamed")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.wolfIsHostile = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Wolf.addEntry(entryBuilder.startBooleanToggle(Text.of("Wolves randomly breed"), config.wolfCanRandomlyBreed)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("lets wolves randomly breed so they dont go extinct\n" +
                            "So you always have babies to tame")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.wolfCanRandomlyBreed = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            ConfigCategory Stealth = builder.getOrCreateCategory(Text.of("Stealth"));
            Stealth.addEntry(entryBuilder.startBooleanToggle(Text.of("Buffed crouch stealth"), config.stealthBuffSneak)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Crouching reduces aggro range even more than Vanilla")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.stealthBuffSneak = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Stealth.addEntry(entryBuilder.startBooleanToggle(Text.of("Buffed Leather stealth"), config.stealthLeatherSneak)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Crouching reduces aggro range even more when wearing Leather Armour\n" +
                            "Extra effective with Leather Boots")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.stealthLeatherSneak = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Stealth.addEntry(entryBuilder.startBooleanToggle(Text.of("Buffed invisibility"), config.stealthBuffInvisibility)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Invisibility reduces aggro range even more than Vanilla")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.stealthBuffInvisibility = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Stealth.addEntry(entryBuilder.startBooleanToggle(Text.of("Buffed Chainmail invisibility"), config.stealthChainInvisibility)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("Wearing Chainmail Armour does not affect invisibility as strongly as other Armours")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.stealthChainInvisibility = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config
            Stealth.addEntry(entryBuilder.startBooleanToggle(Text.of("Buffed Mob heads"), config.stealthBuffHeads)
                    .setDefaultValue(true) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableText("""
                            When wearing a mob's head that mob type will almost completely ignore you
                            Unless you get far too close or hurt it
                            ONLY works for Vanilla mob heads [Zombies, Skeleton, Creeper, Wither Skeleton]
                            I may register VanillaTweaks mob heads in future""")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> config.stealthBuffHeads = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config



            builder.setSavingRunnable(() -> {
                // Serialise the config into the config file. This will be called last after all variables are updated.
                AutoConfig.getConfigHolder(Configurator2000.class).save();

          });
            return builder.build();
        };
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of("minecraft", parent -> new OptionsScreen(parent, MinecraftClient.getInstance().options));
    }
}