# 🎲 Custom / Random / Varied Entity Textures Guide:
<img src="randoms.png" alt="img" width="650"/>

---
### This mod is completely Optifine format compatible!!!!

- It supports all optifine properties present in the link below including Biomes, Names, Heights, etc... 
*(ETF also adds some of its own)*
- you can directly use
  the [Optifine Random Entities Format Guide](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/random_entities.properties)
  or any optifine random mob resource-packs, and it will work.
- This mod also adds a few extra features that Optifine does not have, 
Most notably you can put everything in the default *"minecraft/textures/entity"* or the *etf/random* directory as well as Optifine's directories

---
## Guide & how it works:
- Random textures are applied to each mob on the client-side  
*(based on it's UUID, meaning that mob will always have the same random texture unless texture packs are 
changed, this will even be the same between players if they have the same packs.)*

- Random textures **must** be in one of the following folders in the resource-pack, and will be checked from the top directory first, only using the last directory if none of the others are used
  - the ETF override directory *"etf/random/entity"*
  - the Optifine Random directory *"optifine/random/entity"*
  - the Optifine legacy directory *"optifine/mob"*
  - the Vanilla directory *"minecraft/textures/entity"*
  
- the random textures **must** be named like *"TextureName#.png"* with # 
being any number that **must** start from 2,  
*(if 1.png does not exist then calling 1 in the properties will use the vanilla
texture, the vanilla texture can also be manually called by using the number 0 in the properties)*
- A *"TextureName.properties"* file can be placed alongside these random textures to determine special cases for
  different random textures to be selected, this properties file must support
  the [optifine format](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/random_entities.properties)
  and if one isn't present the game will instead pick randomly from the available random textures present.   
  *(Note. when randomly picked like this, the random textures must contain numbers in sequence from 2 -> however many
  there are, if there are any gaps then textures after the gap will be ignored)*
- The properties file functions just as it does
  with [optifine]((https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/random_entities.properties))
  and if a specific entity does not match any of the conditions in the properties file it will default to the vanilla
  texture.
- This mod also adds two texture Properties "blocks" & "teams"
- The "blocks" property works exactly the same as the "biomes" property
  except it will allow you to set the texture of the entity based on the block it spawned on 
*(or first rendered on)* instead of the Biome, an example of the "blocks" property follows:  
  *blocks.1= stone !bedrock minecraft:stone mod_name:marble*
- The "teams" Property works exactly the same as the "names" property
except it will allow you to set the texture of the entity based on the scoreboard team it is in
*(this will be useful for data-pack creators or map-makers who do not want the mob to display a custom name)*    
an example of the "teams" property follows:  
*teams.1= !red_team "Blue Team" blue_team*
- Additionally, the Names & Teams property support grouping names that have spaces within double quotes e.g "John Smith"

---
## Example properties file
<img src="format_example.png" alt="img" width="650"/>

- The example image above shows that any zombie in a desert will have a 10/17 chance of using "zombie1.png"
- it also shows that any zombie not in a (desert,plains,jungle,nether) biome and is named "John" will always use
  texture "zombie3.png"
- it also shows that any zombie in a taiga biome, without the above names from the previous category, and standing at
  y-level 45 will be texture "zombie1.png"
- "zombie3.png" also has an emissive texture "zombie3_e.png", that will make its eyes glow red

- Certain conditions in the properties file will update the mobs texture, based on the texture update speed, when
  changes are detected, these are:
  *("name", "profession", "collarColors", "baby", "health", "teams")*.
- All the other conditions such as "biomes" & "blocks" will by default only be applied to the Mob when it first spawns / loads.  
*(for example a Zombie spawned in a desert will take the "desert skin" and it will not change to the "forest skin" 
when they walk into a forest biome.)*  
- This can be disabled by the config option "Property update restrictions" for each of these properties individually.  
*(if update restrictions are disabled for biomes, the above example Zombie with the "desert skin" would indeed change to the "forest skin"
upon entering a forest biome)*

- Entities with multiple textures like Wolves, Bees or Ghasts should be given the same amount of random variant textures for 
each texture variant

---
## Special Cases
<table>
<tr>
<td>
<img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/9a/Trident.png/revision/latest/scale-to-width-down/300?cb=20200106005732" alt="img" width="128"/>
</td>
<td>

- Tridents support custom textures, as of *[V2.3.0]*, a trident item named *"Bobby's Trident"* will use the 
texture *"trident_bobbys_trident.png"*
- A named trident will match any texture of the format *trident_"customName".png*, with customName being 
the name of the trident with spaces replaces with "_" and any non letters ignored.

</td>
</tr>
<tr>
<td>
<img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/1/1c/Red_Mooshroom_JE4.png/revision/latest/scale-to-width-down/150?cb=20200510033824" alt="img" width="128"/>
</td>
<td>

- If a custom mushroom texture is placed in *"minecraft/textures/entity/cow/red_mushroom.png"* or 
*"minecraft/textures/entity/cow/brown_mushroom.png"*, it will overwrite the mushrooms on the back
of the respective mooshroom.

</td>
</tr>
<tr>
<td>
<img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/98/Elytra_%28item%29_JE2_BE2.png/revision/latest/scale-to-width-down/160?cb=20190406130540" alt="img" width="64"/>
</td>
<td>

- Custom elytras are handled by the CIT resewn mod, and may utilise ETF emissive textures only
- You can also use the Player skin feature options for custom capes to have a custom elytra, though this does not yet
  support emissive textures

</td>
</tr>
<tr>
<td>
<img src="https://static.wikia.nocookie.net/minecraft_gamepedia/images/0/06/Shulker.png/revision/latest/scale-to-width-down/300?cb=20200107095352" alt="img" width="64"/>
</td>
<td>

- Shulkers will only use the base un-coloured shulker.properties for custom settings (no shulker_black.properties), the
  other coloured textures will still apply as the default to those entities

</td>
</tr>
<tr>
<td>
<img src="lecternBook.png" alt="img" width="150"/>
</td>
<td>

- If a custom book texture is placed in *"minecraft/textures/entity/lectern_book.png"*, it will overwrite the book of
  the lectern block

</td>
</tr>
</table>
