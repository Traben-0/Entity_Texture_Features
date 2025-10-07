


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