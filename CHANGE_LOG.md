[**ETF Changelog:**

[V4.1.dev.2]
- tweaked new property names
- added another property distanceToPlayer
- improved skin tool added enchanted and emissive options

[V4.1.dev.1]
- cloth config no longer required on forge or included in fabric
- updated russian translation thanks to @Felix14-v2
- fixed color property being broken for light_blue/blue & light_gray/gray colors
- fixed a crash when reading corrupt or broken config files
- complete GUI overhaul
- added GUI for selecting player features and printing a player feature ready skin with all your choices *(W.I.P)*
- added many new texture properties

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

Some scenarios exhibit up to 11 times less processing time usage, and in general the mod is much more stable and efficient

The source code has also been ported to a single codebase for forge and fabric using architechtury to streamline same time updating of both. 

Groundwork has been laid to more easily backport the mod to 1.16, 1.17 & 1.18 these will come later

- added legacy optifine biome name support e.g. "ForestHills" *(it is only mapped to current best fit, it is up to RP creators to keep their things updated)*
- added option to disable ETF texture patching to allow iris PBR to function *(this implementation may or may not be final)* *(expect possible z-fighting with etf emissive textures when using certain shaders)*
- added: additional mob textures like "sheep_fur.png" can now optionally utilize a "sheep_fur.properties" file to have settings different to the 'base' texture, if this properties file is not present ETF will try and use the same variant number as the 'base' texture the mob is using, failing all of these it will default to the regular vanilla texture for this variant
- added: config option "advanced_IncreaseCacheSizeModifier" which will only show in the config file, this should only be increased in the event of an extremely modded instance having over 2000 entities loaded on the client (the amount ETF now keeps track of at any given time), to prevent them being removed from ETF's memory.
- added: option to have a different texture on the left elytra wing using *"elytra_left.png"* *(compatible with CITResewn)*

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

- changed: custom potion effects have been removed as due to limits of client/server data transparency there is no reliable way to have it update during-game *(potion info is **only** sent to clients in the mobs first seen/spawn packet :/ )*
- changed: textures with property files that do not read correctly and cause problems will now fall back to the vanilla texture instead of randomly picking and causing confusion
- changed: block entity features will not render if the texture is animated as it is not supported *(no need to change settings)*

- broke: puzzle support will be broken for a short period after release until I PR a fix to the dev
]()
[V3.1.4]
- fixed a logic mistake causing high lag when additional mob textures *(like sheep_fur.png)* did not have the same or higher number of variations as the base texture
- the Blocks property now also check the block the mob spawned inside *(allowing things like water, cave_air, flowers, torches, etc. to be used, and also fixes issues with soul-sand and mud not reading correctly)*
- added an option to enable / disable block entity emissive and custom textures as they currently do not support vanilla animated textures and may want to be disabled by such users
- added some missed translation support *(Still only english atm :/)*

[V3.1.3]
- sheep wool support added *(wool doesn't apply dye color to the emissive texture, use custom texture colour properties to set an altered coloured texture instead if required. A sheep using sheep2.png will try and use the equivalent wool2.png and will use the default otherwise )*
- fixed a crash due to an incorrect texture path for custom capes skin feature
- tweaked property file loading to be more consistent with resource-pack order *(using folders like "etf/random" & the vanilla locations will no longer accidentally override or be overridden by packs out of order)*
- iron golem cracking texture support added *(an iron_golem using texture2.png will try and use "crack_texture2.png" and will use the default if it doesn't exist)*


[V3.1.2]

- fixed a crash related to player skins that could occur when leaving a server

[V3.1.1]

- fixed other Mod integrations for 1.19 *(Mod menu, iris, etc)*
- elytra size fix now set disabled by default

[V3.1.0]

- updated to 1.19 *(for now 1.18.2 will remain supported with feature updates, but 1.18.1 & 1.18.0 will no longer be supported moving forwards, due to very annoying workarounds required)*
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


