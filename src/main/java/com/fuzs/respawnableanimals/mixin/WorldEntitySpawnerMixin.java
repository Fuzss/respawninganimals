package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.config.ConfigBuildHandler;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.class)
public class WorldEntitySpawnerMixin {

    @ModifyVariable(method = "func_234979_a_", at = @At("HEAD"), ordinal = 2)
    private static boolean getShouldSpawnAnimals(boolean shouldSpawnAnimals) {

        // remove 700 tick delay for spawning animals
        return true;
    }

    @Redirect(method = "performWorldGenSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/MobSpawnInfo;getSpawners(Lnet/minecraft/entity/EntityClassification;)Ljava/util/List;"))
    private static List<MobSpawnInfo.Spawners> getSpawners(MobSpawnInfo mobspawninfo, EntityClassification classification) {

        // prevent animals from being spawned on world creation, but exclude blacklisted animals
        List<MobSpawnInfo.Spawners> list = mobspawninfo.getSpawners(classification);
        return list.stream().map(spawner -> {

            if (!ConfigBuildHandler.animalBlacklist.contains(spawner.type)) {

                return new MobSpawnInfo.Spawners(spawner.type, spawner.itemWeight, 0, 0);
            }

            return spawner;
        }).collect(Collectors.toList());
    }

}
