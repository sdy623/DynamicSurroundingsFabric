package org.orecruncher.dsurround.lib.registry;

import org.orecruncher.dsurround.lib.GameUtils;

import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RegistryUtils {

    @SuppressWarnings("unchecked")
    public static <T> Optional<Registry<T>> getRegistry(RegistryKey<? extends Registry<T>> registryKey) {
        return GameUtils.getRegistryManager()
                .flatMap(rm -> rm.getOptional(registryKey))
                .or(() -> (Optional<Registry<T>>) Registries.REGISTRIES.getOrEmpty(registryKey.getValue()));
    }

    public static <T> Optional<RegistryEntry.Reference<T>> getRegistryEntry(RegistryKey<Registry<T>> registryKey, T instance) {
        return getRegistry(registryKey)
                .flatMap(r -> r.getEntry(r.getRawId(instance)));
    }

    public static <T> Optional<RegistryEntry.Reference<T>> getRegistryEntry(RegistryKey<Registry<T>> registryKey, Identifier location) {
        RegistryKey<T> rk = RegistryKey.of(registryKey, location);
        return getRegistry(registryKey)
                .flatMap(registry -> registry.getEntry(rk));
    }
}
