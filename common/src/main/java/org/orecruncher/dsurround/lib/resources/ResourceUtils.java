package org.orecruncher.dsurround.lib.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.Library;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.platform.IPlatform;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public final class ResourceUtils {

    static final IModLog LOGGER = Library.getLogger();
    private static final IPlatform PLATFORM = Library.getPlatform();

    /**
     * Scans the local disk as well as resource packs and JARs locating and creating accessors for the config file
     * in question.
     *
     * @param diskPath Location on disk where external configs can be cached
     * @param config   The config file that is of interest
     * @return A collection of resource accessors that match the config criteria
     */
    public static Collection<IResourceAccessor> findConfigs(final File diskPath, final String config) {
        Map<ResourceLocation, IResourceAccessor> accessorMap = new HashMap<>();
        collectFromResourcePacks(accessorMap, config);
        collectFromDisk(accessorMap, diskPath, config);
        return new ArrayList<>(accessorMap.values());
    }

    private static void collectFromResourcePacks(final Map<ResourceLocation, IResourceAccessor> accessorMap, final String config) {
        var results = findAssets(
                PLATFORM::isModLoaded,
                ns -> new ResourceLocation(ns, String.format("dsconfigs/%s", config)));
        for (var e : results) {
            accessorMap.put(e.location(), e);
        }
    }

    private static void collectFromDisk(final Map<ResourceLocation, IResourceAccessor> accessorMap, File diskPath, final String config) {
        // Gather loaded mods.  We focus on those from within the JAR
        var loadedMods = PLATFORM.getModIdList(true);
        for (var mod : loadedMods) {
            ResourceLocation location = new ResourceLocation(mod, config);
            IResourceAccessor accessor = IResourceAccessor.createExternalResource(diskPath, location);
            if (accessor.exists())
                accessorMap.put(location, accessor);
        }
    }

    /**
     * Scans resource packs locating sound.json configurations.
     *
     * @return Collection of accessors to retrieve sound.json configurations.
     */
    public static Collection<IResourceAccessor> findSounds(String configId) {
        return findAssets(
                ns -> true,
                ns -> new ResourceLocation(ns, configId));
    }

    public static Collection<IResourceAccessor> findClientTagFiles(TagKey<?> tagKey) {
        var tagIdentifier = tagKey.location();
        var tagType = TagManager.getTagDir(tagKey.registry());
        var tagFile = "%s/%s.json".formatted(tagType, tagIdentifier.getPath());

        final List<IResourceAccessor> results = new ArrayList<>();
        var resourceManager = GameUtils.getResourceManager();
        resourceManager.listPacks()
                .forEach(pack -> {
                    try {
                        var location = new ResourceLocation(tagIdentifier.getNamespace(), tagFile);
                        var resource = pack.getResource(PackType.SERVER_DATA, location);
                        if (resource != null)
                            try (var inputStream = resource.get()) {
                                byte[] asset = inputStream.readAllBytes();
                                IResourceAccessor accessor = IResourceAccessor.createRawBytes(location, asset);
                                results.add(accessor);
                            } catch (Throwable t) {
                                LOGGER.error(t, "Unable to read resource stream");
                            }
                    } catch (Throwable ignore) {
                    }
                });

        return results;
    }

    // Modeled after sound list processing in SoundManager
    private static Collection<IResourceAccessor> findAssets(Function<String, Boolean> namespaceFilter, Function<String, ResourceLocation> identitySupplier) {
        final List<IResourceAccessor> results = new ArrayList<>();
        var resourceManager = GameUtils.getResourceManager();

        for (var namespace : resourceManager.getNamespaces()) {
            try {
                if (!namespaceFilter.apply(namespace))
                    continue;

                var location = identitySupplier.apply(namespace);
                var resourceList = resourceManager.getResourceStack(location);

                try {
                    for (var resource : resourceList) {
                        try (InputStream inputStream = resource.open()) {
                            byte[] asset = inputStream.readAllBytes();
                            IResourceAccessor accessor = IResourceAccessor.createRawBytes(location, asset);
                            results.add(accessor);
                        } catch (Throwable t) {
                            LOGGER.error(t, "Unable to read the asset %s from the resource pack", location);
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.error(t, "Unable to enumerate resource for %s", location);
                }
            } catch (Throwable ignore) {
                // Suppress - get a lot of not founds for packs that do not have a resource
            }
        }

        return results;
    }
}
