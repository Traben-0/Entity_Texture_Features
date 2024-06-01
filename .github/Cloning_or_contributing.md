Information accurate as of the 2nd of june 2024

# Contributing
## Code
Feel free to make a pull request if you have any changes you would like to make to the code. If you want to ask first you can reach me on my [discord](https://discord.com/invite/rURmwrzUcz)
## Languages
ETF now has a [Crowdin Translate page](https://crowdin.com/project/entity-texture-features) you can contribute to, message me if you need a language added via my [discord](https://discord.com/invite/rURmwrzUcz)

# Building yourself
## Pre processor
ETF now merges all the different Minecraft versions into one source branch.
This is done using Manifold's java preprocessor.

If you are using IntelliJ you will want to install the manifold [plugin](https://plugins.jetbrains.com/plugin/10057-manifold), which will allow you to see the code correctly in IntelliJ

(You can ignore the warning message to update the dependency in the code as doing so will cause build errors)

You can select which minecraft versions builds by editing the following line in the root gradle.properties file and reloading gradle
```properties
# Set the build MC environment (may not all be re-implemented yet)
# 1.18.2 = 0
# 1.19.2 = 1
# 1.19.4 = 2
# 1.20.1 = 3
# 1.20.2 = 4
# 1.20.4 = 5
# 1.20.6 = 6
# 1.21   = 7
mc_build=6
```

You can then write code that is specific to a certain version by using the following syntax
```java
#if MC < MC_20_6
    int test = oldCode.get();
#else
    int test = newCode.get();
#endif

//or even in-lined
int test = #if MC < MC_20_6 oldCode #else newCode #endif .get();
```

The values for the #if logic are setup in the root build.gradle file

The gradle.properties overrides for each version can be found in the property_variants folder

## fabric forge neoforge

ETF uses architechtury to build fabric forge and neoforge from the one source.

all code in the common: module will be included in each version, however only fabric will include the fabric: module, etc...

the output jars can be found in the build/libs folder of each version:
- fabric/build/libs/
- forge/build/libs/
- neoforge/build/libs/

**NO VALID JARS WILL BE IN THE REGULAR build/libs/ folder**

