package org.orecruncher.dsurround.tags;

import org.orecruncher.dsurround.Constants;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class FluidTags {

    static final Collection<TagKey<Fluid>> TAGS = new HashSet<>();

    public static final TagKey<Fluid> WATER_RIPPLES = of("water_ripples");
    public static final TagKey<Fluid> WATERFALL_SOURCE = of("waterfall_sources");
    public static final TagKey<Fluid> WATERFALL_SOUND = of("waterfall_sounds");

    private static TagKey<Fluid> of(String id) {
        var tagKey = TagKey.of(RegistryKeys.FLUID, new Identifier(Constants.MOD_ID, "effects/" + id));
        TAGS.add(tagKey);
        return tagKey;
    }
}
