package org.orecruncher.dsurround.tags;

import org.orecruncher.dsurround.Constants;

import java.util.Collection;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ReflectanceTags {

    static final Collection<TagKey<Block>> TAGS = new HashSet<>();


    public static final TagKey<Block> NONE = of("none");
    public static final TagKey<Block> VERY_LOW = of("very_low");
    public static final TagKey<Block> LOW = of("low");
    public static final TagKey<Block> MEDIUM = of("medium");
    public static final TagKey<Block> HIGH = of("high");
    public static final TagKey<Block> VERY_HIGH = of("very_high");
    public static final TagKey<Block> MAX = of("max");

    private static TagKey<Block> of(String id) {
        var tagKey = TagKey.of(RegistryKeys.BLOCK, new Identifier(Constants.MOD_ID, "reflectance/" + id));
        TAGS.add(tagKey);
        return tagKey;
    }
}