package org.orecruncher.dsurround.lib.resources;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import static org.orecruncher.dsurround.Configuration.Flags.RESOURCE_LOADING;

import ;

public class ClientResourceFinder extends AbstractResourceFinder {

    private final ResourceManager resourceManager;

    protected ClientResourceFinder(IModLog logger, ResourceManager resourceManager) {
        super(logger);
        this.resourceManager = resourceManager;
    }

    @Override
    public <T> Collection<DiscoveredResource<T>> find(Codec<T> codec, String assetPath) {

        var results = new HashMap<Identifier, Collection<DiscoveredResource<T>>>();

        this.logger.debug(RESOURCE_LOADING, "[%s] - Locating assets", assetPath);

        var assets = this.resourceManager.findAllResources(assetPath, location -> true);
        if (assets.isEmpty()) {
            this.logger.debug(RESOURCE_LOADING, "[%s] - No assets found", assetPath);
            return ImmutableList.of();
        }

        this.logger.debug(RESOURCE_LOADING, "[%s] - %d entries found", assetPath, assets.size());
        for (var kvp : assets.entrySet()) {
            this.logger.debug(RESOURCE_LOADING, "[%s] - Processing %s", assetPath, kvp.getKey());
            var resultList = results.computeIfAbsent(kvp.getKey(), i -> new ObjectArray<>());

            for (var resource : kvp.getValue()) {
                try (var inputStream = resource.getInputStream()) {
                    var assetBytes = inputStream.readAllBytes();
                    var assetString = new String(assetBytes, Charset.defaultCharset());
                    var entity = this.decode(kvp.getKey(), assetString, codec);
                    entity.ifPresent(e -> resultList.add(new DiscoveredResource<>(kvp.getKey().getNamespace(), e)));
                    this.logger.debug(RESOURCE_LOADING, "[%s] - Completed decode of %s", assetPath, kvp.getKey());
                } catch (Throwable t) {
                    this.logger.error(t, "[%s] - Unable to read resource stream for path %s", assetPath, kvp.getKey());
                }
            }
        }

        return results.values().stream().flatMap(Collection::stream).toList();
    }
}
