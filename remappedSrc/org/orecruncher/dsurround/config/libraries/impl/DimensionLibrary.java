package org.orecruncher.dsurround.config.libraries.impl;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.orecruncher.dsurround.config.data.DimensionConfigRule;
import org.orecruncher.dsurround.config.dimension.DimensionInfo;
import org.orecruncher.dsurround.config.libraries.IDimensionLibrary;
import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.lib.collections.ObjectArray;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import org.orecruncher.dsurround.lib.util.IMinecraftDirectories;

import java.util.*;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public final class DimensionLibrary implements IDimensionLibrary {

    private static final String FILE_NAME = "dimensions.json";
    private static final Codec<List<DimensionConfigRule>> CODEC = Codec.list(DimensionConfigRule.CODEC);

    private final IModLog logger;
    private final IMinecraftDirectories directories;
    private final ObjectArray<DimensionConfigRule> dimensionRules = new ObjectArray<>();
    private final Map<RegistryKey<World>, DimensionInfo> configs = new Object2ObjectOpenHashMap<>();
    private int version = 0;

    public DimensionLibrary(IModLog logger, IMinecraftDirectories directories) {
        this.logger = logger;
        this.directories = directories;
    }

    @Override
    public void reload(ResourceUtilities resourceUtilities, IReloadEvent.Scope scope) {

        this.version++;

        if (scope == IReloadEvent.Scope.TAGS) {
            this.logger.info("[DimensionLibrary] received tag update notification; version is now %d", this.version);
            return;
        }
        
        this.configs.clear();
        this.dimensionRules.clear();

        var findResults = resourceUtilities.findModResources(CODEC, FILE_NAME);
        findResults.forEach(result -> this.dimensionRules.addAll(result.resourceContent()));

        this.logger.info("[DimensionLibrary] %d dimension rules loaded; version is now %d", this.dimensionRules.size(), this.version);
    }

    @Override
    public DimensionInfo getData(final World world) {
        return this.configs.computeIfAbsent(
                world.getRegistryKey(),
                key -> {
                    var dimInfo = new DimensionInfo(world);
                    this.dimensionRules.forEach(dimInfo::update);
                    return dimInfo;
                });
    }

    @Override
    public Stream<String> dump() {
        return this.dimensionRules.stream().map(Object::toString).sorted();
    }
}