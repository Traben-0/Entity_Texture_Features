ETF Changelog:

[V3.0.0]

Changed:

- the shader z-fighting fix no longer expands the model leading to weird rendering, emissives should no longer z-fight
  for most mobs while using shaders.(option has been removed)
- illegal identifier fix changed to only affect image files, cause of litematica conflicts
- names property will also match against the whole line ((names.1=john smith) will check the names {"john", "smith" & "
  john smith"})
- Optifine parity: Name property now works identical to optifine
- ETF supports a new Names (plural) property using previous ETF name behaviour

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



