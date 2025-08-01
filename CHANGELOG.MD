v7

- fixed entities in gui in 1.21.6+
- fixed extreme lag caused when un-leashing entities in 1.21.5+ caused by invalid leash data breaking nbt reading
- fixed entity nbt tests in 1.21.5+
- changed nbt list testing behaviour allowing consistent behaviour with uses like `nbt.1.list.*` and `nbt.1.list.*.further.instructions` matching optifine
- reduce nbt crash log spam when entity is null
- large restructure of the source code