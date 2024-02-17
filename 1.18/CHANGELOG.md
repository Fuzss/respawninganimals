# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v3.1.0-1.18.2] - 2024-02-17
### Changed
- Improve handling of creatures from mods that register an animal with a mob category that is different from the one used for spawning
    - This is a big issue for how the implementation of Respawning Animals works, it is now fixed on the fly and the offending mods are printed to the log, so that their author can correct the mismatch
- Improve handling of creatures from mods that fail to call `Mob::finalizeSpawn` and therefore do not have their spawn type saved which is required by Respawning Animals
- Animals are now automatically made persistent when attached to a lead, owned by a player and when equipped with a saddle
### Fixed
- Fix any ridden animal becoming persistent, like striders and chickens from jockeys, when only the player riding an animal was supposed to do so

## [v3.0.1-1.18.2] - 2023-08-17
### Changed
- Made it a lot less likely for Respawning Animals to be responsible for endless animal over-spawning in case of a mod conflict

## [v3.0.0-1.18.2] - 2023-08-16
- Ported to Minecraft 1.18.2

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
