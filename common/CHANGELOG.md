[**ETF Changelog:**]

[V4.1.1.dev.3]
- added new texture property `angry` used like other OptiFine properties. can be `true|false`. works only for Endermen, Blazes, Guardians, Vindicators and Evokers. Triggers when these mobs display their client side 'angered or attacking state' *(open mouth for endermen & blazes igniting)*
- added new properties to tweak mob rendering within the .properties file, they are not numbered like the other OptiFine properties e.g `skins.1` 
  1. `vanillaBrightnessOverride`: can be set as a number from `0-15`, this overrides the brightness of the mob, it can be used to reduce the brightness of mobs like Blazes and Allays, or increase the brightness of others.
  2. `suppressParticles`: if set to `true` will remove ambient particles from mobs *(currently only Blazes and Glow Squids)*
  3. `showHiddenModelParts`: if set to `true` will enable the rendering of model parts normally hidden in vanilla *(currently only zombie piglin right ears)*
- improved block entity code, this should improve compat with armor stand affecting mods (like quark) as armor stands are no longer used as a substitute entity

[V4.1.1.dev.2]

- added emissive and random texture support to saddles *(support pigs, horse-mobs and striders)*
- added a setting to enable rendering of zombie piglin right ears *(mojang force hides them in the code :/)*
- fixed compatibility with the disguised heads mod and skins with etf features not changing *(skin features will not display on disguised players :/)*
- fixed player skin enchanted visuals being brighter than vanilla
- fixed keyboard navigation in the ETF settings gui *(currently just `ESC` key to go back)*
- tweaked button scaling to center themselves in larger gui scales
- tweaked some gui button positions

[V4.1.1]

- added more screens to better separate button groups
- added an option in gui to disable resource-pack screen button
- tweaked the warning screen format
- tweaked many button positions in the GUI for consistency
- tweaked translations
- russian translation updated for the 4.1.0 GUI thanks to @Felix14-v2
- optimized texture sizes in jar thanks to @robotkoer
- fixed parrots not being random or emissive on player shoulders

[V4.1.0]

*Update summary:*

ETF 4.1 mostly brings the new skin feature tool, with a redesigned GUI to fit, as well as several
new texture properties to further customize your mob textures beyond what OptiFine offers

And of course many fixes, enjoy :)


- redesigned the settings GUI, cloth config is no longer required on Forge *(Openable from modmenu or a new button in the resource-pack screen)*
- added a GUI for selecting player features and saving or uploading a player feature ready skin with all your choices *(openable when in-game from the new settings GUI)*
- added new mob texture properties: *([Documentation Here.](readMeAssets/random_entities.properties))*
  1. "speed" texture property to vary textures by their top speed *(useful for horses, and all mobs)*
  2. "jumpStrength" texture property to vary textures by their jump strength *(useful for horses)*
  3. "maxHealth" texture property to vary textures by their max health *(useful for horses, and all mobs)*
  4. "llamaInventory" texture property to vary llama textures by their carry capacity 
  5. "hiddenGene" texture property to vary panda textures by their hidden gene
  6. "playerCreated" texture property to vary iron golem textures by whether they were created by a player
  7. "screamingGoat" texture property to vary goat textures by whether they are the screaming variant
  8. "distanceFromPlayer" texture property to vary mob textures by their distance from the player
  9. "creeperCharged" texture property to vary creeper textures by whether the creeper has been charged by lightning
  10. "statusEffect" texture property to vary textures by status effect or Brown MooShroom suspicious stew variants 
- added more crash prevention, most features should simply not work, and log an error, instead of immediately crashing
- added a new skin feature option "nose" available from the new skin tool GUI
- added an extra skin feature cape option (ETF) available from the new skin tool GUI
- added config option "hideConfigButton" to disable ETF config button in the resource-pack screen *(only available in the config.json file)*


- changed the iris pbr fix setting into the emissive fix settings screen with several options to reflect the more general fix
  possibilities *(fixes for animation mods, iris PBR and a few others)*
- updated russian translation thanks to @Felix14-v2
- tweaked the debug printout


- fixed an ArrayIndexOutOfBoundsException crash related to caches
- fixed an issue with additional textures *(drowned_outer_layer & sheep_fur & more)* not loading right when in the
  optifine or ETF folders
- fixed elytra_left.png not working and other asymmetrical elytra issues
- fixed forge using the */defaultconfigs/* folder instead of the */config/* folder
- fixed vanilla capes *(like migrator capes)* not using emissive and enchanted data from skin features
- fixed third party capes not loading correctly*(OptiFine and minecraftcapes.net)*
- fixed color property being broken for light_blue/blue & light_gray/gray colors
- fixed a crash when reading corrupt or broken config files


- and many more minor tweaks and fixes :)


[V4.0.2]

- fixed ArrayIndexOutOfBoundsException crash
- fixed excessive lag with shields and tridents in the rewrite
- added russian translation thanks to @Felix14-v2
- added more translation support where it was missing

[V4.0.1]

- fixed cape mod compatibility
- added quark compatibility warning that disables incompatible features

[V4.0]

*changes since last stable release*

ETF's source code has been almost entirely rewritten with a focus on optimization.

Some scenarios exhibit up to 11 times less processing time usage, and in general the mod is much more stable and
efficient

The source code has also been ported to a single codebase for forge and fabric using architechtury to streamline same
time updating of both.

Groundwork has been laid to more easily backport the mod to 1.16, 1.17 & 1.18 these will come later

- added legacy optifine biome name support e.g. "ForestHills" *(it is only mapped to current best fit, it is up to RP
  creators to keep their things updated)*
- added option to disable ETF texture patching to allow iris PBR to function *(this implementation may or may not be
  final)* *(expect possible z-fighting with etf emissive textures when using certain shaders)*
- added: additional mob textures like "sheep_fur.png" can now optionally utilize a "sheep_fur.properties" file to have
  settings different to the 'base' texture, if this properties file is not present ETF will try and use the same variant
  number as the 'base' texture the mob is using, failing all of these it will default to the regular vanilla texture for
  this variant
- added: config option "advanced_IncreaseCacheSizeModifier" which will only show in the config file, this should only be
  increased in the event of an extremely modded instance having over 2000 entities loaded on the client (the amount ETF
  now keeps track of at any given time), to prevent them being removed from ETF's memory.
- added: option to have a different texture on the left elytra wing using *"elytra_left.png"* *(compatible with
  CITResewn)*

- fixed blocks property not also checking the block spawned inside correctly
- fixed: issue with capes, and other skin features, having incorrect enchanted pixels
- fixed: armor and elytra emissives rendering behind textures, now works with iris pbr fix
- fixed: skin feature capes and issues preventing vanilla cape rendering in rewrite
- fixed: bed textures getting stuck to a co-ord position between different color beds
- fixed: shulker box textures getting stuck to a co-ord position between different color shulker boxes
- fixed: a minor issue with 2 frame blinking not registering the correct texture
- fixed: armor emissive bright mode bug
- fixed: shulker box emissive bright mode bug
- fixed: elytra emissive bright mode bug

- changed: custom potion effects have been removed as due to limits of client/server data transparency there is no
  reliable way to have it update during-game *(potion info is **only** sent to clients in the mobs first seen/spawn
  packet :/ )*
- changed: textures with property files that do not read correctly and cause problems will now fall back to the vanilla
  texture instead of randomly picking and causing confusion
- changed: block entity features will not render if the texture is animated as it is not supported *(no need to change
  settings)*

[V3.1.4]

- fixed a logic mistake causing high lag when additional mob textures *(like sheep_fur.png)* did not have the same or
  higher number of variations as the base texture
- the blocks property now also check the block the mob spawned inside *(allowing things like water, cave_air, flowers,
  torches, etc. to be used, and also fixes issues with soul-sand and mud not reading correctly)*
- added an option to enable / disable block entity emissive and custom textures as they currently do not support vanilla
  animated textures and may want to be disabled by such users
- added some missed translation support *(Still only english atm :/)*

[V3.1.3]

- sheep wool support added *(wool doesn't apply dye color to the emissive texture, use custom texture colour properties
  to set an altered coloured texture instead if required. A sheep using sheep2.png will try and use the equivalent
  wool2.png and will use the default otherwise )*
- fixed a crash due to an incorrect texture path for custom capes skin feature
- tweaked property file loading to be more consistent with resource-pack order *(using folders like "etf/random" & the
  vanilla locations will no longer accidentally override or be overridden by packs out of order)*
- iron golem cracking texture support added *(an iron_golem using texture2.png will try and use "crack_texture2.png" and
  will use the default if it doesn't exist)*

[V3.1.2]

- fixed a crash related to player skins that could occur when leaving a server

[V3.1.1]

- fixed other Mod integrations for 1.19 *(Mod menu, iris, etc)*
- elytra size fix now set disabled by default

[V3.1.0]

- updated to 1.19 *(for now 1.18.2 will remain supported with feature updates, but 1.18.1 & 1.18.0 will no longer be
  supported moving forwards, due to very annoying workarounds required)*
- support added for the wardens many texture layers

[V3.0.4]

- chest & shulker custom texture caching is more consistent
- texture cache data now also resets on game disconnect

[V3.0.3]

- fixed names property not working with chests (they will still not function on servers unless a mod is used to send
  blockEntity name data to clients)

[V3.0.2]

- fixed certain blocks not reading correctly for the Blocks texture property

[V3.0.1]

- fixed emissive armour & works with CIT Resewn

[V3.0.0]

Changed:

- the shader z-fighting fix no longer expands the model leading to weird rendering, emissives should no longer z-fight
  for most mobs while using shaders.(option has been removed)
- illegal identifier fix changed to only affect image files, cause of litematica conflicts
- Optifine parity: Name property now works identical to optifine
- ETF supports a new Names (plural) property using previous ETF name behaviour
- names property will also match against the whole line ((names.1=john smith) will check the names {"john", "smith" & "
  john smith"})

Added:

- Optifine parity: Size & Color texture properties
- Ender Dragon random / custom and emissive texture support
- drowned outer layers support random and emissive textures using the vanilla format
- choice between brighter or optifine-like(default) emissive rendering modes (bright usually provides bigger bloom with
  shaders and is noticeably brighter in sunlight)
- block entity emissive support for: chests, shulker boxes, beds, bells, enchanting table book & lectern book
- block entity random/custom texture support for: chests, shulker boxes & beds
- custom lectern book texture support with "minecraft/textures/entity/lectern_book.png"
- emissive shields
- debug logging config option to enable logging relevant ETF mob data when right-clicked
- custom horse armour & markings support, "horse2.png will try to use horse_armor2.png & horse_marking2.png"
- added function to ease access to skin features (a setting in the config/modmenu settings can be enabled to apply a
  skin feature guide image to a copy of your skin)
- supported emissive and enchanted capes to match skin when set in skin feature choices.
- parrots showing custom & emissive textures on shoulders
- warnings in mod menu config to inform of and prevent mod incompatibilities, as well as an option to ignore these
  warnings and override them
- translation support

Fixed:

- memory leak / usage issue in V2.6.0 - was related to entity glowing eyes code
- litematica compatibility
- blocks property not working for certain blocks with extra data
- villager profession property not working with namespaces
- Mooshroom mushrooms not being overridden correctly
- various minor issues
