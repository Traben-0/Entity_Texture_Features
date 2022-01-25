Random & Emissive entity textures for Fabric
Like optifine but compatible with sodium :)


 

There is an [ example pack ] if you would like a guide to make your own resourcepack.

 

Compatability
 - ✅Custom Entity Models (CEM) (results may vary from model to model)

 - ✅Sodium

 - ✅Iris Shaders (Support varies depending on the shader)

 - ✅Any server side mod - (This is a client side mod)

 - ✅Mod added entities (only if the mod creator used the vanilla rendering code to render their mobs)

 

How to / Guide for resourcepack creators
For emissive & random entity textures to work you must have a resourcepack thats supports it like the  [ example pack ].



Emissive Textures:
 - Emissive textures must be in the same folder as the texture it is glowing over and must be named like (TextureName_e.png)

 - Note: most Optifine texture packs use this format and I will support changing the "_e" suffix soon.

 - Additionally all transparency in the Emissive textures must be transparent and black, use the [ example pack ]as a base if you are stuck.

 - The example image above adds red glowing eyes for the textures "creeper.png" & "creeper3.png"

Random Textures:
 - Random textures are applied to each mob on the client-side & that mob will always have the same random texture until reloading textures or exiting the world

 - Random textures must be in the same folder as the original texture and must be named like (TextureName#.png)

 - with # being any number starting from 2.

 - At the moment all textures will select randomly and cannot have their odds adjusted, this will be coming soon.

 - The example image above shows cow.png having 4 additional textures that will be applied randomly to cows alongside the original texture,

 - as well as showing 2 random textures that will be applied to creepers (creeper3.png even has an emissive texture to go with it)

 - Mobs with multiple textures must have the same number of variants for all textures (e.g  wolf.png & wolf_angry.png & wolf_tame.png)

Mod Support
 - This mod should be compatible with any Mod added entities as long as they extend LivingEntityRenderer.class for rendering and utilise it correctly

 

 

So far all Mob Entities support Random & Emissive textures though there may still be some quirks, if you find bugs you can message here or reach me on discord Traben#5687

 

I have done my best to optimise the mod (much improved from its beginings)
It is fully compatible with sodium and has a negligible performance impact on my system
