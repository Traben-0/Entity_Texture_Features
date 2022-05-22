ETF Changelog:

[V2.7.0] Betas so far

Various code improvements implemented thanks to @Maximum#8760 Improvements:
logging iris compatibility translation support A great deal of source code support and proofreading, thank you again
@Maximum#8760

Changed:
Debug logging can be sent to chat as well the shader z-fighting fix no longer expands the model leading to weird
rendering, emissives should no longer z-fight for most mobs while using shaders. this has not been expanded to all use
cases yet and is still in development illegal identifier fix changed to only affect image files, cause of litematica
conflicts

Added:
Ender Dragon random / custom and emissive texture support drowned outer layers support random and emissive textures
using the vanilla format choice between bright or optifine-like (default) emissive rendering (bright usually provides
bigger bloom with shaders and is noticeably brighter in sunlight)
Initial block entity emissive support for: chests, beds, bells, enchanting table book & lectern book emissive shields
debug logging config option to enable logging relevant ETF mob data when right-clicked custom lectern book texture
support with "minecraft/textures/entity/lectern_book.png"
custom horse armour & markings support, "horse2.png will try to use horse_armor2.png & horse_marking2.png"
added function to ease access to skin features (a setting in the config/modmenu settings can be enabled to apply a skin
feature guide image to a copy of your skin)
supported emissive and enchanted capes when set in skin feature choices. parrots showing custom textures on shoulders

Fixed:
memory leak / usage issue in V2.6.0 - was related to entity feature renderer code litematica compatibility various minor
issues





