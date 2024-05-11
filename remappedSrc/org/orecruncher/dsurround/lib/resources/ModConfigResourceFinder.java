package org.orecruncher.dsurround.lib.resources;

import com.mojang.serialization.Codec;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import static org.orecruncher.dsurround.Configuration.Flags.RESOURCE_LOADING;

import ;

public class ModConfigResourceFinder extends AbstractResourceFinder {

    private final Map<Identifier, List<Resource>> resources;

    public ModConfigResourceFinder(IModLog logger, ResourceManager resourceManager, String configPath) {
        super(logger);
        this.resources = resourceManager.findAllResources(configPath, location -> true);
    }

    public <T> Collection<DiscoveredResource<T>> find(Codec<T> codec, String path) {
        if (!path.endsWith(".json"))
            path = path + ".json";

        var result = new ObjectArray<DiscoveredResource<T>>();
        for (var kvp : this.resources.entrySet()) {
            var resourcePath = kvp.getKey().getPath();
            if (resourcePath.endsWith(path)) {
                this.logger.debug(RESOURCE_LOADING, "[%s] - Processing %s", resourcePath, kvp.getKey());
                for (var r : kvp.getValue()) {
                    try (var inputStream = r.getInputStream()) {
                        var assetBytes = inputStream.readAllBytes();
                        var assetString = new String(assetBytes, Charset.defaultCharset());
                        var entity = this.decode(kvp.getKey(), assetString, codec);
                        entity.ifPresent(e -> result.add(new DiscoveredResource<>(kvp.getKey().getNamespace(), e)));
                        this.logger.debug(RESOURCE_LOADING, "[%s] - Completed decode of %s", resourcePath, kvp.getKey());
                    } catch (Throwable t) {
                        this.logger.error(t, "[%s] - Unable to read resource stream for path %s", resourcePath, kvp.getKey());
                    }
                }
            }
        }

        return result;
    }
}
