package org.orecruncher.dsurround.tags;

import org.orecruncher.dsurround.Constants;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class BlockEffectTags {

    static final Collection<TagKey<Block>> TAGS = new HashSet<>();

    public static final TagKey<Block> FIREFLIES = of("fireflies");
    public static final TagKey<Block> FLOOR_SQUEAKS = of("floor_squeaks");
    public static final TagKey<Block> BRUSH_STEP = of("brush_step");
    public static final TagKey<Block> LEAVES_STEP = of("leaves_step");
    public static final TagKey<Block> STRAW_STEP = of("straw_step");
    public static final TagKey<Block> WATERY_STEP = of("watery_step");
    public static final TagKey<Block> STEAM_PRODUCERS = of("steam_producers");
    public static final TagKey<Block> HEAT_PRODUCERS = of("heat_producers");

    private static TagKey<Block> of(String id) {
        var tagKey = TagKey.of(RegistryKeys.BLOCK, new Identifier(Constants.MOD_ID, "effects/" + id));
        TAGS.add(tagKey);
        return tagKey;
    }
}
