# 🎲 Custom / Random / Varied Entity Textures Guide:
<img src="randoms.png" alt="img" width="650"/>

---
### This mod is completely Optifine format compatible!!!!

- It supports all optifine properties present in the link below including Biomes, Names, Heights, etc... 
*(It also adds some of it's own)*
- you can directly use the [Optifine Random Entities Format Guide](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/random_entities.properties)
or any optifine random mob resource-packs and it will work.
- This mod also adds a few extra features that Optifine does not have, 
Most notably you can put everything in the default *"minecraft/textures/entity"* directory as well as Optifine's

---
## Guide & how it works:
- Random textures are applied to each mob on the client-side  
*(based on it's UUID, meaning that mob will always have the same random texture unless texture packs are 
changed, this will even be the same between players if they have the same packs.)*

- Random textures **must** either be in the same folder as the vanilla texture or one of the optifine format
folders such as *"optifine/random/entity"* or *"optifine/mob"*, and must be named like *(TextureName#.png with # 
being any number ideally starting from either 1 or 2, (if 1.png does not exist it will use the vanilla 
texture, the vanilla texture can also be manually called by using the number 0)*
- A *"TextureName.properties"* file can be placed alongside these random textures to determine special cases 
for different random textures to be selected, this properties file must 
support the [optifine format](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/random_entities.properties) 
and if one isn't present the game will instead pick randomly from the available random textures present.
- The properties file functions just as it does with [optifine]((https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/random_entities.properties)))
and if a specific entity does not match any of 
the conditions in the properties file it will default to the vanilla texture.
- This mod also adds the Property "blocks" it works exactly the same as the "biomes" property
except it will allow you to set the texture of the entity based on the block it spawned on 
*(or first rendered on)*    
an example of the "blocks" property follows:  
*blocks.1= Stone stone minecraft:stone modname:marble*
- This mod also adds the Property "teams" it works exactly the same as the "names" property
except it will allow you to set the texture of the entity based on the scoreboard team it is in
*(this will be use-full for datapack creators or map makers who do not want the mob to display a custom name)*    
an example of the "teams" property follows:  
*teams.1= !red_team "Blue Team" blue_team*
- Additionally the Names & Teams property support grouping names that have spaces within double quotes e.g "John Smith" 

---
## Example properties file
<img src="format_example.png" alt="img" width="650"/>

- The example image above shows that any zombie in a desert will have a 10/17 chance of using "zombie1.png"
- it also shows that any zombie not in a (desert,plains,jungle,nether) biome and is named "John" will always 
use texture "zombie3.png"
- it also shows that any zombie in a taiga biome, without the above names from the previoues category, and 
standing at y-level 45 will be texture "zombie1.png"
- "zombie3.png" also has an emissive texture "zombie3_e.png", that will make it's eyes glow red

- Certain conditions in the properties file will update the mobs texture when changed, these are 
*("name", "profession", "collarColors", "baby", "health")*.
- The other conditions such as "biomes" will only apply on first spawn / load.  
*(If they do not update (e.g baby doesn't grow into adult texture) you can force a refresh on a mob by 
right clicking it or reloading the game/resourcepacks)*
Entities with multiple textures like Wolves should have the same amount of ranom variant textures for 
each texture variant

---
## Special Cases
- Tridents support custom textures, as of *[V2.3.0]*, a trident item named *"Bobby's Trident"* will use the 
texture *"trident_bobbys_trident.png"*
- A named trident will match any texture of the format *trident_"customName".png*, with customName being 
the name of the trident with spaces replaces with "_" and any non letters ignored.


- If a custom mushroom texture is placed in *"minecraft/textures/entity/cow/red_mushroom.png"* or 
*"minecraft/textures/entity/cow/brown_mushroom.png"*, it will overwrite the mushrooms on the back
of the respective mooshroom.