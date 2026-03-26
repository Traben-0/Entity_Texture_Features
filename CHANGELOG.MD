
[7.0.13]
- fixed the player skin tool menu on 26.1
- fixed fabric api detection
- fixed skin feature part transforms

[7.0.12]
- fixed ETF causing issues with block and item textures in 26.1

[7.0.11]
- fixed ArrayIndexOutOfBoundsException crash

[7.0.9]
- added random entity properties `seedOffset` and `seedSource`
- removed certain OptiFine limitations on newer versions where they have been lifted

[7.0.8]
- neoforge 1.21.11
- fixed various issues with the skin tool and uploading on 1.21.11 specifically and some other versions
- fixed player skin enchanted pixel selections being inverted in 1.21.9+
- fixed a crash with block entities
- fixed the jacket/dress extension skin feature in 1.21.2+

[7.0.7]
- updated to 1.21.11

[7.0.6]
- fixed crash when clicking "Add ETF features to skin" in the skin tool
- fixed forge and neoforge crash, affecting EMF also, relating to checking loaded mods

[7.0.5]
- added a search bar to config screens with more than a dozen listings
- fixed 1.21.9 config lists appearing blank until scrolled
- fixed config titles being missing in 1.21.6+
- added missing config translation support for "Undo changes"

[7.0.2]
- fixed parrots on shoulders crashing the game
- fixed ArrayIndexOutOfBoundsException crash

[7.0.1]
- fixed crash with immediatelyfast
- bumped api number

[7.0.0]
- fixed entities in gui in 1.21.6+
- fixed extreme lag caused when un-leashing entities in 1.21.5+ caused by invalid leash data breaking nbt reading1.20
- fixed entity nbt tests in 1.21.5+
- changed nbt list testing behaviour allowing consistent behaviour with uses like `nbt.1.list.*` and `nbt.1.list.*.further.instructions` matching optifine
- reduce nbt crash log spam when entity is null
- large restructure of the source code
- first release for 1.20 in a while
- fixed a crash when illegal paths were enabled 
- fixed minecart random textures
- fixed several crashes