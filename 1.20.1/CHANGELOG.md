# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.2.1-1.20.1] - 2024-12-23
### Fixed
- Fix baby strider jockeys being made persistent, leading to them building up over time

## [v8.2.0-1.20.1] - 2024-02-17
### Changed
- Improve handling of creatures from mods that register an animal with a mob category that is different from the one used for spawning
  - This is a big issue for how the implementation of Respawning Animals works, it is now fixed on the fly and the offending mods are printed to the log, so that their author can correct the mismatch
- Improve handling of creatures from mods that fail to call `Mob::finalizeSpawn` and therefore do not have their spawn type saved which is required by Respawning Animals
- Animals are now automatically made persistent when attached to a lead, owned by a player and when equipped with a saddle
### Fixed
- Fix any ridden animal becoming persistent, like striders and chickens from jockeys, when only the player riding an animal was supposed to do so

## [v8.1.1-1.20.1] - 2023-08-17
### Changed
- Made it a lot less likely for Respawning Animals to be responsible for endless animal over-spawning in case of a mod conflict

## [v8.1.0-1.20.1] - 2023-08-15
- Completely rewrote the mod to no longer rely on animals being classified as instances of the `Animal` class internally, instead now any mob with the `CREATURE` mob category will be affected fully
- This should mainly solve issues with modded animals being allowed to constantly spawn in the world without ever being forced to despawn
- Also, internally no more mixins are used, all the behavior is achieved using built-in events, hopefully aiding mod compatibility (Forge only)
### Removed
- Removed the config file, the animal mob spawn cap is now controlled via a new `animalMobCap` game rule, the animal blacklist was migrated to a new `respawninganimals:persistent_animals` entity type tag

## [v8.0.0-1.20.1] - 2023-06-27
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
