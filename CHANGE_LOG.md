**ETF Changelog:**

[V3.1.4]
- fixed a logic mistake causing high lag when additional mob textures *(like sheep_fur.png)* did not have the same or higher number of variations as the base texture
- the Blocks property now also check the block the mob spawned inside of *(allowing things like water, cave_air, flowers, torches, etc. to be used, and also fixes issues with soul-sand and mud not reading correctly)*
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



