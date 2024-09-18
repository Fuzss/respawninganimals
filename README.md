# Respawning Animals

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects) and [Modrinth](https://modrinth.com/user/Fuzs).

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/respawninganimals/banner.png)

## Configuration
Respawning Animals is fully controlled via game rules and tags. No other means of configuration are supported.

The whole mod can be toggled on a per-world basis via the `persistentAnimals` game rule. Setting it to `true` disables the mod and allows vanilla behavior to run. Setting it to `false` allows animals to despawn in the world. By default, the game rule is set to `true` for existing worlds (created before installing the mod), and to `false` for newly created world (created with the mod installed).

For controlling the amount of animals spawned, the `animalMobCap` game rule can be changed. The default value is `15`, which is similar to the value used by the animal spawning mechanics in Minecraft Beta which this mod is based on. The same value for e.g. monsters is set to `70` for the sake of comparison.

Lastly it is possible to prevent individual types of entities from being affected by the new spawning mechanics, even when the `persistentAnimals` game rule is disabled and the mod is in effect. Those entities can simply be added to the `respawninganimals:persistent_animals` entity type tag.
