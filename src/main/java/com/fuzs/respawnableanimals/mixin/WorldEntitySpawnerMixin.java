package com.fuzs.respawnableanimals.mixin;

import com.fuzs.respawnableanimals.common.RespawnableAnimalsElements;
import com.fuzs.respawnableanimals.common.element.AnimalsElement;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mixin(WorldEntitySpawner.class)
public abstract class WorldEntitySpawnerMixin {

    @ModifyVariable(method = "performWorldGenSpawning", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), ordinal = 0)
    private static List<Biome.SpawnListEntry> getSpawners(List<Biome.SpawnListEntry> list, IWorld worldIn) {

        AnimalsElement element = RespawnableAnimalsElements.getAs(RespawnableAnimalsElements.RESPAWNABLE_ANIMALS);
        if (!element.isEnabled() || worldIn.getWorld().getGameRules().getBoolean(AnimalsElement.PERSISTENT_ANIMALS)) {

            return list;
        }

        // prevent animals from being spawned on world creation, but exclude blacklisted animals
        return list.stream().map(spawner -> {

            if (!element.animalBlacklist.contains(spawner.entityType)) {

                // do not filter but replace with dummy entries instead to retain weight
                return new Biome.SpawnListEntry(spawner.entityType, spawner.itemWeight, 0, 0);
            }

            return spawner;
        }).collect(Collectors.toList());
    }

}
