package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.class)
public abstract class WorldEntitySpawnerMixin {

    @Redirect(method = "performWorldGenSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/MobSpawnInfo;getSpawners(Lnet/minecraft/entity/EntityClassification;)Ljava/util/List;"))
    private static List<MobSpawnInfo.Spawners> getSpawners(MobSpawnInfo mobspawninfo, EntityClassification classification, IServerWorld worldIn) {

        List<MobSpawnInfo.Spawners> list = mobspawninfo.getSpawners(classification);
        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (!element.isEnabled() || worldIn.getWorld().getGameRules().getBoolean(AnimalsElement.PERSISTENT_ANIMALS)) {

            return list;
        }

        // prevent animals from being spawned on world creation, but exclude blacklisted animals
        return list.stream().map(spawner -> {

            if (!element.animalBlacklist.contains(spawner.type)) {

                // do not filter but replace with dummy entries instead to retain weight
                return new MobSpawnInfo.Spawners(spawner.type, spawner.itemWeight, 0, 0);
            }

            return spawner;
        }).collect(Collectors.toList());
    }

}
