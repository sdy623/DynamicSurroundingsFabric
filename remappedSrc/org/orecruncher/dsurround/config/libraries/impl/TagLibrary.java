package org.orecruncher.dsurround.config.libraries.impl;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.tags.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.orecruncher.dsurround.Constants;
import org.orecruncher.dsurround.config.libraries.IReloadEvent;
import org.orecruncher.dsurround.config.libraries.ITagLibrary;
import org.orecruncher.dsurround.eventing.ClientState;
import org.orecruncher.dsurround.lib.GameUtils;
import org.orecruncher.dsurround.lib.registry.RegistryUtils;
import org.orecruncher.dsurround.lib.logging.IModLog;
import org.orecruncher.dsurround.lib.resources.ClientTagLoader;
import org.orecruncher.dsurround.lib.resources.ResourceUtilities;
import org.orecruncher.dsurround.lib.system.IStopwatch;
import org.orecruncher.dsurround.lib.system.ISystemClock;
import org.orecruncher.dsurround.tags.ModTags;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class TagLibrary implements ITagLibrary {

    private final IModLog logger;
    private final ISystemClock systemClock;
    private final Map<TagKey<?>, Collection<Identifier>> tagCache;
    private final ClientTagLoader tagLoader;

    private boolean isConnected;

    public TagLibrary(IModLog logger, ISystemClock systemClock) {
        this.logger = logger;
        this.systemClock = systemClock;
        this.tagCache = new Reference2ObjectOpenHashMap<>();
        this.tagLoader = new ClientTagLoader(ResourceUtilities.createForCurrentState(), this.logger, this.systemClock);

        // Need to clear the tag caches on disconnect. It's possible that
        // cached biome information will change with the next connection.
        ClientState.ON_CONNECT.register(this::onConnect);
        ClientState.ON_DISCONNECT.register(this::onDisconnect);
    }

    @Override
    public boolean is(TagKey<Block> tagKey, BlockState entry) {
        // For our purposes, blocks that are ignored will not have the
        // tags we are interested in.
        if (Constants.BLOCKS_TO_IGNORE.contains(entry.getBlock()))
            return false;
        if (entry.isIn(tagKey))
            return true;
        var location = entry.getRegistryEntry().getKey().orElseThrow().getValue();
        return this.isInCache(tagKey, location);
    }

    @Override
    public boolean is(TagKey<Item> tagKey, ItemStack entry) {
        if (entry.isEmpty())
            return false;
        if (entry.isIn(tagKey))
            return true;
        var location = entry.getRegistryEntry().getKey().orElseThrow().getValue();
        return this.isInCache(tagKey, location);
    }

    @Override
    public boolean is(TagKey<Biome> tagKey, Biome entry) {
        var registryEntry = RegistryUtils.getRegistryEntry(RegistryKeys.BIOME, entry);
        if (registryEntry.isPresent()) {
            var e = registryEntry.get();
            if (e.isIn(tagKey))
                return true;
            var location = e.registryKey().getValue();
            return this.isInCache(tagKey, location);
        }
        return false;
    }

    @Override
    public boolean is(TagKey<EntityType<?>> tagKey, EntityType<?> entry) {
        if (entry.isIn(tagKey))
            return true;

        var registryEntry = RegistryUtils.getRegistryEntry(RegistryKeys.ENTITY_TYPE, entry);
        if (registryEntry.isPresent()) {
            var location = registryEntry.get().registryKey().getValue();
            return this.isInCache(tagKey, location);
        }
        return false;
    }

    @Override
    public boolean is(TagKey<Fluid> tagKey, FluidState entry) {
        if (entry.isEmpty())
            return false;
        if (entry.isIn(tagKey))
            return true;

        var registryEntry = RegistryUtils.getRegistryEntry(RegistryKeys.FLUID, entry.getFluid());
        if (registryEntry.isPresent()) {
            var location = registryEntry.get().registryKey().getValue();
            return this.isInCache(tagKey, location);
        }
        return false;
    }

    @Override
    public Stream<String> dump() {
        return this.tagCache.entrySet().stream()
                .map(kvp -> {
                    var builder = new StringBuilder();

                    builder.append("Tag: ").append(kvp.getKey().toString());
                    var td = kvp.getValue();

                    if (td.isEmpty()) {
                        // Makes it easier to spot in the logs
                        builder.append("\n*** EMPTY ***");
                    } else {
                        this.formatHelper(builder, "Members", this.tagLoader.getCompleteIds(kvp.getKey()));
                    }

                    builder.append("\n");
                    return builder.toString();
                })
                .sorted();
    }

    @Override
    public void reload(ResourceUtilities resourceUtilities, IReloadEvent.Scope scope) {
        this.tagLoader.setResourceUtilities(resourceUtilities);

        this.logger.info("[TagLibrary] Cache has %d elements", this.tagCache.size());

        // If we are connected to a server, and we get a reload something triggered it
        // like a /dsreload, resource pack change, etc.
        if (this.isConnected)
            this.initializeTagCache();
    }

    @Override
    public <T> String asString(Stream<TagKey<T>> tagStream) {
        return tagStream
                .map(key -> key.id().toString())
                .sorted()
                .collect(Collectors.joining(", "));
    }

    @Override
    public <T> Stream<Pair<TagKey<T>, Set<T>>> getEntriesByTag(RegistryKey<? extends Registry<T>> registryKey) {
        var registry = RegistryUtils.getRegistry(registryKey).orElseThrow();
        return registry.streamEntries()
                .flatMap(e -> this.streamTags(e).map(tag -> Pair.of(tag, e.value())))
                .collect(groupingBy(Pair::key, mapping(Pair::value, toSet())))
                .entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<TagKey<T>> streamTags(RegistryEntry<T> registryEntry) {
        var location = registryEntry.getKey().orElseThrow().getValue();
        Set<TagKey<T>> tags = registryEntry.streamTags().collect(toSet());
        for (var kvp : this.tagCache.entrySet()) {
            if (kvp.getValue().contains(location))
                tags.add((TagKey<T>) kvp.getKey());
        }
        return tags.stream();
    }

    private void onConnect(MinecraftClient client) {
        this.isConnected = true;
        this.tagLoader.setServerType(GameUtils.getServerType());
        this.initializeTagCache();
    }

    private void onDisconnect(MinecraftClient client) {
        this.isConnected = false;
    }

    private void initializeTagCache() {
        // Bootstrap the tag cache. We do this by zipping through our tags
        // and forcing the cache to initialize.
        var stopwatch = this.systemClock.getStopwatch();
        this.logger.info("Repopulating tag cache");
        this.tagCache.clear();
        this.tagLoader.clear();
        for (var tagKey : ModTags.getModTags())
            this.tagCache.computeIfAbsent(tagKey, this.tagLoader::getMembers);
        this.logger.info("Tag cache initialization complete; %d tags cached, %dmillis", this.tagCache.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private boolean isInCache(TagKey<?> tagKey, Identifier entry) {
        if (!ModTags.getModTags().contains(tagKey))
            return false;
        return this.tagCache.computeIfAbsent(tagKey, this.tagLoader::getMembers).contains(entry);
    }

    private void formatHelper(StringBuilder builder, String entryName, Collection<Identifier> data) {
        builder.append("\n").append(entryName).append(" ");
        if (data.isEmpty())
            builder.append("NONE");
        else {
            builder.append("[");
            for (var e : data)
                builder.append("\n  ").append(e.toString());
            builder.append("\n]");
        }
    }
}
