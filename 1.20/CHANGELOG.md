# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.1.0-1.20.1] - 2023-08-15
- Completely rewrote the mod to no longer rely on animals being classified as instances of the `Animal` class internally, instead now any mob with the `CREATURE` mob category will be affected fully
- This should mainly solve issues with modded animals being allowed to constantly spawn in the world without ever being forced to despawn
- Also, internally no more mixins are used, all the behavior is achieved using built-in events, hopefully aiding mod compatibility (Forge only)
### Removed
- Removed the config file, the animal mob spawn cap is now controlled via a new `animalMobCap` game rule, the animal blacklist was migrated to a new `respawninganimals:persistent_animals` entity type tag

## [v8.0.0-1.20.1] - 2023-06-27
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
