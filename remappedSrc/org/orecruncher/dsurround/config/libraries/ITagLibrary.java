package org.orecruncher.dsurround.config.libraries;

import it.unimi.dsi.fastutil.Pair;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public interface ITagLibrary extends ILibrary {

    boolean is(TagKey<Block> tagKey, BlockState entry);
    boolean is(TagKey<Item> tagKey, ItemStack entry);
    default boolean is(TagKey<Item> tagKey, Item item) {
        return this.is(tagKey, new ItemStack(item));
    }
    boolean is(TagKey<Biome> tagKey, Biome entry);
    boolean is(TagKey<EntityType<?>> tagKey, EntityType<?> entry);
    boolean is(TagKey<Fluid> tagKey, FluidState entry);

    <T> String asString(Stream<TagKey<T>> tagStream);
    <T> Stream<Pair<TagKey<T>, Set<T>>> getEntriesByTag(RegistryKey<? extends Registry<T>> registry);
    <T> Stream<TagKey<T>> streamTags(RegistryEntry<T> registryEntry);
}
